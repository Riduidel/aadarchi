package org.ndx.aadarchi.sequence.generator.javaparser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.DiagramsDir;
import org.ndx.aadarchi.base.utils.StructurizrUtils;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.sequence.generator.SequenceGenerator;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.CallGraphModel;

import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.ProjectRoot;
import com.github.javaparser.utils.SourceRoot;
import com.structurizr.model.CodeElement;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;

public class SequenceDiagramVisitor extends ModelElementAdapter {
	@Inject Logger logger;

	@Inject 
	@ConfigProperty(name = DiagramsDir.NAME, defaultValue = DiagramsDir.VALUE)
	FileObject destination;
	
	@Inject FileSystemManager fsManager;

	/**
	 * Map container canonical name to the container object.
	 */
	Map<String, Container> allContainers;

	/**
	 * Map linking classes names to components they're code element for
	 */
	Map<String, Component> codeToComponents;
	/**
	 * Helper map allowing us to understand what is wrong, and when
	 */
	Map<String, Container> pathsToContainers = new LinkedHashMap<String, Container>();
	
	/**
	 * This call graph model should contain all details of calls between components of application.
	 * It is populated at each {@link #startVisit(Container)} call and reset after call
	 */
	CallGraphModel callGraphModel = null;
	
	@Override
	public boolean isParallel() {
		return false;
	}
	/**
	 * When set to true, the container source code and the one of declared
	 * parsable model elements will be read and loaded into a JavaParser model.
	 * @param container
	 * @return true to examine this container more in details.
	 */
	private boolean allowSequenceGeneration(Container container) {
		return container.getProperties().containsKey(SequenceGenerator.GENERATES_WITH);
	}
	
	@Override
	public boolean startVisit(Model model) {
		// First make sure we have all container listed
		allContainers = model.getSoftwareSystems().stream()
			.flatMap(systems -> systems.getContainers().stream())
			.collect(Collectors.toMap(container -> StructurizrUtils.getCanonicalPath(container), Function.identity()));
		// And all components code elements linked to their associated components
		codeToComponents = model.getSoftwareSystems().stream()
				.flatMap(systems -> systems.getContainers().stream())
				.flatMap(containers -> containers.getComponents().stream())
				.flatMap(component -> component.getCode().stream()
						.map(element -> Map.entry(element.getType(), component)))
				.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()))
				;
		return super.startVisit(model);
	}
	
	/**
	 * Create a project root for a given container.
	 * THis is quite long : we have to get all source roots of containers we want to generate code with
	 * create a JavaParser {@link ProjectRoot} object with that.
	 * @param container
	 * @return
	 */
	private ProjectRoot createProjectRootFor(Container container) {
		mapPathsToContainers(getAssociatedContainersOf(container));
		ProjectRoot projectRoot = new ProjectRootBuilder(fsManager, pathsToContainers)
				.build(container);
		return projectRoot;
	}
	/**
	 * From the associated containers, build the {@link #pathsToContainers} map and returns it
	 * @param container
	 * @return
	 */
	private Map<String, Container> mapPathsToContainers(Set<Container> associatedContainers) {
		for(Container associatedContainer : associatedContainers) {
			for(String path : associatedContainer.getProperties().get(ModelElementKeys.JAVA_SOURCES).split(";")) {
				pathsToContainers.put(path, associatedContainer);
			}
		}
		return pathsToContainers;
	}
	private Set<Container> getAssociatedContainersOf(Container container) {
		String containerNames = container.getProperties().get(SequenceGenerator.GENERATES_WITH);
		Set<Container> returned = new HashSet<Container>();
		returned.add(container);
		returned.addAll(Stream.of(containerNames.split(";"))
			.filter(containerName -> allContainers.containsKey(containerNames))
			.map(containerName -> allContainers.get(containerName))
			// now we have a container
			.filter(associatedContainer -> associatedContainer.getTechnology()!=null)
			.filter(associatedContainer -> associatedContainer.getTechnology().toLowerCase().contains("java"))
			.filter(associatedContainer -> associatedContainer.getProperties().containsKey(ModelElementKeys.JAVA_SOURCES))
			.collect(Collectors.toList()));
		return returned;
	}
	
	@Override
	public boolean startVisit(Container container) {
		if(allowSequenceGeneration(container)) {
			if(container.getProperties().containsKey(ModelElementKeys.JAVA_SOURCES)) {
				ProjectRoot projectRoot = createProjectRootFor(container);
				Map<String, CompilationUnit> sources = parseAllSources(projectRoot);
				callGraphModel = new CallGraphModel(codeToComponents, sources, fsManager);
				// Now we have all compilation units parsed, let's try to analyze that a little by, say,
				// mapping class names to their associated compilation units
				callGraphModel.analyzeCalls(
						getComponentsToScanFor(container).stream()
							.flatMap(component -> component.getCode().stream())
							.map(code -> code.getType())
							.collect(Collectors.toList())
						);
				return true;
			} else {
				logger.log(Level.SEVERE, String.format("Unable to generate sequence diagrams since container %s has no associated sources", StructurizrUtils.getCanonicalPath(container)));
			}
		}
		// In any missing info case, return false
		return false;
	}
	
	/**
	 * Get all components to scan.
	 * This obviously includes all component from the given container, but also all components from
	 * associated containers (for calls to interfaces be resolved against implementations
	 * @param container
	 * @return
	 */
	private Set<Component> getComponentsToScanFor(Container container) {
		Set<Component> components = new HashSet<Component>(container.getComponents());
		components.addAll(getAssociatedContainersOf(container).stream()
				.flatMap(associatedContainer -> associatedContainer.getComponents().stream())
				.collect(Collectors.toList())
				);
		return components;
	}
	private Map<String, CompilationUnit> parseAllSources(ProjectRoot projectRoot) {
		List<CompilationUnit> allParsed = new ArrayList<CompilationUnit>();
		// Now we have a big project root full of things to parse!
		for(SourceRoot sourceRoot : projectRoot.getSourceRoots()) {
			try {
				sourceRoot.parseParallelized(new SourceRoot.Callback() {

					@Override
					public Result process(Path localPath, Path absolutePath,
							ParseResult<CompilationUnit> result) {
						result.ifSuccessful(unit -> allParsed.add(unit));
						// We don't want to modify any line of code here !
						return Result.DONT_SAVE;
					}
					
				});
			} catch(IOException e) {
				Container associated = pathsToContainers.get(sourceRoot.getRoot().toString());
				logger.log(Level.SEVERE, String.format("Unable to parse source root %s (associated to container %s)", 
						sourceRoot.getRoot(), StructurizrUtils.getCanonicalPath(associated)), e);
			}
		}
		Map<String, CompilationUnit> namesToSources = new TreeMap<String, CompilationUnit>();
		// Now they're parsed, let's try to map them to public classes or interfaces contained
		allParsed.stream().parallel().forEach(cu ->
			// Source files for which no primary type exists are ignored (they're useless in our case)
			cu.getPrimaryTypeName().ifPresent(name -> 
				cu.getPrimaryType().ifPresent(primaryType ->
					primaryType.getFullyQualifiedName().ifPresent(fullyQualifiedName ->
						namesToSources.put(fullyQualifiedName, cu)
					)
				)
			)
		);
		return namesToSources;
	}
	
	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		pathsToContainers.clear();
		callGraphModel = null;
		super.endVisit(container, builder);
	}
	
	@Override
	public boolean startVisit(Component component) {
		if(allowSequenceGeneration(component.getContainer())) {
			return true;
		} else {
			return false;
		}
	}
	/**
	 * We only analyze method declared as public in the publicized calss
	 * @param analyzed
	 * @return
	 */
	private Collection<Method> getMethodsToAnalyzeIn(Class<?> analyzed) {
		return Stream.of(analyzed.getDeclaredMethods())
				.filter(method -> Modifier.isPublic(method.getModifiers()))
				.collect(Collectors.toList());
	}
	/**
	 * Detect public element of a component.
	 * In Spring parlance, it is most of the time the interface implemented by the associated class.
	 * In other cases (the most dangerous being Qi4J)... well, I don't know and this will fail.
	 */
	private Class<?> detectPublicCodeElementOf(Component component) {
		// Here we use a linked list to accumulate, then return the first element (which should be the interface)
		LinkedList<Class<?>> classesOfComponent = new LinkedList<Class<?>>();
		for(CodeElement element : component.getCode()) {
			try {
				Class<?> clazz = getClass().getClassLoader().loadClass(element.getType());
				if(clazz.isInterface()) {
					if(!classesOfComponent.isEmpty()) {
						if(classesOfComponent.getFirst().isInterface()) {
							logger.severe(String.format("Congratulations! You found a bug in that code!\n"
									+ "The bug being the inhability of this sequence diagram generator to understand what to do when a component is defined by more than one interface."
									+ "To help that tool grow, please enter your case as a ticket in https://github.com/Riduidel.aadarchi-system/issues."
									+ "Offending component is %s which has as linked code elements %s", component,
									component.getCode().stream().map(code -> code.getType()).collect(Collectors.joining(";"))));
						}
					}
					classesOfComponent.addFirst(clazz);
				} else {
					classesOfComponent.addLast(clazz);
				}
			} catch (ClassNotFoundException e) {
				logger.log(Level.WARNING, String.format("Unable to load code element %s of component %s", element, StructurizrUtils.getCanonicalPath(component)), e);
			}
		}
		// Now get all declared methods of first class, cause that's the ones we want!
		if(classesOfComponent.isEmpty()) {
			throw new UnsupportedOperationException(
					String.format("C4 component %s is declared withouth any code element found. Is it a mistake?", component));
		}
		return classesOfComponent.getFirst();
	}
	
	/**
	 * As this method is only called when {@link #startVisit(Component)} returned true,
	 * we can put code only here.
	 * Notice that if an interface is present, it is read first as ONLY methods declared in interfaces
	 * will have sequence diagrams generated for.
	 * Otherwise all methods will have their diagrams (which can create some mess)
	 */
	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		// If component has no code associated, AND we want to use that mechanism
		// there is a probblem, and probably something to change in config
		// TODO Jason, can you please think about that
		if(!component.getCode().isEmpty()) {
			// First step is to detect if we have an interface code element
			// which case we can expose only its methods
			Class<?> analyzed = detectPublicCodeElementOf(component);
			Collection<Method> toAnalyze = getMethodsToAnalyzeIn(analyzed);
			// We should have read all source code, so we can use the sequence navigator to generate all the sequence diagrams
			callGraphModel.generatePlantUMLDiagramFor(component, destination);
		}
	}

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS+100;
	}

}

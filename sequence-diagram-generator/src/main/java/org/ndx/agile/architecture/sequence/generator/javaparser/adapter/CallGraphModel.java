package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.ndx.agile.architecture.sequence.generator.javaparser.generator.SequenceDiagramGenerator;
import org.ndx.agile.architecture.sequence.generator.javaparser.visitor.JavaParserVisitorForBuildingCallGraph;

import com.github.javaparser.ast.CompilationUnit;
import com.structurizr.model.Component;

public class CallGraphModel {
	private static final Logger logger = Logger.getLogger(CallGraphModel.class.getName());

	/**
	 * Mapping between classes and components.
	 * THis allows us to filter the model and remove uninteresting calls 
	 * (think about all calls to JDK classes)
	 */
	public final Map<String, Component> classesToComponents;
	
	/**
	 * Resulting data structure mapping classes names to their representation *in our model*
	 */
	Map<String, TypeRepresentation> namesToClasses = new TreeMap<>();
	
	/**
	 * List of classes that should be visited, as they're discovered during the scan of required classes.
	 */
	public SortedSet<String> unknownClasses = new TreeSet<>();

	/**
	 * Full map of classes that have been parsed.
	 */
	private Map<String, CompilationUnit> sources;

	public CallGraphModel(Map<String, Component> classesToComponents, Map<String, CompilationUnit> sources) {
		super();
		this.sources = sources;
		this.classesToComponents = classesToComponents;
	}
	
	public TypeRepresentation getClassFor(String className) {
		if(!namesToClasses.containsKey(className)) {
			namesToClasses.put(className, new TypeRepresentation(this, className));
			// If class is new, it is unknown as a default
		}
		return namesToClasses.get(className);
	}

	/**
	 * Perform call analysis to build model
	 * @param collect
	 */
	public void analyzeCalls(List<String> typeNames) {
		for(Collection<String> types : Arrays.asList(typeNames, unknownClasses)) {
			for(String type : types) {
				// Some classes may not have associated compilation units (that's the case of inner classes, as an example)
				// So just ignore them
				if (sources.containsKey(type)) {
					CompilationUnit cu = sources.get(type);
					cu.accept(new JavaParserVisitorForBuildingCallGraph(), getClassFor(type));
				} else {
					logger.warning(String.format("No source file found for type %s", type));
				}
			}
		}
	}

	public void generatePlantUMLDiagramFor(Component component, File destination) {
		component.getCode().stream()
			.map(code -> code.getType())
			.map(code -> namesToClasses.get(code))
			.forEach(representation -> 
				representation.accept( new SequenceDiagramGenerator(
						new File(destination, component.getCanonicalName().substring(1).replace('.', '.')),
						classesToComponents,
						this))
					);
	}

	public CodeRepresentation getSubClassFor(TypeRepresentation parentTypeDeclaration, String typeName) {
		classesToComponents.put(typeName, classesToComponents.get(parentTypeDeclaration.name));
		TypeRepresentation returned = getClassFor(typeName);
		returned.setParent(parentTypeDeclaration);
		return returned;
	}
}

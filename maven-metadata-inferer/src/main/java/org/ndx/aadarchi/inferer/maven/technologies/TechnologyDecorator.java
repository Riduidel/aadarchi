package org.ndx.aadarchi.inferer.maven.technologies;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenEnhancer;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;

/**
 * Component dedicated to technology decoration.
 * Given a maven pom and an element, it will detect interesting technologies,
 * add them to the element when possible,
 * and add them to documentation when possible
 */
@Default
@ApplicationScoped
public class TechnologyDecorator {
	@Inject @Named(MvnRepositoryArtifactsProducer.MVNREPOSITORY_ARTIFACTS) Map<String, MvnRepositoryArtifact> mvnRepositoryArtifacts;

	ObjectMapper objectMapper = new ObjectMapper();

	/**
	 * @param element element for which we want the technologies
	 * @param project
	 * @return a string giving details about important project infos
	 */
	public void decorateTechnology(Element element, MavenProject project) {
		Map<String, String> dependencies = new TreeMap<String, String>();
		/**
		 * Unfortunatly, we have to make some cumbersome code to extract versions expressed in dependency management
		 * (or through properties)
		 */
		Map<String, String> managedDependenciesVersions = new TreeMap<String, String>();
		MavenPomDecorator.decorateRecursively(project, (mavenProject, mavenModules) -> {
			if(mavenProject.getDependencyManagement()!=null) {
				updateManagedDependencies(managedDependenciesVersions, mavenProject);
				// Before to write that, let's replace all dependencies versions with the ones we know
				for(String dependencyId : dependencies.keySet()) {
					if(managedDependenciesVersions.containsKey(dependencyId)) {
						dependencies.put(dependencyId, managedDependenciesVersions.get(dependencyId));
					}
				}
			}
			dependencies.putAll(doDecorateTechnologies(mavenProject, element));
			// We should explore all parent poms
			return true;
		});
		try {
			element.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES, objectMapper.writeValueAsString(dependencies));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateManagedDependencies(Map<String, String> managedDependenciesVersions, MavenProject mavenProject) {
		mavenProject.getDependencyManagement().getDependencies()
			.forEach(d -> managedDependenciesVersions.put(d.getGroupId()+"."+d.getArtifactId(), d.getVersion()));
		for(String dependencyId : managedDependenciesVersions.keySet()) {
			String version = managedDependenciesVersions.get(dependencyId);
			String propertyInterpolationStart = "${";
			String propertyInterpolationEnd = "}";
			while(version.contains(propertyInterpolationStart)) {
				String property = version.substring(version.indexOf(
						propertyInterpolationStart)+propertyInterpolationStart.length(), 
						version.indexOf(propertyInterpolationEnd));
				if(mavenProject.getProperties().containsKey(property)) {
					version = version.replace(
							propertyInterpolationStart+property+propertyInterpolationEnd, 
							mavenProject.getProperties().getProperty(property));
					managedDependenciesVersions.put(dependencyId, version);
				} else {
					// If this property is unknown, it may well be defined in a parent pom
					break;
				}
			}
		}
	}

	/**
	 * Decorate the given element with the given technologies by applying the following steps
	 * <ol>
	 * <li>Detect technologies in dependencies</li>
	 * <li>Transform them into a set of names</li>
	 * <li>Merge that set of names with the existing technologies of element</li>
	 * </ol>
	 * @param mavenProject
	 * @param element
	 * @return 
	 */
	private Map<String, String> doDecorateTechnologies(MavenProject mavenProject, Element element) {
		Map<Dependency, MvnRepositoryArtifact> dependenciesToArtifacts = ((List<Dependency>) mavenProject.getDependencies()).stream()
			.filter(d -> mvnRepositoryArtifacts.containsKey(d.getGroupId()+"."+d.getArtifactId()))
			.collect(Collectors.toMap(Function.identity(), 
					d -> mvnRepositoryArtifacts.get(d.getGroupId()+"."+d.getArtifactId())));
		// Now we can map dependencies to artifacts, first put the list of artifact names into technologies
		List<String> technologies = dependenciesToArtifacts.values().stream()
				// We filter out all technologies tagged with "testing" to simplify things a little in technologies
				.filter(a -> !a.tags.contains("testing"))
				.map(a -> a.name)
				.collect(Collectors.toList());
		if(!dependenciesToArtifacts.values().stream()
				.filter(a -> a.tags.contains("language"))
				.findAny()
				.isPresent()) {
			// No language lib has been found, so let's consider it a Java element!
			technologies.add("Java");
		}
		injectTechnologiesInElement(element, technologies);
		return dependenciesToArtifacts.keySet().stream().collect(Collectors.toMap(d -> d.getGroupId()+"."+d.getArtifactId(), d -> d.getVersion()==null ? "":d.getVersion()));
	}

	private void injectTechnologiesInElement(Element element, List<String> technologies) {
		// As I don't want to repeat myself, I use the rude way of the method handle
		try {
			Consumer<String> setter = element instanceof Container ? 
					((Container) element)::setTechnology :
						element instanceof Component ? ((Component) element)::setTechnology : null;  
			Supplier<String> getter = element instanceof Container ? 
					((Container) element)::getTechnology :
						element instanceof Component ? ((Component) element)::getTechnology : null;  
			if(setter!=null && getter!=null) {
				String existingTechnologies = getter.get();
				List<String> existingTechnologiesList = List.of(existingTechnologies==null || existingTechnologies.isBlank() ? new String[0] : existingTechnologies.split(","));
				Set<String> technologiesToInsert = new TreeSet<>();
				technologiesToInsert.addAll(technologies);
				// Little problem : this won't make Java appear first, maybe a solution with some kind
				// of virtual artifact for the JVM and sorting based upon popularity would do the trick
				technologiesToInsert.addAll(existingTechnologiesList);
				String technologiesText = technologiesToInsert.stream().collect(Collectors.joining(","));
				setter.accept(technologiesText);
			}
		} catch (SecurityException | IllegalArgumentException e) {
			// Nothing to do, because some elements of the Structurizr model don't have any technology declared
		}
	}
}

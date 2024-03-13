package org.ndx.aadarchi.inferer.maven.technologies;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.maven.model.Dependency;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenEnhancer;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Component dedicated to technology decoration.
 * Given a maven pom and an element, it will detect interesting technologies,
 * add them to the element when possible,
 * and add them to documentation when possible
 */
@Default
@ApplicationScoped
public class TechnologyDecorator {
	@Inject
	Logger logger;
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
			List<Dependency> popularDependencies = ((List<Dependency>) mavenProject.getDependencies()).stream()
					.filter(d -> mvnRepositoryArtifacts.containsKey(d.getGroupId()+"."+d.getArtifactId()))
					.collect(Collectors.toList());
			dependencies.putAll(
					popularDependencies.stream()
						.collect(Collectors.toMap(
								d -> d.getGroupId()+"."+d.getArtifactId(), 
								d -> d.getVersion()==null ? "":d.getVersion(),
								(a, b) -> a,
								() -> new LinkedHashMap<>()
								)));
			doDecorateTechnologies(popularDependencies, element);
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
	
	private <T extends Object> int compareArtifacts(T t1, T t2) {
		Entry<Dependency, MvnRepositoryArtifact> first = (Entry<Dependency, MvnRepositoryArtifact>) t1;
		Entry<Dependency, MvnRepositoryArtifact> second = (Entry<Dependency, MvnRepositoryArtifact>) t2;
		return Integer.compare(first.getValue().ranking, second.getValue().ranking);
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
	private void doDecorateTechnologies(List<Dependency> popularDependencies, Element element) {
		String[] splitted = element.getProperties()
				.getOrDefault(MavenEnhancer.FilterDpendenciesTagged.NAME, 
						MavenEnhancer.FilterDpendenciesTagged.VALUE)
				.split(",");
		List<String> filteredTags =
				Stream.of(splitted)
					.map(String::trim)
					.collect(Collectors.toList())
				;
		Map<Dependency, MvnRepositoryArtifact> dependenciesToArtifacts = popularDependencies.stream() 
			.map(d -> Map.entry(d, mvnRepositoryArtifacts.get(d.getGroupId()+"."+d.getArtifactId())))
			.filter(entry ->!isAnyTagFiltered(filteredTags, entry.getValue()))
			.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
		// We want to have that list filtered to keep, for each group id, the most popular dependency
		Map<String, Optional<Entry<Dependency, MvnRepositoryArtifact>>> dependenciesToArtifactsByGroup = dependenciesToArtifacts.entrySet().stream()
			.collect(Collectors.groupingBy(entry -> entry.getKey().getGroupId(),
					Collectors.minBy(this::compareArtifacts)));
		// Now we can map dependencies to artifacts, first put the list of artifact names into technologies
		List<String> technologies = dependenciesToArtifactsByGroup.values().stream()
				.flatMap(optional -> optional.stream())
				.map(entry -> entry.getValue())
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
	}

	/**
	 * Check if any of the artifact tags is in the filtered list
	 * @param filteredTags
	 * @param artifact
	 * @return true if any of the artifact tags appears in the filtered list 
	 */
	private boolean isAnyTagFiltered(List<String> filteredTags, MvnRepositoryArtifact artifact) {
		boolean returned = artifact.tags.stream()
				.filter(tag -> filteredTags.contains(tag))
				.findAny()
				.isPresent();
		logger.info(String.format("artifact %s has tags %s. Filtered? %s", 
				artifact.coordinates, artifact.tags, returned));
		return returned;
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

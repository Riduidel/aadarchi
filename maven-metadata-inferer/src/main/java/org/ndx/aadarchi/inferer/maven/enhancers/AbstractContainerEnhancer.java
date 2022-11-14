package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.maven.MavenEnhancer;

import com.structurizr.model.StaticStructureElement;

/**
 * Base class for structurizr model elements that may contain children
 * @author nicolas-delsaux
 *
 * @param <Enhanced>
 * @param <Contained>
 */
abstract class AbstractContainerEnhancer<Enhanced extends StaticStructureElement, Contained extends StaticStructureElement>
		extends ModelElementMavenEnhancer<Enhanced> {

	protected static final Logger logger = Logger.getLogger(AbstractContainerEnhancer.class.getName());
	/**
	 * 
	 */
	private final MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer;
	protected Optional<String> additionalProfiles = Optional.empty();

	public AbstractContainerEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, Enhanced enhanced) {
		super(mavenDetailsInfererEnhancer, enhanced);
		this.mavenDetailsInfererEnhancer = mavenDetailsInfererEnhancer;
		this.additionalProfiles = Optional.ofNullable(
				enhanced.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES));
	}

	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		loadAllSubElements(mavenProject).forEach(module -> findSubComponentFor(mavenProject, module));
	}

	@Override
	protected void endEnhanceWithMavenProject(MavenProject mavenProject) {
		loadAllSubElements(mavenProject).forEach(module -> linkToDependenciesOf(module));
	}

	/**
	 * When needed, add all dependency links between maven modules
	 * 
	 * @param module
	 */
	private void linkToDependenciesOf(MavenProject module) {
		Contained contained = getContainedElementWithName(module);
		// Now, for each dependency of the maven project, if there is an associated
		// artifact, link both of them
		((List<Dependency>) module.getDependencies()).stream()
				.map(dependency -> String.format("%s:%s", dependency.getGroupId(), dependency.getArtifactId()))
				.map(text -> text.replace("${project.groupId}", module.getGroupId())).peek(text -> {
					if (text.contains("${")) {
						logger.warning(String.format(
								"Container %s has one dependency (%s) expressed using Maven property, which we don't parse (excepted some hacks). Please remove that",
								contained, text));
					}
				}).flatMap(artifactKey -> findContainedWithArtifactKey(artifactKey))
				.forEach(found -> containedDependsUpon(contained, found, "maven:dependency"));
	}

	protected abstract void containedDependsUpon(Contained contained, Contained found, String string);

	protected Stream<Contained> findContainedWithArtifactKey(String artifactKey) {
		return getEnhancedChildren().stream()
				.filter(container -> container.getProperties()
						.containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES))
				.filter(container -> container.getProperties()
						.get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES).equals(artifactKey))
				.findFirst().stream();
	}

	protected abstract Collection<Contained> getEnhancedChildren();

	void findSubComponentFor(MavenProject mavenProject, MavenProject module) {
		String key = getContainedElementKey(module);
		Contained linked = getContainedElementWithName(key);
		if (linked == null) {
			linked = addContainedElementWithKey(module, key);
		}
		// Now we know container is loaded. Can we do anything more ?
		// Of course! We can add some useful properties
		if (!linked.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM)) {
			linked.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM,
					module.getProperties().getProperty(MavenDetailsInfererEnhancer.MAVEN_POM_URL));
		}
		linked.addProperty(ModelElementKeys.Scm.PATH, 
				module.getProperties().getProperty(ModelElementKeys.Scm.PATH));
	}

	private Contained getContainedElementWithName(MavenProject module) {
		return getContainedElementWithName(getContainedElementKey(module));
	}

	private String getContainedElementKey(MavenProject module) {
		return module.getArtifactId();
	}

	protected abstract Contained addContainedElementWithKey(MavenProject module, String key);

	/**
	 * Allow us to obtain children with the given name.
	 * This method exists because Structurizr tries to be very clean, and provide meaningful
	 * methods. As a consequence, the SoftwareSystem children can't be accessed by a method
	 * having the same name than Container children
	 * @param key
	 * @return
	 */
	protected abstract Contained getContainedElementWithName(String key);

	/**
	 * Recursively load all submodules of a project. That is, all intermediary pom
	 * modules are automagically flatMapped
	 * 
	 * @param mavenProject
	 * @return a stream of all contained maven projects
	 */
	@SuppressWarnings("unchecked")
	private Stream<MavenProject> loadAllSubElements(MavenProject mavenProject) {
		String pomPath = mavenProject.getProperties().getProperty(MavenDetailsInfererEnhancer.MAVEN_POM_URL);
		String parentScmDir = mavenProject.getProperties().getProperty(ModelElementKeys.Scm.PATH, "");
		final String pomDir = pomPath.substring(0, pomPath.lastIndexOf("/pom.xml"));
		List<String> modules = new ArrayList<>();
		modules.addAll(((List<String>) mavenProject.getModules()));
		Set<String> splittedAdditionalProfiles = additionalProfiles.stream().peek(text -> {
			if (text.contains(",") || text.contains("|")) {
				logger.log(Level.SEVERE, String.format(
						"Separator for AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES is \";\" but you used other strings that may be separators in \"%s\". Is it normal?",
						text));
			}
		}).flatMap(text -> Stream.of(text.split(";"))).collect(Collectors.toSet());
		splittedAdditionalProfiles.stream()
				.flatMap(profileName -> getProfileNamed(mavenProject, profileName).stream())
				.flatMap(profile -> profile.getModules().stream()).forEach(modules::add);
		// In order to ease debug, we also list profiles which declare modules, since
		// it led to a weird bug
		mavenProject.getModel().getProfiles().stream().filter(profile -> !profile.getModules().isEmpty())
				.filter(profile -> !splittedAdditionalProfiles.contains(profile.getId()))
				.forEach(profile -> logger.log(Level.WARNING, String.format(
						"Maven module %s profile %s declares the modules %s, which will not be handled here. Is it normal?\n"
								+ "If it is not normal, add the profile in the maven property \"AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES\"",
						mavenProject, profile.getId(), profile.getModules())));
		return modules.stream().map(module -> {
				MavenProject modulePom = mavenDetailsInfererEnhancer.readMavenProject(String.format("%s/%s/pom.xml", pomDir, module));
				modulePom.getProperties().put(ModelElementKeys.Scm.PATH, 
						parentScmDir.isBlank() ? module : parentScmDir + "/" + module);
				return modulePom;
			})
			.flatMap(module -> module.getPackaging().equals("pom") ? loadAllSubElements(module)
					: Optional.of(module).stream());
	}

	private Optional<Profile> getProfileNamed(MavenProject project, String profileName) {
		return project.getModel().getProfiles().stream().filter(profile -> profileName.equals(profile.getId()))
				.findFirst();
	}

}
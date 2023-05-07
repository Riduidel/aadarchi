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
import org.ndx.aadarchi.base.utils.descriptions.RelationshipDescriptionProvider;
import org.ndx.aadarchi.inferer.maven.MavenEnhancer;
import org.ndx.aadarchi.inferer.maven.MavenPomReader;

import com.structurizr.Workspace;
import com.structurizr.model.StaticStructureElement;

/**
 * Basic extension of enhancer allowing decoration of a Structurizr container element
 * @author Nicolas
 *
 * @param <Enhanced> parent type
 * @param <Contained> child type
 */
abstract class AbstractContainerEnhancer<Enhanced extends StaticStructureElement, Contained extends StaticStructureElement>
		extends ModelElementMavenEnhancer<Enhanced> {
	public static final Logger logger = Logger.getLogger(AbstractContainerEnhancer.class.getName());

	protected Optional<String> additionalProfiles = Optional.empty();

	private MavenPomReader mavenPomReader;

	private RelationshipDescriptionProvider descriptionProvider;

	private Workspace workspace;

	public AbstractContainerEnhancer(MavenPomReader mavenPomReader, Workspace workspace, Enhanced enhanced, RelationshipDescriptionProvider descriptionProvider) {
		super(enhanced);
		this.mavenPomReader = mavenPomReader;
		this.additionalProfiles = Optional.ofNullable(
				enhanced.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES));
		this.descriptionProvider = descriptionProvider;
	}

	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
		loadAllSubElements(mavenProject, mavenPomReader).forEach(module -> findSubComponentFor(mavenProject, module));
	}

	@Override
	protected void endEnhanceWithMavenProject(MavenProject mavenProject) {
		loadAllSubElements(mavenProject, mavenPomReader).forEach(module -> linkToDependenciesOf(module));
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
				.forEach(found -> containedDependsUpon(contained, found, 
						/*
						 * We use the dependency provider to get the relationship name, if it is provided
						 */
						descriptionProvider.provideRelationshipDescription(workspace, contained, found)
							.orElse("maven:dependency")
							));
	}

	/**
	 * Creates a relationship between both elements
	 * @param contained contained module
	 * @param found associated dependency
	 * @param string dependency usage description
	 */
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
					module.getProperties().getProperty(MavenPomReader.MAVEN_POM_URL));
		}
		linked.addProperty(ModelElementKeys.Scm.PATH, 
				module.getProperties().getProperty(ModelElementKeys.Scm.PATH));
	}

	/**
	 * Get the child element linked to the given maven module
	 * @param module
	 * @return
	 */
	private Contained getContainedElementWithName(MavenProject module) {
		return getContainedElementWithName(getContainedElementKey(module));
	}

	private String getContainedElementKey(MavenProject module) {
		return module.getArtifactId();
	}

	protected abstract Contained addContainedElementWithKey(MavenProject module, String key);

	protected abstract Contained getContainedElementWithName(String key);

	/**
	 * Recursively load all submodules of a project. That is, all intermediary pom
	 * modules are automagically flatMapped
	 * 
	 * @param mavenProject
	 * @param mavenPomReader TODO
	 * @return a stream of all contained maven projects
	 */
	@SuppressWarnings("unchecked")
	private Stream<MavenProject> loadAllSubElements(MavenProject mavenProject, MavenPomReader mavenPomReader) {
		String pomPath = mavenProject.getProperties().getProperty(MavenPomReader.MAVEN_POM_URL);
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
		return modules.stream().map(module -> readSubModulePom(mavenPomReader, parentScmDir, pomDir, module))
			.flatMap(module -> module.getPackaging().equals("pom") ? loadAllSubElements(module, mavenPomReader)
					: Optional.of(module).stream());
	}

	private MavenProject readSubModulePom(MavenPomReader mavenPomReader, String parentScmDir, final String pomDir,
			String module) {
		MavenProject modulePom = mavenPomReader.readMavenProject(String.format("%s/%s/pom.xml", pomDir, module));
		modulePom.getProperties().put(ModelElementKeys.Scm.PATH, 
				parentScmDir.isBlank() ? module : parentScmDir + "/" + module);
		return modulePom;
	}

	private Optional<Profile> getProfileNamed(MavenProject project, String profileName) {
		return project.getModel().getProfiles().stream().filter(profile -> profileName.equals(profile.getId()))
				.findFirst();
	}

}
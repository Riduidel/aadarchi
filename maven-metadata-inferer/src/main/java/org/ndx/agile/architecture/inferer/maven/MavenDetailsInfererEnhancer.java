package org.ndx.agile.architecture.inferer.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Profile;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.StaticStructureElement;

/**
 * An enhancer trying to read as much informations as possible from maven pom.
 * @author nicolas-delsaux
 *
 */
public class MavenDetailsInfererEnhancer extends ModelElementAdapter implements ModelEnhancer {
	private abstract class ModelElementMavenEnhancer<Enhanced extends StaticStructureElement> {
		
		protected final Enhanced enhanced;
		
		public ModelElementMavenEnhancer(Enhanced enhanced) {
			this.enhanced = enhanced;
		}

		public void startEnhance() {
			processModelElement(enhanced).ifPresent(this::startEnhanceWithMavenProject);
		}

		public void endEnhance() {
			processModelElement(enhanced).ifPresent(this::endEnhanceWithMavenProject);
		}
		
		protected abstract void startEnhanceWithMavenProject(MavenProject mavenProject);
		
		protected abstract void endEnhanceWithMavenProject(MavenProject mavenProject);

	}
	abstract class AbstractContainerEnhancer<Enhanced extends StaticStructureElement, Contained extends StaticStructureElement> extends ModelElementMavenEnhancer<Enhanced> {

		protected Optional<String> additionalProfiles = Optional.empty();

		public AbstractContainerEnhancer(Enhanced enhanced) {
			super(enhanced);
			this.additionalProfiles = Optional.ofNullable(enhanced.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES));
		}

		@Override
		protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
			loadAllSubElements(mavenProject)
				.forEach(module -> findSubComponentFor(mavenProject, module));
		}
		
		@Override
		protected void endEnhanceWithMavenProject(MavenProject mavenProject) {
			loadAllSubElements(mavenProject)
				.forEach(module -> linkToDependenciesOf(module));
		}


		/**
		 * When needed, add all dependency links between maven modules
		 * @param module
		 */
		private void linkToDependenciesOf(MavenProject module) {
			Contained contained = getContainedElementWithName(module);
			// Now, for each dependency of the maven project, if there is an associated artifact, link both of them
			((List<Dependency>) module.getDependencies()).stream()
				.map(this::getDependencyKey)
				.flatMap(artifactKey -> findContainedWithArtifactKey(artifactKey))
				.forEach(found -> containedDependsUpon(contained, found, "maven:dependency"));
		}
		
		protected abstract void containedDependsUpon(Contained contained, Contained found, String string);

		protected Stream<Contained> findContainedWithArtifactKey(String artifactKey) {
			return getEnhancedChildren().stream()
					.filter(container -> container.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES))
					.filter(container -> container.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES).equals(artifactKey))
					.findFirst().stream()
					;
		}
		
		protected abstract Collection<Contained> getEnhancedChildren();

		private String getDependencyKey(Dependency artifact) {
			return artifact.getGroupId()+":"+artifact.getArtifactId();
		}

		private String getArtifactKey(Artifact artifact) {
			return artifact.getGroupId()+":"+artifact.getArtifactId();
		}

		void findSubComponentFor(MavenProject mavenProject, MavenProject module) {
			String key = getContainedElementKey(module);
			Contained linked = getContainedElementWithName(key);
			if(linked==null) {
				linked = addContainedElementWithKey(module, key);
			}
			// Now we know container is loaded. Can we do anything more ?
			if(!linked.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM)) {
				linked.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM, module.getProperties().getProperty(MAVEN_POM_URL));
			}
		}

		private Contained getContainedElementWithName(MavenProject module) {
			return getContainedElementWithName(getContainedElementKey(module));
		}

		private String getContainedElementKey(MavenProject module) {
			return module.getModel().getName()==null ? module.getArtifactId() : module.getName();
		}

		protected abstract Contained addContainedElementWithKey(MavenProject module, String key);

		protected abstract Contained getContainedElementWithName(String key);

		/**
		 * Recursively load all submodules of a project.
		 * That is, all intermediary pom modules are automagically flatMapped 
		 * @param mavenProject
		 * @return a stream of all contained maven projects
		 */
		@SuppressWarnings("unchecked")
		private Stream<MavenProject> loadAllSubElements(MavenProject mavenProject) {
			String pomPath = mavenProject.getProperties().getProperty(MAVEN_POM_URL);
			final String pomDir = pomPath.substring(0, pomPath.lastIndexOf("/pom.xml"));
			List<String> modules = new ArrayList<>();
			modules.addAll(((List<String>) mavenProject.getModules()));
			Set<String> splittedAdditionalProfiles = additionalProfiles.stream()
					.peek(text -> {
						if(text.contains(",")||text.contains("|")) {
							logger.log(Level.SEVERE, 
									String.format("Separator for AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES is \";\" but you used other strings that may be separators in \"%s\". Is it normal?", text));
						}
					})
					.flatMap(text -> Stream.of(text.split(";")))
					.collect(Collectors.toCollection(() -> new TreeSet()));
			splittedAdditionalProfiles.stream()
				.flatMap(profileName -> getProfileNamed(mavenProject, profileName).stream())
				.flatMap(profile -> profile.getModules().stream())
				.forEach(modules::add);
			// In order to ease debug, we also list profiles which declare modules, since
			// it led to a weird bug
			mavenProject.getModel().getProfiles().stream()
				.filter(profile -> !profile.getModules().isEmpty())
				.filter(profile -> !splittedAdditionalProfiles.contains(profile.getId()))
				.forEach(profile -> logger.log(Level.WARNING, 
						String.format("Maven module %s profile %s declares the modules %s, which will not be handled here. Is it normal?\n"
								+ "If it is not normal, add the profile in the maven property \"AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES\"", 
								mavenProject,
								profile.getId(),
								profile.getModules()
								)
							)
						);
			return modules.stream()
				.map(module -> readMavenProject(String.format("%s/%s/pom.xml", pomDir, module)))
				.flatMap(module -> module.getPackaging().equals("pom") ?
						loadAllSubElements(module) : 
							Optional.of(module).stream())
				;
		}
		
		private Optional<Profile> getProfileNamed(MavenProject project, String profileName) {
			return project.getModel().getProfiles().stream()
				.filter(profile -> profileName.equals(profile.getId()))
				.findFirst()
				;
		}
		
	}
	
	class SoftwareSystemEnhancer extends AbstractContainerEnhancer<SoftwareSystem, Container> {
		
		public SoftwareSystemEnhancer(SoftwareSystem softwareSystem) {
			super(softwareSystem);
		}
		
		@Override
		protected Container addContainedElementWithKey(MavenProject module, String key) {
			return enhanced.addContainer(key, module.getDescription(), decorateTechnology(module));
		}

		@Override
		protected Container getContainedElementWithName(String key) {
			return enhanced.getContainerWithName(key);
		}

		@Override
		protected void containedDependsUpon(Container contained, Container found, String string) {
			contained.uses(found, string);
		}

		@Override
		protected Collection<Container> getEnhancedChildren() {
			return enhanced.getContainers();
		}

	}
	
	class ContainerEnhancer extends AbstractContainerEnhancer<Container, Component> {
		
		public ContainerEnhancer(Container container) {
			super(container);
		}

		@Override
		protected Component addContainedElementWithKey(MavenProject module, String key) {
			return enhanced.addComponent(key, module.getDescription(), decorateTechnology(module));
		}

		@Override
		protected Component getContainedElementWithName(String key) {
			return enhanced.getComponentWithName(key);
		}

		@Override
		protected void containedDependsUpon(Component contained, Component found, String string) {
			contained.uses(found, string);
		}

		@Override
		protected Collection<Component> getEnhancedChildren() {
			return enhanced.getComponents();
		}
		
	}
	
	class ComponentEnhancer extends ModelElementMavenEnhancer<Component> {

		public ComponentEnhancer(Component enhanced) {
			super(enhanced);
		}

		@Override
		protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
			enhanced.setTechnology(decorateTechnology(mavenProject));
		}
		
		@Override
		protected void endEnhanceWithMavenProject(MavenProject mavenProject) {
			
		}
	}

	private static final String MAVEN_POM_URL = MavenDetailsInfererEnhancer.class.getName()+"#MAVEN_POM_URL";
	private static final String MAVEN_MODULE_DIR = MavenDetailsInfererEnhancer.class.getName()+"#MAVEN_MODULE_DIR";

	@Inject Logger logger;
	/**
	 * The maven reader used to read all poms
	 */
	MavenXpp3Reader reader = new MavenXpp3Reader();

	@Override
	public boolean isParallel() {
		return true;
	}

	/**
	 * @return priority is set to one to have this enhancer run almost first
	 */
	@Override
	public int priority() {
		return 1;
	}
	
	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		new SoftwareSystemEnhancer(softwareSystem).startEnhance();
		return super.startVisit(softwareSystem);
	}
	
	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		new SoftwareSystemEnhancer(softwareSystem).endEnhance();
	}
	
	@Override
	public boolean startVisit(Container container) {
		new ContainerEnhancer(container).startEnhance();
		return super.startVisit(container);
	}
	
	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		new ContainerEnhancer(container).endEnhance();
	}
	
	@Override
	public boolean startVisit(Component component) {
		new ComponentEnhancer(component).startEnhance();
		return super.startVisit(component);
	}
	
	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		new ComponentEnhancer(component).endEnhance();
	}

	/**
	 * Creates the string containing details about the used technology.
	 * For that we will simply read the maven project plugins (for compilers) and dependencies (for frameworks)
	 * @param project
	 * @return a string giving details about important project infos
	 */
	private String decorateTechnology(MavenProject project) {
		return decorateTechnologyRecursively(project).stream().collect(Collectors.joining(","));
	}
	
	private Set<String> decorateTechnologyRecursively(MavenProject project) {
		Set<String> technologies = new LinkedHashSet<String>();
		technologies.add("maven");
		switch(project.getPackaging()) {
		case "ear":
			technologies.add("java");
			technologies.add("ear");
			break;
		case "war":
			technologies.add("java");
			technologies.add("war");
		case "jar":
			technologies.add("java");
			for(Dependency dependency : (List<Dependency>) project.getDependencies()) {
				switch(dependency.getGroupId()) {
				case "org.springframework":
					technologies.add("Spring");
					break;
				case "com.google.gwt":
					technologies.add("GWT");
					break;
				}
			}
			break;
		case "pom":
			break;
		default:
			logger.warning(String.format("Maven component %s uses packaging %s which we don't know. Please submit a bug to agile-architecture-documentation-system to have this particular packaging correctly handled",
					project, project.getPackaging()));
		}
		if(project.getParent()!=null) {
			technologies.addAll(decorateTechnologyRecursively(project.getParent()));
		}
		return technologies;
	}


	/**
	 * Decorate the given model element with the possible properties fetched from maven project
	 * @param element
	 * @param mavenProject
	 */
	private void decorate(Element element, MavenProject mavenProject) {
		// I use optional to avoid writing endless if(...!=null) lines.
		// It may be ugly, but I'm trying a /style/ here
		decorateCoordinates(element, mavenProject);
		decorateScmUrl(element, mavenProject);
		decorateIssueManager(element, mavenProject);
		decorateJavaSource(element, mavenProject);
		decorateMavenProperties(element, mavenProject);
		Optional.ofNullable(mavenProject.getDescription())
			.stream()
			.forEach(description -> element.setDescription(description.replaceAll("\n", " ")));
	}

	/**
	 * Extract all properties having the good prefix from maven pom and copy them into model element properties.
	 * THis copy may overwrite properties defined elsewhen
	 * @param element element to copy properties into
	 * @param mavenProject maven project to extract properties from
	 */
	private void decorateMavenProperties(Element element, MavenProject mavenProject) {
		mavenProject.getProperties().entrySet().stream()
			.map(entry -> Map.entry(entry.getKey().toString(), entry.getValue().toString()))
			.filter(entry -> entry.getKey().startsWith(ModelElementKeys.PREFIX))
			.forEach(entry -> element.addProperty(entry.getKey(), entry.getValue()));
	}

	private void decorateJavaSource(Element element, MavenProject mavenProject) {
		String mavenPomUrl = mavenProject.getProperties().get(MAVEN_MODULE_DIR).toString();
		List<String> mavenSourceRoots = Optional.ofNullable((List<String>) mavenProject.getCompileSourceRoots())
				.stream()
				.filter(list -> !list.isEmpty())
				.findAny()
				.orElse(Arrays.asList("src/main/java"));
		String sourcePaths = mavenSourceRoots.stream()
			.map(relativeFolder -> mavenPomUrl+"/"+relativeFolder)
			.map(relativeFolder -> {
				if(relativeFolder.startsWith("file")) {
					try {
						Path file = Paths.get(new URL(relativeFolder).toURI()).normalize();
						return file.toFile().toURI().toString();
					} catch(Exception e) {
						return relativeFolder;
					}
				} else {
					return relativeFolder;
				}
			})
			.filter(sourceFolder -> {
				if(sourceFolder.startsWith("file")) {
					try {
						Path file = Paths.get(new URL(sourceFolder).toURI()).normalize();
						return file.toFile().exists();
					} catch(Exception e) {
						return false;
					}
				} else {
					return true;
				}
			})
			.collect(Collectors.joining(";"))
			;
		if(!sourcePaths.isEmpty())
			element.addProperty(ModelElementKeys.JAVA_SOURCES, sourcePaths);
	}

	private void decorateCoordinates(Element element, MavenProject mavenProject) {
		if(!element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES)) {
			element.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES, 
					String.format("%s:%s", mavenProject.getGroupId(), mavenProject.getArtifactId()));
		}
	}

	void decorateIssueManager(Element element, MavenProject mavenProject) {
		Optional.ofNullable(mavenProject.getIssueManagement())
		.stream()
		.flatMap(issueManagement -> Optional.ofNullable(issueManagement.getUrl()).stream())
		.forEach(scmUrl -> element.addProperty(ModelElementKeys.ISSUE_MANAGER, scmUrl));
	}

	void decorateScmUrl(Element element, MavenProject mavenProject) {
		Optional.ofNullable(mavenProject.getScm())
			.stream()
			.flatMap(scm -> Optional.ofNullable(scm.getUrl()).stream())
			.forEach(scmUrl -> element.addProperty(ModelElementKeys.SCM_PROJECT, scmUrl));
	}

	/**
	 * Provides a maven project object for the given model element, if meaningfull to do so
	 * @param element
	 * @return an optional containing the possible model element
	 */
	protected Optional<MavenProject> processModelElement(Element element) {
		if(element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS)) {
			String className = element.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS);
			return processPomOfClass(element, className);
		} else if(element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM)) {
			String pomPath = element.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM);
			return processPomAtPath(element, pomPath);
		}
		return Optional.empty();
	}

	Optional<MavenProject> processPomOfClass(Element element, String className) {
		try {
			MavenProject mavenProject = findMavenProjectOf(Class.forName(className));
			decorate(element, mavenProject);
			return Optional.of(mavenProject);
		} catch (ClassNotFoundException e) {
			throw new MavenDetailsInfererException(
					String.format("Can't load class %s. Seems like there is a classloader incompatibility", className), 
					e);
		}
	}

	Optional<MavenProject> processPomAtPath(Element element, String pomPath) {
		MavenProject mavenProject = readMavenProject(pomPath);
		decorate(element, mavenProject);
		return Optional.of(mavenProject);
	}

	MavenProject readMavenProject(String pomPath) {
		MavenProject mavenProject = null;
		try {
			try(InputStream input = new URL(pomPath).openStream()) {
				mavenProject = new MavenProject(reader.read(input));
				mavenProject.getProperties().put(MAVEN_POM_URL, pomPath);
				mavenProject.getProperties().put(MAVEN_MODULE_DIR, pomPath.substring(0, pomPath.lastIndexOf('/')+1));
			}
		} catch(XmlPullParserException | IOException e) {
			throw new MavenDetailsInfererException(String.format("Unable to read stream from URL %s", pomPath), e);
		}
		return mavenProject;
	}

	/**
	 * Find the maven project containing the given class name
	 * @param application a class name
	 * @return the associated maven project
	 */
	public MavenProject findMavenProjectOf(Class<?> loadedClass) {
		String className = loadedClass.getName();
		String path = loadedClass.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if(file.isDirectory()) {
			return findMavenProjectOfClassFromDirectory(loadedClass, className, file);
		} else {
			return findMavenProjectOfClassFromJar(loadedClass, className, file);
		}
	}

	private MavenProject findMavenProjectOfClassFromDirectory(Class<?> loadedClass, String className,
			File directory) {
		File pom = new File(directory, "pom.xml");
		if(pom.exists()) {
			return readMavenProject(pom.toURI().toString());
		} else if(!directory.getParentFile().equals(directory)){
			return findMavenProjectOfClassFromDirectory(loadedClass, className, directory.getParentFile());
		} else {
			throw new MavenDetailsInfererException(
					String.format("Seems like class %s is not loaded from a Maven project, as we can't find any pom.xml file", className));
		}
	}

	private MavenProject findMavenProjectOfClassFromJar(Class<?> loadedClass, String className, File jarFile) {
		// OK, we assume path to be a JAR file, so let's explore that jar ...
		try {
			try(FileSystem fs = FileSystems.newFileSystem(jarFile.toPath(), loadedClass.getClassLoader())) {
				Path mavenPomDir = fs.getPath("META-INF", "maven");
				return Files.find(mavenPomDir, Integer.MAX_VALUE, 
						(path, attributes) -> path.getFileName().toString().equals("pom.xml")
						)
					.map(path -> path.toUri().toString())
					.map(this::readMavenProject)
					.findFirst()
					.orElseThrow(() -> new MavenDetailsInfererException(
							String.format("There doesn't seems to be a maven pom in JAR %s", jarFile.getAbsolutePath())));
			}
		} catch(IOException e) {
			throw new MavenDetailsInfererException(String.format("Unable to open %s as JAR", jarFile.getAbsolutePath()), e);
		}
	}
}
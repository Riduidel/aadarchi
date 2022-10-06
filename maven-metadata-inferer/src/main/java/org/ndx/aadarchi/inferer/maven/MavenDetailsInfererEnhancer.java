package org.ndx.aadarchi.inferer.maven;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Scm;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ndx.aadarchi.base.ModelEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.FileResolver;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.StaticStructureElement;

/**
 * An enhancer trying to read as much informations as possible from maven pom.
 * 
 * @author nicolas-delsaux
 *
 */
@com.structurizr.annotation.Component
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

	abstract class AbstractContainerEnhancer<Enhanced extends StaticStructureElement, Contained extends StaticStructureElement>
			extends ModelElementMavenEnhancer<Enhanced> {

		protected Optional<String> additionalProfiles = Optional.empty();

		public AbstractContainerEnhancer(Enhanced enhanced) {
			super(enhanced);
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
									"Container %s has one dependency expressed using Maven property, which we don't parse (excepted some hacks). Please remove that",
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
						module.getProperties().getProperty(MAVEN_POM_URL));
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
			String pomPath = mavenProject.getProperties().getProperty(MAVEN_POM_URL);
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
					MavenProject modulePom = readMavenProject(String.format("%s/%s/pom.xml", pomDir, module));
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
		protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
			enhanced.setTechnology(decorateTechnology(mavenProject));
			super.startEnhanceWithMavenProject(mavenProject);
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

	private static final String MAVEN_POM_URL = MavenDetailsInfererEnhancer.class.getName() + "#MAVEN_POM_URL";
	private static final String MAVEN_MODULE_DIR = MavenDetailsInfererEnhancer.class.getName() + "#MAVEN_MODULE_DIR";

	@Inject
	Logger logger;
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) File basePath;

	@Inject FileContentCache cache;
	@Inject FileResolver fileResolver;
	@Inject Instance<SCMHandler> scmHandler;
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

	private void decorateRecursively(MavenProject project, BiFunction<MavenProject, List<MavenProject>, Boolean> consumer) {
		decorateRecursively(project, new LinkedList<MavenProject>(), consumer);
	}
	
	private void decorateRecursively(MavenProject project, List<MavenProject> children, BiFunction<MavenProject, List<MavenProject>, Boolean> consumer) {
		if(consumer.apply(project, children)) {
			if(project.getParent()!=null) {
				decorateRecursively(project.getParent(), consumer);
			}
		}
	}

	/**
	 * Creates the string containing details about the used technology. For that we
	 * will simply read the maven project plugins (for compilers) and dependencies
	 * (for frameworks)
	 * 
	 * @param project
	 * @return a string giving details about important project infos
	 */
	private String decorateTechnology(MavenProject project) {
		Set<String> technologies = new TreeSet<String>();
		decorateRecursively(project, (p, l) -> { 
			technologies.addAll(doDecorateTechnology(p));
			// We should explore all parent poms
			return true;
		});
		return technologies.stream().collect(Collectors.joining(","));
	}
	
	private static String technologyWithVersionFromProperty(MavenProject mavenProject, String technology, String... propertyNames) {
		return technology + Stream.of(propertyNames)
				.filter(p -> mavenProject.getProperties().containsKey(p))
				.map(p -> mavenProject.getProperties().get(p))
				.map(text -> " "+text)
				.findFirst()
				.orElse("")
				;
	}

	private Set<String> doDecorateTechnology(MavenProject project) {
		Set<String> technologies = new LinkedHashSet<String>();
		switch (project.getPackaging()) {
		case "ear":
			technologies.add("Java");
			technologies.add("ear");
			break;
		case "war":
			technologies.add("Java");
			technologies.add("war");
		case "jar":
			// If there is a java version property, use it to detect Java version
			technologies.add(technologyWithVersionFromProperty(project, "Java", "java.version", "maven.compiler.target"));
			break;
		case "pom":
			break;
		default:
			logger.warning(String.format(
					"Maven component %s uses packaging %s which we don't know. Please submit a bug to aadarchi-documentation-system to have this particular packaging correctly handled",
					project, project.getPackaging()));
		}
		for (Dependency dependency : (List<Dependency>) project.getDependencies()) {
			switch (dependency.getGroupId()) {
			case "org.apache.maven":
				if("maven-plugin-api".equals(dependency.getArtifactId())) {
					technologies.add("maven-plugin");
				}
				break;
			case "io.quarkus":
				technologies.add(technologyWithVersionFromProperty(project, "Quarkus", "quarkus.platform.version"));
				break;
			case "org.springframework":
				technologies.add("Spring");
				break;
			case "org.springframework.boot":
				technologies.add("Spring Boot");
				break;
			case "com.google.gwt":
				technologies.add("GWT");
				break;
			case "javax.enterprise":
				if("cdi-api".equals(dependency.getArtifactId())) {
					technologies.add("CDI");
				}
				break;
			}
		}
		return technologies;
	}

	/**
	 * Decorate the given model element with the possible properties fetched from
	 * maven project
	 * 
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
		decorateJavaPackage(element, mavenProject);
		decorateMavenProperties(element, mavenProject);
		Optional.ofNullable(mavenProject.getDescription()).stream()
				.forEach(description -> element.setDescription(description.replaceAll("\n", " ")));
	}

	/**
	 * Extract all properties having the good prefix from maven pom and copy them
	 * into model element properties. THis copy may overwrite properties defined
	 * elsewhen
	 * 
	 * @param element      element to copy properties into
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
				.stream().filter(list -> !list.isEmpty()).findAny().orElse(Arrays.asList("src/main/java"));
		String sourcePaths = mavenSourceRoots.stream().map(relativeFolder -> mavenPomUrl + "/" + relativeFolder)
				.filter(relativeFolder -> relativeFolder.startsWith("file"))
				.map(ThrowingFunction.unchecked(relativeFolder -> Paths.get(new URL(relativeFolder).toURI()).normalize()))
				.filter(relativePath -> relativePath.toFile().exists())
				.map(relativeFolder -> relativeFolder.toString())
				.collect(Collectors.joining(";"));
		if (!sourcePaths.isEmpty())
			element.addProperty(ModelElementKeys.JAVA_SOURCES, sourcePaths);
	}

	private void decorateJavaPackage(Element element, MavenProject mavenProject) {
		if(!element.getProperties().containsKey(ModelElementKeys.JAVA_PACKAGES)) {
			if(element.getProperties().containsKey(ModelElementKeys.JAVA_SOURCES)) {
				String paths = element.getProperties().get(ModelElementKeys.JAVA_SOURCES);
				String packages = Arrays.asList(paths.split(";")).stream()
					.map(fileResolver::fileAsUrltoPath)
					.filter(path -> path.toFile().exists())
					.map(this::findPackagesInPath)
					.flatMap(Collection::stream)
					.collect(Collectors.joining(";"));
				if(!packages.isEmpty())
					element.addProperty(ModelElementKeys.JAVA_PACKAGES, packages);
			}
		}
	}
	
	/**
	 * Explore the given path looking for the first folder containing more than one subfolder
	 * @param path
	 * @return a collection of java packages string corresponding to the given path
	 */
	private Collection<String> findPackagesInPath(Path path) {
		return findPackagesInPath(path.toFile()).stream()
				.map(packagePath -> path.relativize(packagePath))
				.map(Path::toString)
				.map(packageString -> packageString.replace(File.separatorChar, '.'))
				.collect(Collectors.toList());
	}

	private Collection<Path> findPackagesInPath(File current) {
		File[] childrenArray = current.listFiles();
		if(childrenArray==null)
			// There are no children here, so consider this file as terminal
			return Arrays.asList(current.toPath());
		List<File> childrenList = Arrays.asList(childrenArray);
		// If all elements are folders, then we can consider they're all packages and continue exploration
		// Otherwise, this folder is a terminal package and we can return it "safely"
		if(childrenList.stream().allMatch(File::isDirectory)) {
			return childrenList.stream()
					.map(file -> findPackagesInPath(file))
					.flatMap(descendantList -> descendantList.stream())
					.collect(Collectors.toList());
		} else {
			// There is at least one non-file in package (or list is empty)
			// So this folder is terminal
			return Arrays.asList(current.toPath());
		}
	}

	private void decorateCoordinates(Element element, MavenProject mavenProject) {
		if (!element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES)) {
			element.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					String.format("%s:%s", mavenProject.getGroupId(), mavenProject.getArtifactId()));
		}
	}

	void decorateIssueManager(Element element, MavenProject mavenProject) {
		decorateRecursively(mavenProject, (project, children) -> {
			if(project.getIssueManagement()!=null) {
				IssueManagement issues = project.getIssueManagement();
				if(issues.getUrl()!=null) {
					String url = issues.getUrl();
					element.addProperty(ModelElementKeys.ISSUE_MANAGER, url);
					return false;
				}
			}
			return true;
		});
	}

	void decorateScmUrl(Element element, MavenProject mavenProject) {
		decorateRecursively(mavenProject, (project, children) -> {
			if(project.getScm()!=null) {
				Scm scm = project.getScm();
				if(scm.getUrl()!=null) {
					String url = scm.getUrl();
					// We're not in our project, but in some parent. To go back to our project, we must add to that url
					// all the children paths
					url = url + children.stream().map(p -> p.getArtifactId()).collect(Collectors.joining("/"));
					element.addProperty(org.ndx.aadarchi.base.enhancers.ModelElementKeys.Scm.PROJECT, url);
					return false;
				}
			}
			// We need to go deeper
			return true;
		});
	}

	/**
	 * Provides a maven project object for the given model element, if meaningfull
	 * to do so
	 * 
	 * @param element
	 * @return an optional containing the possible model element
	 */
	protected Optional<MavenProject> processModelElement(Element element) {
		Optional<MavenProject> returned = Optional.empty();
		if (element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS)) {
			String className = element.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_CLASS);
			returned = processPomOfClass(element, className);
		} else if (element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM)) {
			String pomPath = element.getProperties().get(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_POM);
			returned = processPomAtPath(element, pomPath);
		} else if (element.getProperties().containsKey(ModelElementKeys.Scm.PROJECT)) {
			// If there is some kind of SCM path, and a configured SCM provider,
			// let's check if we can find some pom.xml
			returned = processPomAtSCM(element);
		}
		returned.ifPresent(mavenProject -> decorate(element, mavenProject));
		return returned;
	}

	private Optional<MavenProject> processPomAtSCM(Element element) {
		var project= element.getProperties().get(ModelElementKeys.Scm.PROJECT);
		for(SCMHandler handler : scmHandler) {
			try {
				Collection<SCMFile> pomSCMFile = handler.find(project, "/", file -> "pom.xml".equals(file.name()));
				for(SCMFile pom : pomSCMFile) {
					URL url = new URL(pom.url());
					return Optional.ofNullable(readMavenProject(pom.url(), url, 
							cache.openStreamFor(pom)));
				}
			} catch (IOException | XmlPullParserException e) {
				logger.log(Level.FINER, String.format("There is no pom.xml in %s, maybe it's normal", project), e);
			}
		}
		return Optional.empty();
	}

	Optional<MavenProject> processPomOfClass(Element element, String className) {
		try {
			MavenProject mavenProject = findMavenProjectOf(Class.forName(className));
			return Optional.of(mavenProject);
		} catch (ClassNotFoundException e) {
			throw new MavenDetailsInfererException(
					String.format("Can't load class %s. Seems like there is a classloader incompatibility", className),
					e);
		}
	}

	Optional<MavenProject> processPomAtPath(Element element, String pomPath) {
		MavenProject mavenProject = readMavenProject(pomPath);
		return Optional.of(mavenProject);
	}

	/**
	 * Tries to resolve path to something that can be an url.
	 * In other words, when user enters a relative file path, tries to resolve that path to an existing
	 * file then covnert that file to an url.
	 * Otherwise, if input can be successfully parsed to an url 
	 * @see #readMavenProject(String, URL)
	 */
	MavenProject readMavenProject(String pomPath) {
		try {
			URL url = new URL(pomPath);
			return readMavenProject(pomPath, url);
		} catch(MalformedURLException e) {
			// Maybe it's a file, relative to this basePath
			File potential = new File(basePath, pomPath);
			if(potential.exists()) {
				try {
					return readMavenProject(pomPath, potential.toURL());
				} catch (MalformedURLException e1) {
					// No need to catch that one, because the parent catch clause will handle it
				}
			}
			throw new MavenDetailsInfererException(String.format("Unable to read URL %s", pomPath), e);
		}
	}

	private MavenProject readMavenProject(String pomPath, URL url) {
		try (InputStream input = SCMHandler.openStream(scmHandler, url)) {
			return readMavenProject(pomPath, url, input);
		} catch (XmlPullParserException | IOException e) {
			throw new MavenDetailsInfererException(String.format("Unable to read stream from URL %s", pomPath), e);
		}
	}

	private MavenProject readMavenProject(String pomPath, URL url, InputStream input)
			throws IOException, XmlPullParserException, MalformedURLException {
		MavenProject mavenProject = new MavenProject(reader.read(input));
		if(url.toString().startsWith("file:")) {
			File file = FileUtils.toFile(url);
			file = file.getCanonicalFile();
			url = file.toURI().toURL();
			File parentDir = file.getParentFile().getParentFile();
			// If returned pom declares a parent
			if(mavenProject.getModel().getParent()!=null) {
				// And we have a pom in parent directory
				File parentPom = new File(parentDir, "pom.xml");
				if(parentPom.exists()) {
					// Load that pom
					MavenProject parent = readMavenProject(parentPom.toURI().toString());
					// And if artifactId matches, use it!
					if(parent.getArtifactId().equals(mavenProject.getModel().getParent().getArtifactId())) {
						mavenProject.setParent(parent);
					}
					// Obviously, we should use standard maven loading mechanism, but it won't be available until we become a maven plugin
				}
			}
		}
		mavenProject.getProperties().put(MAVEN_POM_URL, pomPath);
		// We do not use the parent file method, because the pom may be read from elsewhere
		mavenProject.getProperties().put(MAVEN_MODULE_DIR, pomPath.substring(0, pomPath.lastIndexOf('/') + 1));
		return mavenProject;
	}

	/**
	 * Find the maven project containing the given class name
	 * 
	 * @param loadedClass a class for which we want a maven project
	 * @return the associated maven project
	 */
	public MavenProject findMavenProjectOf(Class<?> loadedClass) {
		String className = loadedClass.getName();
		String path = loadedClass.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);
		if (file.isDirectory()) {
			return findMavenProjectOfClassFromDirectory(loadedClass, className, file);
		} else {
			return findMavenProjectOfClassFromJar(loadedClass, className, file);
		}
	}

	private MavenProject findMavenProjectOfClassFromDirectory(Class<?> loadedClass, String className, File directory) {
		File pom = new File(directory, "pom.xml");
		File parentDir = directory.getParentFile();
		if (pom.exists()) {
			return readMavenProject(pom.toURI().toString());
		} else if (!parentDir.equals(directory)) {
			return findMavenProjectOfClassFromDirectory(loadedClass, className, parentDir);
		} else {
			throw new MavenDetailsInfererException(String.format(
					"Seems like class %s is not loaded from a Maven project, as we can't find any pom.xml file",
					className));
		}
	}

	private MavenProject findMavenProjectOfClassFromJar(Class<?> loadedClass, String className, File jarFile) {
		// OK, we assume path to be a JAR file, so let's explore that jar ...
		try {
			try (FileSystem fs = FileSystems.newFileSystem(jarFile.toPath(), loadedClass.getClassLoader())) {
				Path mavenPomDir = fs.getPath("META-INF", "maven");
				return Files
						.find(mavenPomDir, Integer.MAX_VALUE,
								(path, attributes) -> path.getFileName().toString().equals("pom.xml"))
						.map(path -> path.toUri().toString()).map(this::readMavenProject).findFirst()
						.orElseThrow(() -> new MavenDetailsInfererException(String
								.format("There doesn't seems to be a maven pom in JAR %s", jarFile.getAbsolutePath())));
			}
		} catch (IOException e) {
			throw new MavenDetailsInfererException(String.format("Unable to open %s as JAR", jarFile.getAbsolutePath()),
					e);
		}
	}
}
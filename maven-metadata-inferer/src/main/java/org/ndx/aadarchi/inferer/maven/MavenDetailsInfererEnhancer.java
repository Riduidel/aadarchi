package org.ndx.aadarchi.inferer.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
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
import org.ndx.aadarchi.inferer.maven.enhancers.ComponentEnhancer;
import org.ndx.aadarchi.inferer.maven.enhancers.ContainerEnhancer;
import org.ndx.aadarchi.inferer.maven.enhancers.SoftwareSystemEnhancer;

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
	public static final String MAVEN_POM_URL = MavenDetailsInfererEnhancer.class.getName() + "#MAVEN_POM_URL";
	public static final String MAVEN_MODULE_DIR = MavenDetailsInfererEnhancer.class.getName() + "#MAVEN_MODULE_DIR";

	@Inject
	Logger logger;
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) File basePath;

	@Inject FileContentCache cache;
	@Inject FileResolver fileResolver;
	@Inject Instance<SCMHandler> scmHandler;
	
	@Inject MavenPomAnalyzer analyzer;
	@Inject MavenPomReader pomReader;


	Stack<Set<? extends StaticStructureElement>> stack = new Stack<>();

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
		stack.push(softwareSystem.getContainers());
		int containersNumber = stack.get(0).size();
		logger.fine(String.format("At the start, there are these containers : %s ", softwareSystem.getContainers()));
		logger.info(String.format("At the start, there are %d containers", containersNumber));
		new SoftwareSystemEnhancer(this, softwareSystem).startEnhance();
		return super.startVisit(softwareSystem);
	}

	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		Set<? extends StaticStructureElement> initial = stack.pop();
		int initialContainersNumber = initial.size();
		int actualContainersNumber = softwareSystem.getContainers().size();
		int newContainersNumber = actualContainersNumber - initialContainersNumber;
		Set<? extends StaticStructureElement> newContainers = softwareSystem.getContainers().stream().filter(element -> !initial.contains(element))
				.collect(Collectors.toSet());
		if( actualContainersNumber > initialContainersNumber) {
			logger.info(String.format("At the end, there are %d containers, including %d new containers", actualContainersNumber, newContainersNumber));
			logger.fine(String.format("At the end, there are these new containers : %s", newContainers ));
		} else
			logger.info("At the end, there are no new containers.");
		new SoftwareSystemEnhancer(this, softwareSystem).endEnhance();
	}

	@Override
	public boolean startVisit(Container container) {
		new ContainerEnhancer(this, container).startEnhance();
		return super.startVisit(container);
	}

	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		new ContainerEnhancer(this, container).endEnhance();
	}

	@Override
	public boolean startVisit(Component component) {
		new ComponentEnhancer(this, component).startEnhance();
		return super.startVisit(component);
	}

	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		new ComponentEnhancer(this, component).endEnhance();
	}

	/**
	 * Provides a maven project object for the given model element, if meaningfull
	 * to do so
	 * 
	 * @param element a Structurizr model element
	 * @return an optional containing the possible model element
	 */
	public Optional<MavenProject> processModelElement(Element element) {
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
		returned.ifPresent(mavenProject -> analyzer.decorate(element, mavenProject));
		return returned;
	}

	private Optional<MavenProject> processPomAtSCM(Element element) {
		var project= element.getProperties().get(ModelElementKeys.Scm.PROJECT);
		for(SCMHandler handler : scmHandler) {
			try {
				Collection<SCMFile> pomSCMFile = handler.find(project, "/", file -> "pom.xml".equals(file.name()));
				for(SCMFile pom : pomSCMFile) {
					URL url = new URL(pom.url());
					return Optional.ofNullable(pomReader.readMavenProject(pom.url(), url, 
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
		MavenProject mavenProject = pomReader.readMavenProject(pomPath);
		return Optional.of(mavenProject);
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
			return pomReader.readMavenProject(pom.toURI().toString());
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
						.map(path -> path.toUri().toString()).map(pomReader::readMavenProject).findFirst()
						.orElseThrow(() -> new MavenDetailsInfererException(String
								.format("There doesn't seems to be a maven pom in JAR %s", jarFile.getAbsolutePath())));
			}
		} catch (IOException e) {
			throw new MavenDetailsInfererException(String.format("Unable to open %s as JAR", jarFile.getAbsolutePath()),
					e);
		}
	}
	

	public String decorateTechnology(MavenProject project) {
		return analyzer.decorateTechnology(project);
	}

	public MavenProject readMavenProject(String pomPath) {
		return pomReader.readMavenProject(pomPath);
	}
	
}
package org.ndx.agile.architecture.inferer.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.logging.Logger;

import javax.inject.Inject;

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

/**
 * An enhancer trying to read as much informations as possible from maven pom.
 * @author nicolas-delsaux
 *
 */
public class MavenDetailsInfererEnhancer extends ModelElementAdapter implements ModelEnhancer {
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
	public void endVisit(Container container, OutputBuilder builder) {
		processModelElement(container, builder).ifPresent(project -> container.setTechnology(decorateTechnology(project)));
	}
	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		processModelElement(component, builder).ifPresent(project -> component.setTechnology(decorateTechnology(project)));
	}

	/**
	 * Creates the string containing details about the used technology.
	 * For that we will simply read the maven project plugins (for compilers) and dependencies (for frameworks)
	 * @param project
	 * @return a string giving details about important project infos
	 */
	private String decorateTechnology(MavenProject project) {
		return "maven";
	}

	protected Optional<MavenProject> processModelElement(Element element, OutputBuilder builder) {
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
			}
		} catch(XmlPullParserException | IOException e) {
			throw new MavenDetailsInfererException(String.format("Unable to read stream from URL %s", pomPath), e);
		}
		return mavenProject;
	}

	/**
	 * Decorate the given model element with the possible properties fetched from maven project
	 * @param element
	 * @param mavenProject
	 */
	private void decorate(Element element, MavenProject mavenProject) {
		// I use optional to avoid writing endless if(...!=null) lines.
		// It may be ugly, but I'm trying a /style/ here
		decorateScmUrl(element, mavenProject);
		decorateIssueManager(element, mavenProject);
		Optional.ofNullable(mavenProject.getDescription())
		.stream()
		.forEach(description -> element.setDescription(description));
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

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		processModelElement(element, builder);
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
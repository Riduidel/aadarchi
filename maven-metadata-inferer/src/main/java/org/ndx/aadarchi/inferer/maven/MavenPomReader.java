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
import java.util.Collection;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.FileContentCache;

import com.structurizr.annotation.Component;
import com.structurizr.model.Element;

@Default
@ApplicationScoped
public class MavenPomReader {
	@Inject Logger logger;
	@Inject Instance<SCMHandler> scmHandler;
	@Inject Instance<MavenPomDecorator> mavenPomDecorator;
	@Inject FileContentCache cache;
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) File basePath;
	/**
	 * The maven reader used to read all poms
	 */
	MavenXpp3Reader reader = new MavenXpp3Reader();
	public static final String MAVEN_MODULE_DIR = MavenDetailsInfererEnhancer.class.getName() + "#MAVEN_MODULE_DIR";
	public static final String MAVEN_POM_URL = MavenDetailsInfererEnhancer.class.getName() + "#MAVEN_POM_URL";
	
	/**
	 * Provides a maven project object for the given model element, if meaningfull
	 * to do so
	 * 
	 * @param element
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
		} else if (element.getProperties().containsKey(ModelElementKeys.ConfigProperties.BasePath.NAME)) {
			File basePath = new File(element.getProperties().get(ModelElementKeys.ConfigProperties.BasePath.NAME));
			File potentialPom = new File(basePath, "pom.xml");
			if(potentialPom.exists()) {
				try {
					returned = processPomAtPath(element, potentialPom.toURI().toURL().toString());
				} catch (MalformedURLException e) {
					logger.log(Level.SEVERE, 
							String.format("Unable to convert file %s to url", potentialPom.getAbsolutePath()), 
							e);
				}
			}
		}
		returned.ifPresent(mavenProject -> mavenPomDecorator.get().decorate(element, mavenProject));
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
	public MavenProject readMavenProject(String pomPath) {
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
		mavenProject.getProperties().put(MavenPomReader.MAVEN_POM_URL, pomPath);
		// We do not use the parent file method, because the pom may be read from elsewhere
		mavenProject.getProperties().put(MavenPomReader.MAVEN_MODULE_DIR, pomPath.substring(0, pomPath.lastIndexOf('/') + 1));
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

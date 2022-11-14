package org.ndx.aadarchi.inferer.maven;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;

public class MavenPomReader {
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) File basePath;
	@Inject Logger logger;
	@Inject MavenPomAnalyzer analyzer;
	@Inject Instance<SCMHandler> scmHandler;
	/**
	 * The maven reader used to read all poms
	 */
	MavenXpp3Reader reader = new MavenXpp3Reader();
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

	MavenProject readMavenProject(String pomPath, URL url, InputStream input)
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
		mavenProject.getProperties().put(MavenDetailsInfererEnhancer.MAVEN_POM_URL, pomPath);
		// We do not use the parent file method, because the pom may be read from elsewhere
		mavenProject.getProperties().put(MavenDetailsInfererEnhancer.MAVEN_MODULE_DIR, pomPath.substring(0, pomPath.lastIndexOf('/') + 1));
		return mavenProject;
	}


}

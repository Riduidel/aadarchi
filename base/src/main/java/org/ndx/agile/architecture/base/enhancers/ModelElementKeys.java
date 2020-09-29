package org.ndx.agile.architecture.base.enhancers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.model.Element;

/**
 * This interface only contains a list of strings, each one providing some additionnal content 
 * for elements. Defining one of them could trigger additionnal enhancements, provided the 
 * associated Java component is in CLASSPATH
 * @author nicolas-delsaux
 *
 */
public interface ModelElementKeys {

	/**
	 * Should contain the full SCM url of project, including the domain!
	 * This property has no default. 
	 * Not setting this property implies that the associated element has no associated SCM project. 
	 */
	String SCM_PROJECT = "agile.architecture.scm.project";
	/**
	 * Path of project in SCM. 
	 */
	String SCM_PATH = "agile.architecture.scm.path";
	/**
	 * Readme path in project. Defaults to "readme.md". This should be a subpath of scm path.
	 */
	String SCM_README = "agile.architecture.scm.readme.path";
	/**
	 * Issue manager key should contain the url of the, well, issue manager system used by an element
	 */
	String ISSUE_MANAGER = "agile.architecture.issue.manager";
	/**
	 * String-stored List (separated by ;) of java packages contained by a given model element.
	 * Why a List ? Because some Spring applications tend to have very bad separations due to
	 * the MVC thingie : the controllers may be in one container, and the services/repositories
	 * may be in another.
	 */
	String JAVA_PACKAGES = "agile.architecture.java.packages";

	/**
	 * Path to source folders (separated by ";", which doesn't seems to be valid in an url). This does not include test sources.
	 * It must be an url, but can be a file url.
	 */
	String JAVA_SOURCES = "agile.architecture.java.source";

	static List<File> getJavaSourcesFor(Element container) {
		return Stream.of(container.getProperties().get(ModelElementKeys.JAVA_SOURCES).split(";"))
			.map(ThrowingFunction.unchecked(ModelElementKeys::fileAsUrltoFile))
			.filter(file -> file.exists())
			.collect(Collectors.toList());
	}
	
	static File fileAsUrltoFile(String fileUrl) throws MalformedURLException, URISyntaxException {
		return Paths.get(new URL(fileUrl).toURI()).toFile();
	}

	static Path fileAsUrltoPath(String fileUrl) throws MalformedURLException, URISyntaxException {
		return Paths.get(new URL(fileUrl).toURI());
	}
}

package org.ndx.aadarchi.inferer.javascript;

import com.structurizr.model.*;
import org.ndx.aadarchi.base.ModelEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.inferer.javascript.npm.JavascriptProject;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.ndx.aadarchi.base.ModelEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;

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
public class JavascriptDetailsInfererEnhancer extends ModelElementAdapter implements ModelEnhancer {

	public static final String NPM_PACKAGE_URL = JavascriptDetailsInfererEnhancer.class.getName() + "#NPM_PACKAGE_URL";
	public static final String NPM_MODULE_DIR = JavascriptDetailsInfererEnhancer.class.getName() + "#NPM_PACKAGE_DIR";
	@Inject
	JavascriptPackageAnalyzer javascriptPackageAnalyzer;
	@Inject
	Logger logger;
	@Inject
	JavascriptPackageReader javascriptPackageReader;
	@Inject FileSystemManager fsManager;
	Stack<Set<? extends StaticStructureElement>> stack = new Stack<>();

	@Inject
	Instance<SCMHandler> scmHandler;

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public int priority() {
		return 0;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		processModelElement(element);
	}

	/**
	 * Provides an npm project object for the given model element, if meaningfull to do so
	 *
	 * @param element a Structurizr model element
	 * @return an optional containing the possible model element
	 */
	public Optional<JavascriptProject> processModelElement(Element element) {
		String technologies = "";
		Optional<JavascriptProject> toReturn = Optional.empty();

		if (element.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE)) {
			String packagePath = element.getProperties().get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE);
			toReturn = processPackageFromPath(element, packagePath);
		} else if (element.getProperties().containsKey(ModelElementKeys.Scm.PROJECT)) {
			// If there is some kind of SCM path, and a configured SCM provider,
			// let's check if we can find some package.json
			toReturn = processPackageAtSCM(element);
		}
        /*toReturn.ifPresent(javascriptProject -> javascriptPackageAnalyzer.decorate(element, javascriptProject));
		return returned;*/

		 technologies = toReturn.stream()
				.map(javascriptProject -> javascriptPackageAnalyzer.decorateTechnology(javascriptProject))
				 .collect(Collectors.joining());
		 System.out.println(technologies);
		 return toReturn;
	}

	/**
	 * When we only have scm information, we check if there is a package.json and, if so, we read it using {@link #javascriptPackageReader}
	 * @param element the model element
	 * @return an optional containing the infos we obtained if they exist
	 */
	private Optional<JavascriptProject> processPackageAtSCM(Element element) {
		var project = element.getProperties().get(ModelElementKeys.Scm.PROJECT);

		for(SCMHandler handler : scmHandler) {
			if(handler.canHandle(project)) {
				try {
					FileObject projectRoot = handler.getProjectRoot(project);
					FileObject packageJson = projectRoot.getChild("package.json");
					return Optional.ofNullable(javascriptPackageReader.readNpmProject(packageJson));
				} catch (IOException e) {
					logger.log(Level.FINER, String.format("There is no package.json in %s, maybe it's normal", project), e);
				}
			}
		}
		return Optional.empty();
	}

	Optional<JavascriptProject> processPackageFromPath(Element element, String packagePath) {
		try {
			return Optional.of(javascriptPackageReader.readNpmProject(fsManager.resolveFile(packagePath)));
		} catch (FileSystemException e) {
			logger.log(Level.WARNING, String.format("Unable to read package at path %s", packagePath), e);
			return Optional.empty();
		}
	}

	/**
	 * Find the javascript project containing the given class name
	 *
	 * @param loadedClass a class for which we want a maven project
	 * @return the associated maven project
	 */
	public JavascriptProject findJavascriptProjectOf(Class<?> loadedClass) {
		String className = loadedClass.getName();
		String path = loadedClass.getProtectionDomain().getCodeSource().getLocation().getPath();
		File file = new File(path);

		if (file.isDirectory()) {
			return findJavascriptProjectOfClassFromDirectory(loadedClass, className, file);
		} else {
			throw new JavascriptDetailsInfererException(String.format("Couldn't find javascript project in %s", className));
		}
	}

	private JavascriptProject findJavascriptProjectOfClassFromDirectory(Class<?> loadedClass, String className, File directory) {

		File packageJson = new File(directory, "package.json");
		File parentDir = directory.getParentFile();
		if (packageJson.exists()) {
			//return javascriptPackageReader.readNpmProject(parentDir);
		} else if (!parentDir.equals(directory)) {
			return findJavascriptProjectOfClassFromDirectory(loadedClass, className, parentDir);
		} else {
			throw new JavascriptDetailsInfererException(String.format(
					"Seems like class %s is not loaded from a Javascript project, as we can't find any package.json file",
					className));
		}
		return null;
	}
}
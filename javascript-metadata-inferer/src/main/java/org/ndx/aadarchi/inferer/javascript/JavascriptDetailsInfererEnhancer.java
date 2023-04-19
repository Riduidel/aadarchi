package org.ndx.aadarchi.inferer.javascript;

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
import org.ndx.aadarchi.inferer.javascript.enhancers.ComponentEnhancer;
import org.ndx.aadarchi.inferer.javascript.enhancers.ContainerEnhancer;
import org.ndx.aadarchi.inferer.javascript.enhancers.SoftwareSystemEnhancer;

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
	public boolean startVisit(SoftwareSystem softwareSystem) {
		stack.push(softwareSystem.getContainers());
		int containersNumber = stack.get(0).size();
		logger.fine(String.format("At the start, there are these containers : %s ", softwareSystem.getContainers()));
		logger.info(String.format("At the start, there are %d containers", containersNumber));
		new SoftwareSystemEnhancer(this, softwareSystem).startEnhance();
		return super.startVisit(softwareSystem);
	}

	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder outputBuilder) {
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
	public void endVisit(Container container, OutputBuilder outputBuilder) {
		new ContainerEnhancer(this, container).endEnhance();
	}

	@Override
	public boolean startVisit(Component component) {
		new ComponentEnhancer(this, component).startEnhance();
		return super.startVisit(component);
	}

	@Override
	public void endVisit(Component component, OutputBuilder outputBuilder) {
		new ComponentEnhancer(this, component).endEnhance();
	}

	/**
	 * Provides an npm project object for the given model element, if meaningfull to do so
	 *
	 * @param element a Structurizr model element
	 * @return an optional containing the possible model element
	 */
	public Optional<JavascriptProject> processModelElement(Element element) {
		Optional<JavascriptProject> returned = Optional.empty();
		if(element.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_CLASS)) {
			String className = element.getProperties().get((JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_CLASS));
			returned = processPackageFromClass(element, className);
		} else if (element.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE)) {
			String packagePath = element.getProperties().get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE);
			returned = processPackageFromPath(element, packagePath);
		} else if (element.getProperties().containsKey(ModelElementKeys.Scm.PROJECT)) {
		// If there is some kind of SCM path, and a configured SCM provider,
		// let's check if we can find some pom.xml
		returned = processPackageAtSCM(element);
	}
		returned.ifPresent(javascriptProject -> javascriptPackageAnalyzer.decorate(element, javascriptProject));
		return returned;
	}

	private Optional<JavascriptProject> processPackageAtSCM(Element element) {
		var project = element.getProperties().get(ModelElementKeys.Scm.PROJECT);
		for(SCMHandler handler : scmHandler) {
			try {
				FileObject projectRoot = handler.getProjectRoot(project);
				FileObject packageJson = projectRoot.getChild("package.json");
					return Optional.ofNullable(javascriptPackageReader.readNpmProject(packageJson));
			} catch (IOException e) {
				logger.log(Level.FINER, String.format("There is no package.json in %s, maybe it's normal", project), e);
			}
		}
		return Optional.empty();
	}

	Optional<JavascriptProject> processPackageFromClass(Element element, String className) {
		try {
			JavascriptProject javascriptProject = findJavascriptProjectOf(Class.forName(className));
			return Optional.of(javascriptProject);
		}catch(ClassNotFoundException e) {
			throw new JavascriptDetailsInfererException(
					String.format("Can't load class %s. Seems like there is a classloader incompatibility", className),
					e);
		}
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
			throw new JavascriptDetailsInfererException(String.format("", className));
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
					"Seems like class %s is not loaded from a Javascript project, as we can't find any pom.xml file",
					className));
		}
		return null;
	}
	public String decorateTechnology(JavascriptProject javascriptProject) {
		return javascriptPackageAnalyzer.decorateTechnology(javascriptProject);
	}
}
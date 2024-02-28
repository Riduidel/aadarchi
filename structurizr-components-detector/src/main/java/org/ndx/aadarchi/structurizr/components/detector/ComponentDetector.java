package org.ndx.aadarchi.structurizr.components.detector;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileSystemManager;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.ComponentFinderStrategy;
import com.structurizr.analysis.SourceCodeComponentFinderStrategy;
import com.structurizr.analysis.StructurizrAnnotationsComponentFinderStrategy;
import com.structurizr.analysis.TypeRepository;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;

/**
 * Enhancer that detects the components in each container for various
 * sub-elements to work correctly
 */

public class ComponentDetector extends ModelElementAdapter {
	@Inject
	Logger logger;
	@Inject
	FileSystemManager fileSystemManager;

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int priority() {
		return 10;
	}
	
	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		super.endVisit(softwareSystem, builder);
	}

	/**
	 * When visiting container, we fond all associated components and load them
	 * immediatly
	 */
	@Override
	public boolean startVisit(Container container) {
		if (container.getProperties().containsKey(ModelElementKeys.JAVA_PACKAGES)) {
			try {
				detectComponentsIn(container);
			} catch (Throwable t) {
				logger.log(Level.WARNING, String.format("Unable to detect components in %s", container));
			}
		} else {
			logger.warning(String.format(
					"As there are no %s property defined on container %s, we're not able to detect components in that container",
					ModelElementKeys.JAVA_PACKAGES, container.getCanonicalName()));
		}
		return super.startVisit(container);
	}

	protected ComponentFinder detectComponentsIn(Container container)
			throws MalformedURLException, URISyntaxException, Exception {
		ComponentFinder componentFinder = new ComponentFinder(container,
				container.getProperties().get(ModelElementKeys.JAVA_PACKAGES), strategies(container));
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader instanceof URLClassLoader) {
			URLClassLoader urlClassLoader = (URLClassLoader) contextClassLoader;
			componentFinder.setUrlClassLoader(urlClassLoader);
		}
		doDetectComponentsIn(container, componentFinder);
		return componentFinder;
	}

	/**
	 * Effective component detection method.
	 * This extracted code is here to allow easier tests (which is bad, but cool)
	 * @param container
	 * @param componentFinder
	 * @throws Exception
	 */
	void doDetectComponentsIn(Container container, ComponentFinder componentFinder) throws Exception {
		componentFinder.findComponents();
		logger.info(String.format("%d modules found in %s.", container.getComponents().size(),
				container.getName()));
	}

	private Collection<Component> findComponentsImplementing(Container container, TypeRepository typeRepository,
			Class<?> implementedInterface) {
		List<Component> returned = typeRepository.getAllTypes().stream()
				.filter(clazz -> !clazz.isInterface())
				.filter(clazz -> implementedInterface.isAssignableFrom(clazz))
				.map(clazz -> findComponentForClass(container, clazz))
				.filter(clazz -> clazz != null)
				.collect(Collectors.toList());
		return returned;
	}

	private Component findComponentForClass(Container container, Class<?> clazz) {
		Component returned = container.getComponentWithName(clazz.getSimpleName());
		if (returned == null)
			logger.warning(
					String.format("We couldn't find any component for class %s. Seems like one annotation is missing",
							clazz.getSimpleName()));
		return returned;
	}

	private ComponentFinderStrategy[] strategies(Container container) throws MalformedURLException, URISyntaxException {
		List<ComponentFinderStrategy> returned = new LinkedList<>();
		returned.add(new StructurizrAnnotationsComponentFinderStrategy());
		if (container.getProperties().containsKey(ModelElementKeys.JAVA_SOURCES)) {
			Stream.of(container.getProperties().get(ModelElementKeys.JAVA_SOURCES).split(";"))
				.map(ThrowingFunction.unchecked(fileSystemManager::resolveFile))
				.map(folder -> folder.getPath().toFile())
				.map(SourceCodeComponentFinderStrategy::new)
				.forEach(returned::add);
		}
		return returned.toArray(new ComponentFinderStrategy[returned.size()]);
	}

	@Override
	public boolean startVisit(Component component) {
		return false;
	}
}

package org.ndx.aadarchi;

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

import javax.inject.Inject;

import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.FileResolver;

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
	FileResolver fileResolver;

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
				switch (container.getName()) {
				case "base":
					startVisitBase(container, detectComponentsIn(container));
					break;
				default:
//					detectComponentsIn(container);
				}
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

	private ComponentFinder detectComponentsIn(Container container)
			throws MalformedURLException, URISyntaxException, Exception {
		ComponentFinder componentFinder = new ComponentFinder(container,
				container.getProperties().get(ModelElementKeys.JAVA_PACKAGES), strategies(container));
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		if (contextClassLoader instanceof URLClassLoader) {
			URLClassLoader urlClassLoader = (URLClassLoader) contextClassLoader;
			componentFinder.setUrlClassLoader(urlClassLoader);
		}
		logger.info(String.format("Detecting components of %s. It can be long ...", container.getName()));
		componentFinder.findComponents();
		logger.info(String.format("Detected %d components of %s.", container.getComponents().size(),
				container.getName()));
		return componentFinder;
	}

	private void startVisitBase(Container container, ComponentFinder componentFinder) {
		// Now we have all components, let's wire them
		Component architectureEnhancer = container.getComponentWithName(ArchitectureEnhancer.class.getSimpleName());
		Collection<Component> enhancers = findComponentsImplementing(container, componentFinder.getTypeRepository(),
				Enhancer.class);
		for (Component enhancer : enhancers) {
			architectureEnhancer.uses(enhancer, "enhances documentation");
		}
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
			Path sourceFolderAsPath = fileResolver
					.fileAsUrltoPath(container.getProperties().get(ModelElementKeys.JAVA_SOURCES));
			returned.add(new SourceCodeComponentFinderStrategy(sourceFolderAsPath.toFile()));
		}
		return returned.toArray(new ComponentFinderStrategy[returned.size()]);
	}

	@Override
	public boolean startVisit(Component component) {
		return false;
	}
}

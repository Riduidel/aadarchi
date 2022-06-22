package org.ndx.aadarchi;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.FileResolver;

import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.ComponentFinderStrategy;
import com.structurizr.analysis.SourceCodeComponentFinderStrategy;
import com.structurizr.analysis.StructurizrAnnotationsComponentFinderStrategy;
import com.structurizr.model.Component;
import com.structurizr.model.Container;

/**
 * Enhancer that detects the components in each container for various
 * sub-elements to work correctly
 */

public class ComponentDetector extends ModelElementAdapter {
	@Inject Logger logger;
	@Inject FileResolver fileResolver;

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS;
	}

	/**
	 * When visiting container, we fond all associated components and load them immediatly
	 */
	@Override
	public boolean startVisit(Container container) {
		if(container.getProperties().containsKey(ModelElementKeys.JAVA_PACKAGES)) {
			try {
				ComponentFinder componentFinder = new ComponentFinder(
						container,
						"org.ndx.aadarchi",
						strategies(container)
						);
				ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
				if (contextClassLoader instanceof URLClassLoader) {
					URLClassLoader urlClassLoader = (URLClassLoader) contextClassLoader;
					componentFinder.setUrlClassLoader(urlClassLoader);
				}
				logger.info(String.format("Detecting components of %s. It can be long ...", container.getName()));
				componentFinder.findComponents();
				logger.info(String.format("Detected %d components of %s.", container.getComponents().size(), container.getName()));
			} catch (Exception e) {
				logger.log(Level.WARNING,
						String.format("Unable to detect components in %s", container));
			}
		} else {
			logger.warning(
					String.format("As there are no %s property defined on container %s, we're not able to detect components in that container",
							ModelElementKeys.JAVA_PACKAGES, container.getCanonicalName()));
		}
		return super.startVisit(container);
	}

	private ComponentFinderStrategy[] strategies(Container container) throws MalformedURLException, URISyntaxException {
		List<ComponentFinderStrategy> returned = new LinkedList<>();
		returned.add(new StructurizrAnnotationsComponentFinderStrategy());
		if(container.getProperties().containsKey(ModelElementKeys.JAVA_SOURCES)) {
			Path sourceFolderAsPath = fileResolver.fileAsUrltoPath(container.getProperties().get(ModelElementKeys.JAVA_SOURCES));
			returned.add(new SourceCodeComponentFinderStrategy(sourceFolderAsPath.toFile()));
		}
		return returned.toArray(new ComponentFinderStrategy[returned.size()]);
	}

	@Override
	public boolean startVisit(Component component) {
		return false;
	}
}

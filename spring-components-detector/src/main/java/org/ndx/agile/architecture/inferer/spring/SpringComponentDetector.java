package org.ndx.agile.architecture.inferer.spring;

import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.FirstImplementationOfInterfaceSupportingTypesStrategy;
import com.structurizr.analysis.ReferencedTypesInSamePackageSupportingTypesStrategy;
import com.structurizr.analysis.ReferencedTypesSupportingTypesStrategy;
import com.structurizr.analysis.SpringComponentFinderStrategy;
import com.structurizr.model.Container;

@ApplicationScoped
public class SpringComponentDetector extends ModelElementAdapter {
	@Inject Logger logger;

	@Override
	public int priority() {
		return 5;
	}
	
	@Override
	public boolean startVisit(Container container) {
		if(container.getTechnology().toLowerCase().contains("spring")) {
			logger.info(String.format("Seems like container %s is a Spring container. Can we detect its components ?", container.getCanonicalName()));
			if(container.getProperties().containsKey(ModelElementKeys.JAVA_PACKAGES)) {
				String packageNames = container.getProperties().get(ModelElementKeys.JAVA_PACKAGES);
				String[] allPackagesNames = packageNames.split(";");
				ComponentFinder componentFinder = new ComponentFinder(container, allPackagesNames[0], 
						new AdaptableSpringComponentFinderStrategy(
								new FirstImplementationOfInterfaceSupportingTypesStrategy(),
								new ImplementedInterfacesSupportingTypesStrategy()
//								new ReferencedTypesSupportingTypesStrategy()
								));
				for (int i = 1; i < allPackagesNames.length; i++) {
					componentFinder.addPackageName(allPackagesNames[i]);
				}
				if (getClass().getClassLoader() instanceof URLClassLoader) {
					componentFinder.setUrlClassLoader((URLClassLoader) getClass().getClassLoader());
				}
				try {
					componentFinder.findComponents();
				} catch (Exception e) {
					logger.log(Level.WARNING, String.format("Unable to read components in packages %s of container %s", packageNames, container.getCanonicalName()), e);
				}
			}
			logger.log(Level.INFO, String.format("Detected %d components in %s", container.getComponents().size(), container.getCanonicalName()));
		}
		return false;
	}
}

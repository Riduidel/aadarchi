package org.ndx.agile.architecture.inferer.spring;

import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.MetaInfServices;
import org.ndx.agile.architecture.base.Enhancer;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;
import org.ndx.agile.architecture.base.utils.StructurizrUtils;

import com.structurizr.analysis.ComponentFinder;
import com.structurizr.analysis.FirstImplementationOfInterfaceSupportingTypesStrategy;
import com.structurizr.model.Container;

@MetaInfServices(value = Enhancer.class)
public class SpringComponentDetector extends ModelElementAdapter {
	private static final Logger logger = Logger.getLogger(SpringComponentDetector.class.getName());

	@Override
	public int priority() {
		return 5;
	}
	
	@Override
	public boolean startVisit(Container container) {
		if(container.getTechnology().toLowerCase().contains("spring")) {
			logger.info(String.format("Seems like container %s is a Spring container. Can we detect its components ?", StructurizrUtils.getCanonicalPath(container)));
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
					logger.log(Level.WARNING, String.format("Unable to read components in packages %s of container %s", packageNames, StructurizrUtils.getCanonicalPath(container)), e);
				}
			}
			logger.log(Level.INFO, String.format("Detected %d components in %s", container.getComponents().size(), StructurizrUtils.getCanonicalPath(container)));
		}
		return false;
	}
}

package org.ndx.adarchi.inferer.spring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.structurizr.analysis.SupportingTypesStrategy;
import com.structurizr.model.Component;

/**
 * A supporting type strategy allowing support of the not-so-good pattern
 * where a DAO interface is implemented by a DAO @Repository.
 * Obviously, the annotation should be set on the interface, but some people just want to see the world burn. 
 * @author Nicolas
 *
 */
public class ImplementedInterfacesSupportingTypesStrategy extends SupportingTypesStrategy {

    private static final Log log = LogFactory.getLog(ImplementedInterfacesSupportingTypesStrategy.class);

	@Override
	public Set<Class<?>> findSupportingTypes(Component component) {
        Set<Class<?>> set = new HashSet<>();

        try {
            Class componentType = getTypeRepository().loadClass(component.getType().getType());
            if (!componentType.isInterface()) {
            	// So this is a valid class. Let's add all interfaces
            	Arrays.asList(componentType.getInterfaces()).stream().forEach(set::add);
            }
        } catch (ClassNotFoundException e) {
            log.warn("Could not load type " + component.getType().getType());
        }

        return set;
	}

}

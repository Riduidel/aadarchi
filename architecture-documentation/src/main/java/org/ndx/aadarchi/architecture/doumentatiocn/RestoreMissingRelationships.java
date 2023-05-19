package org.ndx.aadarchi.architecture.doumentatiocn;

import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import com.structurizr.model.CodeElement;
import com.structurizr.model.Component;
import com.structurizr.model.Container;

/**
 * Restore dependency relationships to ArchitectureEnhancer
 */
public class RestoreMissingRelationships extends ModelElementAdapter {

	@Override
	public int priority() {
		return 50;
	}

	@Override
	public boolean startVisit(Container container) {
		return container.getName().equals("base");
	}

	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		Component arcitectureEnhancer = container.getComponentWithName("ArchitectureEnhancer");
		for (Component c : container.getComponents()) {
			boolean enhancer = false;
			for(CodeElement code : c.getCode()) {
				if(!enhancer) {
					try {
						Class<?> clazz = Class.forName(code.getType());
						enhancer = Enhancer.class.isAssignableFrom(clazz);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(enhancer) {
				arcitectureEnhancer.uses(c, "Enhances architecture");
			}
		}
	}
}

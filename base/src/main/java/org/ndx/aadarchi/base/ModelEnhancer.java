package org.ndx.aadarchi.base;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

public interface ModelEnhancer extends Enhancer {
	boolean startVisit(Model model);

	boolean startVisit(SoftwareSystem softwareSystem);

	boolean startVisit(Container container);

	boolean startVisit(Component component);

	void endVisit(Component component, OutputBuilder builder);

	void endVisit(Container container, OutputBuilder builder);

	void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder);

	void endVisit(Model model, OutputBuilder builder);
}

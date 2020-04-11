package org.ndx.agile.architecture.base.enhancers;

import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

/** An adapter redirecting all model element processing to the same method */
public abstract class ModelElementAdapter implements ModelEnhancer {

	/**
	 * Override this method to do anything on the given element
	 * @param element element we want to enhance
	 * @param builder output builder to write data
	 */
	protected abstract void processElement(Element element, OutputBuilder builder);

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return true;
	}

	@Override public void endVisit(Workspace workspace, OutputBuilder builder) {}

	@Override public boolean startVisit(Model model) {
		return true;
	}

	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		return true;
	}

	@Override
	public boolean startVisit(Container container) {
		return true;
	}

	@Override
	public boolean startVisit(Component component) {
		return true;
	}

	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		processElement(component, builder);
	}

	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		processElement(container, builder);
	}

	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		processElement(softwareSystem, builder);
	}

	@Override
	public void endVisit(Model model, OutputBuilder builder) {
	}

}

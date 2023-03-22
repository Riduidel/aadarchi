package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;

import javax.inject.Inject;

public class ModelLinterEnhancer extends ModelElementAdapter {
    @Inject
    ModelLinter modelLinter;

    @Override
    public boolean isParallel() {
        return true;
    }

    @Override
    public int priority() {
        return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS - 1;
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
        modelLinter.checksSomeFieldForComponent(component);
    }

    @Override
    public void endVisit(Container container, OutputBuilder builder) {
        modelLinter.checksSomeFieldForContainer(container);
    }

    @Override
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        modelLinter.checksSomeFieldForAnElement(softwareSystem);
    }
}





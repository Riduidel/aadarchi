package org.ndx.aadarchi.inferer.javascript.enhancers;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import org.ndx.aadarchi.inferer.javascript.JavascriptDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.javascript.JavascriptProject;

import java.util.Collection;

public class SoftwareSystemEnhancer extends AbstractContainerEnhancer<SoftwareSystem, Container> {
    private final JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer;

    public SoftwareSystemEnhancer(JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer, SoftwareSystem softwareSystem) {
        super(javascriptDetailsInfererEnhancer, softwareSystem);
        this.javascriptDetailsInfererEnhancer = javascriptDetailsInfererEnhancer;
    }

    @Override
    protected void containedDependsUpon(Component component, Component found, String string) {
        component.uses(found, string);
    }

    @Override
    protected Container addContainedElementWithKey(JavascriptProject javascriptProject, String key) {
        return enhanced.addContainer(key, this.javascriptDetailsInfererEnhancer.decorateTechnology(javascriptProject));
    }

    @Override
    protected Container getContainedElementWithName(String key) {
        return enhanced.getContainerWithName(key);
    }

    @Override
    protected Collection<Container> getEnhancedChildren() {
        return enhanced.getContainers();
    }
}

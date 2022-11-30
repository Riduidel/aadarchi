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
    protected Container addContainedElementWithKey(JavascriptProject module, String key) {
        return enhanced.addContainer(key, this.javascriptDetailsInfererEnhancer.decorateTechnology(module));
    }

    @Override
    protected Container getContainedElementWithName(String key) {
        return enhanced.getContainerWithName(key);
    }

    @Override
    protected Collection<Container> getEnhancedChildren() {
        return enhanced.getContainers();
    }

    @Override
    protected void containedDependsUpon(Container contained, Container found, String string) {
         contained.uses(found, string);
    }
}

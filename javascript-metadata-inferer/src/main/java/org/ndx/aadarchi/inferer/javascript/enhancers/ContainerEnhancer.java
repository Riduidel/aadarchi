package org.ndx.aadarchi.inferer.javascript.enhancers;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import org.ndx.aadarchi.inferer.javascript.JavascriptDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.javascript.JavascriptProject;

import java.util.Collection;

public class ContainerEnhancer extends AbstractContainerEnhancer<Container, Component> {

    private final JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer;

    public ContainerEnhancer(JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer, Container container) {
        super(javascriptDetailsInfererEnhancer, container);
        this.javascriptDetailsInfererEnhancer = javascriptDetailsInfererEnhancer;
    }


    @Override
    protected void startEnhancedWithJavascriptProject(JavascriptProject javascriptProject) {
        enhanced.setTechnology(this.javascriptDetailsInfererEnhancer.decorateTechnology(javascriptProject));
        super.startEnhancedWithJavascriptProject(javascriptProject);
    }

    @Override
    protected void containedDependsUpon(Component component, Component found, String string) {
        component.uses(found, string);
    }

    @Override
    protected Component addContainedElementWithKey(JavascriptProject javascriptProject, String key) {
        return enhanced.addComponent(key, javascriptProject.getDescription(), this.javascriptDetailsInfererEnhancer.decorateTechnology(javascriptProject));
    }

    @Override
    protected Component getContainedElementWithName(String key) {
        return enhanced.getComponentWithName(key);
    }

    @Override
    protected Collection<Component> getEnhancedChildren() {
        return enhanced.getComponents();
    }


}

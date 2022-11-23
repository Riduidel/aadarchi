package org.ndx.aadarchi.inferer.javascript.enhancers;

import com.structurizr.model.Component;
import org.ndx.aadarchi.inferer.javascript.JavascriptDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.javascript.JavascriptProject;

public class ComponentEnhancer extends ModelElementJavascriptEnhancer<Component> {

    private final JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer;

    public ComponentEnhancer(JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer, Component component) {
        super(javascriptDetailsInfererEnhancer, component);
        this.javascriptDetailsInfererEnhancer = javascriptDetailsInfererEnhancer;
    }

    @Override
    protected void startEnhancedWithJavascriptProject(JavascriptProject javascriptProject) {
        enhanced.setTechnology(this.javascriptDetailsInfererEnhancer.decorateTechnology(javascriptProject));
    }

    @Override
    protected void endEnhanceWithJavascriptProject(JavascriptProject javascriptProject) {

    }
}

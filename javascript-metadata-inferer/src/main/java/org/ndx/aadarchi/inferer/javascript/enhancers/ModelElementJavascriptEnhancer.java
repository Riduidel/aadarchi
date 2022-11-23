package org.ndx.aadarchi.inferer.javascript.enhancers;

import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;
import org.ndx.aadarchi.inferer.javascript.JavascriptDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.javascript.JavascriptProject;

/**
 * Provides base features and code organization for all model enhancers having a PACKAGE provided
 *
 * @param <Enhanced>
 */
abstract class ModelElementJavascriptEnhancer<Enhanced extends StaticStructureElement> {
    private final JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer;
    protected final Enhanced enhanced;

    public ModelElementJavascriptEnhancer(JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer, Enhanced enhanced) {
        this.javascriptDetailsInfererEnhancer = javascriptDetailsInfererEnhancer;
        this.enhanced = enhanced;
    }

    /**
     * When an element has a package, we call the {@link #startEnhancedWithJavascriptProject(JavascriptProject)}
     * method on the enhanced element
     * @see JavascriptDetailsInfererEnhancer#processModelElement(Element)
     */
    public void startEnhance() {
        javascriptDetailsInfererEnhancer.processModelElement(enhanced).ifPresent(this::startEnhancedWithJavascriptProject);
    }

    private void startEnhancedWithJavascriptProject(Object o) {
    }

    /**
     * When an element has a package, we call the {@link #endEnhanceWithJavascriptProject(JavascriptProject)}
     * method on the enhanced element
     * @see JavascriptDetailsInfererEnhancer#processModelElement(Element)
     */
    public void endEnhance() {
        javascriptDetailsInfererEnhancer.processModelElement(enhanced).ifPresent(this::endEnhanceWithJavascriptProject);
    }

    protected abstract void startEnhancedWithJavascriptProject(JavascriptProject javascriptProject);

    protected abstract void endEnhanceWithJavascriptProject(JavascriptProject javascriptProject);
    
}


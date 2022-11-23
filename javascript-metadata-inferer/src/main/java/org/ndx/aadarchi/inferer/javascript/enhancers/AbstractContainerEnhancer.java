package org.ndx.aadarchi.inferer.javascript.enhancers;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.StaticStructureElement;
import org.ndx.aadarchi.inferer.javascript.JavascriptDetailsInfererEnhancer;
import org.ndx.aadarchi.inferer.javascript.JavascriptEnhancer;
import org.ndx.aadarchi.inferer.javascript.JavascriptProject;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Base class for structurizr model elements that may contain children
 * @author nicolas-delsaux
 *
 * @param <Enhanced>
 * @param <Contained>
 */
abstract class AbstractContainerEnhancer<Enhanced extends StaticStructureElement, Contained extends StaticStructureElement> extends ModelElementJavascriptEnhancer<Enhanced> {

    protected static final Logger logger = Logger.getLogger(AbstractContainerEnhancer.class.getName());

    private final JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer;
    protected Optional<String> additionalProfiles = Optional.empty();

    protected AbstractContainerEnhancer(JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer, Enhanced enhanced) {
        super(javascriptDetailsInfererEnhancer, enhanced);
        this.javascriptDetailsInfererEnhancer = javascriptDetailsInfererEnhancer;
        this.additionalProfiles = Optional.ofNullable(enhanced.getProperties().get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_ADDITIONAL_PROFILES));
    }

    @Override
    protected void startEnhancedWithJavascriptProject(JavascriptProject javascriptProject) {
            loadAllSubElements(javascriptProject).forEach(module -> findSubComponentFor(javascriptProject, (JavascriptProject) module));
    }

    @Override
    protected void endEnhanceWithJavascriptProject(JavascriptProject javascriptProject) {
        loadAllSubElements(javascriptProject).forEach(module -> linkToDependenciesOf(javascriptProject));
    }

    private void linkToDependenciesOf(JavascriptProject javascriptProjectModule) {
        Contained contained = getContainedElementWithName(javascriptProjectModule);
        /*
        For each dependency of the npm project, if there is an associated
        name, link both of them
         */
    }
    //A REVOIR : NAME IN PACKAGE.JSON ?
    protected Stream<Contained> findContainedWithName(String name) {
        return getEnhancedChildren().stream()
                .filter(container -> container.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES))
                .filter(container -> container.getProperties().get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES).equals(name))
                .findFirst().stream();
    }

    protected abstract Collection<Contained> getEnhancedChildren();

    //TO DOcheck addProperty and scm
    void findSubComponentFor(JavascriptProject javascriptProject, JavascriptProject javascriptProjectModule) {
        String key = getContainedElementKey(javascriptProjectModule);
        Contained linked = getContainedElementWithName(key);
        if(linked == null) {
            linked =addContainedElementWithKey(javascriptProjectModule, key);
        }
        //Now the container is loaded. Then, we can add some useful properties
        if(!linked.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE)) {
            linked.addProperty(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE,
                    javascriptProjectModule.getProperties().get(JavascriptDetailsInfererEnhancer.NPM_PACKAGE_URL));
        }
    }
    protected abstract void containedDependsUpon(Component component, Component found, String string);
    private Contained getContainedElementWithName(JavascriptProject javascriptProject) {
        return getContainedElementWithName(getContainedElementKey(javascriptProject));
    }

    /**
     * Allow us to obtain children with the given name.
     * This method exists because Structurizr tries to be very clean, and provide meaningful
     * methods. As a consequence, the SoftwareSystem children can't be accessed by a method
     * having the same name than Container children
     * @param key
     * @return
     */
    protected abstract Contained getContainedElementWithName(String key);
    private String getContainedElementKey(JavascriptProject javascriptProject) {
        return javascriptProject.getName();
    }
    protected abstract Contained addContainedElementWithKey(JavascriptProject javascriptProject, String key);

    private Stream<JavascriptProject> loadAllSubElements(JavascriptProject javascriptProject) {
        String packagePath = javascriptProject.getProperties().get(JavascriptDetailsInfererEnhancer.NPM_PACKAGE_URL);
        final String packageDir = packagePath.substring(0, packagePath.lastIndexOf("/package.json"));
        List<String> modules = new ArrayList<>();
        modules.addAll(((List<String>) javascriptProject.getModules()));

        return null;
    }
}

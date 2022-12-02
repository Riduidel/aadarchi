package org.ndx.aadarchi.inferer.javascript.enhancers;

import com.structurizr.model.Component;
import com.structurizr.model.StaticStructureElement;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.inferer.javascript.Dependency;
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
abstract class AbstractContainerEnhancer<Enhanced extends StaticStructureElement, Contained extends StaticStructureElement>
        extends ModelElementJavascriptEnhancer<Enhanced> {

    protected static final Logger logger = Logger.getLogger(AbstractContainerEnhancer.class.getName());
    private final JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer;
    protected Optional<String> additionalProfiles = Optional.empty();

    protected AbstractContainerEnhancer(JavascriptDetailsInfererEnhancer javascriptDetailsInfererEnhancer, Enhanced enhanced) {
        super(javascriptDetailsInfererEnhancer, enhanced);
        this.javascriptDetailsInfererEnhancer = javascriptDetailsInfererEnhancer;
        this.additionalProfiles = Optional.ofNullable(enhanced.getProperties().get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_ADDITIONAL_PROFILES));
    }
    //TODO : how to retrieve component list
    @Override
    protected void startEnhancedWithJavascriptProject(JavascriptProject javascriptProject) {
            loadAllSubElements(javascriptProject).forEach(component -> findSubComponentFor(javascriptProject, component));
    }
    @Override
    protected void endEnhanceWithJavascriptProject(JavascriptProject javascriptProject) {
        loadAllSubElements(javascriptProject).forEach(this::linkToDependenciesOf);
    }

    /**
     * When needed, add all dependency links between javascript project
     *
     * @param module
     */
    private void linkToDependenciesOf(JavascriptProject module) {
        Contained contained = getContainedElementWithName(module);
        /*
        For each dependency of the npm project, if there is an associated
        name, link both of them
         */
        ((List<Dependency>) module.getDependencies()).stream()
                .map(dependency -> String.format("%s",dependency.getName()))
                .flatMap(artifactKey -> findContainedWithArtifactKey(artifactKey))
                .forEach(found -> containedDependsUpon(contained, found, "maven:dependency"));
    }

    private Stream<Contained> findContainedWithArtifactKey(String artifactKey) {
        return getEnhancedChildren().stream()
                .filter(container -> container.getProperties()
                        .containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES))
                .filter(container -> container.getProperties()
                        .get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES).equals(artifactKey))
                .findFirst().stream();
    }

    protected Stream<Contained> findContainedWithName(String name) {
        return getEnhancedChildren().stream()
                .filter(container -> container.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES))
                .filter(container -> container.getProperties().get(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES).equals(name))
                .findFirst().stream();
    }

    protected abstract Collection<Contained> getEnhancedChildren();

    //TODO : module.getProperties
    void findSubComponentFor(JavascriptProject javascriptProject, JavascriptProject module) {
        String key = getContainedElementKey(module);
        Contained linked = getContainedElementWithName(key);
        if(linked == null) {
            linked =addContainedElementWithKey(module, key);
        }
        //Now the container is loaded. Then, we can add some useful properties
        if(!linked.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE)) {
            linked.addProperty(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_PACKAGE,
                    module.getProperties().get(JavascriptDetailsInfererEnhancer.NPM_PACKAGE_URL));
        }
        /*linked.addProperty(ModelElementKeys.Scm.PATH,
                module.getProperties().getProperty(ModelElementKeys.Scm.PATH));*/
    }
    protected abstract void containedDependsUpon(Contained contained, Contained found, String string);
    private Contained getContainedElementWithName(JavascriptProject module) {
        return getContainedElementWithName(getContainedElementKey(module));
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

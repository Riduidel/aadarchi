package org.ndx.aadarchi.inferer.javascript;

import com.structurizr.model.Element;
import org.ndx.aadarchi.base.utils.FileResolver;
import javax.inject.Inject;
import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class JavascriptPackageAnalyzer extends JavascriptDetailsInfererEnhancer {

    @Inject Logger logger;

    @Inject FileResolver fileResolver;

    /**
     * Creates the string containing details about the used technology. For that we
     * will simply read the javascript dependencies
     * (for frameworks)
     *
     * @param project
     * @return a string giving details about important project infos
     */
    public String decorateTechnology(JavascriptProject project) {
        Set<String> technologies = new TreeSet<>();
        decorateRecursively(project, (p, l) -> {
            technologies.addAll(doDecorateTechnology(p));
            return true;
        });
        return String.join(",", technologies);
    }

    private String technologyWithVersionFromDependency(JavascriptProject javascriptProject, String technology, String... propertyNames) {
        return technology + Stream.of(propertyNames)
                .flatMap(p -> new HashSet(Arrays.asList(p, p.replace('.', '-'), p.replace('-', '.'))).stream())
                .filter(p -> javascriptProject.getDependencies().containsKey(p))
                .map(p -> javascriptProject.getDependencies().get(p))
                .map(text -> " "+text)
                .findFirst()
                .orElse("")
                ;
    }

    private void decorateRecursively(JavascriptProject project, BiFunction<JavascriptProject, List<JavascriptProject>, Boolean> consumer) {
        decorateRecursively(project, new LinkedList<>(), consumer);
    }

    //TODO : parent?
    private void decorateRecursively(JavascriptProject project, List<JavascriptProject> children, BiFunction<JavascriptProject, List<JavascriptProject>, Boolean> consumer) {
        if(consumer.apply(project, children)) {
            if(project.getParent()!=null) {
                decorateRecursively(project.getParent(), consumer);
            }
        }
    }
    private Set<String> doDecorateTechnology(JavascriptProject project) {
        Set<String> technologies = new LinkedHashSet<>();
        for (Dependency dependency : (List<Dependency>) project.getDependencies()) {
            if (dependency.getName().startsWith("@angular")) {
                technologies.add("angular");
            } else if (dependency.getName().startsWith("react")) {
                technologies.add("react.js");
            } else if (dependency.getName().startsWith("vue")) {
                technologies.add("vue.js");
            } else if (dependency.getName().startsWith("node")) {
                technologies.add("node.js");
            }
        }
        return technologies;
    }
    public void decorate(Element element, JavascriptProject javascriptProject) {
        decorateCoordinates(element, javascriptProject);
        Optional.ofNullable(javascriptProject.getDescription()).stream()
                .forEach(description -> element.setDescription(description.replaceAll("\n", " ")));

    }

    private void decorateCoordinates(Element element, JavascriptProject javascriptProject) {
        if (!element.getProperties().containsKey(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES)) {
            element.addProperty(JavascriptEnhancer.AGILE_ARCHITECTURE_NPM_COORDINATES,
                    String.format("%s", javascriptProject.getName()));
        }
    }
}

package org.ndx.aadarchi.inferer.javascript;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import com.structurizr.model.Element;
import javax.inject.Inject;

import org.ndx.aadarchi.inferer.javascript.npm.Dependency;
import org.ndx.aadarchi.inferer.javascript.npm.JavascriptProject;

import java.util.*;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.structurizr.model.Element;

public class JavascriptPackageAnalyzer {

    @Inject Logger logger;

    /**
     * Creates the string containing details about the used technology. For that we
     * will simply read the javascript dependencies
     * (for frameworks)
     *
     * @param project
     * @return a string giving details about important project infos
     */
    public String decorateTechnology(JavascriptProject project) {
        /*decorateRecursively(project, (p, l) -> {
            technologies.addAll(doDecorateTechnology(p));
            return true;
        });*/
        //Set<String> technologies = new TreeSet<>(doDecorateTechnology(project));
        Set<String> technologies = doDecorateTechnology(project);
        //technologies.forEach(System.out::println);
        return String.join(",", technologies);
    }

    private Set<String> doDecorateTechnology(JavascriptProject project) {
        Set<String> technologies = new LinkedHashSet<>();

        project.dependencies.forEach((k, v) -> {
            if (k.toString().startsWith("@angular")) {
                technologies.add("angular");
            } else if (k.toString().startsWith("react")) {
                technologies.add("react.js");
            } else if (k.toString().startsWith("vue")) {
                technologies.add("vue.js");
            } else if (k.toString().startsWith("node")) {
                technologies.add("node.js");
            }
        });
        return technologies;
    }

    /*private Set<String> doDecorateTechnology(JavascriptProject project) {
        System.out.println("[][][]Method " + Thread.currentThread().getStackTrace()[1].getMethodName() + " called");
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
    }*/

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
            /*if(project.getParent()!=null) {
                decorateRecursively(project.getParent(), consumer);
            }*/
        }
    }

    public void decorate(Element element, JavascriptProject javascriptProject) {
        Optional.ofNullable(javascriptProject.getDescription()).stream()
                .forEach(description -> element.setDescription(description.replaceAll("\n", " ")));
    }
    /*private void decorateScmUrl(Element element, JavascriptProject javascriptProject) {
        decorateRecursively(javascriptProject, (project,children) -> {
            if(project.getScm()!=null) {
                Scm scm = project.getScm();
                if(scm.getUrl()!=null) {
                    String url = scm.getUrl();
                    element.addProperty(org.ndx.aadarchi.base.enhancers.ModelElementKeys.Scm.PROJECT, url);
                    return false;
                }
            }
            return null;
        });
    }*/
}
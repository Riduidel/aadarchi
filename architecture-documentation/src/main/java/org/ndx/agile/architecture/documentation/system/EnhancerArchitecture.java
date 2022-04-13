package org.ndx.agile.architecture.documentation.system;


import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.Enhancer;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ClassUtils.getSimpleName;

public class EnhancerArchitecture implements ModelEnhancer {
    String architecture = null;
    String relations = "\n";

    @Inject
    Logger logger;

    @Override
    public boolean isParallel() {
        return false;
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE-1;
    }

    @Override
    public boolean startVisit(Workspace workspace, OutputBuilder builder) {
        architecture = String.format("workspace \"%s\" {\n\n", workspace.getName());
        return true;
    }

    @Override
    public boolean startVisit(Model model) {
        architecture = architecture + "\tmodel {\n";
        relations += parseRelation(model.getRelationships()) + "\n";
        return true;
    }

    @Override
    public boolean startVisit(SoftwareSystem softwareSystem) {
        architecture += String.format("\t\t%s = softwareSystem \"%s\" {\n", noSpecialChar(softwareSystem.getName()), softwareSystem.getName());
        relations += parseRelation(softwareSystem.getRelationships()) + "\n";
        return true;
    }

    @Override
    public boolean startVisit(Container container) {
        architecture += String.format("\t\t\t%s = container \"%s\" {\n", noSpecialChar(container.getName()), container.getName());
        relations += parseRelation(container.getRelationships()) + "\n";
        return true;
    }

    @Override
    public boolean startVisit(Component component) {
        architecture += String.format("\t\t\t\t%s = component \"%s\" {\n", noSpecialChar(component.getName()), component.getName());
        relations += parseRelation(component.getRelationships()) + "\n";
        return true;
    }

    @Override
    public void endVisit(Component component, OutputBuilder builder) {
        architecture += "\t\t\t\t}\n";
    }

    @Override
    public void endVisit(Container container, OutputBuilder builder) {
        architecture += "\t\t\t}\n";
    }

    @Override
    public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
        architecture += "\t\t}\n";
    }

    @Override
    public void endVisit(Model model, OutputBuilder builder) {
        architecture += "\t}\n";
    }

    @Inject @ConfigProperty(name= ModelElementKeys.PREFIX+"enhancements") File enhancementsBase;

    @Override
    public void endVisit(Workspace workspace, OutputBuilder outputBuilder) {
        architecture = architecture +
                "\tviews {\n" +
                "\t\tstyles {\n" +
                "\t\t\telement \"Software System\" {\n" +
                "\t\t\t\tbackground #1168bd\n" +
                "\t\t\t\tcolor #ffffff\n" +
                "\t\t\t}\n" +
                "\t\t\telement \"Person\" {\n" +
                "\t\t\t\tshape person\n" +
                "\t\t\t\tbackground #08427b\n" +
                "\t\t\t\tcolor #ffffff\n" +
                "\t\t\t}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "\n" +
                "}";

        StringBuilder builder = new StringBuilder(architecture);
        File target = new File(enhancementsBase.getParentFile(), "workspace.dsl");
        try {
            FileUtils.write(target, builder, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String noSpecialChar(String name) {
        return name.replaceAll("[^A-Za-z0-9]","");
    }

    private String parseRelation(Set relations) {
        String parsed = String.valueOf(relations);
        StringBuilder test = new StringBuilder();
        while (parsed.length() > 6) {
            parsed = parsed.substring(parsed.indexOf("|") + 2);
            test.append(parsed, 0, parsed.indexOf("|") - 1);
            test.append("/");
            parsed = parsed.substring(parsed.indexOf("|") + 2);
            test.append(parsed, 0, parsed.indexOf("}"));
            test.append("_");
            parsed = parsed.substring(parsed.indexOf("[") + 1);
            test.append(parsed, 0, parsed.indexOf("]"));
            test.append("_");
            parsed = parsed.substring(parsed.indexOf("|") + 2);
            test.append(parsed, 0, parsed.indexOf("|") - 1);
            test.append("/");
            parsed = parsed.substring(parsed.indexOf("|") + 2);
            test.append(parsed, 0, parsed.indexOf("}"));
            test.append("\n");
            parsed = parsed.substring(parsed.indexOf("}") + 1);
        }
        logger.info(String.valueOf(test));
        return parsed;
    }

}

package org.ndx.agile.architecture.documentation.system;


import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import org.apache.commons.io.FileUtils;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.Enhancer;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.ClassUtils.getSimpleName;

public class EnhancerArchitecture implements ModelEnhancer {
    String architecture = null;

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
        architecture = String.format("workspace \"%s\" {\n\n", getSimpleName(workspace.getName()));
        return true;
    }

    @Override
    public boolean startVisit(Model model) {
        architecture = architecture + "\tmodel {\n";
        return true;
    }

    @Override
    public boolean startVisit(SoftwareSystem softwareSystem) {
        architecture += String.format("\t\t%s = softwareSystem \"%s\" {\n", getSimpleName(softwareSystem.getName()), getSimpleName(softwareSystem.getName()));
        return true;
    }

    @Override
    public boolean startVisit(Container container) {
        architecture += String.format("\t\t\t%s = container \"%s\" {\n", getSimpleName(container.getName()), getSimpleName(container.getName()));
        return true;
    }

    @Override
    public boolean startVisit(Component component) {
        architecture += String.format("\t\t\t\t%s = component \"%s\" {\n", getSimpleName(component.getName()), getSimpleName(component.getName()));
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

    @Override
    public void endVisit(Workspace workspace, OutputBuilder builder) {
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
//        File output = builder.outputFor(AgileArchitectureSection.software_architecture, workspace, this, "dsl");
//        try {
//            FileUtils.write(output, architecture, "UTF-8");
//        } catch (IOException e) {
//            throw new RuntimeException("Can't believe I can't write the file " + output.getAbsolutePath(), e);
//        }
        logger.info(architecture);
    }

    private String getSimpleName(String name) {
        return name.replaceAll("[^A-Za-z0-9]","");
    }

}

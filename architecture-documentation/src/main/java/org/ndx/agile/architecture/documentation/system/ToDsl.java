package org.ndx.agile.architecture.documentation.system;


import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.structurizr.model.*;
import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

import com.structurizr.Workspace;

/**
 * Enhancer that generate a structurizr dsl file
 * The dsl file is used to generate structurizr diagrams of the project architecture
 * This dsl file will be located in target/structurizr/workspace.dsl
 * @author jason-sycz feat nicolas-delsaux
 *
 */

public class ToDsl implements ModelEnhancer {
    String architecture = null;
    String relations = "";

    @Inject @ConfigProperty(name= ModelElementKeys.PREFIX+"enhancements") File enhancementsBase;

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
        if(!model.getPeople().isEmpty()) {
            architecture += personSetToDsl(model.getPeople());
        }
        if(!model.getRelationships().isEmpty()) {
            relations += relationshipSetToDsl(model.getRelationships());
        }
        return true;
    }

    @Override
    public boolean startVisit(SoftwareSystem softwareSystem) {
        architecture += String.format("\t\t%s = softwareSystem \"%s\" {\n", asVariableName(softwareSystem), softwareSystem.getName());
        if(!softwareSystem.getRelationships().isEmpty()) {
            relations += relationshipSetToDsl(softwareSystem.getRelationships());
        }
        return true;
    }

    @Override
    public boolean startVisit(Container container) {
        architecture += String.format("\t\t\t%s = container \"%s\" {\n", asVariableName(container), container.getName());
        if(!container.getRelationships().isEmpty()) {
            relations += relationshipSetToDsl(container.getRelationships());
        }
        return true;
    }

	@Override
    public boolean startVisit(Component component) {
        architecture += String.format("\t\t\t\t%s = component \"%s\" {\n", asVariableName(component), component.getName());
        if(!component.getRelationships().isEmpty()) {
            relations += relationshipSetToDsl(component.getRelationships());
        }
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
        architecture += relations + "\t}\n";
    }

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

    private String personSetToDsl(Set<Person> person) {
        return person.stream()
                .map(this::personToDsl)
                .collect(Collectors.joining("\n", "", "\n"));
    }

    private String personToDsl(Person person) {
        String description = "";
        if(person.getDescription() != null) description = person.getDescription();
        return String.format("\t\t%s = person \"%s\" \"%s\"",
                Objects.requireNonNull(asVariableName(person)),
                Objects.requireNonNull(person.getName()),
                description
        );
    }

    private String relationshipSetToDsl(Set<Relationship> relationships) {
        return relationships.stream()
                .map(this::relationshipToDsl)
                .collect(Collectors.joining("\n", "", "\n"));
    }

    private String relationshipToDsl(Relationship relationship) {
        String description = "";
        if(relationship.getDescription() != null) description = relationship.getDescription();
        String technology = "";
        if(relationship.getTechnology() != null) technology = relationship.getTechnology();
        return String.format("\t\t%s -> %s \"%s\" \"%s\"",
                asVariableName(Objects.requireNonNull(relationship.getSource())),
                asVariableName(Objects.requireNonNull(relationship.getDestination())),
                description,
                technology
        );
    }

    private String asVariableName(Element element) {
    	return String.format("%s_%s",
    			element.getName().replaceAll("[^A-Za-z0-9]", "_"), 
    			element.getId());
    }
}
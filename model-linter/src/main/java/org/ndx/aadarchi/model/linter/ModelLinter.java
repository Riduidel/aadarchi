package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;

import javax.inject.Inject;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModelLinter {
    @Inject
    Logger logger;

    public String verifyElementDescription(Element element) {
        String elementDescription = element.getDescription();
        if (elementDescription.isEmpty())
            logger.info(String.format("your element %s should have a description", element.getName()));
        return elementDescription;
    }

    public String verifyElementTechnology(Container container) {
        String containerTechnology = container.getTechnology();
        if (containerTechnology.isEmpty())
            logger.info(String.format("your container %s should have a technology", containerTechnology));
        return containerTechnology;
    }

    public String verifyElementTechnology(Component component) {
        String componentTechnology = component.getTechnology();
        if (componentTechnology.isEmpty())
            logger.info(String.format("your component %s should have a technology", componentTechnology));
        return componentTechnology;
    }

    public String verifyElementRelationshipDescription(Element element) {
        return element.getRelationships().stream().map(Relationship::getDescription).collect(Collectors.joining());
    }

    public String verifyElementRelationshipTechnology(Element element) {
        return element.getRelationships().stream().map(Relationship::getTechnology).collect(Collectors.joining());
    }

    public void checksSomeFieldForAnElement(Element element) {
        verifyElementDescription(element);
        verifyElementRelationshipDescription(element);
        verifyElementRelationshipTechnology(element);
    }

    public void checksSomeFieldForContainer(Container container) {
        verifyElementDescription(container);
        verifyElementRelationshipDescription(container);
        verifyElementRelationshipTechnology(container);
        verifyElementTechnology(container);
    }

    public void checksSomeFieldForComponent(Component component) {
        verifyElementDescription(component);
        verifyElementRelationshipDescription(component);
        verifyElementRelationshipTechnology(component);
        verifyElementTechnology(component);
    }
}


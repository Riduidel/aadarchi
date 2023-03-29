package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

import java.util.stream.Collectors;

public class ModelLinter {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelLinter.class);
    public void setLogger(String message) {
        LOGGER.error(message);
    }
    public Optional verifyElementDescription(Element element) {
        Optional<String> elementDescription = Optional.ofNullable(element.getDescription());
        if (elementDescription.isEmpty()) {
            setLogger(String.format("your element %s should have a description. " +
                    "A lack of a description prevent user to understand the aim of the element. " +
                    "You should add a description to your element", element.getName()));
        }
        return elementDescription;
    }

    public Optional verifyContainerTechnology(Container container) {
        Optional <String> containerTechnology = Optional.ofNullable(container.getTechnology());
        if (containerTechnology.isEmpty()) {
            setLogger(String.format("your container %s should have a technology. " +
                    "A lack of a technology prevent user to know which technology is associated to this element. " +
                    "You should add technologies used on your container.", container.getName()));
        }
        return containerTechnology;
    }

    public Optional verifyComponentTechnology(Component component) {
        Optional <String> componentTechnology = Optional.ofNullable(component.getTechnology());
        if (componentTechnology.isEmpty()) {
            setLogger(String.format("your component %s should have a technology. " +
                    "A lack of a technology prevent user to know which technology is used for this element. " +
                    "You should add technologies used on your component.", component.getName()));
        }
        return componentTechnology;
    }


    public Optional<Set<String>> verifyElementRelationshipDescription(Element element) {
        Set<String> relationshipDescriptions = element.getRelationships().stream().map(Relationship::getDescription).collect(Collectors.toSet());
        try {
            for (String description : relationshipDescriptions) {
                if(description.isEmpty())
                    setLogger(String.format("The description between element %s and element %s should specify description to help users to know which description is associated to this element." +
                            "Please specify description for this relationship.", element.getName(), getElementRelationshipName(element)));
            }
        }
        catch (Exception exception) {
            throw new IllegalArgumentException(String.format("The description of relationship between element %s and %s cannot be null. " +
                    "Please specify a description to this this relationship.", element.getName(), getElementRelationshipName(element)));
        }
        return Optional.of(relationshipDescriptions);
    }

    public Optional<Set<String>> verifyElementRelationshipTechnology(Element element) {
        Set<String> relationshipTechnologies = element.getRelationships().stream().map(Relationship::getTechnology).collect(Collectors.toSet());
        try {
            for (String technology : relationshipTechnologies) {
                if (technology.isEmpty())
                    setLogger(String.format("The technology used in relationship between element %s and element %s should specify technologies to help users to know which technology is associated to this element." +
                            "Please specify technologies used in these relationship.", element.getName(), getElementRelationshipName(element)));
            }
        } catch (Exception exception) {
        throw new IllegalArgumentException(String.format("The technology of relationship between element %s and %s cannot be null. " +
                "Please specify technologies used in these relationship.", element.getName(), getElementRelationshipName(element)));
    }
        return Optional.of(relationshipTechnologies);
    }
    public String getElementRelationshipName(Element element) {
        Set<Element> relationshipDestination =   element.getRelationships().stream().map(Relationship::getDestination).collect(Collectors.toSet());
        for (Element elementRelationship : relationshipDestination) {
                return elementRelationship.getName();
        }
        return null;
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
        verifyContainerTechnology(container);
    }

    public void checksSomeFieldForComponent(Component component) {
        verifyElementDescription(component);
        verifyElementRelationshipDescription(component);
        verifyElementRelationshipTechnology(component);
        verifyComponentTechnology(component);
    }
}


package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;

import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModelLinter {
    public static final Logger logger = Logger.getLogger(ModelLinter.class.getName());

    public Optional verifyElementDescription(Element element) {
        Optional<String> elementDescription = Optional.ofNullable(element.getDescription());
        if (elementDescription.isEmpty()) {
            logger.log(Level.SEVERE,(String.format("your element %s should have a description. " +
                    "A lack of a description prevent user to understand the aim of the element. ", element.getName())));
        }
        return elementDescription;
    }

    public Optional verifyContainerTechnology(Container container) {
        Optional <String> containerTechnology = Optional.ofNullable(container.getTechnology());
        if (containerTechnology.isEmpty()) {
            logger.log(Level.SEVERE,(String.format("your container %s should have a technology. " +
                    "A lack of a technology prevent user to know which technology is associated to this element. ", container.getName())));
        }
        return containerTechnology;
    }

    public Optional verifyComponentTechnology(Component component) {
        Optional <String> componentTechnology = Optional.ofNullable(component.getTechnology());
        if (componentTechnology.isEmpty()) {
            logger.log(Level.SEVERE,(String.format("your component %s should have a technology. " +
                    "A lack of a technology prevent user to know which technology is used for this element. " +
                    "You should add technologies used on your component.", component.getName())));
        }
        return componentTechnology;
    }


    public Optional<Set<String>> verifyElementRelationshipDescription(Element element) {
        Set<String> relationshipDescriptions = element.getRelationships().stream().map(Relationship::getDescription).collect(Collectors.toSet());
        try {
            for (String description : relationshipDescriptions) {
                if(description.isEmpty() || relationshipDescriptions.contains(null))
                    logger.log(Level.SEVERE,(String.format("The description between element %s and element %s should specify description to help users to know which description is associated to this element." +
                            "Please specify description for this relationship.", element.getName(), getElementRelationshipName(element))));
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
                    logger.log(Level.SEVERE,(String.format("The technology used in relationship between element %s and element %s should specify technologies to help users to know which technology is associated to this element." +
                            "Please specify technologies used in these relationship.", element.getName(), getElementRelationshipName(element))));
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
}


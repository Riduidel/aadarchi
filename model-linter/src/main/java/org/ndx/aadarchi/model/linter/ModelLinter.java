package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Relationship;

import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class ModelLinter {
    public static final Logger logger = Logger.getLogger(ModelLinter.class.getName());

    public Optional<String> verifyElementDescription(Element element) {
        String description = element.getDescription();
        if (StringUtils.isBlank(description)) {
            logger.log(Level.SEVERE,(String.format("your element %s should have a description. " +
                    "A lack of a description prevent user to understand the aim of the element. ", element.getName())));
        }
        return Optional.ofNullable(description);
    }

    public Optional<String> verifyContainerTechnology(Container container) {
        String containerTechnology = container.getTechnology();
        if (StringUtils.isBlank(containerTechnology)) {
            logger.log(Level.SEVERE,(String.format("your container %s should have a technology. " +
                    "A lack of a technology prevent user to know which technology is associated to this element. ", container.getName())));
        }
        return Optional.ofNullable(containerTechnology);
    }

    public Optional<String> verifyComponentTechnology(Component component) {
        String componentTechnology = component.getTechnology();
        if (StringUtils.isBlank(componentTechnology)) {
            logger.log(Level.SEVERE,(String.format("your component %s should have a technology. " +
                    "A lack of a technology prevent user to know which technology is associated to this element. ", component.getName())));
        }
        return Optional.ofNullable(componentTechnology);
    }


    public Optional<Set<String>> verifyElementRelationshipDescription(Element element) {
        Set<String> relationshipDescriptions = element.getRelationships().stream().map(Relationship::getDescription).collect(Collectors.toSet());
            for (String description : relationshipDescriptions) {
                if(StringUtils.isBlank(description))
                    logger.log(Level.SEVERE,(String.format("The description between element %s and element %s should specify description to help " +
                            "users to know which description is associated to this element." +
                            "Please specify description for this relationship.", element.getName(), getElementRelationshipName(element))));
            }
        return Optional.of(relationshipDescriptions);
    }

    public Optional<Set<String>> verifyElementRelationshipTechnology(Element element) {
        Set<String> relationshipTechnologies = element.getRelationships().stream().map(Relationship::getTechnology).collect(Collectors.toSet());
            for (String technology : relationshipTechnologies) {
                if (StringUtils.isBlank(technology))
                    logger.log(Level.SEVERE,(String.format("The technology used in relationship between element %s and element %s should specify " +
                            "technologies to help users to know which technology is associated to this element." +
                            "Please specify technologies used in these relationship.", element.getName(), getElementRelationshipName(element))));
            }
        return Optional.of(relationshipTechnologies);
    }
    public String getElementRelationshipName(Element element) {
        Set<Element> relationshipDestination = element.getRelationships().stream().map(Relationship::getDestination).collect(Collectors.toSet());
        for (Element elementRelationship : relationshipDestination) {
                return elementRelationship.getName();
        }
        return null;
    }
}



package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Relationship;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@EnableWeld
public class ModelLinterTest extends AbstractModelLinterTest {

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();
    @Inject
    ModelLinter modelLinter;
    @Test
    public void element_should_have_description() {
    	// Given
    	Optional<String> elementDescriptions = Optional.of(container.getDescription());
    	// When
    	Optional<String> tested = modelLinter.verifyElementDescription(container);
    	// Then
		Assertions.assertThat(tested).isEqualTo(elementDescriptions);
    }

    @Test
    public void element_doesnt_have_description_then_return_logger() {
    	// Given
        String expectedLogMessage = "your element container3 should have a description. " +
                "A lack of a description prevent user to understand the aim of the element. ";
        // When
        modelLinter.verifyElementDescription(container3);
        // Then
        String logMessage = loggerHandler.getLogMessage();
        Assertions.assertThat(logMessage).isEqualTo(expectedLogMessage);
    }

    @Test
    public void container_have_a_technology() {
        //Given
        Optional<String> containerTechnology = Optional.of(container.getTechnology());
        //When
        Optional<String> tested = modelLinter.verifyContainerTechnology(container);
        //Then
        Assertions.assertThat(tested).isEqualTo(containerTechnology);
    }

    @Test
    public void component_have_a_technology() {
        //Given
        Optional<String> elementDescriptions = Optional.of(component.getTechnology());
        //When
        Optional<String> tested = modelLinter.verifyComponentTechnology(component);
        //Then
        Assertions.assertThat(tested).isEqualTo(elementDescriptions);
    }

    @Test
    public void container_doesnt_have_a_technology_then_return_logger() {
    	// Given
        String expectedLogMessage = "your container container3 should have a technology. " +
                "A lack of a technology prevent user to know which technology is associated to this element. ";
        // When
        modelLinter.verifyContainerTechnology(container3);
        // Then
        String logMessage = loggerHandler.getLogMessage();
        Assertions.assertThat(logMessage).isEqualTo(expectedLogMessage);
    }

    @Test
    public void component_doesnt_have_a_technology_then_return_logger() {
        // Given
        String expectedLogMessage = "your component component2 should have a technology. " +
                "A lack of a technology prevent user to know which technology is associated to this element. ";
        // When
        modelLinter.verifyComponentTechnology(component2);
        // Then
        String logMessage = loggerHandler.getLogMessage();
        Assertions.assertThat(logMessage).isEqualTo(expectedLogMessage);
    }


    @Test
    public void relationship_must_have_description() {
        //Given
        Set<Relationship> relationships =  container.getRelationships();
        Optional<Set<String>> description = Optional.of(relationships.stream().map(Relationship::getDescription).collect(Collectors.toSet()));
        //When
        Optional<Set<String>> tested = modelLinter.verifyElementRelationshipDescription(container);
        //Then
        Assertions.assertThat(tested).isEqualTo(description);
    }

    @Test
    public void relationship_doesnt_have_description_then_send_logger_info() {
        //Given
        String expectedLogMessage = "The description between element container2 and element container3 should specify description to " +
                "help users to know which description is associated to this element." +
                "Please specify description for this relationship.";
        //When
        modelLinter.verifyElementRelationshipDescription(container2);
        //Then
        String logMessage = loggerHandler.getLogMessage();
        Assertions.assertThat(logMessage).isEqualTo(expectedLogMessage);
    }

    @Test
    public void relationship_should_have_a_technology() {
        //Given
        Set<Relationship> relationships =  container.getRelationships();
        Optional<Set<String>> relationshipsTechnology = Optional.of(relationships.stream().map(Relationship::getTechnology).collect(Collectors.toSet()));
        //When
        Optional<Set<String>> tested = modelLinter.verifyElementRelationshipTechnology(container);
        //Then
        Assertions.assertThat(tested).isEqualTo(relationshipsTechnology);
    }

    @Test
    public void relationship_doesnt_have_technology_then_send_logger_info() {
    	// Given
        String expectedLogMessage = "The technology used in relationship between element container2 and element container3 should specify technologies to help users to " +
                "know which technology is associated to this element.Please specify technologies used in these relationship.";
        // When
        modelLinter.verifyElementRelationshipTechnology(container2);
        // Then
        String logMessage = loggerHandler.getLogMessage();
        Assertions.assertThat(logMessage).isEqualTo(expectedLogMessage);
    }
}



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
    	Optional tested = modelLinter.verifyElementDescription(container);
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
        loggerHandler.getLogRecordSourceMethodName("verifyElementDescription");
        String log = loggerHandler.getLogMessage();
        Assertions.assertThat(log).isEqualTo(expectedLogMessage);
    }

    @Test
    public void container_have_a_technology() {
        Optional<String> containerTechnology = Optional.of(container.getTechnology());
        Assertions.assertThat(containerTechnology).isEqualTo(modelLinter.verifyContainerTechnology(container));
    }

    @Test
    public void component_have_a_technology() {
        Optional<String> elementDescriptions = Optional.of(container.getTechnology());
        Assertions.assertThat(elementDescriptions).isEqualTo(modelLinter.verifyComponentTechnology(component));
    }

    @Test
    public void container_doesnt_have_a_technology_then_return_logger() {
    	// Given
        String expectedLogMessage = "your element container3 should have a technology. " +
                "A lack of a technology prevent user to understand the aim of the element. ";
        // When
        modelLinter.verifyContainerTechnology(container3);
        // Then
        loggerHandler.getLogRecordSourceMethodName("verifyContainerTechnology");
        String log = loggerHandler.getLogMessage();
        Assertions.assertThat(log).isEqualTo(expectedLogMessage);
    }


    @Test
    public void relationship_must_have_description() {
        Set<Relationship> relationships =  container.getRelationships();
        Optional<Set<String>> description = Optional.of(relationships.stream().map(Relationship::getDescription).collect(Collectors.toSet()));
        Assertions.assertThat(description).isEqualTo(modelLinter.verifyElementRelationshipDescription(container));
    }

    @Test
    public void relationship_doesnt_have_description_then_send_logger_info() {
        String expectedLogMessage = "The description between element container2 and element container3 should specify description to " +
                "help users to know which description is associated to this element." +
                "Please specify description for this relationship.";
        modelLinter.verifyElementRelationshipDescription(container2);
        //loggerHandler.getLogRecordSourceMethodName("verifyElementRelationshipDescription");
        String log = loggerHandler.getLogMessage();
        Assertions.assertThat(log).isEqualTo(expectedLogMessage);
    }

    @Test
    public void relationship_should_have_a_technology() {
        Set<Relationship> relationships =  container.getRelationships();
        Optional<Set<String>> relationshipsTechnology = Optional.of(relationships.stream().map(Relationship::getTechnology).collect(Collectors.toSet()));
        Assertions.assertThat(relationshipsTechnology).isEqualTo(modelLinter.verifyElementRelationshipTechnology(container));
    }

    @Test
    public void relationship_doesnt_have_technology_then_send_logger_info() {
    	// Given
        String expectedLogMessage = "The technology used in relationship between element container2 and element container3 should specify technologies to help users to " +
                "know which technology is associated to this element.Please specify technologies used in these relationship.";
        // When
        modelLinter.verifyElementRelationshipTechnology(container2);
        // Then
        loggerHandler.getLogRecordSourceMethodName("verifyElementRelationshipTechnology");
        String log = loggerHandler.getLogMessage();
        Assertions.assertThat(log).isEqualTo(expectedLogMessage);
    }
}


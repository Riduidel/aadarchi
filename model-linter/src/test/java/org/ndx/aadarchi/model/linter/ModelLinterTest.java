package org.ndx.aadarchi.model.linter;

import ch.qos.logback.classic.Level;
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
        Optional<String> elementDescriptions = Optional.of(container.getDescription());
        Assertions.assertThat(elementDescriptions).isEqualTo(modelLinter.verifyElementDescription(container));
    }

    @Test
    public void element_doesnt_have_description_then_return_logger() {
        ModelLinter modelLinter = new ModelLinter();
        modelLinter.verifyElementDescription(container2);
        MemoryAppender memoryAppender = new MemoryAppender();
        final String message = "your element %s should have a description. " +
                "A lack of a description prevent user to understand the aim of the element. You should add a description to your element";
        modelLinter.setLogger(message);
        Assertions.assertThat(memoryAppender.search(message, Level.ERROR).size()).isEqualTo(1);
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

    /*@Test
    public void container_doesnt_have_a_technology() {
        Assertions.assertThat("your container2 should have a technology").isEqualTo(modelLinter.verifyElementTechnology(component2));
    }*/


    @Test
    public void relationship_must_have_description() {
        Set<Relationship> relationships =  container.getRelationships();
        Optional<Set<String>> description = Optional.of(relationships.stream().map(Relationship::getDescription).collect(Collectors.toSet()));
        Assertions.assertThat(description).isEqualTo(modelLinter.verifyElementRelationshipDescription(container));
    }

    /*@Test
    public void relationship_doesnt_have_description_then_send_logger_info() {
        Relationship relationship = container2.getRelationships().iterator().next();
        String relationshipDescription = relationship.getDescription();
        Assertions.assertThat(relationshipDescription).isEqualTo(modelLinter.verifyElementRelationshipDescription(container2));
    }*/

    @Test
    public void relationship_should_have_a_technology() {
        Set<Relationship> relationships =  container.getRelationships();
        Optional<Set<String>> relationshipsTechnology = Optional.of(relationships.stream().map(Relationship::getTechnology).collect(Collectors.toSet()));
        Assertions.assertThat(relationshipsTechnology).isEqualTo(modelLinter.verifyElementRelationshipTechnology(container));
    }

    /*@Test
    public void relationship_doesnt_have_technology_then_send_logger_info() {
        String relationshipTechnology = container2.getRelationships().iterator().next().getTechnology();
        Assertions.assertThat("your element container2 should have a technology").isEqualTo(modelLinter.verifyElementRelationshipTechnology(container2));
    }*/
}


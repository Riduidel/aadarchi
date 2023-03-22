package org.ndx.aadarchi.model.linter;

import com.structurizr.model.Relationship;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

@EnableWeld
public class ModelLinterTest extends AbstractModelLinterTest {
 /*
    An element must have a description set
    A container/component must have a technology set
    A relationship must have a description set
    A relationship should have a technology set (as well as an interaction style)
     */

    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

    @Inject
    ModelLinter modelLinter;

    @Test
    public void element_should_have_description() {
        String systemDescription = system.getDescription();
        Assertions.assertThat(systemDescription).isEqualTo(modelLinter.verifyElementDescription(system));
    }

    @Test
    public void element_doesnt_have_description_then_return_logger() {
        Assertions.assertThat("your element container2 should have a description").isEqualTo(modelLinter.verifyElementDescription(container2));
    }

    @Test
    public void container_have_a_technology() {
        String containerTechnology = container.getTechnology();
        Assertions.assertThat(containerTechnology).isEqualTo(modelLinter.verifyElementTechnology(container));
    }

    @Test
    public void component_have_a_technology() {
        String containerTechnology = component.getTechnology();
        Assertions.assertThat(containerTechnology).isEqualTo(modelLinter.verifyElementTechnology(component));
    }

    @Test
    public void container_doesnt_have_a_technology() {
        Assertions.assertThat("your container2 should have a technology").isEqualTo(modelLinter.verifyElementTechnology(component2));
    }


    @Test
    public void relationship_must_have_description() {
        Relationship relationship = container.getRelationships().iterator().next();
        String relationshipDescription = relationship.getDescription();
        Assertions.assertThat(relationshipDescription).isEqualTo(modelLinter.verifyElementRelationshipDescription(container));
    }

    @Test
    public void relationship_doesnt_have_description_then_send_logger_info() {
        Relationship relationship = container2.getRelationships().iterator().next();
        String relationshipDescription = relationship.getDescription();
        Assertions.assertThat(relationshipDescription).isEqualTo(modelLinter.verifyElementRelationshipDescription(container2));
    }

    @Test
    public void relationship_should_have_a_technology() {
        String relationshipTechnology = container.getRelationships().iterator().next().getTechnology();
        Assertions.assertThat(relationshipTechnology).isEqualTo(modelLinter.verifyElementRelationshipTechnology(container));
    }

    @Test
    public void relationship_doesnt_have_technology_then_send_logger_info() {
        //String relationshipTechnology = container2.getRelationships().iterator().next().getTechnology();
        Assertions.assertThat("your element container2 should have a technology").isEqualTo(modelLinter.verifyElementRelationshipTechnology(container2));
    }
}


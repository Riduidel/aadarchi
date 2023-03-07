package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.structurizr.model.Relationship;

public class SipocModelTest extends AbstractSipocTest {

	@Test public void can_create_input_list() {
		// given
		SipocModel sipocModel = new SipocModel();
		// when
		// then
		Set<String> buildIncomingRelationship = Set.of(
				String.format("%s - %s", inputContainer.getName(), inputContainer.getDescription()));
		Assertions.assertThat(sipocModel.buildIncomingRelationships(centerContainer)).isEqualTo(buildIncomingRelationship);
	}

	@Test public void can_create_description_input_list() {
		// given
		SipocModel sipocModel = new SipocModel();
		Relationship relationship = inputContainer.getRelationships().iterator().next();
		// when
		// then
		Set<String> buildIncomingRelationshipDescription = Set.of(relationship.getDescription());
		Assertions.assertThat(sipocModel.buildIncomingRelationshipDescriptions(centerContainer)).isEqualTo(buildIncomingRelationshipDescription);
	}

	@Test public void can_create_process_description_list() {
		String buildProcessDescription = centerContainer.getDescription();
		SipocModel sipocModel = new SipocModel();
		Assertions.assertThat(sipocModel.buildProcessDescriptions(centerContainer)).isEqualTo(buildProcessDescription);
	}

    @Test public void can_create_outgoing_relationships_description() {
        Set<String> buildOutgoingRelationshipDescriptions = Set.of(outputContainer.getDescription());
        SipocModel sipocModel = new SipocModel();
        Assertions.assertThat(sipocModel.buildOutgoingRelationshipDescriptions(centerContainer)).isEqualTo(buildOutgoingRelationshipDescriptions);
    }

    @Test public void can_create_outgoing_relationships() {
        Set<String> buildOutgoingRelationships = Set.of(outputContainer.getName());
        SipocModel sipocModel = new SipocModel();
        Assertions.assertThat(sipocModel.buildOutgoingRelationships(centerContainer)).isEqualTo(buildOutgoingRelationships);
    }
}

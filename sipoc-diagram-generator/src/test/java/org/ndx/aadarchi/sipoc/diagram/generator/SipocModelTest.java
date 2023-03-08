package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.structurizr.model.Relationship;

public class SipocModelTest extends AbstractSipocTest {

	@Test public void can_create_input_list() {
		// given
		SipocModel sipocModel = new SipocModel();
		Set<String> buildIncomingRelationship = Set.of(
				String.format("%s - %s", inputContainer.getName(), inputContainer.getDescription()));
		// when
		// then
		Assertions.assertThat(sipocModel.buildIncomingRelationships(centerContainer)).isEqualTo(buildIncomingRelationship);
	}

	@Test public void can_create_description_input_list() {
		// given
		SipocModel sipocModel = new SipocModel();
		Relationship relationship = inputContainer.getRelationships().iterator().next();
		Set<String> buildIncomingRelationshipDescription = Set.of(relationship.getDescription());
		// when
		// then
		Assertions.assertThat(sipocModel.buildIncomingRelationshipDescriptions(centerContainer)).isEqualTo(buildIncomingRelationshipDescription);
	}

	@Test public void can_create_process_description_list() {
		String buildProcessDescription = centerContainer.getDescription();
		SipocModel sipocModel = new SipocModel();
		Assertions.assertThat(sipocModel.buildProcessDescriptions(centerContainer)).isEqualTo(buildProcessDescription);
	}

    @Test public void can_create_outgoing_relationships_description() {
		// given
		SipocModel sipocModel = new SipocModel();
		for (Relationship relationship : outputContainer.getRelationships()) {
			// when
			// then
			Set<String> buildOutgoingRelationshipDescription = Set.of(relationship.getDescription());
			Assertions.assertThat(sipocModel.buildOutgoingRelationshipDescriptions(centerContainer)).isEqualTo(buildOutgoingRelationshipDescription);
		}
    }

    @Test public void can_create_outgoing_relationships() {
		// given
		SipocModel sipocModel = new SipocModel();
		// when
		// then
		Set<String> buildOutgoingRelationships = Set.of(
				String.format("%s - %s", outputContainer.getName(), outputContainer.getDescription()));
		Assertions.assertThat(sipocModel.buildOutgoingRelationships(centerContainer)).isEqualTo(buildOutgoingRelationships);
    }

	@Test public void can_create_multiple_input_list() {
		// given
		SipocModel sipocModel = new SipocModel();
		Set<String> buildIncomingRelationship = Set.of(
				String.format("%s - %s", inputContainer1.getName(), inputContainer1.getDescription()),
				String.format("%s - %s", inputContainer2.getName(), inputContainer2.getDescription()));
		// when
		// then
		Assertions.assertThat(sipocModel.buildIncomingRelationships(centerContainer1)).isEqualTo(buildIncomingRelationship);
	}

	@Test public void can_create_multiple_output_list() {
		// given
		SipocModel sipocModel = new SipocModel();
		Set<String> buildOutgoingRelationship = Set.of(
				String.format("%s - %s", outputContainer1.getName(), outputContainer1.getDescription()),
				String.format("%s - %s", outputContainer2.getName(), outputContainer2.getDescription()));
		// when
		// then
		Assertions.assertThat(sipocModel.buildOutgoingRelationships(centerContainer1)).isEqualTo(buildOutgoingRelationship);
	}
}

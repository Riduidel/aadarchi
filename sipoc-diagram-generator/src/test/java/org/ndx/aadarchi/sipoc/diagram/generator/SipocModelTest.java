package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SipocModelTest extends AbstractSipocTest {

	@Test public void can_create_input_list() {
		Set<String> buildIncomingRelationship = Set.of(inputContainer.getName());
		SipocModel sipocModel = new SipocModel();
		Assertions.assertThat(sipocModel.buildIncomingRelationships(centerContainer)).isEqualTo(buildIncomingRelationship);
	}

	@Test public void can_create_description_input_list() {
		Set<String> buildIncomingRelationshipDescription = Set.of(inputContainer.getDescription());
		SipocModel sipocModel = new SipocModel();
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

package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SipocModelTest extends AbstractSipocTest {

	@Test public void can_create_input_list() {
		Set<String> buildIncomingRelationship = Set.of(inputContainer.getDescription());
		// Should the model knowledge be added here by, as an example, setting the model as a parameter here?
		SipocModel sipocModel = new SipocModel();
		Assertions.assertThat(sipocModel.buildIncomingRelationship(centerContainer)).isEqualTo(buildIncomingRelationship);
	}
	@Test
	public void can_create_sipoc_model() {
		Set<String> buildIncomingRelationship = Set.of(inputContainer.getDescription());
		Set<String> buildIncomingRelationshipDescription = Set.of(SipocTestUtils.CONNECTS_INPUT_TO_CENTER);
		String buildProcessDescription = centerContainer.getDescription();
		Set<String> buildOutgoingRelationships = Set.of(outputContainer.getDescription());
		Set<String> buildOutgoingRelationshipDescriptions = Set.of(SipocTestUtils.CONNECTS_CENTER_TO_OUTPUT);
		
		// Should the model knowledge be added here by, as an example, setting the model as a parameter here?
		SipocModel sipocModel = new SipocModel();

		Assertions.assertThat(sipocModel.buildIncomingRelationshipDescriptions(centerContainer)).isEqualTo(buildIncomingRelationshipDescription);
		Assertions.assertThat(sipocModel.buildProcessDescriptions(centerContainer)).isEqualTo(buildIncomingRelationship);
		Assertions.assertThat(sipocModel.buildOutgoingRelationships(centerContainer)).isEqualTo(buildOutgoingRelationships);
		Assertions.assertThat(sipocModel.buildOutgoingRelationshipDescriptions(centerContainer)).isEqualTo(buildOutgoingRelationshipDescriptions);

		String sipoc = sipocModel.generateSipocDiagram(centerContainer);

		Assertions.assertThat(sipoc).isEqualTo("[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" + "\n|===" +
				buildIncomingRelationship +
				buildIncomingRelationshipDescription +
				buildProcessDescription +
				buildOutgoingRelationships +
				buildOutgoingRelationshipDescriptions);
	}

}

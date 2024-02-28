package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Arrays;
import java.util.List;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

import static org.ndx.aadarchi.sipoc.diagram.generator.SipocTestUtils.*;

class SipocEnhancerTest extends AbstractSipocTest {
    @Inject SipocEnhancer sipocEnhancer;
	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

	@Test
	void test() {
    	// Given
		// When
		enhancer.enhance(workspace, sipocEnhancer);
		// Then
		FileObject outputFolderForSystem = enhancer.getOutputBuilder()
				.outputFor(AgileArchitectureSection.code, centerContainer, sipocEnhancer, OutputBuilder.Format.adoc);
		Assertions.assertThat(outputFolderForSystem.getPath())
			.isNotEmptyFile();
	}


	@Test
	public void can_create_a_sipoc_table() {
		//given
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		//when
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(centerContainer);
		//then
		Assertions.assertThat(sipocEnhancerTable).isEqualTo(
				"[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" +
						"| *"+ inputContainer.getName() + "* " + inputContainer.getDescription() +
						"|" + CONNECTS_INPUT_TO_CENTER +
						"|"+ centerContainer.getDescription() +
						"|" + CONNECTS_CENTER_TO_OUTPUT +
						"| *"+ outputContainer.getDescription() + "* " + outputContainer.getName() +
						"\n|===");
	}

	@Test
	public void can_create_a_sipoc_table_with_multiple_inputs_and_outputs() {
		//given
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		//when
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(centerContainer1);
		//then
		Assertions.assertThat(sipocEnhancerTable).isEqualTo(
				"[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" +
						"| *"+ inputContainer1.getName() + "* " + inputContainer1.getDescription() + "\n\n" + 
						" *" + inputContainer2.getName() + "* " + inputContainer2.getDescription()+
						"|" + CONNECTS_INPUT_TO_CENTER + "\n\n" + CONNECTS_INPUT2_TO_CENTER +
						"|"+ centerContainer1.getDescription() +
						"|" + CONNECTS_CENTER_TO_OUTPUT + "\n\n" + CONNECTS_CENTER_TO_OUTPUT2 +
						"| *"+ outputContainer1.getDescription() + "* " + outputContainer1.getName() + "\n\n" +
						" *" + outputContainer2.getName() + "* " + outputContainer2.getDescription() +
						"\n|===");
	}
	
	@Test
	public void do_not_generate_sipoc_table_when_there_are_no_connections() {
		// Given
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		// When
		String sipocTableForSystem = sipocEnhancer.generateSipocDiagram(system);
		// Then
		Assertions.assertThat(sipocTableForSystem).isEmpty();
	}
}

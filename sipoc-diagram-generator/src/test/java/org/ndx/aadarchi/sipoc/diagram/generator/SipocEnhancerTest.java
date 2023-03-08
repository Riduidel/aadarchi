package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Arrays;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;

import static org.ndx.aadarchi.sipoc.diagram.generator.SipocTestUtils.*;

class SipocEnhancerTest extends AbstractSipocTest {
    @Inject SipocEnhancer sipocEnhancer;

	@Inject SipocEnhancer sipocEnhancer1;
	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

	@Test
	void test() {
    	// Given
		// When
		enhancer.enhance(workspace, Arrays.asList(sipocEnhancer));
		// Then
		FileObject outputFolderForSystem = enhancer.getOutputBuilder()
				.outputFor(AgileArchitectureSection.code, system, sipocEnhancer, OutputBuilder.Format.adoc);
		Assertions.assertThat(outputFolderForSystem.getPath())
			.isNotEmptyFile();
	}


	@Test
	public void can_create_a_sipoc_table() {
		//given
		// What is this ?
		//when
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(centerContainer);
		sipocEnhancer.generateSipocDiagram(centerContainer);
		//then
		Assertions.assertThat(sipocEnhancerTable).isEqualTo(
				"[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" +
						"|"+ inputContainer.getName() + " - " + inputContainer.getDescription() +
						"|" + CONNECTS_INPUT_TO_CENTER +
						"|"+ centerContainer.getDescription() +
						"|" + CONNECTS_CENTER_TO_OUTPUT +
						"|"+ outputContainer.getDescription() + " - " + outputContainer.getName() +
						"\n|===");
	}

	@Test
	public void can_create_a_sipoc_table_with_multiple_inputs() {
		//given
		// What is this ?
		//when
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(centerContainer1);
		sipocEnhancer.generateSipocDiagram(centerContainer1);
		//then
		Assertions.assertThat(sipocEnhancerTable).isEqualTo(
				"[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" +
						"|"+ (inputContainer1.getName() + " - " + inputContainer1.getDescription() + inputContainer2.getName() + " - " + inputContainer2.getDescription())+
						"|" + CONNECTS_INPUT_TO_CENTER + CONNECTS_INPUT_TO_CENTER +
						"|"+ centerContainer1.getDescription() +
						"|" + CONNECTS_CENTER_TO_OUTPUT + CONNECTS_CENTER_TO_OUTPUT +
						"|"+ outputContainer1.getDescription() + " - " + outputContainer1.getName() + outputContainer2.getName() + " - " + outputContainer2.getDescription() +
						"\n|===");
	}
}

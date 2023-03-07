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

class SipocEnhancerTest extends AbstractSipocTest {
    @Inject SipocEnhancer sipocEnhancer;

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
		class sipocEnhancer extends ModelElementAdapter {
			@Override
			public int priority() {
				return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS - 1;
			}
		}
		//when
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(centerContainer);
		sipocEnhancer.generateSipocDiagram(centerContainer);
		//then
		Assertions.assertThat(sipocEnhancerTable).isEqualTo(
				"[cols=\"1,1,1,1,1\"]\n" + "|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" +
						"|"+ inputContainer.getName() +
						"|"+ inputContainer.getDescription() +
						"|"+ centerContainer.getDescription() +
						"|"+ outputContainer.getDescription() +
						"|"+ outputContainer.getName());
	}
}

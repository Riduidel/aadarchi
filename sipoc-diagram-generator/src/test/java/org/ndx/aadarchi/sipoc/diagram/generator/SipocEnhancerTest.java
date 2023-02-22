package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.structurizr.model.Component;
import com.structurizr.model.Element;

import org.apache.commons.vfs2.FileObject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.Enhancer;
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
		Assertions.assertThat(sipocEnhancerTable).isEqualTo("[cols=\"1,1,1,1,1\"]\n" +
				"|===\n" +
				"|Incoming|Input|Process|Output|Outgoing\n" +
				"\n" +
				"|[]|[]|Base module of a\n" +
				"set of tools created\n" +
				"to allow easy\n" +
				"architecture\n" +
				"documentation\n" +
				"based upon Simon\n" +
				"Brown work.|[]|[]\n" +
				"|===");
	}
}

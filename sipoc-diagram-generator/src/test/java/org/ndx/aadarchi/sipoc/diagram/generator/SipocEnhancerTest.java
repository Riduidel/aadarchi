package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Arrays;

import javax.inject.Inject;

import com.structurizr.model.Element;
import org.apache.commons.vfs2.FileObject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.BasePath;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
class SipocEnhancerTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

	@Inject SipocEnhancer sipocEnhancer;

	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

	@Inject Element element;

	@Test
	void test() {
    	// Given
    	var w = new Workspace(getClass().getName(), "a test workspace");
    	SoftwareSystem system = w.getModel().addSoftwareSystem("The system to decorate with maven informations");
		// When
		enhancer.enhance(w, Arrays.asList(sipocEnhancer));
		// Then
		FileObject outputFolderForSystem = enhancer.getOutputBuilder()
				.outputFor(AgileArchitectureSection.code, system, sipocEnhancer, OutputBuilder.Format.adoc);
		Assertions.assertThat(outputFolderForSystem.getPath())
			.isNotEmptyFile();
	}

	@Test
	public void can_create_a_sipoc_table() {
		//given
		class sipocEnhancer extends ModelElementAdapter {
			@Override
			public int priority() {
				return 0;
			}
		}
		//when
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(element);
		sipocEnhancer.generateSipocDiagram(element);
		//then
		org.junit.jupiter.api.Assertions.assertEquals(sipocEnhancerTable, "[cols=\"1,1,1,1,1\"]\n" +
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

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
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.Enhancer;
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

	@Inject SipocModel sipocModel;

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
	public void can_create_sipoc_model() {
		Set<String> buildIncomingRelationship = new HashSet<>();
		Set<String> buildIncomingRelationshipDescription = new HashSet<>();
		String buildProcessDescription = "Generates SIPOC (suppliers/inputs/process/outputs/consumers) diagrams in an asciidoc table, for each element.";
		Set<String> buildOutgoingRelationships = new HashSet<>(Arrays.asList("aadarchi-test-utils", "base"));
		Set<String> buildOutgoingRelationshipDescriptions = new HashSet<>(Arrays.asList("Some test utilities dedicated to the improvement of Aadarchi quality. Mainly contains tools allowing easy injection of Maven properties into tests","Base module defining the various used interfaces, and some very useful implementations"));

		Assertions.assertThat(sipocModel.buildOutgoingRelationships(element)).isEqualTo(buildIncomingRelationship);
		Assertions.assertThat(sipocModel.buildIncomingRelationshipDescriptions(element)).isEqualTo(buildIncomingRelationshipDescription);
		Assertions.assertThat(sipocModel.buildProcessDescriptions(element)).isEqualTo(buildIncomingRelationship);
		Assertions.assertThat(sipocModel.buildOutgoingRelationships(element)).isEqualTo(buildOutgoingRelationships);
		Assertions.assertThat(sipocModel.buildOutgoingRelationshipDescriptions(element)).isEqualTo(buildOutgoingRelationshipDescriptions);

		String sipoc = sipocModel.generateSipocDiagram(element);

		Assertions.assertThat(sipoc).isEqualTo("[cols=\"1,1,1,1,1\"]\n" + "|===\n|Incoming|Input|Process|Output|Outgoing\n\n\n\n\n" + "\n|===" +
				buildIncomingRelationship +
				buildIncomingRelationshipDescription +
				buildProcessDescription +
				buildOutgoingRelationships +
				buildOutgoingRelationshipDescriptions);
	}

	@Test
	public void can_create_a_sipoc_table() {
		//given
		class sipocEnhancer extends ModelElementAdapter {
			@Override
			public int priority() {
				return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS - 1;
			}
		}
		//when
		SipocEnhancer sipocEnhancer = new SipocEnhancer();
		String sipocEnhancerTable = sipocEnhancer.generateSipocDiagram(element);
		sipocEnhancer.generateSipocDiagram(element);
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

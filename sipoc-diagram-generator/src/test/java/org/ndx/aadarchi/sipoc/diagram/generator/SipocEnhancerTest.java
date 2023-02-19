package org.ndx.aadarchi.sipoc.diagram.generator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import org.apache.commons.vfs2.FileObject;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.assertj.core.api.Assertions;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;
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
    private static final String CONNECTS_CENTER_TO_OUTPUT = "connects center to output";

	private static final String CONNECTS_INPUT_TO_CENTER = "connects input to center";

	@WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

	@Inject SipocEnhancer sipocEnhancer;

	@Inject ArchitectureEnhancer enhancer;
	
	@Inject @ConfigProperty(name=BasePath.NAME, defaultValue = BasePath.VALUE) FileObject basePath;

	private SoftwareSystem system;

	private Workspace workspace;

	private Container inputContainer;

	private Container centerContainer;

	private Container outputContainer;

	@BeforeEach
	void createModel() {
    	workspace = new Workspace(getClass().getName(), "a test workspace");
    	system = workspace.getModel().addSoftwareSystem("The system to decorate with maven informations");
    	inputContainer = system.addContainer("Input container"); inputContainer.setDescription("Input container");
    	centerContainer = system.addContainer("Center container"); centerContainer.setDescription("Center container");
    	outputContainer = system.addContainer("Output container"); outputContainer.setDescription("Output container");
    	
    	inputContainer.uses(centerContainer, CONNECTS_INPUT_TO_CENTER);
    	centerContainer.uses(outputContainer, CONNECTS_CENTER_TO_OUTPUT);
	}
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
	public void can_create_sipoc_model() {
		Set<String> buildIncomingRelationship = Set.of(inputContainer.getDescription());
		Set<String> buildIncomingRelationshipDescription = Set.of(CONNECTS_INPUT_TO_CENTER);
		String buildProcessDescription = centerContainer.getDescription();
		Set<String> buildOutgoingRelationships = Set.of(outputContainer.getDescription());
		Set<String> buildOutgoingRelationshipDescriptions = Set.of(CONNECTS_CENTER_TO_OUTPUT);
		
		// Should the model knowledge be added here by, as an example, setting the model as a parameter here?
		SipocModel sipocModel = new SipocModel();

		Assertions.assertThat(sipocModel.buildIncomingRelationship(centerContainer)).isEqualTo(buildIncomingRelationship);
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

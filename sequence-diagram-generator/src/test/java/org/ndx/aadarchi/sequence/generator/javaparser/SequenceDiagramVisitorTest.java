package org.ndx.aadarchi.sequence.generator.javaparser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.assertj.core.api.AssertionsForClassTypes;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ndx.aadarchi.base.ArchitectureEnhancer;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.SimpleOutputBuilder;
import org.ndx.aadarchi.base.utils.StructurizrUtils;
import org.ndx.aadarchi.sequence.generator.SequenceGenerator;
import org.ndx.aadarchi.sequence.generator.javaparser.adapter.CallGraphModel;
import org.ndx.aadarchi.sequence.generator.javaparser.generator.SequenceDiagramGenerator;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
class SequenceDiagramVisitorTest {
    @WeldSetup
    public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

	@Inject ArchitectureEnhancer enhancer;
	@Inject SequenceDiagramVisitor tested;
	@Test @Disabled
	void can_read_a_model() {
		// Given all those elements
		Workspace workspace = new Workspace("test workspace", "test workspace");
		Model model = workspace.getModel();
		SoftwareSystem system = model.addSoftwareSystem("aadarchi-documentation-system");
		Container sequenceGenerator = system.addContainer("sequence-generator", "generator", "Java/Maven");
		sequenceGenerator.addProperty(SequenceGenerator.GENERATES_WITH, StructurizrUtils.getCanonicalPath(sequenceGenerator));
		sequenceGenerator.addProperty(ModelElementKeys.JAVA_SOURCES, new File("src/main/java").toURI().toString());
		Component visitor = sequenceGenerator.addComponent(SequenceDiagramVisitor.class.getSimpleName(), "visitor building the sequence");
		visitor.addSupportingType(SequenceDiagramVisitor.class.getName());
		Component callGraphModel = sequenceGenerator.addComponent(CallGraphModel.class.getSimpleName(), "navigator accumulating data");
		callGraphModel.addSupportingType(CallGraphModel.class.getName());
		Component generator = sequenceGenerator.addComponent(SequenceDiagramGenerator.class.getSimpleName(), "Component producing the sequence diagram");
		generator.addSupportingType(SequenceDiagramGenerator.class.getName());
		// Let's build a sequence diagram visitor
		// Emulate the standard visit
		enhancer.enhance(workspace, Arrays.asList(tested));
		assertThat(tested.allContainers).hasSize(1);
		assertThat(tested.codeToComponents).hasSize(3);
		assertThat(tested.callGraphModel).isNull();
		// And finally, visit the container and parse source code
		assertThat(tested.callGraphModel).isNotNull();
		// And visit components to see what happens
		// And that something is generated
//		AssertionsForClassTypes.assertThat(tested.destination).isDirectoryContaining(file -> file.getName().equals(system.getName()));
	}

}

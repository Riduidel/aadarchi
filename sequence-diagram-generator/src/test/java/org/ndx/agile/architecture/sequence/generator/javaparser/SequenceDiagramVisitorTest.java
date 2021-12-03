package org.ndx.agile.architecture.sequence.generator.javaparser;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.logging.Logger;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;
import org.ndx.agile.architecture.base.utils.SimpleOutputBuilder;
import org.ndx.agile.architecture.base.utils.StructurizrUtils;
import org.ndx.agile.architecture.sequence.generator.SequenceGenerator;
import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.CallGraphModel;
import org.ndx.agile.architecture.sequence.generator.javaparser.generator.SequenceDiagramGenerator;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

class SequenceDiagramVisitorTest {

	@Test @Disabled
	void can_read_a_model() {
		// Given all those elements
		Workspace workspace = new Workspace("test workspace", "test workspace");
		Model model = workspace.getModel();
		SoftwareSystem system = model.addSoftwareSystem("agile-architecture-documentation-system");
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
		SequenceDiagramVisitor tested = new SequenceDiagramVisitor();
		tested.destination = new File("target/tests/"+getClass().getSimpleName());
		// Emulate the standard visit
		tested.startVisit(model);
		tested.startVisit(system);
		assertThat(tested.allContainers).hasSize(1);
		assertThat(tested.codeToComponents).hasSize(3);
		assertThat(tested.callGraphModel).isNull();
		// And finally, visit the container and parse source code
		tested.startVisit(sequenceGenerator);
		assertThat(tested.callGraphModel).isNotNull();
		// And visit components to see what happens
		assertThat(tested.startVisit(visitor)).isTrue();
		// And that something is generated
		tested.endVisit(visitor, new SimpleOutputBuilder(tested.destination));
		assertThat(tested.destination).isDirectoryContaining(file -> file.getName().equals(system.getName()));
	}

}

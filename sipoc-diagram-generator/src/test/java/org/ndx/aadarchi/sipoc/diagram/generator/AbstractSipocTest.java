package org.ndx.aadarchi.sipoc.diagram.generator;

import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.BeforeEach;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

@EnableWeld
public abstract class AbstractSipocTest {

	@WeldSetup
	public WeldInitiator weld = WeldInitiator.performDefaultDiscovery();

	protected SoftwareSystem system;
	protected SoftwareSystem system2;
	protected Workspace workspace;
	Container inputContainer;
	Container inputContainer1;
	Container inputContainer2;
	protected Container centerContainer;
	protected Container centerContainer1;
	Container outputContainer;
	Container outputContainer1;
	Container outputContainer2;

	@BeforeEach
	void createModel() {
		workspace = SipocTestUtils.createTestWorkspace();
		Model model = workspace.getModel();
		system = model.getSoftwareSystems().iterator().next();
		inputContainer = system.getContainerWithName("Input container");
		centerContainer = system.getContainerWithName("Center container");
		outputContainer = system.getContainerWithName("Output container");


		system2 = model.getSoftwareSystemWithName("The system2 to decorate with maven informations");
		assert system2 != null;
		inputContainer1 = system2.getContainerWithName("Input container1");
		inputContainer2 = system2.getContainerWithName("Input container2");
		centerContainer1 = system2.getContainerWithName("Center container1");
		outputContainer1 = system2.getContainerWithName("Output container1");
		outputContainer2 = system2.getContainerWithName("Output container2");

	}
}

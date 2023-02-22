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
	protected Workspace workspace;
	Container inputContainer;
	protected Container centerContainer;
	Container outputContainer;

	@BeforeEach
	void createModel() {
		workspace = SipocTestUtils.createTestWorkspace();
		Model model = workspace.getModel();
		system = model.getSoftwareSystems().iterator().next();
		inputContainer = system.getContainerWithName("Input container");
		centerContainer = system.getContainerWithName("Center container");
		outputContainer = system.getContainerWithName("Output container");
	}

}

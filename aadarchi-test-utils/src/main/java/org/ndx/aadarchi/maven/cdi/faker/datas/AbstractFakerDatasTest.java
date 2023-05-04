package org.ndx.aadarchi.maven.cdi.faker.datas;


import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;
import org.junit.jupiter.api.BeforeEach;

public abstract class AbstractFakerDatasTest {

	public SoftwareSystem system;
	public SoftwareSystem system2;

	public Workspace workspace;
	public Container inputContainer;
	public Container inputContainer1;
	public Container inputContainer2;
	public Container centerContainer;
	public Container centerContainer1;
	public Container outputContainer;
	public Container outputContainer1;
	public Container outputContainer2;

	@BeforeEach
	void createModel() {
		workspace = FakerDatasUtils.createTestWorkspace();
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

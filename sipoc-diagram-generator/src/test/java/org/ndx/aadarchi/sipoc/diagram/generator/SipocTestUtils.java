package org.ndx.aadarchi.sipoc.diagram.generator;

import com.structurizr.Workspace;

public class SipocTestUtils {

	public static Workspace createTestWorkspace() {
		Workspace workspace = new Workspace(SipocTestUtils.class.getName(), "a test workspace");

		var system = workspace.getModel().addSoftwareSystem("The system to decorate with maven informations");
		var inputContainer = system.addContainer("Input container", "Input container");
		var centerContainer = system.addContainer("Center container", "Center container");
		var outputContainer = system.addContainer("Output container", "Output container");

		inputContainer.uses(centerContainer, SipocTestUtils.CONNECTS_INPUT_TO_CENTER);
		centerContainer.uses(outputContainer, SipocTestUtils.CONNECTS_CENTER_TO_OUTPUT);


		var system2 = workspace.getModel().addSoftwareSystem("The system2 to decorate with maven informations");
		var inputContainer1 = system2.addContainer("Input container1", "Input container1");
		var inputContainer2 = system2.addContainer("Input container2", "Input container2");
		var centerContainer1 = system2.addContainer("Center container1", "Center container1");
		var outputContainer1 = system2.addContainer("Output container1", "Output container1");
		var outputContainer2 = system2.addContainer("Output container2", "Output container2");

		inputContainer1.uses(centerContainer1, SipocTestUtils.CONNECTS_INPUT_TO_CENTER);
		inputContainer2.uses(centerContainer1, SipocTestUtils.CONNECTS_INPUT2_TO_CENTER);
		inputContainer1.uses(centerContainer1, SipocTestUtils.CONNECTS_INPUT_TO_CENTER);
		centerContainer1.uses(outputContainer1, SipocTestUtils.CONNECTS_CENTER_TO_OUTPUT);
		centerContainer1.uses(outputContainer1, SipocTestUtils.CONNECTS_CENTER_TO_OUTPUT);
		centerContainer1.uses(outputContainer2, SipocTestUtils.CONNECTS_CENTER_TO_OUTPUT2);

		return workspace;
	}
	static final String CONNECTS_INPUT_TO_CENTER = "connects input to center";
	static final String CONNECTS_INPUT2_TO_CENTER = "connects input2 to center";
	static final String CONNECTS_CENTER_TO_OUTPUT = "connects center to output";
	static final String CONNECTS_CENTER_TO_OUTPUT2 = "connects center to output2";

}

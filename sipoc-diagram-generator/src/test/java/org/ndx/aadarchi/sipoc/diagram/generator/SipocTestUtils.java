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
		return workspace;
	}

	static final String CONNECTS_INPUT_TO_CENTER = "connects input to center";
	static final String CONNECTS_CENTER_TO_OUTPUT = "connects center to output";
}

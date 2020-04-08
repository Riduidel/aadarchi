package org.ndx.agile.architecture.example;

import java.io.IOException;

import org.ndx.agile.architecture.base.AbstractArchitecture;

import com.structurizr.Workspace;
import com.structurizr.model.Location;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

public class Architecture extends AbstractArchitecture {

	/**
	 * Main method simply starts the {@link Architecture#run()} method after having injected all parameters
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws Throwable {
		AbstractArchitecture.main(Architecture.class, args);
	}

	/**
	 * Creates the workspace object and add in it both the architecture components
	 * AND the views used to display it
	 * 
	 * @return
	 */
	protected Workspace describeArchitecture() {
		Workspace workspace = new Workspace("Getting Started", "This is a model of my software system.");
		Model model = workspace.getModel();

		Person waiting = model.addPerson("Waiting person", "Someone waiting for his transport.");
		Person inTrain = model.addPerson("Person in transport", "Someone already in the transport.");
		SoftwareSystem softwareSystem = model.addSoftwareSystem("kafkatrain", "Crowd-sourced transport timetable prediction system");
		waiting.uses(softwareSystem, "See train delay");
		inTrain.uses(softwareSystem, "Informs application that train is running");
		SoftwareSystem navitia = model.addSoftwareSystem(Location.External, "navitia", "Official train time-table");
		SoftwareSystem sncfLocation = model.addSoftwareSystem(Location.External, "SNCF geolocation", "SNCF train real-time location");
		softwareSystem.uses(navitia, "Get official time table");
		softwareSystem.uses(sncfLocation, "Get real-time train position");

		ViewSet views = workspace.getViews();
		SystemContextView contextView = views.createSystemContextView(softwareSystem, "SystemContext",
				"Systems involved in train prediction");
		contextView.addAllSoftwareSystems();
		contextView.addAllPeople();

//		Styles styles = views.getConfiguration().getStyles();
//		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
//		styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
		return workspace;
	}

}

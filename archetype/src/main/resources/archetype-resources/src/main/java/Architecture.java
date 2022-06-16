package ${package};

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;

import org.ndx.aadarchi.base.ArchitectureModelProvider;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

@ApplicationScoped
public class Architecture implements ArchitectureModelProvider {

	public static final String SOFTWARE_SYSTEM_TARGET = "Software System";
	public static final String CONTAINER_EXAMPLE = "TODO";
	public static final String PERSON_USER = "User";

	/**
	 * Creates the workspace object and add in it both the architecture components
	 * AND the views used to display it
	 * 
	 * @return
	 */
	public Workspace describeArchitecture() {
		Workspace workspace = new Workspace("Getting Started", "This is a model of my software system.");
		Model model = workspace.getModel();

		Person user = model.addPerson(PERSON_USER, "A user of my software system.");
		SoftwareSystem softwareSystem = model.addSoftwareSystem(SOFTWARE_SYSTEM_TARGET, "My software system.");
		user.uses(softwareSystem, "Uses");

		Container aContainer = softwareSystem.addContainer(CONTAINER_EXAMPLE, "An example container", "What technology do you use?");
		user.uses(aContainer, "Do something");
		return workspace;
	}

}

package ${package};

import java.io.IOException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ndx.agile.architecture.base.ArchitectureModelProvider;
import org.ndx.agile.architecture.base.Enhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.myCompany.mySystem.architecture.Architecture;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.Person;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

@ApplicationScoped
public class ViewsGenerator implements Enhancer {
	@Inject Logger logger;

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public int priority() {
		return 10_000;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return true;
	}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {
		ViewSet views = workspace.getViews();
		Model model = workspace.getModel();
		logger.info("Adding a system context view");
		SoftwareSystem softwareSystem = model.getSoftwareSystemWithName(Architecture.SOFTWARE_SYSTEM_TARGET);
		SystemContextView contextView = views.createSystemContextView(softwareSystem, "SystemContext",
				"An example of a System Context diagram.");
		contextView.addAllSoftwareSystems();
		contextView.addAllPeople();

		logger.info("Adding a containers view");
		ContainerView softwareSystemContainers = views.createContainerView(softwareSystem, "software.system.containers", "Software system containers");
		softwareSystemContainers.addAllContainersAndInfluencers();

//		Styles styles = views.getConfiguration().getStyles();
//		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
//		styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
	}
}

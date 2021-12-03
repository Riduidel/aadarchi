package org.ndx.agile.architecture.documentation.system;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.kohsuke.MetaInfServices;
import org.ndx.agile.architecture.base.Enhancer;
import org.ndx.agile.architecture.base.OutputBuilder;

import com.structurizr.Workspace;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.ViewSet;

@MetaInfServices
public class ViewsGenerator implements Enhancer {

	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS-1;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return false;
	}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {
		ViewSet views = workspace.getViews();
		SoftwareSystem agileArchitecture = workspace.getModel().getSoftwareSystemWithName(Architecture.AGILE_ARCHITECTURE_DOCUMENTATION);
		SystemContextView contextView = views.createSystemContextView(agileArchitecture, "SystemContext",
				"Illustration of agile-architecture-documentation usage");
		contextView.addAllSoftwareSystems();
		contextView.addAllPeople();

		ContainerView agileArchitectureContainers = views.createContainerView(agileArchitecture, "agile.architecture.containers", "Agile architecture containers");
		agileArchitectureContainers.addAllContainersAndInfluencers();
		
		ComponentView agileArchitectureBaseComponents = views.createComponentView(agileArchitecture.getContainerWithName(Architecture.CONTAINERS_BASE), 
				"agile.architecture.base.components", "Agile architecture base components view");
		agileArchitectureBaseComponents.addAllComponents();
		
//		Styles styles = views.getConfiguration().getStyles();
//		styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
//		styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
	}

	@Override
	public void configure(ImmutableConfiguration configuration) {
		// TODO Auto-generated method stub
		
	}

}

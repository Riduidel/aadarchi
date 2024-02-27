package org.ndx.aadarchi.base.enhancers.graph;

import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;

import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ViewEnhancerAdapter;

import jakarta.inject.Inject;
import java.util.logging.Logger;

public class ViewUpdater extends ViewEnhancerAdapter {
	public static final String NAME = ModelElementKeys.ConfigProperties.AutoUpdateViews.NAME;

	@Inject Logger logger;
	@Override
	public boolean isParallel() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS+1;
	}
	
	@Override
	protected boolean startVisit(ContainerView c) {
		return true;
	}
	
	@Override
	protected boolean startVisit(ComponentView c) {
		return true;
	}
	
	@Override
	protected boolean startVisit(DeploymentView c) {
		return true;
	}
	
	@Override
	protected boolean startVisit(SystemContextView c) {
		return true;
	}
	@Override
	protected boolean startVisit(SystemLandscapeView c) {
		return true;
	}
	@Override
	protected void endVisit(DeploymentView c, OutputBuilder builder) {
		if(c.getProperties().containsKey(NAME)) {
			c.addAllDeploymentNodes();
		}
		super.endVisit(c, builder);
	}
	@Override
	protected void endVisit(SystemContextView c, OutputBuilder builder) {
		if(c.getProperties().containsKey(NAME)) {
			c.addAllSoftwareSystems();
			c.addAllPeople();
		}
		super.endVisit(c, builder);
	}
	@Override
	protected void endVisit(SystemLandscapeView c, OutputBuilder builder) {
		if(c.getProperties().containsKey(NAME)) {
			c.addAllSoftwareSystems();
			c.addAllPeople();
		}
	}
	
	@Override
	protected void endVisit(ComponentView c, OutputBuilder builder) {
		if(c.getProperties().containsKey(NAME)) {
			c.addAllComponents();
		}
		super.endVisit(c, builder);
	}
	@Override
	protected void endVisit(ContainerView c, OutputBuilder builder) {
		if (c.getProperties().containsKey(NAME)) {
			c.addAllContainersAndInfluencers();
		}
		super.endVisit(c, builder);
	}
}

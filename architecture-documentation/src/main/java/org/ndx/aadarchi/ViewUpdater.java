package org.ndx.aadarchi;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.ViewEnhancer;
import org.ndx.aadarchi.base.enhancers.ViewEnhancerAdapter;

import com.structurizr.Workspace;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

public class ViewUpdater extends ViewEnhancerAdapter {
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
	protected void endVisit(ComponentView c, OutputBuilder builder) {
		if(c.getKey().equals("base_components")) {
			c.addAllComponents();
		}
		super.endVisit(c, builder);
	}
	@Override
	protected void endVisit(ContainerView c, OutputBuilder builder) {
		if(c.getKey().equals("system_containers")) {
			c.addAllContainersAndInfluencers();
		}
		super.endVisit(c, builder);
	}
}

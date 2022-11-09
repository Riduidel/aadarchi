package org.ndx.aadarchi;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.ndx.aadarchi.base.OutputBuilder;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.ViewEnhancerAdapter;

import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;

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
	protected void endVisit(ComponentView c, OutputBuilder builder) {
		if(c.getProperties().equals(NAME)) {
			c.addAllComponents();
		}
		super.endVisit(c, builder);
	}
	@Override
	protected void endVisit(ContainerView c, OutputBuilder builder) {
		if (c.getProperties().equals(NAME)) {
			c.addAllContainersAndInfluencers();
		}
		super.endVisit(c, builder);
	}
}

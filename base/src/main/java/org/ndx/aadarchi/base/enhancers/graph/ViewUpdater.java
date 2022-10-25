package org.ndx.aadarchi.base.enhancers.graph;

import java.util.logging.Logger;

import javax.inject.Inject;

import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ViewEnhancerAdapter;

import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.FilteredView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;


public class ViewUpdater extends ViewEnhancerAdapter {
	private static final String INCLUDE_TAG = "auto-include";
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
	public boolean startVisit(FilteredView filteredView) {
		return true;
	}

	@Override
	public void endVisit(FilteredView filteredView, OutputBuilder builder) {
		if(filteredView.getTags().contains(INCLUDE_TAG)) {
			View source = filteredView.getView();
			if(source instanceof ComponentView) {
				((ComponentView) source).addAllComponents();
			} else if(source instanceof ContainerView) {
				((ContainerView) source).addAllContainersAndInfluencers();
			}
		}
		super.endVisit(filteredView, builder);
	}

	public void endVisit(ViewSet v, OutputBuilder b) {
		if(v.getFilteredViews().isEmpty()) {
			logger.warning("There are no filtered views in viewset, so we don't add any discovered container/component");
		}
	}
}
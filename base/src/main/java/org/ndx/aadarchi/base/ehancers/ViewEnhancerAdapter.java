package org.ndx.aadarchi.base.ehancers;

import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.ViewEnhancer;

import com.structurizr.Workspace;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.CustomView;
import com.structurizr.view.DeploymentView;
import com.structurizr.view.DynamicView;
import com.structurizr.view.SystemContextView;
import com.structurizr.view.SystemLandscapeView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

/**
 * The view enhancer adapter allow visiting all view, and provides detailed informations about the view type.
 * As a consequence implementing the {@link #startVisit(View)} is repalced by implementing
 * one method for each view type, where each case is provided a sensible default implementation
 * @author nicolas-delsaux
 *
 */
public abstract class ViewEnhancerAdapter implements ViewEnhancer {

	@Override 
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return true;
	}

	@Override public void endVisit(Workspace workspace, OutputBuilder builder) {}

	@Override public boolean startVisit(ViewSet viewset) { return true;	}

	@Override
	public void endVisit(ViewSet viewset, OutputBuilder builder) {
	}

	@Override
	public boolean startVisit(View s) {
		if (s instanceof CustomView) {
			return startVisit((CustomView) s);
		} else if (s instanceof DeploymentView) {
			return startVisit((DeploymentView) s);
		} else if (s instanceof DynamicView) {
			return startVisit((DynamicView) s);
		} else if (s instanceof ComponentView) {
			return startVisit((ComponentView) s);
		} else if (s instanceof ContainerView) {
			return startVisit((ContainerView) s);
		} else if (s instanceof SystemContextView) {
			return startVisit((SystemContextView) s);
		} else if (s instanceof SystemLandscapeView) {
			return startVisit((SystemLandscapeView) s);
		} else {
			throw new UnsupportedOperationException(String.format("I don't know how to manage a %s", s.getClass().getName()));
		}
	}
	
	protected boolean startVisit(SystemLandscapeView c) {
		return false;
	}
	
	protected boolean startVisit(SystemContextView c) {
		return false;
	}
	
	protected boolean startVisit(ContainerView c) {
		return false;
	}
	
	protected boolean startVisit(ComponentView c) {
		return false;
	}
	
	protected boolean startVisit(DynamicView c) {
		return false;
	}
	
	protected boolean startVisit(DeploymentView c) {
		return false;
	}
	
	protected boolean startVisit(CustomView c) {
		return false;
	}

	@Override
	public void endVisit(View s, OutputBuilder builder) {
		if (s instanceof CustomView) {
			endVisit((CustomView) s, builder);
		} else if (s instanceof DeploymentView) {
			endVisit((DeploymentView) s, builder);
		} else if (s instanceof DynamicView) {
			endVisit((DynamicView) s, builder);
		} else if (s instanceof ComponentView) {
			endVisit((ComponentView) s, builder);
		} else if (s instanceof ContainerView) {
			endVisit((ContainerView) s, builder);
		} else if (s instanceof SystemContextView) {
			endVisit((SystemContextView) s, builder);
		} else if (s instanceof SystemLandscapeView) {
			endVisit((SystemLandscapeView) s, builder);
		} else {
			throw new UnsupportedOperationException(String.format("I don't know how to manage a %s", s.getClass().getName()));
		}
	}

	
	protected void endVisit(SystemLandscapeView c, OutputBuilder builder) {
	}
	
	protected void endVisit(SystemContextView c, OutputBuilder builder) {
	}
	
	protected void endVisit(ContainerView c, OutputBuilder builder) {
	}
	
	protected void endVisit(ComponentView c, OutputBuilder builder) {
	}
	
	protected void endVisit(DynamicView c, OutputBuilder builder) {
	}
	
	protected void endVisit(DeploymentView c, OutputBuilder builder) {
	}
	
	protected void endVisit(CustomView c, OutputBuilder builder) {
	}
}

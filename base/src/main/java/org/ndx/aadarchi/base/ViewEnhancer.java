package org.ndx.aadarchi.base;

import com.structurizr.view.FilteredView;
import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

public interface ViewEnhancer extends Enhancer {
	boolean startVisit(ViewSet viewset);

	boolean startVisit(View s);

	boolean startVisit(FilteredView filteredView);

	void endVisit(View s, OutputBuilder builder);

	void endVisit(ViewSet viewset, OutputBuilder builder);

	void endVisit(FilteredView filteredView, OutputBuilder builder);
}

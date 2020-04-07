package org.ndx.agile.architecture.base;

import com.structurizr.view.View;
import com.structurizr.view.ViewSet;

public interface ViewEnhancer extends Enhancer {
	boolean startVisit(ViewSet viewset);

	boolean startVisit(View s);

	void endVisit(View s, OutputBuilder builder);

	void endVisit(ViewSet viewset, OutputBuilder builder);
}

package org.ndx.agile.architecture.base;

import com.structurizr.Workspace;

public interface Enhancer {
	/**
	 * @return true if enhancer allows parallel run
	 */
	boolean isParallel();
	
	/**
	 * Get priority level of this enhancer.
	 * As enhancers are sorted in numerical order (and outputted by ArchitectureEnhancer)
	 * please choose a "sensibl" value.
	 * @return Any int. Lower values will run first, higher values will run last.
	 */
	int priority();

	boolean startVisit(Workspace workspace, OutputBuilder builder);

	void endVisit(Workspace workspace, OutputBuilder builder);

}

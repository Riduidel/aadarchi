package org.ndx.agile.architecture.base;

import com.structurizr.Workspace;

public interface Enhancer {
	/**
	 * This is the best priority an enhancer which manipulates generated containers/components should get.
	 * In other words, users enhancers generating those model elements can get best priorities,
	 * but not the generating enhancers that allow linking to documentation, and so on
	 */
	public static final int TOP_PRIORITY_FOR_INTERNAL_ENHANCERS = 100_000;
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

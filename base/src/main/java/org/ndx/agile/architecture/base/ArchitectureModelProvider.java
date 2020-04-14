package org.ndx.agile.architecture.base;

import com.structurizr.Workspace;

/**
 * Interface implemented by the class responsible for model description
 * @author nicolas-delsaux
 *
 */
public interface ArchitectureModelProvider {

	/**
	 * This is the method user has to implement.
	 * The whole goal is to let the architect write it and have pluggable extensions allowing easy enhancement of this architecture description.
	 * @return a structurizr workspace we will decorate and extend.
	 */
	public Workspace describeArchitecture();

}

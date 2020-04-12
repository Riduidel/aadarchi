package org.ndx.agile.architecture.base.enhancers.includes;

import org.ndx.agile.architecture.base.AgileArchitectureException;

public class CantCreateImplicitInclude extends AgileArchitectureException {

	public CantCreateImplicitInclude(String message, Throwable cause) {
		super(message, cause);
	}

	public CantCreateImplicitInclude(String message) {
		super(message);
	}

}

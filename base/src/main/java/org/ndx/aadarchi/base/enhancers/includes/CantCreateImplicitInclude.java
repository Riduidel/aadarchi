package org.ndx.aadarchi.base.enhancers.includes;

import org.ndx.aadarchi.base.AgileArchitectureException;

public class CantCreateImplicitInclude extends AgileArchitectureException {

	public CantCreateImplicitInclude(String message, Throwable cause) {
		super(message, cause);
	}

	public CantCreateImplicitInclude(String message) {
		super(message);
	}

}

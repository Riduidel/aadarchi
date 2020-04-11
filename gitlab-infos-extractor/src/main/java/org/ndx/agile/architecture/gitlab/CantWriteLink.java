package org.ndx.agile.architecture.gitlab;

import org.ndx.agile.architecture.base.AgileArchitectureException;

public class CantWriteLink extends AgileArchitectureException {

	public CantWriteLink(String message, Throwable cause) {
		super(message, cause);
	}

}

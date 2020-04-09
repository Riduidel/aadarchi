package org.ndx.agile.architecture.github.readme;

import org.ndx.agile.architecture.base.AgileArchitectureException;

public class CantExtractReadme extends AgileArchitectureException {

	public CantExtractReadme(String message) {
		super(message);
	}

	public CantExtractReadme(String message, Throwable cause) {
		super(message, cause);
	}

}

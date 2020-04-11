package org.ndx.agile.architecture.gitlab;

import org.ndx.agile.architecture.base.AgileArchitectureException;

public class GitLabHandlerException extends AgileArchitectureException {

	public GitLabHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public GitLabHandlerException(String message) {
		super(message);
	}

}

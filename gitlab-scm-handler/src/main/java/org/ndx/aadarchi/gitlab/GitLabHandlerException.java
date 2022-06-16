package org.ndx.aadarchi.gitlab;

import org.ndx.aadarchi.base.AgileArchitectureException;

public class GitLabHandlerException extends AgileArchitectureException {

	public GitLabHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public GitLabHandlerException(String message) {
		super(message);
	}

}

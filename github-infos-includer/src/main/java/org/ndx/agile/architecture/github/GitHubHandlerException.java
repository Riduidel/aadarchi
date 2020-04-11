package org.ndx.agile.architecture.github;

import org.ndx.agile.architecture.base.AgileArchitectureException;

public class GitHubHandlerException extends AgileArchitectureException {

	public GitHubHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

	public GitHubHandlerException(String message) {
		super(message);
	}
	
}
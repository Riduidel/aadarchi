package org.ndx.agile.architecture.tickets;

import org.ndx.agile.architecture.base.AgileArchitectureException;

public class UnableToCreateDecisionLog extends AgileArchitectureException {

	public UnableToCreateDecisionLog(String message) {
		super(message);
	}

	public UnableToCreateDecisionLog(Throwable cause) {
		super(cause);
	}

	public UnableToCreateDecisionLog(String message, Throwable cause) {
		super(message, cause);
	}

}

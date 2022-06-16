package org.ndx.aadarchi.tickets;

import org.ndx.aadarchi.base.AgileArchitectureException;

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

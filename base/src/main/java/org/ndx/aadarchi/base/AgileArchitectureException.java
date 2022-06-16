package org.ndx.aadarchi.base;

/**
 * Base class for all exceptions.
 * This class should not be implemented directly, but rather extended
 * @author nicolas-delsaux
 *
 */
public abstract class AgileArchitectureException extends RuntimeException {
	public AgileArchitectureException(String message) {
		super(message);
	}

	public AgileArchitectureException(Throwable cause) {
		super(cause);
	}

	public AgileArchitectureException(String message, Throwable cause) {
		super(message, cause);
	}
}

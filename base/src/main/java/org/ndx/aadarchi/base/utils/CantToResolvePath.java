package org.ndx.aadarchi.base.utils;

import org.ndx.aadarchi.base.AgileArchitectureException;

/**
 * Thrown when it is impossible to resolve a given file
 */
public final class CantToResolvePath extends AgileArchitectureException {
	public CantToResolvePath(String message, Throwable cause) {
		super(message, cause);
	}
}
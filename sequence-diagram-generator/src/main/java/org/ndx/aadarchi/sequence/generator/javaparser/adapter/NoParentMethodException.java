package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;

public class NoParentMethodException extends SequenceGeneratorException {

	public NoParentMethodException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoParentMethodException(String message) {
		super(message);
	}

	public NoParentMethodException(Throwable cause) {
		super(cause);
	}

}

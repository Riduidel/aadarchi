package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import org.ndx.agile.architecture.sequence.generator.SequenceGeneratorException;

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

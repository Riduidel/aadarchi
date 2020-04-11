package org.ndx.agile.architecture.base.utils;

import org.ndx.agile.architecture.base.AgileArchitectureException;

import nl.jworks.markdown_to_asciidoc.Converter;

public class AsciidocProducer {
	public static class UnableToGetAsciidocFrom extends AgileArchitectureException {

		public UnableToGetAsciidocFrom(String message) {
			super(message);
		}

	}

	private String asAsciidoc(String filename, String content) {
		if(filename.endsWith(".md")) {
			return Converter.convertMarkdownToAsciiDoc(
					content);
		} else if(filename.endsWith(".adoc")) {
			return content;
		} else {
			throw new UnableToGetAsciidocFrom(String.format("Unable to get asciidoc from %s (we don't know ow to convert that)", filename));
		}
	}
}

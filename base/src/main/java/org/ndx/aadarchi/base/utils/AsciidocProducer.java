package org.ndx.aadarchi.base.utils;

import com.kodcu.asciidocfx.MarkdownToAsciidoc;
import org.ndx.aadarchi.base.AgileArchitectureException;

public class AsciidocProducer {
	public static class UnableToGetAsciidocFrom extends AgileArchitectureException {

		public UnableToGetAsciidocFrom(String message) {
			super(message);
		}

	}

	private String asAsciidoc(String filename, String content) {
		if(filename.endsWith(".md")) {
			return MarkdownToAsciidoc.convert(content);
		} else if(filename.endsWith(".adoc")) {
			return content;
		} else {
			throw new UnableToGetAsciidocFrom(String.format("Unable to get asciidoc from %s (we don't know ow to convert that)", filename));
		}
	}
}

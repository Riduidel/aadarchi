package org.ndx.agile.architecture.base;

import java.io.File;

import com.structurizr.model.Element;

public interface OutputBuilder {
	public static interface HandledFormat {
		/**
		 * get the used extension for this format, as declared in {@link OutputBuilder#outputFor(AgileArchitectureSection, Element, Enhancer, String)}
		 * @return
		 */
		String getExtension();

		/**
		 * Creates the comment documenting which enhancer generated that file.
		 * @param enhancer
		 * @return a comment in the format specified indicating which enhancer generated the file
		 */
		String createCommentForEnhancer(Enhancer enhancer);

		default String encoding() {
			return "UTF-8";
		}
	}
	/**
	 * This enum defines the formats we're able to write.
	 * It exists to ease things out for aadarchi developers, because the outputFor method
	 * using this enum as parameter is able to write comments for each enhancer, which is cool, believe me) 
	 */
	public static enum Format implements HandledFormat {
		adoc("adoc") {
			@Override
			public String createCommentForEnhancer(Enhancer enhancer) {
				return String.format("// Generated by %s\n",
						enhancer.getClass().getName());
			}
		};
		
		private final String extension;
		
		public String getExtension() {
			return extension;
		}
		private Format(String extension) {
			this.extension = extension;
		}
	}

	/**
	 * Provide an output file for the given section and the given model element. 
	 * @param section the section in which we want to add some content
	 * @param element the element to which we want to add some content
	 * @param format file format (without the dot!). Typically it will be "adoc"
	 * @param enhancer the enhancer producing that content
	 * @return a path, relative to base
	 * @deprecated prefer the version using the Format enum
	 */
	File outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, String format);
	/**
	 * Provide an output file for the given section and the given model element
	 * @param section the section in which we want to add some content
	 * @param element the element to which we want to add some content
	 * @param format file format. For asciidoc, one should use {@link Format#adoc}
	 * @param enhancer the enhancer producing that content
	 * @return a path, relative to base
	 */
	File outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, HandledFormat format);
	/**
	 * Write the given text in the output file for the given enhancer.
	 * BEWARE: This is not append. Content of file will be replaced.
	 * @param section the section in which we want to add some content
	 * @param element the element to which we want to add some content
	 * @param format file format. For asciidoc, one should use {@link Format#adoc}
	 * @param enhancer the enhancer producing that content
	 * @return the file in which content has been written, for later reference (typically useful for generating links)
	 */
	File writeToOutputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, HandledFormat format, CharSequence text);
	/**
	 * Get the output directory in which all output is to be written.
	 */
	File outputFor(AgileArchitectureSection section, Element element);
}

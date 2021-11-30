package org.ndx.agile.architecture.base;

import java.io.File;

import com.structurizr.model.Element;

public interface OutputBuilder {

	/**
	 * Provide an output file for the given section and the given model element. 
	 * @param section the section in which we want to add some content
	 * @param element the element to which we want to add some content
	 * @param format file format (without the dot!). Typically it will be "adoc"
	 * @param enhancer the enhancer producing that content
	 * @return a path, relative to base
	 */
	File outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, String format);
	File outputFor(AgileArchitectureSection section, Element element);
}

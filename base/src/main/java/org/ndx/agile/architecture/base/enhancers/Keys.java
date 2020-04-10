package org.ndx.agile.architecture.base.enhancers;

/**
 * This interface only contains a list of strings, each one providing some additionnal content for elements
 * @author nicolas-delsaux
 *
 */
public interface Keys {

	/**
	 * Should contain the full SCM url of project, including the domain!
	 * This property has no default. 
	 * Not setting this property implies that the associated element has no associated SCM project. 
	 */
	String ELEMENT_PROJECT = "agile.architecture.github.project";
	/**
	 * Readme path in project. Defaults to "readme.md"
	 */
	String ELEMENT_README = "agile.architecture.github.readme.path";

}

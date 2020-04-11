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
	String SCM_PROJECT = "agile.architecture.scm.project";
	/**
	 * Path of project in SCM. 
	 */
	String SCM_PATH = "agile.architecture.scm.path";
	/**
	 * Readme path in project. Defaults to "readme.md". THis should be a subpath of scm path.
	 */
	String SCM_README = "agile.architecture.github.readme.path";

}

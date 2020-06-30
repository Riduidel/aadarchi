package org.ndx.agile.architecture.base.enhancers;

/**
 * This interface only contains a list of strings, each one providing some additionnal content 
 * for elements. Defining one of them could trigger additionnal enhancements, provided the 
 * associated Java component is in CLASSPATH
 * @author nicolas-delsaux
 *
 */
public interface ModelElementKeys {

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
	 * Readme path in project. Defaults to "readme.md". This should be a subpath of scm path.
	 */
	String SCM_README = "agile.architecture.scm.readme.path";
	/**
	 * Issue manager key should contain the url of the, well, issue manager system used by an element
	 */
	String ISSUE_MANAGER = "agile.architecture.issue.manager";

}

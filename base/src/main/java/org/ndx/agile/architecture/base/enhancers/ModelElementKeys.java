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
	 * All properties defined for agile architecture **must** start with that prefix.
	 */
	String PREFIX = "agile.architecture.";
	/**
	 * Should contain the full SCM url of project, including the domain!
	 * This property has no default. 
	 * Not setting this property implies that the associated element has no associated SCM project. 
	 */
	String SCM_PROJECT = ModelElementKeys.PREFIX+"scm.project";
	/**
	 * Path of project in SCM. 
	 */
	String SCM_PATH = ModelElementKeys.PREFIX+"scm.path";
	/**
	 * Readme path in project. Defaults to "readme.md". This should be a subpath of scm path.
	 */
	String SCM_README = ModelElementKeys.PREFIX+"scm.readme.path";
	/**
	 * Issue manager key should contain the url of the, well, issue manager system used by an element
	 */
	String ISSUE_MANAGER = ModelElementKeys.PREFIX+"issue.manager";
	/**
	 * String-stored List (separated by ;) of java packages contained by a given model element.
	 * Why a List ? Because some Spring applications tend to have very bad separations due to
	 * the MVC thingie : the controllers may be in one container, and the services/repositories
	 * may be in another.
	 */
	String JAVA_PACKAGES = ModelElementKeys.PREFIX+"java.packages";

	/**
	 * Path to source folders (separated by ";", which doesn't seems to be valid in an url). This does not include test sources.
	 * It must be an url, but can be a file url.
	 */
	String JAVA_SOURCES = ModelElementKeys.PREFIX+"java.source";
	/**
	 * This property is used in Maven POM to list the elements this element depends upon
	 * This is specially useful when describing dependencies upon non-maven elements
	 * (typically other systems).
	 * Those external dependencies should be separated by ";"
	 */
	String EXTERNAL_DEPENDENCIES = PREFIX+"depends.on";
	/**
	 * When set, this property contains a description used for {@link #EXTERNAL_DEPENDENCIES}
	 * generated links
	 */
	String EXTERNAL_DEPENDENCY_DESCRIPTION = PREFIX+".depends.description";
	/**
	 * Path in which diagrams will be generated.
	 * It should be aligned with what asciidoc wants, so don't play too much with that property :-D
	 */
	public String AGILE_ARCHITECTURE_DIAGRAMS_PATH = PREFIX+"diagrams";
}

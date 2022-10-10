package org.ndx.aadarchi.base.enhancers;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.model.Element;

/**
 * This interface only contains a list of strings, each one providing some additionnal content 
 * for elements. Defining one of them could trigger additionnal enhancements, provided the 
 * associated Java component is in CLASSPATH
 * @author nicolas-delsaux
 *
 */
public interface ModelElementKeys {
	/**
	 * A not to implement interface containing common properties names and default
	 * values to avoid having to modify them everywhere
	 * 
	 * Each contained interface should define exactly two static strings
	 * <ul>
	 * <li>NAME is the config property name
	 * <li>VALUE is the config property default value
	 * </ul>
	 * @author nicolas-delsaux
	 *
	 */
	public static interface ConfigProperties {
		public static interface WorkspaceDsl {
			public String NAME = ModelElementKeys.PREFIX + "dsl";
			public String VALUE = "${project.basedir}/src/architecture/resources/workspace.dsl";
		}
		public static interface CacheDir {
			public String NAME = ModelElementKeys.PREFIX + "cache.dir";
			public String VALUE = "${project.basedir}/.cache";
		}
		public static interface AsciidocSourceDir {
			public String NAME = "asciidoc.source.docs.directory";
			public String VALUE = "${project.basedir}/src/docs/asciidoc";
		}
		public static interface EnhancementsDir {
			public String NAME = ModelElementKeys.PREFIX+"enhancements";
			public String VALUE = "${basedir}/target/structurizr/enhancements";
		}
		public static interface DiagramsDir {
			/**
			 * Path in which diagrams will be generated.
			 * It should be aligned with what asciidoc wants, so don't play too much with that property :-D
			 */
			String NAME = ModelElementKeys.PREFIX+"diagrams";
			String VALUE = "${basedir}/target/structurizr/diagrams";
		}
		public static interface Force {
			String NAME = "force";
			String VALUE = "false";
		}
		public static interface BasePath {
			String VALUE = "${basedir}";
			/**
			 * The base path is the reference from which all paths are set.
			 * IT IS NOT TO BE DEFINED by user code. Instead, it is aadarchi which take care of setting a "good" value.
			 */
			String NAME = ModelElementKeys.PREFIX+"base.path";
		}
	}
	public static interface Scm {

		/**
		 * Should contain the full SCM url of project, including the domain!
		 * This property has no default. 
		 * Not setting this property implies that the associated element has no associated SCM project. 
		 */
		String PROJECT = ModelElementKeys.PREFIX+"scm.project";
		/**
		 * Path of project in SCM. 
		 */
		String PATH = ModelElementKeys.PREFIX+"scm.path";
		/**
		 * Readme path in project. Defaults to "readme.md". This should be a subpath of scm path.
		 */
		String README = ModelElementKeys.PREFIX+"scm.readme.path";
		/**
		 * When property is set to true, we will check if model element has an associated SCM project value 
		 * which can be checked out
		 * @author Nicolas
		 *
		 */
		public static interface CheckoutEnabled {

			String NAME = ModelElementKeys.PREFIX + "scm.checkout.enabled";
			/**
			 * As a default, we don't check out projects
			 */
			String VALUE = "false";
		}
		/**
		 * When property is set (and #PROJECT is also set), either from a maven property or from an element property,
		 * the path will be used as base folder for project checkout (when a GitHub/GitLab project exists)
		 *
		 */
		public static interface CheckoutLocation {

			String NAME = ModelElementKeys.PREFIX + "scm.checkout.target";
			String VALUE = "${project.basedir}/../";
			
		}
		
	}


	/**
	 * All properties defined for agile architecture **must** start with that prefix.
	 */
	String PREFIX = "aadarchi.";
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
}

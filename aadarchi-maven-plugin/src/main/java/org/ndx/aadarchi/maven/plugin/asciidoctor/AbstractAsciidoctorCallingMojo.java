package org.ndx.aadarchi.maven.plugin.asciidoctor;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.dependency;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.ndx.aadarchi.maven.plugin.GenerateDiagramsMojo;
import org.twdata.maven.mojoexecutor.MojoExecutor;
import org.twdata.maven.mojoexecutor.MojoExecutor.Element;

/**
 * Base class for all mojos invoking asciidoctor-maven-plugin. It provides a set
 * of common method useful to easily configure and execute the
 * asciidoctor-maven-plugin
 * 
 * @author nicolas-delsaux
 *
 */
public abstract class AbstractAsciidoctorCallingMojo extends AbstractMojoExecutorMojo {
	private static FileSystemManager fsManager;

	private static Map<String, FileObject> bundledGems;
	/**
	 * Version of the asciidoctor-maven-plugin
	 * 
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctor-maven-plugin
	 */
	@Parameter(name = "asciidoctor-maven-plugin-version", defaultValue = "2.1.0", property = "version.asciidoctor.maven.plugin")
	private String asciidoctorMavenPluginVersion;
	/**
	 * Used version of JRuby interpreter
	 * 
	 * @see https://mvnrepository.com/artifact/org.jruby/jruby
	 */
	@Parameter(name = "jruby-version", defaultValue = "9.2.9.0", property = "version.jruby")
	private String jrubyVersion;
	/**
	 * Used version of asciidoctor Java implementation
	 * 
	 * @see https://mvnrepository.com/artifact/org.asciidoctor/asciidoctorj
	 */
	@Parameter(name = "asciidoctorj-version", defaultValue = "2.4.3", property = "version.asciidoctorj")
	private String asciidoctorjVersion;

	/**
	 * URL of the kroki server which will be used to render diagrams
	 */
	@Parameter(name = "kroki-server-url", defaultValue = "http://kroki.io", property = "kroki.server.url")
	public String krokiServerUrl;

	/**
	 * Folder where agile architecture documentation enhancements files are stored.
	 * This folder should be in target, since it contains files generated by the
	 * various agile architecture documentation enhancers (SCM link, SCM readme, and
	 * so on).
	 */
	@Parameter(name = "enhancements-dir", defaultValue = "${project.build.directory}/structurizr/enhancements", property = "aadarchi.output.enhancements")
	public String enhancementsDir;
	/**
	 * Set this parameter to true to hide the bug report admonition that is included
	 * atop each section.
	 */
	@Parameter(name = "hide-bug-report", defaultValue = "false", property = "asciidoc.documents.hide.bug.report")
	private String hideBugReport;
	/**
	 * Folder where gems are stored. This folder is important if you want to use
	 * ruby extensions such as asciidoctor-kroki
	 */
	@Parameter(name = "gems-path", defaultValue = "${project.build.directory}/gems")
	private String gemsPath;
	/**
	 * Used bug tracker for architecture project. This parameter allow sending bug
	 * report to architecture documentation writers when documentation is not
	 * correct.
	 */
	@Parameter(name = "issues-url", defaultValue = "${issueManagement.url}")
	private String issuesUrl;
	/**
	 * Value of this parameter will be guessed automatically from
	 * {@link #getIssuesUrl()} value. Supported values are
	 * <ul>
	 * <li>project-issues-on-github</li>
	 * <li>project-issues-on-gitlab</li>
	 * </ul>
	 */
	@Parameter(name = "project-issues")
	private String projectIssues;

	/**
	 * Folder where the structurizr data is to be stored
	 * 
	 * @see GenerateDiagramsMojo#agileArchitectureOutputDiagrams
	 */
	@Parameter(name = "structurizr-dir", defaultValue = "${project.build.directory}/structurizr/diagrams", property = "aadarchi.output.diagrams")
	private String structurizrDir;

	protected String executedMojo() {
		return goal("process-asciidoc");
	}

	public String getIssuesUrl() {
		if (issuesUrl == null) {
			if (mavenProject.getIssueManagement() != null) {
				issuesUrl = mavenProject.getIssueManagement().getUrl();
			}
		}
		return issuesUrl;
	}

	protected Plugin executedMavenPlugin() {
		return plugin(groupId("org.asciidoctor"), artifactId("asciidoctor-maven-plugin"),
				version(asciidoctorMavenPluginVersion), dependencies());
	}

	/**
	 * As each plugin requires its own set of dependencies, this method is left
	 * abstract
	 * 
	 * @return
	 */
	protected abstract List<Dependency> dependencies();

	protected final Dependency dependencyAsciidoctor() {
		return dependency("org.asciidoctor", "asciidoctorj", asciidoctorjVersion);
	}

	protected final Dependency dependencyJRuby() {
		return dependency("org.jruby", "jruby-complete", jrubyVersion);
	}

	protected Xpp3Dom configuration() {
		Element requiredGems = requiredGems();
		processContainedGems(requiredGems);
		return MojoExecutor.configuration(
				// TODO conditionalize that invocation : add all gems dependencies here
				requiredGems, gemsPath(),
				element(name("attributes"), configurationAttributes().toArray(new Element[] {})),
				configurationBackend(), configurationSourceDirectory(), configurationOutputDirectory());
	}

	/**
	 * Scan the list of contained gems looking for the ones which are bundled with
	 * this plugin. Each bundled element that is not present in effective folder
	 * will be copied
	 * 
	 * @param requiredGems
	 * @see #requiredGems()
	 */
	private void processContainedGems(Element requires) {
		for (Xpp3Dom dependency : requires.toDom().getChildren("require")) {
			String gemName = dependency.getValue();
			if (getBundledGems().containsKey(gemName)) {
				copyContainedGem(gemName, getBundledGems().get(gemName));
			}
		}
	}

	protected static Map<String, FileObject> getBundledGems() {
		if (bundledGems == null) {
			bundledGems = loadBundledGems();
		}
		return bundledGems;
	}

	/**
	 * Bundled gems should be sub-elements of META-INF/gems
	 * @return
	 */
	private static Map<String, FileObject> loadBundledGems() {
		try {
			Map<String, FileObject> returned = new TreeMap<>();
			FileObject gemsFolder = getThisJarAsVFS().getChild("META-INF").getChild("gems").getChild("gems");
			for(FileObject gem : gemsFolder.getChildren()) {
				if(gem.isFolder()) {
					String gemVersionnedName = gem.getName().getBaseName();
					String gemName = gemVersionnedName.substring(0, gemVersionnedName.lastIndexOf('-'));
					returned.put(gemName, gem);
				}
			}
			return returned;
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	private static FileObject getThisJarAsVFS() {
		URI currentJarUri;
		try {
			currentJarUri = AbstractAsciidoctorCallingMojo.class
			    .getProtectionDomain()
			    .getCodeSource()
			    .getLocation()
			    .toURI();
		} catch (URISyntaxException e) {
			throw new RuntimeException("Interestingly, we can't resolve the path of the current jar...");
		}
		String currentJarPath = currentJarUri.getPath();
		String commonsVfsPath = "jar:"+currentJarPath+"!";
		try {
			return getFileSystemManager().resolveFile(commonsVfsPath);
		} catch (FileSystemException e) {
			throw new RuntimeException("Unable to resolve path to current jar (uri was "+commonsVfsPath+")", e);
		}
	}

	private static FileSystemManager getFileSystemManager() {
		if (fsManager == null) {
			try {
				fsManager = VFS.getManager();
			} catch (FileSystemException e) {
				throw new RuntimeException("Unable to get a Commons-VFS file system manager. THings are really in a bad state");
			}
		}
		return fsManager;
	}

	private void copyContainedGem(String value, FileObject sourceGem) {
		try {
			FileObject gems = getGemsFolder();
			FileObject target = gems.resolveFile(sourceGem.getName().getBaseName());
			target.copyFrom(sourceGem, new AllFileSelector());
		} catch (FileSystemException e) {
			throw new RuntimeException(String.format("Unable to copy gem %s due to", value, e));
		}
	}

	private FileObject getGemsFolder() throws FileSystemException {
		FileObject destination = getFileSystemManager().resolveFile("file:"+gemsPath);
		if(!destination.exists()) {
			destination.createFolder();
		}
		FileObject gems = destination.resolveFile("gems");
		if(!gems.exists()) {
			gems.createFolder();
		}
		return gems;
	}

	public Element configurationSourceDirectory() {
		return element(name("sourceDirectory"), getSourceDirectory());
	}

	protected abstract String getSourceDirectory();

	public Element configurationOutputDirectory() {
		return element(name("outputDirectory"), getOutputDirectory());
	}

	protected abstract String getOutputDirectory();

	/**
	 * This pom path is relative to documents directory
	 * 
	 * @see #configurationSourceDirectory()
	 * @return
	 */
	protected Element attributePomPath() {
		return element(name("project-pom-path"), "../../../pom.xml");
	}

	protected Element attributesEnhancementsDir() {
		return element(name("enhancements-dir"), enhancementsDir);
	}

	// Depends upon the used SCM, should not be an absolute value!
	protected Element attributeIssuesUrl() {
		if (Boolean.valueOf(hideBugReport)) {
			return element(name("project-issues-ignored"),
					"due to hide-bug-report being true, no need to auto-guess project-issues");
		} else {
			if (projectIssues == null) {
				if (getIssuesUrl() == null) {
					return element(name("project-issues-undefined"), getIssuesUrl());
				} else {
					if (getIssuesUrl().contains("github.com")) {
						return element(name("project-issues-on-github"), getIssuesUrl());
					} else if (getIssuesUrl().contains("gitlab")) {
						return element(name("project-issues-on-github"), getIssuesUrl());
					} else {
						return element(name("project-issues-undefined"), getIssuesUrl());
					}
				}
			} else {
				if (projectIssues.startsWith("project-issues")) {
					return element(name(projectIssues), getIssuesUrl());
				} else {
					throw new UnsupportedOperationException(String.format(
							"All project issues templates start with \"project-issues\". "
									+ "But you used the string `%s`, which doesn't. "
									+ "If your SCM isn't supported, you can either\n"
									+ "1. Set \"hide-bug-report\" to true\n" + "2. Implement your own SCM provider\n"
									+ "3. Ask the team how to go through that bug by filling a bug report",
							projectIssues));
				}
			}
		}
	}

	protected Element attributeHideBugReport() {
		return element(name("hideBugReport"), hideBugReport);
	}

	protected Element attributeImagesDir() {
		return element(name("imagesdir"), "./images");
	}

	protected Element attributeStructurizrDir() {
		return element(name("structurizrdir"), structurizrDir);
	}

	protected Element attributeKrokiServerUri() {
		return element(name("kroki-server-url"), krokiServerUrl);
	}

	private Element gemsPath() {
		return element(name("gemPath"), gemsPath);
	}

	protected Element requiredGems() {
		return element(name("requires"), element(name("require"), "asciidoctor-kroki"));
	}

	protected abstract Element configurationBackend();

	public List<Element> configurationAttributes() {
		return Arrays.asList(element(name("plantumldir"), "."), element(name("allow-uri-read")), // allow to include
																									// distant content
																									// in the created
																									// document
				element(name("toc"), "left"), // put the table of content on the left side of the window
				element(name("icons"), "font"), // allow to use icons from "fonticones"
				element(name("sectanchors"), "true"), // sections behave like anchors/links to move around the document
				element(name("idseparator"), "-"), // put a separator between identifiers pieces

				element(name("sectnums"), "true"), // display section number in the summary
				element(name("revnumber"), "${project.version}"), // add project version in the footer
				element(name("revdate"), "${maven.build.timestamp}"), // add the date in the footer

				element(name("project-group-id"), "${project.groupId}"), // catch the groupId defined in the pom.xml
																			// file
				element(name("project-artifact-id"), "${project.artifactId}"), // catch the artifactId defined in the
																				// pom.xml file
				element(name("project-name"), "${project.name}"), // catch the project name defined in the pom.xml file
				element(name("project-version"), "${project.version}"), // catch the project version defined in the
																		// pom.xml file
				element(name("project-build-timestamp"), "${maven.build.timestamp}"), // catch the timestamp defined
																						// when maven build
				attributePomPath(), // catch pom.xml file path

				attributeKrokiServerUri(), attributeStructurizrDir(), attributeImagesDir(), attributeHideBugReport(), // add
																														// link
																														// to
																														// allow
																														// users
																														// to
																														// report
																														// some
																														// bugs
				attributeIssuesUrl(), // catch the issue url defined in the pom.xml file
				element(name("organization"), "${project.organization.name}"), // catch the organization name defined in
																				// the pom.xml file
				attributesEnhancementsDir() // catch the path to the enhancements directory defined in the pom.xml file
		);
	}

}

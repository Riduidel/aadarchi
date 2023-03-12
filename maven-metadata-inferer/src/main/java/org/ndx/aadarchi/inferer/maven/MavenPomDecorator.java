package org.ndx.aadarchi.inferer.maven;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.apache.commons.vfs2.AllFileSelector;
import org.apache.commons.vfs2.FileDepthSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.FileTypeSelector;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.ThrowingPredicate;
import com.structurizr.model.Element;

@Default
@ApplicationScoped
public class MavenPomDecorator {
	private static final Logger logger = Logger.getLogger(MavenPomDecorator.class.getName());
	@Inject
	FileSystemManager fileSystemManager;

	public static void decorateRecursively(MavenProject project, BiFunction<MavenProject, List<MavenProject>, Boolean> consumer) {
		decorateRecursively(project, new LinkedList<MavenProject>(), consumer);
	}
	
	public static void decorateRecursively(MavenProject project, List<MavenProject> children, BiFunction<MavenProject, List<MavenProject>, Boolean> consumer) {
		if(consumer.apply(project, children)) {
			if(project.getParent()!=null) {
				decorateRecursively(project.getParent(), consumer);
			}
		}
	}

	/**
	 * Decorate the given model element with the possible properties fetched from
	 * maven project
	 * 
	 * @param element
	 * @param mavenProject
	 */
	public void decorate(Element element, MavenProject mavenProject) {
		// I use optional to avoid writing endless if(...!=null) lines.
		// It may be ugly, but I'm trying a /style/ here
		decorateCoordinates(element, mavenProject);
		decorateScmUrl(element, mavenProject);
		decorateIssueManager(element, mavenProject);
		decorateJavaSource(element, mavenProject);
		decorateJavaPackage(element, mavenProject);
		decorateMavenProperties(element, mavenProject);
		Optional.ofNullable(mavenProject.getDescription()).stream()
				.forEach(description -> element.setDescription(description.replaceAll("\n", " ")));
	}

	protected void decorateCoordinates(Element element, MavenProject mavenProject) {
		if (!element.getProperties().containsKey(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES)) {
			element.addProperty(MavenEnhancer.AGILE_ARCHITECTURE_MAVEN_COORDINATES,
					String.format("%s:%s", mavenProject.getGroupId(), mavenProject.getArtifactId()));
		}
	}

	public void decorateIssueManager(Element element, MavenProject mavenProject) {
		decorateRecursively(mavenProject, (project, children) -> {
			if(project.getIssueManagement()!=null) {
				IssueManagement issues = project.getIssueManagement();
				if(issues.getUrl()!=null) {
					String url = issues.getUrl();
					element.addProperty(ModelElementKeys.ISSUE_MANAGER, url);
					return false;
				}
			}
			return true;
		});
	}

	public void decorateJavaPackage(Element element, MavenProject mavenProject) {
		if(!element.getProperties().containsKey(ModelElementKeys.JAVA_PACKAGES)) {
			if(element.getProperties().containsKey(ModelElementKeys.JAVA_SOURCES)) {
				String paths = element.getProperties().get(ModelElementKeys.JAVA_SOURCES);
				String packages = Stream.of(paths.split(";"))
					.map(ThrowingFunction.unchecked(fileSystemManager::resolveFile))
					.filter(ThrowingPredicate.unchecked(FileObject::exists))
					.map(ThrowingFunction.unchecked(this::findPackagesInPath))
					.flatMap(Collection::stream)
					.collect(Collectors.joining(";"));
				if(!packages.isEmpty())
					element.addProperty(ModelElementKeys.JAVA_PACKAGES, packages);
			}
		}
	}

	public void decorateJavaSource(Element element, MavenProject mavenProject) {
		String mavenPomUrl = mavenProject.getProperties().get(MavenPomReader.MAVEN_MODULE_DIR).toString();
		List<String> mavenSourceRoots = Optional.ofNullable((List<String>) mavenProject.getCompileSourceRoots())
				.stream().filter(list -> !list.isEmpty()).findAny().orElse(Arrays.asList("src/main/java"));
		String sourcePaths = mavenSourceRoots.stream().map(relativeFolder -> mavenPomUrl + "/" + relativeFolder)
				.map(ThrowingFunction.unchecked(relativeFolder -> fileSystemManager.resolveFile(relativeFolder)))
				.filter(ThrowingPredicate.unchecked(relativePath -> relativePath.exists()))
				.map(ThrowingFunction.unchecked(relativeFolder -> relativeFolder.getURL().toString()))
				.collect(Collectors.joining(";"));
		if (!sourcePaths.isEmpty())
			element.addProperty(ModelElementKeys.JAVA_SOURCES, sourcePaths);
	}

	/**
	 * Extract all properties having the good prefix from maven pom and copy them
	 * into model element properties. THis copy may overwrite properties defined
	 * elsewhen
	 * 
	 * @param element      element to copy properties into
	 * @param mavenProject maven project to extract properties from
	 */
	public void decorateMavenProperties(Element element, MavenProject mavenProject) {
		mavenProject.getProperties().entrySet().stream()
				.map(entry -> Map.entry(entry.getKey().toString(), entry.getValue().toString()))
				.filter(entry -> entry.getKey().startsWith(ModelElementKeys.PREFIX))
				.forEach(entry -> element.addProperty(entry.getKey(), entry.getValue()));
	}

	public void decorateScmUrl(Element element, MavenProject mavenProject) {
		decorateRecursively(mavenProject, (project, children) -> {
			if(project.getScm()!=null) {
				Scm scm = project.getScm();
				if(scm.getUrl()!=null) {
					String url = scm.getUrl();
					// We're not in our project, but in some parent. To go back to our project, we must add to that url
					// all the children paths
					url = url + children.stream().map(p -> p.getArtifactId()).collect(Collectors.joining("/"));
					element.addProperty(org.ndx.aadarchi.base.enhancers.ModelElementKeys.Scm.PROJECT, url);
					return false;
				}
			}
			// We need to go deeper
			return true;
		});
	}
	
	public Collection<String> findPackagesInPath(FileObject current) throws FileSystemException {
		FileObject[] children = current.findFiles(new FileTypeSelector(FileType.FILE));
		return Stream.of(children)
			.map(ThrowingFunction.unchecked(FileObject::getParent))
			.distinct()
			.map(FileObject::getPath)
			.map(folder -> current.getPath().relativize(folder).toString())
			.map(folder -> folder.replace('/', '.').replace('\\', '.'))
			.sorted()
			.collect(Collectors.toList());
	}


	protected static String technologyWithVersionFromProperty(MavenProject mavenProject, String technology, String... propertyNames) {
		return technology + Stream.of(propertyNames)
				.flatMap(p -> new HashSet(Arrays.asList(p, p.replace('.', '-'), p.replace('-', '.'))).stream())
				.filter(p -> mavenProject.getProperties().containsKey(p))
				.map(p -> mavenProject.getProperties().get(p))
				.map(text -> " "+text)
				.findFirst()
				.orElse("")
				;
	}

	public static Set<String> doDecorateTechnology(MavenProject project) {
		Set<String> technologies = new LinkedHashSet<String>();
		switch (project.getPackaging()) {
		case "ear":
			technologies.add("Java");
			technologies.add("ear");
			break;
		case "war":
			technologies.add("Java");
			technologies.add("war");
		case "jar":
			// If there is a java version property, use it to detect Java version
			technologies.add(MavenPomDecorator.technologyWithVersionFromProperty(project, "Java", "java.version", "maven.compiler.target"));
			break;
		case "pom":
			break;
		default:
			logger.warning(String.format(
					"Maven component %s uses packaging %s which we don't know. Please submit a bug to aadarchi-documentation-system to have this particular packaging correctly handled",
					project, project.getPackaging()));
		}
		for (Dependency dependency : (List<Dependency>) project.getDependencies()) {
			switch (dependency.getGroupId()) {
			case "org.apache.maven":
				if("maven-plugin-api".equals(dependency.getArtifactId())) {
					technologies.add("maven-plugin");
				}
				break;
			case "io.quarkus":
				technologies.add(MavenPomDecorator.technologyWithVersionFromProperty(project, "Quarkus", "quarkus.platform.version"));
				break;
			case "org.springframework":
				technologies.add("Spring");
				break;
			case "org.apache.camel":
				technologies.add(MavenPomDecorator.technologyWithVersionFromProperty(project, "Apache Camel", "camel.version"));
				break;
			case "org.springframework.boot":
				technologies.add("Spring Boot");
				break;
			case "com.google.gwt":
				technologies.add("GWT");
				break;
			case "javax.enterprise":
				if("cdi-api".equals(dependency.getArtifactId())) {
					technologies.add("CDI");
				}
				break;
			}
		}
		return technologies;
	}

	/**
	 * Creates the string containing details about the used technology. For that we
	 * will simply read the maven project plugins (for compilers) and dependencies
	 * (for frameworks)
	 * 
	 * @param project
	 * @return a string giving details about important project infos
	 */
	public static String decorateTechnology(MavenProject project) {
		Set<String> technologies = new TreeSet<String>();
		decorateRecursively(project, (p, l) -> { 
			technologies.addAll(MavenPomDecorator.doDecorateTechnology(p));
			// We should explore all parent poms
			return true;
		});
		return technologies.stream().collect(Collectors.joining(","));
	}

}

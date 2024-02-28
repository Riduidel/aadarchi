package org.ndx.aadarchi.inferer.maven;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.Scm;
import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.inferer.maven.technologies.TechnologyDecorator;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.ThrowingPredicate;
import com.structurizr.model.Element;

@Default
@ApplicationScoped
public class MavenPomDecorator {
	@Inject
	Logger logger;
	@Inject
	FileSystemManager fileSystemManager;
	
	@Inject TechnologyDecorator technologyDecorator;

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
		decorateMavenProperties(element, mavenProject);
		// I use optional to avoid writing endless if(...!=null) lines.
		// It may be ugly, but I'm trying a /style/ here
		decorateCoordinates(element, mavenProject);
		decorateScmUrl(element, mavenProject);
		decorateIssueManager(element, mavenProject);
		decorateJavaSource(element, mavenProject);
		decorateJavaPackage(element, mavenProject);
		technologyDecorator.decorateTechnology(element, mavenProject);
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
	 * into model element properties. This copy will not overwrite properties defined
	 * elsewhen
	 * 
	 * @param element      element to copy properties into
	 * @param mavenProject maven project to extract properties from
	 */
	public void decorateMavenProperties(Element element, MavenProject mavenProject) {
		mavenProject.getProperties().entrySet().stream()
				.map(entry -> Map.entry(entry.getKey().toString(), entry.getValue().toString()))
				.filter(entry -> entry.getKey().startsWith(ModelElementKeys.PREFIX))
				.filter(entry -> !element.getProperties().containsKey(entry.getKey()))
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
	
	public String findPackagesInPath(FileObject current) throws FileSystemException {
		return findFirstPackageLevelInPath(current, current);
	}


	private String findFirstPackageLevelInPath(FileObject root, FileObject current) throws FileSystemException {
		FileObject[] children = current.getChildren();
		if(children.length==1) {
			FileObject child = children[0];
			if(child.isFolder()) {
				return findFirstPackageLevelInPath(root, child);
			}
		}
		// In any other case,  we assume we have found the first package folder
		return root.getPath().relativize(current.getPath())
			.toString()
			.replace('/', '.').replace('\\', '.');
	}
}

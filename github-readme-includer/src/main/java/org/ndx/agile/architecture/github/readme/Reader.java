package org.ndx.agile.architecture.github.readme;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

import nl.jworks.markdown_to_asciidoc.Converter;

public class Reader implements ModelEnhancer {
	/**
	 * Should contain the full GitHub url of project, including the github.com domain!
	 * This property has no default. When not set, this enhancer won't be invoked. 
	 */
	public static final String GITHUB_PROJECT = "agile.architecture.github.project";
	/**
	 * Readme path in project. Defaults to "readme.md"
	 */
	public static final String GITHUB_README = "agile.architecture.github.readme.path";
	
	/**
	 * Github access token.
	 */
	@Inject @ConfigProperty(name="agile.architecture.github.token")
	public String token;

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public int priority() {
		return 1000;
	}

	@Override
	public boolean startVisit(Model model) {
		return true;
	}

	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		return true;
	}

	@Override
	public boolean startVisit(Container container) {
		return container.getProperties().containsKey(GITHUB_PROJECT);
	}

	@Override
	public boolean startVisit(Component component) {
		return false;
	}

	@Override public void endVisit(Component component, OutputBuilder builder) {}

	/**
	 * On end visit, we will read the project infos and readme and write all that
	 * in the code subfolder of this container.
	 */
	@Override public void endVisit(Container container, OutputBuilder builder) {
		String githubProject = container.getProperties().get(GITHUB_PROJECT);
		try {
			String readmePath = container.getProperties().get(GITHUB_README);
			String content = getReadmeContent(token, 
					githubProject, 
					readmePath);
			// Now we have content as asciidoc, so let's write it to the conventional location
			FileUtils.write(builder.outputFor(AgileArchitectureSection.code, container, this, "adoc"), content, "UTF-8");
		} catch (IOException e) {
			throw new CantExtractReadme(String.format("Can't extract readme of container %s which is linked to GitHub project %s", 
					container.getCanonicalName(), githubProject), 
					e);
		}

	}

	String getReadmeContent(@Nonnull String oauthToken, @Nonnull String githubProject, @Nullable String readmePath) throws IOException {
		GitHub github = new GitHubBuilder().withOAuthToken(oauthToken).build();
		if(githubProject.contains("github.com")) {
			githubProject = githubProject.substring(githubProject.indexOf("github.com")+"github.com".length());
		}
		if(githubProject.startsWith("/")) {
			githubProject = githubProject.substring(1);
		}
		GHRepository repository = github.getRepository(githubProject);
		// Now we have a repository, let's get the readme
		// TODO use the version extracted from the dependency pom to get the good branch/tag
		GHContent readme = readReadmeByPath(repository, readmePath);
		// Now we have a readme, improve it to have asciidoc!
		String content = IOUtils.toString(readme.read());
		if(readme.getName().endsWith(".md")) {
			content = Converter.convertMarkdownToAsciiDoc(content);
		}
		return content;
	}

	GHContent readReadmeByPath(GHRepository repository, @Nullable String readmePath) throws IOException {
		if(readmePath==null) {
			List<GHContent> directory = repository.getDirectoryContent("/");
			return directory.stream()
				.filter(content -> content.getName().toLowerCase().startsWith("readme"))
				.findFirst()
				.orElseThrow(() -> new CantExtractReadme(
						String.format("We couldn't find any readme in %s", repository.getUrl())));
		} else {
			return repository.getFileContent(readmePath);
		}
	}

	@Override public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {}

	@Override public void endVisit(Model model, OutputBuilder builder) {}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return true;
	}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {}

}

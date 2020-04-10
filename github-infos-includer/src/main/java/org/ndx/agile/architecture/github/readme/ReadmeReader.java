package org.ndx.agile.architecture.github.readme;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
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
import org.ndx.agile.architecture.base.enhancers.Keys;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

import nl.jworks.markdown_to_asciidoc.Converter;

@ApplicationScoped
public class ReadmeReader implements ModelEnhancer {
	private static final String GITHUB_TOKEN = "agile.architecture.github.token";
	/**
	 * Github access token.
	 */
	@Inject @ConfigProperty(name=GITHUB_TOKEN) String token;
	
	@Inject @ConfigProperty(name="force") boolean force;
	
	@Inject Logger logger;

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public int priority() {
		return Constants.COMMON_PRIORITY;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		if(token==null) {
			throw new CantExtractReadme(
					String.format("No token has been provided. Have you set the %s system property?",
							GITHUB_TOKEN));
		}
		return true;
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
		return container.getProperties().containsKey(Keys.ELEMENT_PROJECT);
	}

	@Override
	public boolean startVisit(Component component) {
		return false;
	}

	@Override public void endVisit(Component component, OutputBuilder builder) {
		writeReadmeFor(component, builder);
	}

	/**
	 * On end visit, we will read the project infos and readme and write all that
	 * in the code subfolder of this container.
	 */
	@Override public void endVisit(Container container, OutputBuilder builder) {
		writeReadmeFor(container, builder);
	}

	@Override public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		writeReadmeFor(softwareSystem, builder);
	}

	@Override public void endVisit(Model model, OutputBuilder builder) {}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {}

	void writeReadmeFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Keys.ELEMENT_PROJECT)) {
			String elementProject = element.getProperties().get(Keys.ELEMENT_PROJECT);
			if(elementProject.contains(Constants.GITHUB_DOMAIN)) {
				String githubProject = elementProject;
				File outputFor = builder.outputFor(AgileArchitectureSection.code, element, this, "adoc");
				if(force && outputFor.exists()) {
					return;
				}
				try {
					String readmePath = element.getProperties().get(Keys.ELEMENT_README);
					logger.info(String.format("Reading readme for %s from %s/%s", element.getCanonicalName(), githubProject, readmePath));
					String content = getReadmeContent(token, 
							githubProject, 
							readmePath);
					// Now we have content as asciidoc, so let's write it to the conventional location
					FileUtils.write(outputFor, content, "UTF-8");
				} catch (IOException e) {
					throw new CantExtractReadme(String.format("Can't extract readme of container %s which is linked to GitHub project %s", 
							element.getCanonicalName(), githubProject), 
							e);
				}
			}
		}
	}

	String getReadmeContent(@Nonnull String oauthToken, @Nonnull String githubProject, @Nullable String readmePath) throws IOException {
		GitHub github = new GitHubBuilder().withOAuthToken(oauthToken).build();
		if(githubProject.contains(Constants.GITHUB_DOMAIN)) {
			githubProject = githubProject.substring(githubProject.indexOf(Constants.GITHUB_DOMAIN)+Constants.GITHUB_DOMAIN.length());
		}
		if(githubProject.startsWith("/")) {
			githubProject = githubProject.substring(1);
		}
		GHRepository repository = github.getRepository(githubProject);
		// Now we have a repository, let's get the readme
		// TODO use the version extracted from the dependency pom to get the good branch/tag
		GHContent readme = readReadmeByPath(repository, readmePath);
		// Now we have a readme, improve it to have asciidoc!
		String content = IOUtils.toString(readme.read(), "UTF-8");
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


}

package org.ndx.agile.architecture.github.readme;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
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
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.Keys;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import com.structurizr.model.Element;

import nl.jworks.markdown_to_asciidoc.Converter;

@ApplicationScoped
public class GitHubReadmeReader extends ModelElementAdapter {
	private static final String GITHUB_TOKEN = "agile.architecture.github.token";
	@Inject @ConfigProperty(name="force") boolean force;
	
	@Inject Logger logger;
	private GitHub github;

	@PostConstruct public void createGithubApi(@ConfigProperty(name=GITHUB_TOKEN) String token) {
		if(token==null) {
			throw new CantExtractReadme(
					String.format("No token has been provided. Have you set the %s system property?",
							GITHUB_TOKEN));
		}
		try {
			github = new GitHubBuilder().withOAuthToken(token).build();
		} catch (IOException e) {
			throw new CantExtractReadme(String.format("Unable to build a GitHUb client instance"), e);
		}
	}


	@Override
	public int priority() {
		return Constants.COMMON_PRIORITY;
	}

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
					String content = getReadmeContent(githubProject, readmePath);
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

	String getReadmeContent(@Nonnull String githubProject, @Nullable String readmePath) {
		try {
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
		} catch(IOException e) {
			throw new CantExtractReadme(String.format("Can't extract readme for %s", githubProject), e);
		} 
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

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		writeReadmeFor(element, builder);
	}


}

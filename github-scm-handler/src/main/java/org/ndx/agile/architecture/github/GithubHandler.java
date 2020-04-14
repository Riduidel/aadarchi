package org.ndx.agile.architecture.github;

import static org.ndx.agile.architecture.github.Constants.CONFIG_GITHUB_TOKEN;
import static org.ndx.agile.architecture.github.Constants.GITHUB_DOMAIN;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.ndx.agile.architecture.base.enhancers.scm.SCMFile;
import org.ndx.agile.architecture.base.enhancers.scm.SCMHandler;

@ApplicationScoped
public class GithubHandler implements SCMHandler {
	@Inject Logger logger;
	private GitHub github;
	@Inject public void initialize(@ConfigProperty(name=CONFIG_GITHUB_TOKEN) String token) {
		if(token==null) {
			throw new GitHubHandlerException(String.format("Can't connect to GitHub if token %s isn't defined as system property", Constants.CONFIG_GITHUB_TOKEN));
		}
		try {
			github = new GitHubBuilder().withOAuthToken(token).build();
		} catch (IOException e) {
			throw new GitHubHandlerException("Can't connect to GitHub! Maybe the token is bad");
		}
	}

	@Override
	public boolean canHandle(String project) {
		return project.contains(GITHUB_DOMAIN);
	}

	@Override
	public Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) {
		if(project.contains(GITHUB_DOMAIN)) {
			project = project.substring(project.indexOf(GITHUB_DOMAIN)+GITHUB_DOMAIN.length());
		}
		if(project.startsWith("/")) {
			project = project.substring(1);
		}
		try {
			GHRepository repository = github.getRepository(project);
			List<GHContent> dir = repository.getDirectoryContent(path);
			return dir.stream()
				.map(content -> new GitHubFile(logger, repository, content))
				.filter(content -> filter.test(content))
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new GitHubHandlerException(
					String.format("Unable to find file in %s/%s", project, path)
					,e);
		}
	}

	@Override
	public String linkTo(String project, String path) {
		return String.format("%s/blob/master/%s", project, path);
	}

	@Override
	public String asciidocText() {
		return "icon:github[set=fab] GitHub";
	}

}

package org.ndx.agile.architecture.github;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.Configuration;
import org.kohsuke.MetaInfServices;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.ndx.agile.architecture.base.enhancers.scm.SCMFile;
import org.ndx.agile.architecture.base.enhancers.scm.SCMHandler;

@MetaInfServices
public class GithubSCMHandler implements SCMHandler {
	private static final Logger logger = Logger.getLogger(GithubSCMHandler.class.getName());
	GitHub github;
	
	@Override
	public boolean canHandle(String project) {
		return Constants.isGitHubProject(project);
	}

	@Override
	public Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) {
		if(Constants.isGitHubProject(project)) {
			project = Constants.getGitHubProjectName(project);
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

	@Override
	public void configure(Configuration configuration) {
		github = new GitHubProducer().initialize(configuration.getString(Constants.CONFIG_GITHUB_TOKEN));
	}

}

package org.ndx.agile.architecture.github;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.commons.configuration2.Configuration;
import org.kohsuke.MetaInfServices;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.ndx.agile.architecture.base.enhancers.tickets.Ticket;
import org.ndx.agile.architecture.base.enhancers.tickets.TicketsHandler;

@MetaInfServices
public class GitHubTicketsHandler implements TicketsHandler {
	private static final Logger logger = Logger.getLogger(GitHubTicketsHandler.class.getName());
	GitHub github;

	@Override
	public boolean canHandle(String ticketsProject) {
		return Constants.isGitHubProject(ticketsProject);
	}

	@Override
	public Collection<Ticket> getTicketsTagged(String project, String label) {
		if(Constants.isGitHubProject(project)) {
			project = Constants.getGitHubProjectName(project);
		}
		try {
			GHRepository repository = github.getRepository(project);
			List<GHIssue> allIssues = repository.getIssues(GHIssueState.ALL);
			return allIssues.stream()
				.filter(issue -> this.hasLabel(issue, label))
				.map(this::toTicket)
				.collect(Collectors.toList());
		} catch(IOException e) {
			throw new GitHubHandlerException(String.format("Unable to read issues tagged %s in project %s", 
					label, project), e);
		}
	}
	
	private Ticket toTicket(GHIssue issue) {
		return new GitHubTicket(issue);
	}

	private boolean hasLabel(GHIssue issue, String label) {
		return issue.getLabels().stream()
				.map(l -> l.getName())
				.filter(l -> label.equals(l))
				.findAny()
				.isPresent();
	}

	@Override
	public String getIssuesUrl(String project) {
		if(Constants.isGitHubProject(project)) {
			project = Constants.getGitHubProjectName(project);
		}
		try {
			GHRepository repository = github.getRepository(project);
			return repository.getHtmlUrl()+"/issues";
		} catch (IOException e) {
			throw new GitHubHandlerException(String.format("Unable to read project %s", 
					project), e);
		}
	}

	@Override
	public String getProjectName(String project) {
		if(Constants.isGitHubProject(project)) {
			project = Constants.getGitHubProjectName(project);
		}
		try {
			GHRepository repository = github.getRepository(project);
			return "icon:github[set=fab] " + repository.getName();
		} catch (IOException e) {
			throw new GitHubHandlerException(String.format("Unable to read project %s", 
					project), e);
		}
	}


	@Override
	public void configure(Configuration configuration) {
		github = new GitHubProducer().initialize(configuration.getString(Constants.CONFIG_GITHUB_TOKEN));
	}

}

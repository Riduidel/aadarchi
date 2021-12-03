package org.ndx.agile.architecture.gitlab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.configuration2.ImmutableConfiguration;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.IssueFilter;
import org.ndx.agile.architecture.base.enhancers.tickets.Ticket;
import org.ndx.agile.architecture.base.enhancers.tickets.TicketsHandler;

import com.structurizr.annotation.Component;

/**
 * Gitlab implementation of ticket handler
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java")
public class GitLabTicketsHandler implements TicketsHandler {
	GitLabApi gitlab;

	@Override
	public void configure(ImmutableConfiguration configuration) {
		gitlab = new GitLabProducer().initialize(
				configuration.getString(Constants.CONFIG_GITLAB_TOKEN),
				configuration.getString(Constants.CONFIG_GITLAB_URL, "https://gitlab.com"));
	}

	@Override
	public boolean canHandle(String ticketsProject) {
		return Constants.isGitLabProject(gitlab, ticketsProject);
	}

	@Override
	public Collection<Ticket> getTicketsTagged(String project, String label) {
		try {
			Object projectId = gitlab.getIssuesApi().getProjectIdOrPath(project);
			IssueFilter filter = new IssueFilter();
			filter.setLabels(Arrays.asList(label));
			Collection<Ticket> returned = new ArrayList<>();
			for(Issue issue : gitlab.getIssuesApi().getIssues(filter)) {
				returned.add(new GitLabTicket(issue, 
						gitlab.getDiscussionsApi().getIssueDiscussions(projectId, issue.getId())));
			}
			return returned;
		} catch (GitLabApiException e) {
			throw new GitLabHandlerException(String.format("Unable to et project name of %s",  project), e);
		}
	}

	@Override
	public String getIssuesUrl(String project) {
		return "TODO";
	}

	@Override
	public String getProjectName(String project) {
		try {
			Object projectId = gitlab.getProjectApi().getProjectIdOrPath(project);
			return gitlab.getProjectApi().getProject(projectId).getName();
		} catch (GitLabApiException e) {
			throw new GitLabHandlerException(String.format("Unable to et project name of %s",  project), e);
		}
	}

}

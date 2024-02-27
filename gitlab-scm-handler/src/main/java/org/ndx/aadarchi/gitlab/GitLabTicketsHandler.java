package org.ndx.aadarchi.gitlab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.IssueFilter;
import org.ndx.aadarchi.base.enhancers.tickets.Ticket;
import org.ndx.aadarchi.base.enhancers.tickets.TicketsHandler;

import com.structurizr.annotation.Component;

/**
 * Gitlab implementation of ticket handler
 * @author nicolas-delsaux
 *
 */
@ApplicationScoped
@Component(technology = "Java")
public class GitLabTicketsHandler implements TicketsHandler {
	@Inject GitLabContainer gitlab;

	@Override
	public boolean canHandle(String ticketsProject) {
		return Constants.isGitLabProject(gitlab.getApi(), ticketsProject);
	}

	@Override
	public Collection<Ticket> getTicketsTagged(String project, String label) {
		try {
			Object projectId = gitlab.getApi().getIssuesApi().getProjectIdOrPath(project);
			IssueFilter filter = new IssueFilter();
			filter.setLabels(Arrays.asList(label));
			Collection<Ticket> returned = new ArrayList<>();
			for(Issue issue : gitlab.getApi().getIssuesApi().getIssues(filter)) {
				returned.add(new GitLabTicket(issue, 
						gitlab.getApi().getDiscussionsApi().getIssueDiscussions(projectId, issue.getId())));
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
			Object projectId = gitlab.getApi().getProjectApi().getProjectIdOrPath(project);
			return gitlab.getApi().getProjectApi().getProject(projectId).getName();
		} catch (GitLabApiException e) {
			throw new GitLabHandlerException(String.format("Unable to et project name of %s",  project), e);
		}
	}

}

package org.ndx.agile.architecture.gitlab;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.gitlab4j.api.models.Discussion;
import org.gitlab4j.api.models.Issue;
import org.ndx.agile.architecture.base.enhancers.tickets.Comment;
import org.ndx.agile.architecture.base.enhancers.tickets.Ticket;
import org.ndx.agile.architecture.base.enhancers.tickets.TicketStatus;

public class GitLabTicket implements Ticket {

	private Issue source;
	private List<Discussion> discussion;

	public GitLabTicket(Issue issue, List<Discussion> list) {
		this.source = issue;
		this.discussion = list;
	}

	@Override
	public String getUrl() {
		return source.getWebUrl();
	}

	@Override
	public String getId() {
		return source.getExternalId();
	}

	@Override
	public String getTitle() {
		return source.getTitle();
	}

	@Override
	public String getText() {
		return source.getDescription();
	}

	@Override
	public TicketStatus getStatus() {
		switch(source.getState()) {
		case OPENED:
		case REOPENED:
			return TicketStatus.OPEN;
		case CLOSED:
			return TicketStatus.CLOSED;
		default:
			throw new GitLabHandlerException(String.format("How can an issue %s be in state %s?", source.getWebUrl(), source.getState()));
		}
	}

	@Override
	public Date getLastDate() {
		switch (getStatus()) {
		case OPEN:
			return source.getCreatedAt();
		case CLOSED:
			return source.getClosedAt();
		default:
			throw new GitLabHandlerException(String.format("How can an issue %s be in state %s?", source.getWebUrl(), source.getState()));
		}
	}

	@Override
	public Optional<Date> getDateOf(TicketStatus changeTo) {
		switch (changeTo) {
		case OPEN:
			return Optional.ofNullable(source.getCreatedAt());
		case CLOSED:
			return Optional.ofNullable(source.getClosedAt());
		default:
			throw new GitLabHandlerException(String.format("How can an issue %s be in state %s?", source.getWebUrl(), source.getState()));
		}
	}

	@Override
	public Collection<Comment> getComments() {
		return discussion.stream()
			.flatMap(discussion -> discussion.getNotes().stream())
			.map(note -> new GitLabComment(note))
			.collect(Collectors.toList());
	}

}

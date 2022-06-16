package org.ndx.aadarchi.github;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import org.kohsuke.github.GHIssue;
import org.ndx.aadarchi.base.enhancers.tickets.Comment;
import org.ndx.aadarchi.base.enhancers.tickets.Ticket;
import org.ndx.aadarchi.base.enhancers.tickets.TicketStatus;

import nl.jworks.markdown_to_asciidoc.Converter;

public class GitHubTicket implements Ticket {

	private GHIssue source;

	public GitHubTicket(GHIssue issue) {
		this.source = issue;
	}

	@Override
	public String getId() {
		return Integer.toString(source.getNumber());
	}

	@Override
	public String getTitle() {
		return source.getTitle();
	}

	@Override
	public String getText() {
		return Converter.convertMarkdownToAsciiDoc(source.getBody());
	}

	@Override
	public TicketStatus getStatus() {
		switch(source.getState()) {
		case OPEN:
			return TicketStatus.OPEN;
		case CLOSED:
			return TicketStatus.CLOSED;
		default:
			throw new GitHubHandlerException(String.format("How can an issue %s be in state %s?",
					source.getHtmlUrl(),
					source.getState()
					));
		}
	}

	@Override
	public Optional<Date> getDateOf(TicketStatus changeTo) {
		switch(changeTo) {
		case CLOSED:
			return Optional.ofNullable(source.getClosedAt());
		case OPEN:
			try {
				return Optional.ofNullable(source.getCreatedAt());
			} catch (IOException e) {
				throw new GitHubHandlerException(String.format("Can't get open date of %s",
						source.getHtmlUrl()
						));
			}
		default:
			throw new GitHubHandlerException(String.format("How can an issue %s be in state %s?",
					source.getHtmlUrl(),
					changeTo
					));
		}
	}

	@Override
	public Date getLastDate() {
		try {
			switch(getStatus()) {
			case OPEN:
				return source.getCreatedAt();
			case CLOSED:
				return source.getClosedAt();
			default:
				throw new GitHubHandlerException(String.format("How can an issue %s be in state %s?",
						source.getHtmlUrl()
						));
			}
		} catch(IOException ioe) {
			throw new GitHubHandlerException(
					String.format("Unable to read date of issue %s", source.getHtmlUrl()),
							ioe);
		}
	}

	@Override
	public String getUrl() {
		return source.getHtmlUrl().toExternalForm();
	}

	@Override
	public Collection<Comment> getComments() {
		try {
			return source.getComments().stream()
				.map(GitHubComment::new)
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new GitHubHandlerException(String.format("Unable to get issue comments for %s",
					source.getHtmlUrl()
					));
		}
	}

}

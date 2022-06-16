package org.ndx.aadarchi.github;

import java.io.IOException;
import java.util.Date;

import org.kohsuke.github.GHIssueComment;
import org.ndx.aadarchi.base.enhancers.tickets.Comment;

import nl.jworks.markdown_to_asciidoc.Converter;

public class GitHubComment implements Comment {
	private GHIssueComment source;

	public GitHubComment(GHIssueComment source) {
		this.source = source;
	}

	@Override
	public Date getDate() {
		try {
			return source.getUpdatedAt();
		} catch (IOException e) {
			throw new GitHubHandlerException(String.format("Unable to get date for comment %s", source.getHtmlUrl()));
		}
	}

	@Override
	public String getText() {
		return Converter.convertMarkdownToAsciiDoc(source.getBody());
	}

}

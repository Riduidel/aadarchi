package org.ndx.aadarchi.gitlab;

import java.util.Date;

import org.gitlab4j.api.models.Note;
import org.ndx.aadarchi.base.enhancers.tickets.Comment;

public class GitLabComment implements Comment {

	private Note source;

	public GitLabComment(Note note) {
		this.source = note;
	}

	@Override
	public Date getDate() {
		return source.getUpdatedAt();
	}

	@Override
	public String getText() {
		return source.getBody();
	}

}

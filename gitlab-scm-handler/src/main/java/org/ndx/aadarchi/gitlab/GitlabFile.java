package org.ndx.aadarchi.gitlab;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;

import org.gitlab4j.api.models.RepositoryFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;

public class GitlabFile implements SCMFile {

	private RepositoryFile file;
	private long lastModified;

	public GitlabFile(RepositoryFile file, Date date) {
		this.file = file;
		this.lastModified = date.getTime();
	}

	@Override
	public String name() {
		return file.getFileName();
	}

	@Override
	public InputStream content() {
		return new ByteArrayInputStream(file.getContent().getBytes());
	}

	@Override
	public long lastModified() {
		return lastModified;
	}

	@Override
	public String url() {
		return null;
	}

}

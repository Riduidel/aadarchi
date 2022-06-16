package org.ndx.aadarchi.gitlab;

import java.io.InputStream;

import org.gitlab4j.api.models.RepositoryFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;

public class GitlabFile implements SCMFile {

	public GitlabFile(RepositoryFile file) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InputStream content() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long lastModified() {
		// TODO Auto-generated method stub
		return 0;
	}

}

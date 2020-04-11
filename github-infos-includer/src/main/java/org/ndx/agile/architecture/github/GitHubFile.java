package org.ndx.agile.architecture.github;

import java.io.IOException;
import java.io.InputStream;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.ndx.agile.architecture.base.enhancers.scm.SCMFile;

public class GitHubFile implements SCMFile {

	private GHContent source;
	private GHRepository repository;

	public GitHubFile(GHRepository repository, GHContent content) {
		this.repository = repository;
		this.source = content;
	}

	@Override
	public InputStream content() {
		try {
			return source.read();
		} catch (IOException e) {
			throw new GitHubHandlerException(String.format("Can't get content of %s", source.getPath()), e);
		}
	}

	@Override
	public String name() {
		return source.getName();
	}

	@Override
	public long lastModified() {
		GHCommit commit;
		try {
			commit = repository.getCommit(source.getSha());
			return commit.getCommitDate().getTime();
		} catch (IOException e) {
			throw new GitHubHandlerException(
					String.format("Unable to get last commit info for %s (sha1 is %s)",
							source.getPath(),
							source.getSha()
					),
					e);
		}
	}

}
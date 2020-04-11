package org.ndx.agile.architecture.github;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.ndx.agile.architecture.base.enhancers.scm.SCMFile;

public class GitHubFile implements SCMFile {

	private GHContent source;
	private GHRepository repository;
	private Logger logger;

	public GitHubFile(Logger logger, GHRepository repository, GHContent content) {
		this.logger = logger;
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
			logger.info(() -> String.format("Getting last commit info for path %s", source.getPath())); 
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
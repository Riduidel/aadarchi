package org.ndx.aadarchi.github;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.exception.WrappedException;

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
		try {
			PagedIterable<GHCommit> commits = repository.queryCommits().path(source.getPath()).list();
			return commits.toList().stream()
				.findFirst()
				.map(ThrowingFunction.unchecked(commit -> commit.getCommitDate().getTime()))
				.orElse(0l);
		} catch (WrappedException | IOException e) {
			throw new GitHubHandlerException(
					String.format("Unable to get last commit info for %s (sha1 is %s)",
							source.getPath(),
							source.getSha()
					),
					e);
		}
	}

	@Override
	public String toString() {
		return source.getHtmlUrl();
	}

	@Override
	public String url() {
		return source.getUrl();
	}
}
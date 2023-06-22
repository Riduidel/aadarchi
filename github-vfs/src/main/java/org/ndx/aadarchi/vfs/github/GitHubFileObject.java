package org.ndx.aadarchi.vfs.github;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystemException;
import java.util.Optional;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.exception.WrappedException;

public class GitHubFileObject extends AbstractFileObject<GitHubFileSystem> implements FileObject {

	private GHRepository repository;
	
	private Optional<GHContent> ghContent = Optional.empty();

	public GitHubFileObject(GitHubFileName name, GitHubFileSystem gitHubFileSystem, GHRepository ghRepository) {
		super(name, gitHubFileSystem);
		this.repository = ghRepository;
	}
	
	private GHContent getGHContent() throws IOException {
		if(ghContent.isEmpty()) {
			ghContent = Optional.of(repository.getFileContent(getName().getPathInRepository()));
		}
		return ghContent.get();
	}

	@Override
	protected long doGetContentSize() throws Exception {
		return getGHContent().getSize();
	}

	@Override
	protected FileType doGetType() throws Exception {
		try {
			return getGHContent().getType().equals("file") ? FileType.FILE : FileType.FOLDER;
		} catch(Exception e) {
			return FileType.FOLDER;
		}
	}
	
	@Override
	protected InputStream doGetInputStream() throws Exception {
		return getGHContent().read();
	}
	
	@Override
	protected long doGetLastModifiedTime() throws Exception {
		try {
			PagedIterable<GHCommit> commits = repository.queryCommits()
					.pageSize(1)
					.path(getName().getPathInRepository())
					.list();
			return commits.toList().stream()
				.findFirst()
				.map(ThrowingFunction.unchecked(commit -> commit.getCommitDate().getTime()))
				.orElse(0l);
		} catch (WrappedException | IOException e) {
			throw new IOException(
					String.format("Unable to get last commit info for %s",
							getPath()
					),
					e);
		}

	}
	
	@Override
	public GitHubFileName getName() {
		return (GitHubFileName) super.getName();
	}

	@Override
	protected String[] doListChildren() throws Exception {
		switch(getType()) {
		case FILE: return new String[0];
		default:
			return repository.getDirectoryContent(getName().getPathInRepository())
					.stream()
					.map(GHContent::getPath)
					.toArray(String[]::new);
		}
	}

	@Override
	protected FileObject[] doListChildrenResolved() throws Exception {
		switch(getType()) {
		case FILE: return new FileObject[0];
		default:
			return repository.getDirectoryContent(getName().getPathInRepository())
					.stream()
					.map(this::createChildFrom)
					.map(githubFileName -> 
						new GitHubFileObject(githubFileName, getAbstractFileSystem(), repository))
					.toArray(size -> new GitHubFileObject[size]);
		}
	}
	
	private GitHubFileName createChildFrom(GHContent childDescription) {
		return new GitHubFileName(getName(), childDescription);
	}
}

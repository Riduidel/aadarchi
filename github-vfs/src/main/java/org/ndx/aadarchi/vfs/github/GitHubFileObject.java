package org.ndx.aadarchi.vfs.github;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;

public class GitHubFileObject extends AbstractFileObject<GitHubFileSystem> implements FileObject {

	private GHRepository repository;
	
	private Optional<GHContent> ghContent = Optional.empty();

	private FileType type = FileType.FILE_OR_FOLDER;

	public GitHubFileObject(GitHubFileName name, GitHubFileSystem gitHubFileSystem, GHRepository ghRepository) {
		super(name, gitHubFileSystem);
		this.repository = ghRepository;
	}
	
	private GHContent getGHContent() throws IOException {
		if(ghContent.isEmpty()) {
			ghContent = Optional.of(repository.getFileContent(getName().getPathInRepository()));
			switch(ghContent.get().getType()) {
			case "dir":
				type = FileType.FOLDER;
				break;
			case "file":
				type = FileType.FILE;
				break;
			}
		}
		return ghContent.get();
	}

	@Override
	protected long doGetContentSize() throws Exception {
		return getGHContent().getSize();
	}

	@Override
	protected FileType doGetType() throws Exception {
		return type;
	}
	
	@Override
	protected InputStream doGetInputStream() throws Exception {
		return getGHContent().read();
	}
	
	@Override
	public GitHubFileName getName() {
		return (GitHubFileName) super.getName();
	}

	@Override
	protected String[] doListChildren() throws Exception {
		return repository.getDirectoryContent(getName().getPathInRepository())
				.stream()
				.map(GHContent::getPath)
				.toArray(String[]::new);
	}

}

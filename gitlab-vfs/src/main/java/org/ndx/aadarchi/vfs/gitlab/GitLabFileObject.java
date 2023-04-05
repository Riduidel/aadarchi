package org.ndx.aadarchi.vfs.gitlab;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileType;
import org.apache.commons.vfs2.provider.AbstractFileObject;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;

public class GitLabFileObject extends AbstractFileObject<GitLabFileSystem> implements FileObject {

	private GitLabApi gitlab;
	private Optional<RepositoryFile> repositoryFile = Optional.empty();

	public GitLabFileObject(GitLabFileName name, GitLabFileSystem gitHubFileSystem, GitLabApi gitlab) {
		super(name, gitHubFileSystem);
		this.gitlab = gitlab;
	}
	
	public RepositoryFile getRepositoryFile() throws FileSystemException {
		if(repositoryFile.isEmpty()) {
			try {
				repositoryFile = 
						Optional.of(gitlab.getRepositoryFileApi().getFile(
								getName().getProjectObject().getId(), 
								getName().getPathInRepository(), 
								getName().getBranch()));
			} catch (GitLabApiException e) {
				throw new FileSystemException("Unable to get repository file for "+getName().getFriendlyURI(), e);
			}
		}
		return repositoryFile.get();
	}

	@Override
	protected long doGetContentSize() throws Exception {
		return getRepositoryFile().getSize();
	}

	@Override
	protected FileType doGetType() throws Exception {
		return FileType.FILE_OR_FOLDER;
	}
	
	@Override
	protected InputStream doGetInputStream() throws Exception {
		return gitlab.getRepositoryFileApi().getRawFile(getName().getProject(), getName().getBranch(), getName().getPath());
	}
	
	@Override
	public GitLabFileName getName() {
		return (GitLabFileName) super.getName();
	}

	@Override
	protected String[] doListChildren() throws Exception {
		List<TreeItem> items = gitlab.getRepositoryApi().getTree(getName().getProjectObject().getId(), getName().getPathInRepository(), null);
		return items.stream()
				.map(TreeItem::getPath)
				.toArray(String[]::new);
	}

}

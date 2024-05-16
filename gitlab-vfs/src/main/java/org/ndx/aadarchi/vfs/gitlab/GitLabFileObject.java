package org.ndx.aadarchi.vfs.gitlab;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
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

	public GitLabFileObject(GitLabFileName name, GitLabFileSystem gitLabFileSystem, GitLabApi gitlab) {
		super(name, gitLabFileSystem);
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
		try {
			long size = doGetContentSize();
			if(size==0) {
				return FileType.FOLDER;
			} else {
				return FileType.FILE;
			}
		} catch(FileSystemException e) {
			if (e.getCause() instanceof GitLabApiException) {
				GitLabApiException gitlabException = (GitLabApiException) e.getCause();
				if(gitlabException.getMessage().contains("404")) {
					return FileType.FILE_OR_FOLDER;
				} else {
					return FileType.FOLDER;
				}
			}
			return FileType.FOLDER;
		}
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
		List<TreeItem> items = gitlab.getRepositoryApi().getTree(
				getName().getProjectObject().getId(), 
				getName().getPathInRepository(), 
				getName().getBranch());
		return items.stream()
				.map(TreeItem::getName)
				.toArray(String[]::new);
	}

	@Override
	protected InputStream doGetInputStream(int bufferSize) throws Exception {
		String encodedContent = getRepositoryFile().getContent();
		var decodedContent = Base64.decodeBase64(encodedContent);
		return new BufferedInputStream(
				new ByteArrayInputStream(decodedContent), bufferSize);
	}
}

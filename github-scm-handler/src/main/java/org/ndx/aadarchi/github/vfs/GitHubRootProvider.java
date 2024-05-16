package org.ndx.aadarchi.github.vfs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.ndx.aadarchi.vfs.github.GitHubFileProvider;

import jakarta.inject.Inject;

public class GitHubRootProvider {
	@Inject FileSystemManager fileSystemManager;
	@Inject FileSystemOptions options;
	public FileObject getProjectRoot(String project) throws FileSystemException {
		return fileSystemManager.resolveFile(GitHubFileProvider.urlFor(project), options);
	}
}

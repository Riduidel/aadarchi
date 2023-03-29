package org.ndx.aadarchi.github.vfs;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.kohsuke.github.GitHub;
import org.ndx.aadarchi.vfs.github.GitHubFileProvider;

/**
 * Load a pseudo-http file system dedicated to access to GitHub (by using our GitHub API)
 * @author nicolas-delsaux
 *
 */
public class GitHubFileSystemProvider {
	@Inject FileSystemManager fileSystemManager;

	public FileObject getProjectRoot(String project) throws FileSystemException {
		return fileSystemManager.resolveFile(GitHubFileProvider.urlFor(project));
	}

}

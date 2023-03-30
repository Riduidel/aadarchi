package org.ndx.aadarchi.github.vfs;

import static org.ndx.aadarchi.github.Constants.CONFIG_GITHUB_TOKEN;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.vfs.github.GitHubFileProvider;

/**
 * Load a pseudo-http file system dedicated to access to GitHub (by using our GitHub API)
 * @author nicolas-delsaux
 *
 */
public class GitHubFileSystemProvider {
	@Inject FileSystemManager fileSystemManager;
	private FileSystemOptions authenticationOptions;

	@Inject
	public void initializeAuthentication(@ConfigProperty(name=CONFIG_GITHUB_TOKEN) String token) {
		StaticUserAuthenticator auth = new StaticUserAuthenticator("github.com", 
				null, token);
		authenticationOptions = new FileSystemOptions();
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(authenticationOptions, auth);
	}
	public FileObject getProjectRoot(String project) throws FileSystemException {
		return fileSystemManager.resolveFile(GitHubFileProvider.urlFor(project), authenticationOptions);
	}

}

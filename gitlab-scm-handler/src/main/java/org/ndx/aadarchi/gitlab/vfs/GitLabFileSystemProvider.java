package org.ndx.aadarchi.gitlab.vfs;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.gitlab.Constants;
import org.ndx.aadarchi.vfs.gitlab.GitLabFileProvider;

/**
 * Load a pseudo-http file system dedicated to access to GitLab (by using our GitLab API)
 * @author nicolas-delsaux
 *
 */
public class GitLabFileSystemProvider {
	@Inject FileSystemManager fileSystemManager;
	private FileSystemOptions authenticationOptions;
	private String gitlabServer;

	@Inject
	public void initializeAuthentication(
			@ConfigProperty(name = Constants.CONFIG_GITLAB_TOKEN) String token,
			@ConfigProperty(name = Constants.CONFIG_GITLAB_URL, defaultValue = "gitlab.com") String gitlabUrl) {
		StaticUserAuthenticator auth = new StaticUserAuthenticator(gitlabUrl, 
				null, token);
		authenticationOptions = new FileSystemOptions();
		this.gitlabServer = gitlabUrl;
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(authenticationOptions, auth);
	}

	public FileObject getProjectRoot(String project) throws FileSystemException {
		return fileSystemManager.resolveFile(GitLabFileProvider.urlFor(gitlabServer, project), authenticationOptions);
	}

}

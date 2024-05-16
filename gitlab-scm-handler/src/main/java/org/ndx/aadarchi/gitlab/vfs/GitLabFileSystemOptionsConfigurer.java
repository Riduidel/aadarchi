package org.ndx.aadarchi.gitlab.vfs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.ndx.aadarchi.base.utils.commonsvfs.FileSystemOptionsConfigurer;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.gitlab.Constants;
import org.ndx.aadarchi.vfs.gitlab.GitLabFileProvider;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

/**
 * Load a pseudo-http file system dedicated to access to GitLab (by using our
 * GitLab API)
 * 
 * @author nicolas-delsaux
 *
 */
public class GitLabFileSystemOptionsConfigurer implements FileSystemOptionsConfigurer {
	@Inject
	@ConfigProperty(name = Constants.CONFIG_GITLAB_TOKEN)
	String token;
	@Inject
	@ConfigProperty(name = Constants.CONFIG_GITLAB_URL, defaultValue = "gitlab.com")
	String gitlabUrl;
	@Inject
	FileSystemManager fileSystemManager;

	@Override
	public void accept(FileSystemOptions opts) {
		StaticUserAuthenticator auth = new StaticUserAuthenticator(gitlabUrl, null, token);
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
	}

}

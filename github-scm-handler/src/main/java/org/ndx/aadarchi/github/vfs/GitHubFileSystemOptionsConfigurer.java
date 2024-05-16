package org.ndx.aadarchi.github.vfs;

import static org.ndx.aadarchi.github.Constants.CONFIG_GITHUB_TOKEN;

import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.auth.StaticUserAuthenticator;
import org.apache.commons.vfs2.impl.DefaultFileSystemConfigBuilder;
import org.ndx.aadarchi.base.utils.commonsvfs.FileSystemOptionsConfigurer;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.vfs.github.GitHubFileProvider;

/**
 * Load a pseudo-http file system dedicated to access to GitHub (by using our GitHub API)
 * @author nicolas-delsaux
 *
 */
public class GitHubFileSystemOptionsConfigurer implements FileSystemOptionsConfigurer {
	@Inject FileSystemManager fileSystemManager;
	@Inject @ConfigProperty(name=CONFIG_GITHUB_TOKEN) String token;

	@Override
	public void accept(FileSystemOptions opts) {
		StaticUserAuthenticator auth = new StaticUserAuthenticator("github.com", null, token);
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(opts, auth);
	}

}

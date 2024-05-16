package org.ndx.aadarchi.gitlab.vfs;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.gitlab.Constants;
import org.ndx.aadarchi.vfs.gitlab.GitLabFileProvider;

import jakarta.inject.Inject;

public class GitLabRootProvider {
	@Inject FileSystemManager fileSystemManager;
	@Inject FileSystemOptions fileSystemOptions;
	@Inject @ConfigProperty(name = Constants.CONFIG_GITLAB_URL, defaultValue = "gitlab.com") String gitlabUrl;

	public FileObject getProjectRoot(String project) throws FileSystemException {
		return fileSystemManager.resolveFile(GitLabFileProvider.urlFor(gitlabUrl, project), fileSystemOptions);
	}

}

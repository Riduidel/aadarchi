package org.ndx.aadarchi.gitlab;

import java.io.File;
import java.io.IOException;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gitlab4j.api.GitLabApiException;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.icon.FontIcon;
import org.ndx.aadarchi.gitlab.vfs.GitLabFileSystemProvider;

import com.structurizr.annotation.Component;

@Component
public class GitlabSCMHandler implements SCMHandler {
	private @Inject GitLabContainer gitlab;
	@Inject @Named("gitlab") Instance<GitOperator> cloner;
	@Inject
	GitLabFileSystemProvider gitlabFileSystem;
	@Inject
	@FontIcon(name = "gitlab")
	String gitlabIcon;

	@Override
	public boolean canHandle(String project) {
		return Constants.isGitLabProject(gitlab.getApi(), project);
	}

	@Override
	public String linkTo(String project, String path) {
		return String.format("%s/-/blob/main/%s", project, path);
	}

	@Override
	public String asciidocText() {
		return (gitlabIcon + " Gitlab").trim();
	}

	@Override
	public void checkout(String projectUrl, File checkoutLocation) throws IOException {
		if(Constants.isGitLabProject(gitlab.getApi(), projectUrl)) {
			try {
				String httpTransportUrl = gitlab.getApi().getProjectApi().getProject(projectUrl).getHttpUrlToRepo();
				cloner.get().clone(httpTransportUrl, checkoutLocation);
			} catch (GitLabApiException | GitAPIException e) {
				throw new IOException(String.format("Unable to clone %s to %s", projectUrl, checkoutLocation.getAbsolutePath()), e);
			}
		}
	}

	@Override
	public FileObject getProjectRoot(String project) {
		try {
			return gitlabFileSystem.getProjectRoot(project);
		} catch (FileSystemException e) {
			throw new GitLabHandlerException("Unable to obtain VFS", e);
		}
	}
}

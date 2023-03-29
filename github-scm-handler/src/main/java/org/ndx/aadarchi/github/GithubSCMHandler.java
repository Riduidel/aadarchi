package org.ndx.aadarchi.github;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.icon.FontIcon;
import org.ndx.aadarchi.github.vfs.GitHubFileSystemProvider;

import com.structurizr.annotation.Component;

@Component
@ApplicationScoped
public class GithubSCMHandler implements SCMHandler {
	@Inject Logger logger;
	@Inject GitHub github;
	@Inject FileContentCache fileCache;
	@Inject Instance<GitOperator> cloner;
	@Inject @FontIcon(name="github") String githubIcon;
	@Inject GitHubFileSystemProvider gitHubFileSystem;
	@Override
	public boolean canHandle(String project) {
		return Constants.isGitHubProject(project);
	}

	@Override
	public String linkTo(String project, String path) {
		return String.format("%s/blob/main/%s", project, path);
	}

	@Override
	public String asciidocText() {
		return (githubIcon + " GitHub").trim();
	}

	private InputStream doOpenStream(URL url) throws IOException {
		throw new UnsupportedOperationException(String.format("Can't read the file %s with GitHubSCMHandler", url));
	}

	@Override
	public void checkout(String projectUrl, File checkoutLocation) throws IOException {
		if(Constants.isGitHubProject(projectUrl)) {
			String project = Constants.getGitHubProjectName(projectUrl);
			GHRepository repository = github.getRepository(project);
			String gitTransportUrl = repository.getHttpTransportUrl();
			try {
				cloner.get().clone(gitTransportUrl, checkoutLocation.getCanonicalFile());
			} catch (GitAPIException e) {
				throw new IOException(String.format("Unable to clone %s to %s", projectUrl, checkoutLocation.getAbsolutePath()), e);
			}
		}
		
	}

	@Override
	public FileObject getProjectRoot(String project) {
		try {
			return gitHubFileSystem.getProjectRoot(project);
		} catch (FileSystemException e) {
			throw new GitHubHandlerException("Unable to obtain VFS", e);
		}
	}
}

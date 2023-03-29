package org.ndx.aadarchi.gitlab;


import java.io.File;
import java.io.IOException;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.icon.FontIcon;

import com.structurizr.annotation.Component;

@Component
public class GitlabSCMHandler implements SCMHandler {
	private @Inject GitLabContainer gitlab;
	@Inject @FontIcon(name="gitlab") String gitlabIcon;

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
		throw new UnsupportedOperationException(String.format("SCMHandler#checkout(%s,%s) is not yet implemented in GitlabSCMHandler. Sorry", projectUrl, checkoutLocation.getAbsolutePath()));
	}

	@Override
	public FileObject getProjectRoot(String project) {
		throw new UnsupportedOperationException("TODO Implement "+getClass().getSimpleName()+"#getProjectRoot()");
	}
}

package org.ndx.aadarchi.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

public interface Constants {

	String CONFIG_GITLAB_TOKEN = ModelElementKeys.PREFIX+"gitlab.token";
	String CONFIG_GITLAB_LOGIN = ModelElementKeys.PREFIX+"gitlab.login";
	String CONFIG_GITLAB_URL = ModelElementKeys.PREFIX+"gitlab.url";
	String CONFIG_GIT_BRANCHES_TO_CHECKOUT = ModelElementKeys.PREFIX+"git.branches.to.checkout";
	static boolean isGitLabProject(GitLabApi api, String project) {
		return project.contains(api.getGitLabServerUrl());
	}

}

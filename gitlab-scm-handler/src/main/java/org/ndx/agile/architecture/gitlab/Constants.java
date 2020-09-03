package org.ndx.agile.architecture.gitlab;

import org.gitlab4j.api.GitLabApi;

public interface Constants {

	String CONFIG_GITLAB_TOKEN = "agile.architecture.gitlab.token";
	String CONFIG_GITLAB_URL = "agile.architecture.gitlab.url";
	static boolean isGitLabProject(GitLabApi api, String project) {
		return project.contains(api.getGitLabServerUrl());
	}

}

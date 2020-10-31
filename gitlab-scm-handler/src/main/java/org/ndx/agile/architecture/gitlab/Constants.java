package org.ndx.agile.architecture.gitlab;

import org.gitlab4j.api.GitLabApi;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

public interface Constants {

	String CONFIG_GITLAB_TOKEN = ModelElementKeys.PREFIX+"gitlab.token";
	String CONFIG_GITLAB_URL = ModelElementKeys.PREFIX+"gitlab.url";
	static boolean isGitLabProject(GitLabApi api, String project) {
		return project.contains(api.getGitLabServerUrl());
	}

}

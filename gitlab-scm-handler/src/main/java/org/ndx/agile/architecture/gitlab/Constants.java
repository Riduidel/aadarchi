package org.ndx.agile.architecture.gitlab;

public interface Constants {

	String CONFIG_GITLAB_TOKEN = "agile.architecture.gitlab.token";
	String GITLAB_DOMAIN = "gitlab.com";
	static boolean isGitLabProject(String project) {
		return project.contains(GITLAB_DOMAIN);
	}

}

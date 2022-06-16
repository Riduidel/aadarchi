package org.ndx.aadarchi.github;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

public interface Constants {

	String CONFIG_GITHUB_TOKEN = ModelElementKeys.PREFIX+"github.token";
	String GITHUB_DOMAIN = "github.com";
	static boolean isGitHubProject(String project) {
		return project.contains(GITHUB_DOMAIN);
	}
	/**
	 * Obtains project name from project url by cutting github domain out
	 * @param project
	 * @return
	 */
	static String getGitHubProjectName(String project) {
		project = project.substring(project.indexOf(GITHUB_DOMAIN)+GITHUB_DOMAIN.length());
		if(project.startsWith("/")) {
			project = project.substring(1);
		}
		if(project.endsWith(".git")) {
			project = project.substring(0, project.indexOf(".git"));
		}
		return project;
	}

}

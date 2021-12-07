package org.ndx.agile.architecture.gitlab;

import org.gitlab4j.api.GitLabApi;

public class GitLabProducer {
	public GitLabApi initialize(
			String token,
			String gitlabUrl) {
		if(token==null || token.isBlank()) {
			throw new GitLabHandlerException(String.format("Can't connect to Gitlab if token %s isn't defined as system property", 
					Constants.CONFIG_GITLAB_TOKEN));
		}
		return new GitLabApi(gitlabUrl, token);
	}

}

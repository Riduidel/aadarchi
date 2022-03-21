package org.ndx.agile.architecture.gitlab;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.gitlab4j.api.GitLabApi;

public class GitLabProducer {
	@Produces @ApplicationScoped public GitLabApi initialize(
			@ConfigProperty(name=Constants.CONFIG_GITLAB_TOKEN) String token,
			@ConfigProperty(name=Constants.CONFIG_GITLAB_URL, defaultValue = "https://gitlab.com") String gitlabUrl) {
		if(token==null || token.isBlank()) {
			throw new GitLabHandlerException(String.format("Can't connect to Gitlab if token %s isn't defined as system property", 
					Constants.CONFIG_GITLAB_TOKEN));
		}
		return new GitLabApi(gitlabUrl, token);
	}

}

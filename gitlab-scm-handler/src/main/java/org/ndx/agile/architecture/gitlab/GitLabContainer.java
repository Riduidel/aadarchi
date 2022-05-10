package org.ndx.agile.architecture.gitlab;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.gitlab4j.api.GitLabApi;

/**
 * Since {@link GitLabApi} contains a public final method (
 * {@link GitLabApi#duplicate()} ), it can be proxied by CDI. As a consequence,
 * this contains "holds" a GitlLabApi object, which won't be directly injected,
 * but nevertheless usable
 * 
 * @author nicolas-delsaux
 *
 */
@ApplicationScoped
public class GitLabContainer {
	/**
	 * Exposed gitlab api is public final, cause there is no reason to hide it
	 * behind a getter, and no reason to be able to modify it.
	 */
	private final GitLabApi api;

	public GitLabApi getApi() {
		return api;
	}

	@Inject
	public GitLabContainer(@ConfigProperty(name = Constants.CONFIG_GITLAB_TOKEN) String token,
			@ConfigProperty(name = Constants.CONFIG_GITLAB_URL, defaultValue = "https://gitlab.com") String gitlabUrl) {
		if (token == null || token.isBlank()) {
			throw new GitLabHandlerException(
					String.format("Can't connect to Gitlab if token %s isn't defined as system property",
							Constants.CONFIG_GITLAB_TOKEN));
		}
		api = new GitLabApi(gitlabUrl, token);
	}

}

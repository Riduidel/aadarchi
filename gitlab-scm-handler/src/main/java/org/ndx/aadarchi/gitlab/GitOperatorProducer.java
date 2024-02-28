package org.ndx.aadarchi.gitlab;

import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * Component dedicated to low-level git operations
 * @author Nicolas
 */
public class GitOperatorProducer {
	@Produces @Named("gitlab")
	public GitOperator buildGitOperator(
			@ConfigProperty(name=Constants.CONFIG_GITLAB_LOGIN) String login,
			@ConfigProperty(name=Constants.CONFIG_GITLAB_TOKEN) String token,
			@ConfigProperty(name=Constants.CONFIG_GIT_BRANCHES_TO_CHECKOUT, defaultValue = "develop, main, main") String names) {
		GitOperator returned = new GitOperator();
		returned.setLogin(login);
		returned.setToken(token);
		returned.setBranchesToCheckout(names);
		return returned;
	}
}
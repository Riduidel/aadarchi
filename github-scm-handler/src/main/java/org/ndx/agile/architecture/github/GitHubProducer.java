package org.ndx.agile.architecture.github;

import static org.ndx.agile.architecture.github.Constants.CONFIG_GITHUB_TOKEN;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

/**
 * Injectable base class loading a GitHub api client on initialization
 * @author nicolas-delsaux
 *
 */
public class GitHubProducer {

	public @Produces @ApplicationScoped GitHub initialize(@ConfigProperty(name=CONFIG_GITHUB_TOKEN) String token) {
		if(token==null) {
			throw new GitHubHandlerException(String.format("Can't connect to GitHub if token %s isn't defined as system property", Constants.CONFIG_GITHUB_TOKEN));
		}
		try {
			return new GitHubBuilder().withOAuthToken(token).build();
		} catch (IOException e) {
			throw new GitHubHandlerException("Can't connect to GitHub! Maybe the token is bad");
		}
	}

}

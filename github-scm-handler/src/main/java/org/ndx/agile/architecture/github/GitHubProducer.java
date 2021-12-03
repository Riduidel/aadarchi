package org.ndx.agile.architecture.github;

import java.io.IOException;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;

/**
 * Injectable base class loading a GitHub api client on initialization
 * @author nicolas-delsaux
 *
 */
public class GitHubProducer {

	public GitHub initialize(String token) {
		if(token==null || token.isBlank()) {
			throw new GitHubHandlerException(String.format("Can't connect to GitHub if token %s isn't defined as system property", Constants.CONFIG_GITHUB_TOKEN));
		}
		try {
			return new GitHubBuilder().withOAuthToken(token).build();
		} catch (IOException e) {
			throw new GitHubHandlerException("Can't connect to GitHub! Maybe the token is bad");
		}
	}

}

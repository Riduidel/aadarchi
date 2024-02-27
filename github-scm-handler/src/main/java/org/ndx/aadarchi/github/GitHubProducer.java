package org.ndx.aadarchi.github;

import static org.ndx.aadarchi.github.Constants.CONFIG_GITHUB_TOKEN;

import java.io.IOException;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

/**
 * Injectable base class loading a GitHub api client on initialization
 * @author nicolas-delsaux
 *
 */
public class GitHubProducer {
	@Inject Logger logger;

	public @Produces @ApplicationScoped GitHub initialize(@ConfigProperty(name=CONFIG_GITHUB_TOKEN) String token) {
		if(token==null || token.isBlank()) {
			throw new GitHubHandlerException(String.format("Can't connect to GitHub if token %s isn't defined as system property" +
							"\nSee https://github.com/Riduidel/aadarchi/wiki/How-to-add-aadarchi.github.token for details",
					Constants.CONFIG_GITHUB_TOKEN));
		}
		try {
			GitHub returned = new GitHubBuilder().withOAuthToken(token).build();
			logger.info(
					String.format("Connected to GitHub as %s, homepage is %s", 
							returned.getMyself().getLogin(), returned.getMyself().getHtmlUrl()));
			return returned;
		} catch (IOException e) {
			throw new GitHubHandlerException("Can't connect to GitHub! Maybe the token is bad");
		}
	}

}

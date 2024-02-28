package org.ndx.aadarchi.github;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;
import org.ndx.aadarchi.gitlab.GitOperator;

import com.structurizr.annotation.Component;

/**
 * Component dedicated to low-level git operations
 * @author Nicolas
 */
public class GitOperatorProducer {
	@Produces @Named("github")
	public GitOperator buildGitOperator(
			@ConfigProperty(name=Constants.CONFIG_GITHUB_LOGIN) String login,
			@ConfigProperty(name=Constants.CONFIG_GITHUB_TOKEN) String token,
			@ConfigProperty(name=Constants.CONFIG_GIT_BRANCHES_TO_CHECKOUT, defaultValue = "develop, main, main") String names) {
		GitOperator returned = new GitOperator();
		returned.setLogin(login);
		returned.setToken(token);
		returned.setBranchesToCheckout(names);
		return returned;
	}
}
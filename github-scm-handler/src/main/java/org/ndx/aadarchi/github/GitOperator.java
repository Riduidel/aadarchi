package org.ndx.aadarchi.github;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import com.structurizr.annotation.Component;

/**
 * Component dedicated to low-level git operations
 * @author Nicolas
 */
@Component
@ApplicationScoped
public class GitOperator {
	@Inject @ConfigProperty(name=Constants.CONFIG_GITHUB_LOGIN) private String login;
	@Inject @ConfigProperty(name=Constants.CONFIG_GITHUB_TOKEN) private String token;
	private Set<String> branchesToCheckout;
	@Inject 
	public void setBranchesToCheckout(@ConfigProperty(name=Constants.CONFIG_GIT_BRANCHES_TO_CHECKOUT, defaultValue = "develop, main, master") String names) {
		this.branchesToCheckout = Arrays.asList(names.split(",")).stream()
				.map(name -> name.trim())
				.collect(Collectors.toSet());
	}
	@Inject Logger logger;

	/**
	 * Clone the repo
	 * @param from an https url
	 * @param into a non-existing local folder
	 * @throws GitAPIException if git operation fails
	 * @throws IOException if local file operation fails
	 */
	public void clone(String from, File into) throws GitAPIException, IOException {
		if(login==null || login.isBlank()) {
			throw new UnsupportedOperationException(
					String.format("Unable to checkout projects of no Github login is provided. Please set the %s property to a non-null value", Constants.CONFIG_GITHUB_LOGIN));
		}
		if(into.exists()) {
			if(new File(into, ".git").exists()) {
				logger.info(String.format("%s seems to already be a git repository, we consider job's done.", into.getAbsolutePath()));
				Git localRepository = Git.open(into);
			} else {
				logger.info(String.format("%s doesn't seems to be a git repository, but already exists. There is something strange there ...", into.getAbsolutePath()));
			}
		} else {
			Git localRepository = Git.cloneRepository()
				.setCredentialsProvider(new UsernamePasswordCredentialsProvider(login, token))
				.setURI(from)
				.setDirectory(into)
				.call();
			// Now list remote branches
			List<Ref> branches = localRepository.branchList().setListMode(ListMode.REMOTE).call();
			Set<String> branchesNames = branches.stream()
				.map(Ref::getName)
				.map(name -> name.substring(name.lastIndexOf('/')+1))
				.collect(Collectors.toSet());
			branchesToCheckout.stream()
				.filter(name -> branchesNames.contains(name))
				.findFirst()
				.ifPresentOrElse(branchName -> {
					logger.info(String.format("Checking out project %s branch %s into folder %s", 
							from,
							branchName,
							into.getAbsolutePath()));
					// And check out the first branch in the branchesToCheckout;
					localRepository.checkout()
						.setAllPaths(true)
						.setCreateBranch(true)
						.setAllPaths(true)
						.setName(from);
				}, () -> {
					logger.warning(String.format("We found none of the %s branches in remote (but found %s). Please add one to %s in order for checkout to work", 
							branchesToCheckout, branchesNames, Constants.CONFIG_GIT_BRANCHES_TO_CHECKOUT));
				});
		}
	}
}
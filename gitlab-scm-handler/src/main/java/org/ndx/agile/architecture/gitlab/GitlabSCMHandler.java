package org.ndx.agile.architecture.gitlab;


import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.configuration2.Configuration;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.TreeItem;
import org.kohsuke.MetaInfServices;
import org.ndx.agile.architecture.base.enhancers.scm.SCMFile;
import org.ndx.agile.architecture.base.enhancers.scm.SCMHandler;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.exception.WrappedException;

@MetaInfServices
public class GitlabSCMHandler implements SCMHandler {
	private GitLabApi gitlab;

	@Override
	public void configure(Configuration configuration) {
		gitlab = new GitLabProducer().initialize(
				configuration.getString(Constants.CONFIG_GITLAB_TOKEN),
				configuration.getString(Constants.CONFIG_GITLAB_URL, "https://gitlab.com"));
	}

	@Override
	public boolean canHandle(String project) {
		return Constants.isGitLabProject(gitlab, project);
	}

	@Override
	public Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) {
		try {
			List<TreeItem> items = gitlab.getRepositoryApi().getTree(project, path, null);
			return items.stream()
					.map(ThrowingFunction.unchecked(item -> new GitlabFile(
							gitlab.getRepositoryFileApi().getFile(project, item.getPath(), "master"))))
					.filter(file -> filter.test(file))
					.collect(Collectors.toList());
		} catch (WrappedException e) {
			throw new GitLabHandlerException(
					String.format("Unable to search for files in %s/%s", project, path), 
					e);
		} catch (GitLabApiException e) {
			throw new GitLabHandlerException(
					String.format("Unable to search for files in %s/%s", project, path), 
					e);
		}
	}

	@Override
	public String linkTo(String project, String path) {
		return String.format("%s/-/blob/master/%s", project, path);
	}

	@Override
	public String asciidocText() {
		return "icon:gitlab[set=fab] Gitlab";
	}

}

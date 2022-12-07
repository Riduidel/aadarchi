package org.ndx.aadarchi.gitlab;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.Pager;
import org.gitlab4j.api.models.Blame;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.icon.FontIcon;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.exception.WrappedException;
import com.structurizr.annotation.Component;

@Component
public class GitlabSCMHandler implements SCMHandler {
	private @Inject GitLabContainer gitlab;
	@Inject @FontIcon(name="gitlab") String gitlabIcon;

	@Override
	public boolean canHandle(String project) {
		return Constants.isGitLabProject(gitlab.getApi(), project);
	}

	@Override
	public Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) throws FileNotFoundException {
		try {
			var repositoryFileApi = gitlab.getApi().getRepositoryFileApi();
			List<TreeItem> items = gitlab.getApi().getRepositoryApi().getTree(project, path, null);
			return items.stream()
					.map(ThrowingFunction.unchecked(item -> {
						RepositoryFile gitlabFile = repositoryFileApi.getFile(project, item.getPath(), "main");
						Pager<Blame> blame = repositoryFileApi.getBlame(project, item.getPath(), gitlabFile.getRef(), 1);
						var commitDate = blame.first().get(0).getCommit().getCommittedDate();
						return new GitlabFile(
							gitlabFile,
							commitDate
							);
					}))
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
		return String.format("%s/-/blob/main/%s", project, path);
	}

	@Override
	public String asciidocText() {
		return (gitlabIcon + " Gitlab").trim();
	}

	@Override
	public InputStream openStream(URL url) throws IOException {
		throw new UnsupportedOperationException(String.format("SCMHandler#openStream(%s) is not yet implemented in GitlabSCMHandler. Sorry", url));
	}


	@Override
	public void checkout(String projectUrl, File checkoutLocation) throws IOException {
		throw new UnsupportedOperationException(String.format("SCMHandler#checkout(%s,%s) is not yet implemented in GitlabSCMHandler. Sorry", projectUrl, checkoutLocation.getAbsolutePath()));
	}
}

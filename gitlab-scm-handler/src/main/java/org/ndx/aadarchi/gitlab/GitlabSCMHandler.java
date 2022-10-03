package org.ndx.aadarchi.gitlab;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.TreeItem;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;

import com.pivovarit.function.ThrowingFunction;
import com.pivovarit.function.exception.WrappedException;
import com.structurizr.annotation.Component;

@Component
public class GitlabSCMHandler implements SCMHandler {
	private @Inject GitLabContainer gitlab;

	@Override
	public boolean canHandle(String project) {
		return Constants.isGitLabProject(gitlab.getApi(), project);
	}

	@Override
	public Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) throws FileNotFoundException {
		try {
			List<TreeItem> items = gitlab.getApi().getRepositoryApi().getTree(project, path, null);
			return items.stream()
					.map(ThrowingFunction.unchecked(item -> new GitlabFile(
							gitlab.getApi().getRepositoryFileApi().getFile(project, item.getPath(), "master"))))
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

	@Override
	public InputStream openStream(URL url) throws IOException {
		throw new UnsupportedOperationException(String.format("SCMHandler#openStream(%s) is not yet implemented in GitlabSCMHandler. Sorry", url));
	}

}

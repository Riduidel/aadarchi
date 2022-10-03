package org.ndx.aadarchi.github;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.weld.exceptions.UnsupportedOperationException;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.ndx.aadarchi.base.enhancers.scm.SCMFile;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.FileContentCache;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.annotation.Component;

@Component
@ApplicationScoped
public class GithubSCMHandler implements SCMHandler {
	@Inject Logger logger;
	@Inject GitHub github;
	@Inject FileContentCache fileCache;
	@Override
	public boolean canHandle(String project) {
		return Constants.isGitHubProject(project);
	}

	@Override
	public Collection<SCMFile> find(String project, String path, Predicate<SCMFile> filter) throws FileNotFoundException {
		if(Constants.isGitHubProject(project)) {
			project = Constants.getGitHubProjectName(project);
		}
		try {
			GHRepository repository = github.getRepository(project);
			List<GHContent> dir = repository.getDirectoryContent(path);
			return dir.stream()
				.map(content -> new GitHubFile(logger, repository, content))
				.filter(content -> filter==null ? true : filter.test(content))
				.collect(Collectors.toList());
		} catch (IOException e) {
			throw new FileNotFoundException(
					String.format("Unable to find file in %s/%s", project, path)
					);
		}
	}

	@Override
	public String linkTo(String project, String path) {
		return String.format("%s/blob/master/%s", project, path);
	}

	@Override
	public String asciidocText() {
		return "icon:github[set=fab] GitHub";
	}

	/**
	 * We assume here the url to be the one given by navigating the GitHub web UI
	 * @param url the url of a file in GitHub UI
	 * @return an InputStrema to read that file content
	 */
	@Override
	public InputStream openStream(URL url) throws IOException {
		return fileCache.openStreamFor(url, ThrowingFunction.unchecked(this::doOpenStream));
	}

	private InputStream doOpenStream(URL url) throws IOException {
		String urlText = url.toString();
		if(Constants.isGitHubProject(urlText)) {
			String project = Constants.getGitHubProjectName(urlText);
			GHRepository repository = github.getRepository(project);
			var blobIndex = urlText.indexOf("/blob/");
			if(blobIndex>0) {
				var inBlobPath = urlText.substring(blobIndex+6);
				// I do hope people won't use branches with "/" in names, otherwise it'll be a mess
				int firstSlashAfterBlob = inBlobPath.indexOf('/');
				var branchName = inBlobPath.substring(0, firstSlashAfterBlob);
				var path = inBlobPath.substring(firstSlashAfterBlob+1);
				return new GitHubFile(logger, repository, repository.getFileContent(path)).content();
			} else {
				throw new UnsupportedOperationException(
						String.format("We currently only support path below /blob, which is not the case of the url \"%s\"", urlText));
			}
		}
		throw new UnsupportedOperationException(String.format("Can't read the file %s with GitHubSCMHandler", url));
	}

}

package org.ndx.agile.architecture.gitlab;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.TreeItem;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.Keys;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import com.structurizr.Workspace;
import com.structurizr.model.Element;

import nl.jworks.markdown_to_asciidoc.Converter;

@ApplicationScoped
public class GitlabReadmeReader extends ModelElementAdapter {
	private static final String GITLAB_TOKEN = "agile.architecture.gitlab.token";
	
	@Inject @ConfigProperty(name="force") boolean force;
	
	@Inject Logger logger;
	private GitLabApi gitLabApi;
	
	@PostConstruct public void createGitlabApi(@ConfigProperty(name=GITLAB_TOKEN) String token) {
		if(token==null) {
			throw new CantExtractReadme(
					String.format("No token has been provided. Have you set the %s system property?",
							GITLAB_TOKEN));
		}
		gitLabApi = new GitLabApi(String.format("https://%s", Constants.GITLAB_DOMAIN), token);
	}

	@Override
	public int priority() {
		return Constants.COMMON_PRIORITY;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return super.startVisit(workspace, builder);
	}

	void writeReadmeFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Keys.ELEMENT_PROJECT)) {
			String elementProject = element.getProperties().get(Keys.ELEMENT_PROJECT);
			if(elementProject.contains(Constants.GITLAB_DOMAIN)) {
				String githubProject = elementProject;
				File outputFor = builder.outputFor(AgileArchitectureSection.code, element, this, "adoc");
				if(force && outputFor.exists()) {
					return;
				}
				try {
					String readmePath = element.getProperties().get(Keys.ELEMENT_README);
					logger.info(String.format("Reading readme for %s from %s/%s", element.getCanonicalName(), githubProject, readmePath));
					String content = getReadmeContent(githubProject, readmePath);
					// Now we have content as asciidoc, so let's write it to the conventional location
					FileUtils.write(outputFor, content, "UTF-8");
				} catch (IOException e) {
					throw new CantExtractReadme(String.format("Can't extract readme of container %s which is linked to GitHub project %s", 
							element.getCanonicalName(), githubProject), 
							e);
				}
			}
		}
	}

	String getReadmeContent(@Nonnull String gitlabProject, @Nullable String readmePath) {
		try {
			if(readmePath==null) {
				List<TreeItem> files = gitLabApi.getRepositoryApi().getTree(gitlabProject);
				for(TreeItem item : files) {
					if(item.getName().toLowerCase().startsWith("readme.")) {
						var infos = gitLabApi.getRepositoryFileApi().getFile(gitlabProject, item.getPath(), "master");
						return asAsciidoc(infos);
					}
				}
				throw new CantExtractReadme(String.format("We couldn't find any readme in project %s", gitlabProject));
			} else {
				RepositoryFile readmeFileObject = gitLabApi.getRepositoryFileApi().getFile(gitlabProject, readmePath, null);
				return asAsciidoc(readmeFileObject);
			}
		} catch(GitLabApiException e) {
			throw new CantExtractReadme(String.format("Can't extract a readme from project %s (readme is %s - null implies we try to find it by ourself", gitlabProject, readmePath), e);
		}
	}

	private String asAsciidoc(RepositoryFile file) {
		return asAsciidoc(file.getFileName(), file.getContent());
	}
	private String asAsciidoc(String filename, String content) {
		if(filename.endsWith(".md")) {
			return Converter.convertMarkdownToAsciiDoc(
					content);
		} else if(filename.endsWith(".adoc")) {
			return content;
		} else {
			throw new UnableToGetAsciidocFrom(String.format("Unable to get asciidoc from %s (we don't know ow to convert that)", filename));
		}
	}

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		writeReadmeFor(element, builder);
	}


}

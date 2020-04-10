package org.ndx.agile.architecture.github.readme;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.Keys;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

import nl.jworks.markdown_to_asciidoc.Converter;

/**
 * Generate a link to folder containing readme (because it's where the code is)
 * @author nicolas-delsaux
 *
 */
@ApplicationScoped
public class LinkGenerator implements ModelEnhancer {
	@Inject Logger logger;

	@Override
	public boolean isParallel() {
		return true;
	}

	@Override
	public int priority() {
		return Constants.COMMON_PRIORITY-1;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		return true;
	}

	@Override
	public boolean startVisit(Model model) {
		return true;
	}

	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		return true;
	}

	@Override
	public boolean startVisit(Container container) {
		return true;
	}

	@Override
	public boolean startVisit(Component component) {
		return false;
	}

	@Override public void endVisit(Component component, OutputBuilder builder) {
		writeLinkFor(component, builder);
	}

	/**
	 * On end visit, we will read the project infos and readme and write all that
	 * in the code subfolder of this container.
	 */
	@Override public void endVisit(Container container, OutputBuilder builder) {
		writeLinkFor(container, builder);
	}

	@Override public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		writeLinkFor(softwareSystem, builder);
	}

	@Override public void endVisit(Model model, OutputBuilder builder) {}

	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {}

	void writeLinkFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Keys.GITHUB_PROJECT)) {
			String project = element.getProperties().get(Keys.GITHUB_PROJECT);
			String readme = element.getProperties().get(Keys.GITHUB_README);
			if(readme!=null) {
				if(readme.indexOf('/')>0) {
					project = String.format("%s/blob/master/%s",
							project,
							readme.substring(0, readme.lastIndexOf('/')));
				}
			}
			String content = String.format("%s[See on icon:github[set=fab] GitHub]", project);
			// Now we have content as asciidoc, so let's write it to the conventional location
			try {
				FileUtils.write(builder.outputFor(AgileArchitectureSection.code, element, this, "adoc"), 
						content, "UTF-8");
			} catch (IOException e) {
				throw new CantWriteLink(String.format("Can't write link for element %s which is linked to %s", 
						element.getCanonicalName(), project), 
						e);
			}
		}
	}
}

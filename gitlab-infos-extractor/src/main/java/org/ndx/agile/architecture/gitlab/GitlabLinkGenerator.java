package org.ndx.agile.architecture.gitlab;

import java.io.IOException;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.Keys;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import com.structurizr.model.Element;

/**
 * Generate a link to folder containing readme (because it's where the code is)
 * @author nicolas-delsaux
 *
 */
@ApplicationScoped
public class GitlabLinkGenerator extends ModelElementAdapter {
	@Inject Logger logger;

	@Override
	public int priority() {
		return Constants.COMMON_PRIORITY-1;
	}

	void writeLinkFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Keys.ELEMENT_PROJECT)) {
			String project = element.getProperties().get(Keys.ELEMENT_PROJECT);
			if(project.contains(Constants.GITLAB_DOMAIN)) {
				String readme = element.getProperties().get(Keys.ELEMENT_README);
				if(readme!=null) {
					if(readme.indexOf('/')>0) {
						project = String.format("%s/-/blob/master/%s",
								project,
								readme.substring(0, readme.lastIndexOf('/')));
					}
				}
				String content = String.format("%s[See on icon:gitlab[set=fab] GitHub]", project);
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

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		writeLinkFor(element, builder);
	}
}

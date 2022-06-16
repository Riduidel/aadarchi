package org.ndx.aadarchi.base.enhancers.scm;

import java.io.IOException;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.Scm;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.structurizr.annotation.Component;
import com.structurizr.annotation.UsesComponent;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

/**
 * Generate a link to folder containing readme (because it's where the code is)
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java/CDI")
@ApplicationScoped
public class SCMLinkGenerator extends ModelElementAdapter {
	@Inject @ConfigProperty(name="force", defaultValue="false") boolean force;
	
	@Inject Logger logger;
	
	@Inject @UsesComponent(description = "Get SCM infos") Instance<SCMHandler> scmHandlers;

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS+2;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		writeLinkFor(element, builder);
		
	}

	/**
	 * Write the readme as fetched from any SCM provider
	 * @param element
	 * @param builder
	 */
	void writeLinkFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Scm.PROJECT)) {
			String elementProject = element.getProperties().get(Scm.PROJECT);
			String elementPath = element.getProperties().getOrDefault(Scm.PATH, "");
			Optional<SCMHandler> usableHandler = scmHandlers.stream()
				.filter(handler -> handler.canHandle(elementProject))
				.findFirst()
				;
			if(usableHandler.isPresent()) {
				SCMHandler handler = usableHandler.get();
				builder.writeToOutput(AgileArchitectureSection.code, element, this, OutputBuilder.Format.adoc,
					String.format("%s[See on %s]", 
						 handler.linkTo(elementProject, elementPath),
						 handler.asciidocText()
						 ));
			} else {
				logger.warning(String.format("We have this set of handlers\n%s\nin which we couldn't find one for element %s associated project %s",
						scmHandlers.stream().map(handler -> handler.toString()).collect(Collectors.joining()),
						StructurizrUtils.getCanonicalPath(element),
						elementProject
						));
			}
		}
	}
}

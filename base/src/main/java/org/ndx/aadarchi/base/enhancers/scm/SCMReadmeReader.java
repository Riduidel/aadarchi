package org.ndx.aadarchi.base.enhancers.scm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.OutputBuilder.Format;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.Scm;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.structurizr.annotation.Component;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

import nl.jworks.markdown_to_asciidoc.Converter;

/**
 * Collect each model element readme (well, when the {@link Scm#PROJECT} key is set)
 * and output them in generated elements folder
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java, CDI")
public class SCMReadmeReader extends SCMModelElementAdapter {
	@Inject @ConfigProperty(name="force", defaultValue="false") boolean force;
	
	@Inject FileContentCache cache;
	
	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS+2;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		writeReadmeFor(element, builder);
		
	}

	/**
	 * Write the readme as fetched from any SCM provider
	 * @param element
	 * @param builder
	 */
	void writeReadmeFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(Scm.PROJECT)) {
			String elementProject = element.getProperties().get(Scm.PROJECT);
			withHandlerFor(elementProject)
				.ifPresentOrElse(handler -> writeReadmeFor(handler, element, elementProject, builder), 
						() -> {
							logger.warning(String.format("We have this set of handlers\n%s\nin which we couldn't find one for element %s associated project %s",
									scmHandlers.stream().map(handler -> handler.toString()).collect(Collectors.joining()),
									StructurizrUtils.getCanonicalPath(element),
									elementProject
									));
						});
		}
	}

	void writeReadmeFor(SCMHandler handler, Element element, String elementProject, OutputBuilder builder) {
		String elementPath = element.getProperties().getOrDefault(Scm.PATH, "");
		String elementReadme = element.getProperties().get(Scm.README);
		Predicate<SCMFile> filter;
		if(elementReadme==null) {
			filter = (file) -> file.name().toLowerCase().startsWith("readme.");
		} else {
			filter = (file) -> file.name().equals(elementReadme);
		}
		try {
			Collection<SCMFile> file = handler.find(elementProject, elementPath, filter);
			if(file.isEmpty()) {
				logger.severe(String.format("Couldn't find any Readme for element %s"
						+ "(project is %s, path %s and readme should be %s)", 
						StructurizrUtils.getCanonicalPath(element), elementProject, elementPath, elementReadme));
			} else if(file.size()>1) {
				logger.severe(String.format("There are more than one valid Readme for element %s"
						+ "(project is %s, path %s and readme should be %s)", 
						StructurizrUtils.getCanonicalPath(element), elementProject, elementPath, elementReadme));
			} else {
				SCMFile readme = file.iterator().next();
				File outputFor = builder.outputFor(AgileArchitectureSection.code, element, this, Format.adoc);
				if(force) {
					outputFor.delete();
				} else {
					if(readme.lastModified()<outputFor.lastModified())
						return;
				}
				try {
					// Now we have content as asciidoc, so let's write it to the conventional location
					String readmeText = IOUtils.toString(cache.openStreamFor(readme), "UTF-8");
					if(readme.name().toLowerCase().endsWith(".md")) {
						readmeText = Converter.convertMarkdownToAsciiDoc(readmeText);
					}
					builder.writeToOutput(AgileArchitectureSection.code, element, this, Format.adoc, readmeText);
				} catch (Throwable e) {
					throw new CantExtractReadme(String.format(
							"Can't extract readme of container %s using SCM project %s, path %s, readme %s", 
							StructurizrUtils.getCanonicalPath(element), elementProject, elementPath, elementReadme), 
							e);
				}
			}
		} catch(FileNotFoundException e) {
			logger.log(Level.SEVERE, String.format("Couldn't find any Readme for element %s"
					+ "(project is %s, path %s and readme should be %s)", 
					StructurizrUtils.getCanonicalPath(element), elementProject, elementPath, elementReadme), e);
		}
	}
}

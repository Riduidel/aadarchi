package org.ndx.agile.architecture.base.enhancers.scm;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;
import org.ndx.agile.architecture.base.enhancers.ModelElementAdapter;

import com.structurizr.model.Element;

import nl.jworks.markdown_to_asciidoc.Converter;

/**
 * Collect each model element readme (well, when the {@link ModelElementKeys#SCM_PROJECT} key is set)
 * and output them in generated elements folder
 * @author nicolas-delsaux
 *
 */
public class SCMReadmeReader extends ModelElementAdapter {
	@Inject @ConfigProperty(name="force", defaultValue="false") boolean force;
	
	@Inject Logger logger;
	
	@Inject Instance<SCMHandler> scmHandlers;

	@Override
	public int priority() {
		return 1001;
	}

	@Override
	protected void processElement(Element element, OutputBuilder builder) {
		writeReadmeFor(element, builder);
		
	}

	/**
	 * Write the readme as fetched from any SCM provider
	 * @param element
	 * @param builder
	 */
	void writeReadmeFor(Element element, OutputBuilder builder) {
		if(element.getProperties().containsKey(ModelElementKeys.SCM_PROJECT)) {
			String elementProject = element.getProperties().get(ModelElementKeys.SCM_PROJECT);
			Optional<SCMHandler> usableHandler = scmHandlers.stream()
				.filter(handler -> handler.canHandle(elementProject))
				.findFirst()
				;
			if(usableHandler.isPresent()) {
				SCMHandler handler = usableHandler.get();
				writeReadmeFor(handler, element, elementProject, builder);
			} else {
				logger.warning(String.format("We have this set of handlers\n%s\nin which we couldn't find one for element %s associated project %s",
						scmHandlers.stream().map(handler -> handler.toString()).collect(Collectors.joining()),
						element.getCanonicalName(),
						elementProject
						));
			}
		}
	}

	void writeReadmeFor(SCMHandler handler, Element element, String elementProject, OutputBuilder builder) {
		String elementPath = element.getProperties().getOrDefault(ModelElementKeys.SCM_PATH, "");
		String elementReadme = element.getProperties().get(ModelElementKeys.SCM_README);
		File outputFor = builder.outputFor(AgileArchitectureSection.code, element, this, "adoc");
		Predicate<SCMFile> filter;
		if(elementReadme==null) {
			filter = (file) -> file.name().toLowerCase().startsWith("readme.");
		} else {
			filter = (file) -> file.name().equals(elementReadme);
		}
		Collection<SCMFile> file = handler.find(elementProject, elementPath, filter);
		if(file.isEmpty()) {
			logger.severe(String.format("Couldn't find any Readme for element %s"
					+ "(project is %s, path %s and readme should be %s)", 
					element.getCanonicalName(), elementProject, elementPath, elementReadme));
		} else if(file.size()>1) {
			logger.severe(String.format("There are more than one valid Readme for element %s"
					+ "(project is %s, path %s and readme should be %s)", 
					element.getCanonicalName(), elementProject, elementPath, elementReadme));
		} else {
			SCMFile readme = file.iterator().next();
			if(force) {
				outputFor.delete();
			} else {
				if(readme.lastModified()<outputFor.lastModified())
					return;
			}
			try {
				// Now we have content as asciidoc, so let's write it to the conventional location
				String readmeText = IOUtils.toString(readme.content(), "UTF-8");
				if(readme.name().toLowerCase().endsWith(".md")) {
					readmeText = Converter.convertMarkdownToAsciiDoc(readmeText);
				}
				FileUtils.write(outputFor, readmeText, "UTF-8");
			} catch (Throwable e) {
				throw new CantExtractReadme(String.format(
						"Can't extract readme of container %s using SCM project %s, path %s, readme %s", 
						element.getCanonicalName(), elementProject, elementPath, elementReadme), 
						e);
			}
		}
	}
}

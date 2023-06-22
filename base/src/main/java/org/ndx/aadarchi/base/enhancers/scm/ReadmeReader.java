package org.ndx.aadarchi.base.enhancers.scm;

import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.filter.RegexFileFilter;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.OutputBuilder.Format;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.StructurizrUtils;
import org.ndx.aadarchi.base.utils.commonsvfs.FileObjectDetector;

import com.structurizr.annotation.Component;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

import nl.jworks.markdown_to_asciidoc.Converter;

/**
 * Collect each model element readme and output them in generated elements folder
 * 
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java, CDI")
public class ReadmeReader extends ModelElementAdapter {
	@Inject Logger logger;

	@Inject
	@ConfigProperty(name = "force", defaultValue = "false")
	boolean force;

	@Inject
	FileContentCache cache;
	
	@Inject FileObjectDetector detector;

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS + 2;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		detector.whenFileDetected(element, new RegexFileFilter("(readme|README)\\.(adoc|md)"), 
				// No file detected
				elementRoot -> { logger.severe(String.format(
						"Couldn't find any Readme for element %s " + "(path is %s)",
						StructurizrUtils.getCanonicalPath(element), elementRoot)); },
				// One file detected
				(elementRoot, readme) -> { writeReadmeFor(readme, element, builder); },
				// on multiple file detected
				(elementRoot, detectedFiles) -> { logger.severe(String.format(
						"There are more than one valid Readme for element %s"
								+ "(path is %s)",
						StructurizrUtils.getCanonicalPath(element), elementRoot)); }
		);
	}

	void writeReadmeFor(FileObject readme, Element element, OutputBuilder builder) {
		FileObject outputFor = builder.outputFor(AgileArchitectureSection.code, element, this, Format.adoc);
		try {
			try {
				if (force) {
					outputFor.delete();
				} else {
					if (outputFor.exists()
							&& readme.getContent().getLastModifiedTime() < outputFor.getContent().getLastModifiedTime())
						return;
				}
				// Now we have content as asciidoc, so let's write it to the conventional
				// location
				String readmeText = IOUtils.toString(cache.openStreamFor(readme), "UTF-8");
				if (readme.getName().getExtension().toLowerCase().equals("md")) {
					readmeText = Converter.convertMarkdownToAsciiDoc(readmeText);
				}
				builder.writeToOutput(AgileArchitectureSection.code, element, this, Format.adoc, readmeText);
			} finally {
				readme.close();
			}
		} catch (Exception e) {
			throw new CantExtractReadme(String.format(
					"Can't extract readme of container %s from file %s",
					StructurizrUtils.getCanonicalPath(element), readme), e);
		}
	}
}

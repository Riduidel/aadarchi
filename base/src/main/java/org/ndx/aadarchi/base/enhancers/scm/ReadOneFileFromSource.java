package org.ndx.aadarchi.base.enhancers.scm;

import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileObject;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.OutputBuilder.Format;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.StructurizrUtils;
import org.ndx.aadarchi.base.utils.commonsvfs.FileObjectDetector;

import com.kodcu.asciidocfx.MarkdownToAsciidoc;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

/**
 * A component allowing to easily include one file (markdown or asciidoc)
 * in a specific documentation part
 */
@Dependent
public class ReadOneFileFromSource {
	@Inject Logger logger;

	@Inject
	FileContentCache cache;
	
	@Inject FileObjectDetector detector;

	public void read(StaticStructureElement element, 
			FileFilter fileFilter, 
			AgileArchitectureSection section,
			OutputBuilder builder,
			Enhancer enhancer,
			boolean force) {
		detector.whenFileDetected(element, fileFilter, 
				// No file detected
				elementRoot -> { logger.severe(String.format(
						"Couldn't find any file matching %s for element %s " + "(path is %s)",
						StructurizrUtils.getCanonicalPath(element), elementRoot)); },
				// One file detected
				(elementRoot, readme) -> { writeContentOf(readme, element, section, builder, enhancer, force); },
				// on multiple file detected
				(elementRoot, detectedFiles) -> { logger.severe(String.format(
						"There are more than one valid file matching %s for element %s"
								+ "(path is %s)",
						StructurizrUtils.getCanonicalPath(element), elementRoot)); }
		);
	}

	void writeContentOf(FileObject readme, Element element, AgileArchitectureSection section, OutputBuilder builder, Enhancer enhancer, boolean force) {
		FileObject outputFor = builder.outputFor(section, element, enhancer, Format.adoc);
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
				String fileText = IOUtils.toString(cache.openStreamFor(readme), "UTF-8");
				if (readme.getName().getExtension().toLowerCase().equals("md")) {
					fileText = MarkdownToAsciidoc.convert(fileText);
				}
				builder.writeToOutput(section, element, enhancer, Format.adoc, fileText);
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

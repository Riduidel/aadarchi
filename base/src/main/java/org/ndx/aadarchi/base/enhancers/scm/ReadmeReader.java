package org.ndx.aadarchi.base.enhancers.scm;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.filter.RegexFileFilter;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.OutputBuilder.Format;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.pivovarit.function.ThrowingFunction;
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
	@Inject
	Logger logger;

	@Inject
	FileSystemManager fileSystemManager;
	@Inject Instance<SCMHandler> scmHandlers;

	@Inject
	@ConfigProperty(name = "force", defaultValue = "false")
	boolean force;

	@Inject
	FileContentCache cache;

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS + 2;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		whenFileDetected(element, 
				new RegexFileFilter("(readme|README)\\.(adoc|md)"),
				// No file detected
				elementRoot -> logger.severe(String.format(
						"Couldn't find any Readme for element %s " + "(path is %s)",
						StructurizrUtils.getCanonicalPath(element), elementRoot)),
				// One file detected
				(elementRoot, readme) -> writeReadmeFor(readme, element, builder),
				// on multiple file detected
				(elementRoot, detectedFiles) -> logger.severe(String.format(
						"There are more than one valid Readme for element %s"
								+ "(path is %s)",
						StructurizrUtils.getCanonicalPath(element), elementRoot))
		);
	}

	/**
	 * Perform the given success operation when file is detected.
	 * 
	 * @param element
	 * @param onMultipleFileDetected 
	 */
	private void whenFileDetected(StaticStructureElement element, FileFilter fileFilter, 
			Consumer<FileObject> onNoFileDetected,
			BiConsumer<FileObject, FileObject> onFileDetected, 
			BiConsumer<FileObject, FileObject[]> onMultipleFileDetected) {
		Map<String, String> properties = element.getProperties();
		Optional<FileObject> analyzedPath = Optional.empty();
		if (properties.containsKey(ModelElementKeys.ConfigProperties.BasePath.NAME)) {
			String localPath = properties.get(ModelElementKeys.ConfigProperties.BasePath.NAME);
			try {
				analyzedPath = Optional.ofNullable(
						fileSystemManager.resolveFile(localPath));
			} catch (FileSystemException e) {
				logger.log(Level.SEVERE, String.format("Unable to resolve path to %s", localPath), e);
			}
		} else if (properties.containsKey(ModelElementKeys.Scm.PROJECT)) {
			var scmProject = properties.get(ModelElementKeys.Scm.PROJECT);
			var path = properties.getOrDefault(ModelElementKeys.Scm.PATH, "");
			analyzedPath = scmHandlers.stream()
				.filter(handler -> handler.canHandle(scmProject))
				.map(handler -> handler.getProjectRoot(scmProject))
				.findFirst()
				.map(ThrowingFunction.unchecked(scmProjectRoot -> scmProjectRoot.resolveFile(path)));
		}

		if(analyzedPath.isPresent()) {
			var elementRoot = analyzedPath.get();
			FileSelector filter = new FileFilterSelector(fileFilter);
			try {
				FileObject[] found = elementRoot.findFiles(filter);
				if (found.length == 0) {
					onNoFileDetected.accept(elementRoot);
				} else if (found.length > 1) {
					onMultipleFileDetected.accept(elementRoot, found);
				} else {
					FileObject detected = found[0];
					onFileDetected.accept(elementRoot, detected);
				}
			} catch (FileSystemException e) {
				logger.log(Level.SEVERE,
						String.format(
								"Couldn't find any file matching pattern %s for element %s"
										+ "(element path is %s)",
								StructurizrUtils.getCanonicalPath(element),
								filter,
								elementRoot),
						e);
			}
		}
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

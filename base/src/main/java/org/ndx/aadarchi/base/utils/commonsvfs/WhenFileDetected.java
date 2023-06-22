package org.ndx.aadarchi.base.utils.commonsvfs;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.annotation.Component;
import com.structurizr.model.StaticStructureElement;

/**
 * This component allow other ones to have easy file detection implemented as a service.
 */
@ApplicationScoped
@Component(technology="Java, CDI, Commons VFS")
public class WhenFileDetected {
	@Inject public Logger logger;

	@Inject public FileSystemManager fileSystemManager;
	@Inject public Instance<SCMHandler> scmHandlers;

	/**
	 * Perform the given success operation when file is detected.
	 * @param element
	 * @param fileFilter TODO
	 * @param onNoFileDetected TODO
	 * @param onFileDetected TODO
	 * @param onMultipleFileDetected 
	 */
	public void whenFileDetected(StaticStructureElement element, FileFilter fileFilter, Consumer<FileObject> onNoFileDetected, BiConsumer<FileObject, FileObject> onFileDetected, BiConsumer<FileObject, FileObject[]> onMultipleFileDetected) {
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

}

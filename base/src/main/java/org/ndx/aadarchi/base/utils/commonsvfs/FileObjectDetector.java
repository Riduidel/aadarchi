package org.ndx.aadarchi.base.utils.commonsvfs;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import org.apache.commons.vfs2.FileFilter;
import org.apache.commons.vfs2.FileFilterSelector;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;
import org.ndx.aadarchi.base.enhancers.scm.SCMHandler;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.pivovarit.function.ThrowingFunction;
import com.structurizr.annotation.Component;
import com.structurizr.model.Element;

/**
 * This component allow other ones to have easy file detection implemented as a service.
 */
@ApplicationScoped
@Component(technology="Java, CDI, Commons VFS")
public class FileObjectDetector {
	@Inject Logger logger;

	@Inject FileSystemManager fileSystemManager;
	@Inject Instance<SCMHandler> scmHandlers;

	@Inject FileSystemOptions fileSystemOptions;

	/**
	 * Perform the given success operation when file is detected.
	 * 
	 * File can currently be detected in two cases
	 * <ol>
	 * <li>When the base path ({@link ModelElementKeys.ConfigProperties.BasePath} has been defined as element property</li>
	 * <li>When the {@link ModelElementKeys.Scm.PROJECT} (and {@link ModelElementKeys.Scm.PATH}) have been defined</li>
	 * </ol>
	 * @param element
	 * @param fileFilter allows to select the file to detect
	 * @param onNoFileDetected
	 * @param onFileDetected
	 * @param onMultipleFileDetected 
	 */
	public void whenFileDetected(Element element, 
			FileFilter fileFilter, 
			Consumer<FileObject> onNoFileDetected, 
			BiConsumer<FileObject, FileObject> onFileDetected, 
			BiConsumer<FileObject, FileObject[]> onMultipleFileDetected) {
		whenFileDetected(element, fileFilter, 
				elementRoot -> { onNoFileDetected.accept(elementRoot); return Optional.empty(); }, 
				(elementRoot, found) -> { onFileDetected.accept(elementRoot, found); return Optional.empty(); }, 
				(elementRoot, files) -> { onMultipleFileDetected.accept(elementRoot, files); return Optional.empty(); });
	}
	
	public <Returned> Optional<Returned> whenFileDetected(Element element, 
			FileFilter fileFilter, 
			Function<FileObject, Optional<Returned>> onNoFileDetected, 
			BiFunction<FileObject, FileObject, Optional<Returned>> onFileDetected, 
			BiFunction<FileObject, FileObject[], Optional<Returned>> onMultipleFileDetected) {
		Map<String, String> properties = element.getProperties();
		Optional<FileObject> analyzedPath = Optional.empty();
		if (properties.containsKey(ModelElementKeys.ConfigProperties.BasePath.NAME)) {
			String localPath = properties.get(ModelElementKeys.ConfigProperties.BasePath.NAME);
			try {
				analyzedPath = Optional.ofNullable(
						fileSystemManager.resolveFile(localPath, fileSystemOptions));
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
					return onNoFileDetected.apply(elementRoot);
				} else if (found.length > 1) {
					return onMultipleFileDetected.apply(elementRoot, found);
				} else {
					FileObject detected = found[0];
					return onFileDetected.apply(elementRoot, detected);
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
		return Optional.empty();
	}

}

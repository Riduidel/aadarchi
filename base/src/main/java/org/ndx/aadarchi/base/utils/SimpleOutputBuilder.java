package org.ndx.aadarchi.base.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.NameScope;
import org.ndx.aadarchi.base.AgileArchitectureException;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.structurizr.model.Element;

public class SimpleOutputBuilder implements OutputBuilder {
	public static final class UnableToWriteOutput extends AgileArchitectureException {
		public UnableToWriteOutput(String message, Throwable cause) {
			super(message, cause);
		}
	}

	private final FileObject enhancementsBase;
	public static final String SECTION_PATTERN = "%02d-%s";

	public SimpleOutputBuilder(FileObject outputBase) {
		super();
		this.enhancementsBase = outputBase;
	}

	@Override
	public FileObject outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, String format) {
		// Yup, we use hex values for priority, to have less characters
		String path = String.format("%s/" + SECTION_PATTERN + "/_%08x-%s.%s",
				sanitize(element), section.index(), section.name(),
				enhancer.priority(), enhancer.getClass().getSimpleName(), format);
		return outputFor(path);
	}

	private FileObject outputFor(String path) {
		try {
			while (path.startsWith("/")) {
				path = path.substring(1);
			}
			return enhancementsBase.resolveFile(path, NameScope.DESCENDENT);
		} catch (FileSystemException e) {
			throw new CantToResolvePath(
					String.format("Unable to resolve path %s relatively to %s", path, enhancementsBase), e);
		}
	}

	@Override
	public FileObject outputDirectoryFor(AgileArchitectureSection section, Element element) {
		String path = String.format("%s/" + SECTION_PATTERN, sanitize(element),
				section.index(), section.name());
		return outputFor(path);
	}
	
	private String sanitize(Element element) {
		String sanitized = "";
		if(element.getParent()!=null) {
			sanitized += sanitize(element.getParent());
			sanitized+="/";
		}
		String elementName = element.getProperties().containsKey(ModelElementKeys.FILE_NAME_OVERRIDE) ?
				element.getProperties().get(ModelElementKeys.FILE_NAME_OVERRIDE) : element.getName();
		return sanitized + sanitize(elementName);
	}

	private String sanitize(String element) {
		return Stream.of(element.split("\\/"))
				.map(name -> name.replaceAll("[^ a-zA-Z0-9]", "_"))
				.collect(Collectors.joining("/"));
	}

	@Override
	public FileObject outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer,
			HandledFormat format) {
		return outputFor(section, element, enhancer, format.getExtension());
	}

	public FileObject writeToOutput(AgileArchitectureSection section, Element element, Enhancer enhancer,
			HandledFormat format, CharSequence text, boolean append) {
		FileObject returned = outputFor(section, element, enhancer, format);
		if (!StringUtils.isBlank(text)) {
			try {
				if (!append)
					returned.delete();
				returned.getParent().createFolder();
				try (OutputStream outputStream = returned.getContent().getOutputStream()) {
					IOUtils.write(format.createCommentForEnhancer(enhancer), outputStream, format.encoding());
					IOUtils.write(text, outputStream, format.encoding());
				} finally {
					returned.getContent().close();
				}
			} catch (IOException e) {
				throw new UnableToWriteOutput(
						String.format("Unable to write output to file %s", returned.getName().getPath()), e);
			}
		}
		return returned;
	}

	@Override
	public FileObject writeToOutput(AgileArchitectureSection section, Element element, Enhancer enhancer,
			HandledFormat format, CharSequence text) {
		return writeToOutput(section, element, enhancer, format, text, false);
	}

	@Override
	public FileObject appendToOutput(AgileArchitectureSection section, Element element, Enhancer enhancer,
			HandledFormat format, CharSequence text) {
		return writeToOutput(section, element, enhancer, format, text, true);
	}

}

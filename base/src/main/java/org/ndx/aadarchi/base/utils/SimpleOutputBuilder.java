package org.ndx.aadarchi.base.utils;

import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureException;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.Enhancer;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.EnhancementsDir;

import com.structurizr.model.Element;

public class SimpleOutputBuilder implements OutputBuilder {
	public static final class UnableToWiteOutput extends AgileArchitectureException {
		public UnableToWiteOutput(String message, Throwable cause) {
			super(message, cause);
		}
	}
	private final File enhancementsBase;
	public static final String SECTION_PATTERN = "%02d-%s";

	@Inject
	public SimpleOutputBuilder(@ConfigProperty(name=EnhancementsDir.NAME, defaultValue = EnhancementsDir.VALUE) File outputBase) {
		super();
		this.enhancementsBase = outputBase;
	}

	@Override
	public File outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, String format) {
		return new File(enhancementsBase,
				// Yup, we use hex values for priority, to have less characters
				String.format("%s/"+SECTION_PATTERN+"/_%08x-%s.%s", 
					sanitize(StructurizrUtils.getCanonicalPath(element)),
					section.index(), section.name(),
					enhancer.priority(), enhancer.getClass().getSimpleName(), format
					)
				);
	}

	@Override
	public File outputDirectoryFor(AgileArchitectureSection section, Element element) {
		return new File(enhancementsBase,
				// Yup, we use hex values for priority, to have less characters
				String.format("%s/"+SECTION_PATTERN, 
					sanitize(StructurizrUtils.getCanonicalPath(element)),
					section.index(), section.name()
					)
				);
	}

	private String sanitize(String canonicalPath) {
		return Stream.of(canonicalPath.split("\\/"))
				.map(name -> name.replaceAll("[:\\\\/*?|<>]", "_"))
				.collect(Collectors.joining("/"));
	}

	@Override
	public File outputFor(AgileArchitectureSection section, Element element, Enhancer enhancer, HandledFormat format) {
		return outputFor(section, element, enhancer, format.getExtension());
	}

	public File writeToOutput(AgileArchitectureSection section, Element element, Enhancer enhancer,
			HandledFormat format, CharSequence text, boolean append) {
		File returned = outputFor(section, element, enhancer, format);
		returned.getParentFile().mkdirs();
		try {
			FileUtils.write(returned, format.createCommentForEnhancer(enhancer), format.encoding(), append);
			FileUtils.write(returned, text, format.encoding(), true);
		} catch(IOException e) {
			throw new UnableToWiteOutput(
					String.format("Unable to write output to file %s", returned.getAbsolutePath()),
					e
					);
		}
		return returned;
	}

	@Override
	public File writeToOutput(AgileArchitectureSection section, Element element, Enhancer enhancer,
			HandledFormat format, CharSequence text) {
		return writeToOutput(section, element, enhancer, format, text, false);
	}

	@Override
	public File appendToOutput(AgileArchitectureSection section, Element element,
			Enhancer enhancer, HandledFormat format, CharSequence text) {
		return writeToOutput(section, element, enhancer, format, text, true);
	}

}

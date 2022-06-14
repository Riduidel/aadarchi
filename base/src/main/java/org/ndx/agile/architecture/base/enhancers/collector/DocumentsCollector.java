package org.ndx.agile.architecture.base.enhancers.collector;

import java.io.File;
import java.io.IOException;
import java.text.Normalizer;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;
import org.ndx.agile.architecture.base.enhancers.ModelElementKeys.ConfigProperties.EnhancementsDir;
import org.ndx.agile.architecture.base.utils.StructurizrUtils;

import com.structurizr.Workspace;
import com.structurizr.model.Component;
import com.structurizr.model.Container;
import com.structurizr.model.Element;
import com.structurizr.model.Model;
import com.structurizr.model.SoftwareSystem;

/**
 * A utility enhancer allowing enhancers not to worry about collecting documents into a structure.
 * This enhancer will build for each section a <code>_${section}_include.adoc</code> which will include 
 * in alphabetical order all generated elements with reursive scan of all component data.
 * @author nicolas-delsaux
 *
 */
@com.structurizr.annotation.Component(technology = "Java/CDI")
public class DocumentsCollector implements ModelEnhancer {
	private File enhancementsBase;
	/**
	 * Injecting enhancements base to have a folder where to put our documents.
	 */
	@Inject public void setEnhancementsBase(@ConfigProperty(name=EnhancementsDir.NAME, defaultValue = EnhancementsDir.VALUE) File enhancementsBase) {
		this.enhancementsBase = enhancementsBase.getAbsoluteFile();
	}
	/**
	 * Map that contains all elements that should be automagically included.
	 * First level keys are the enums.
	 * Second level keys are the components, sorted by their canonical name
	 * And value is a sorted set of file names.
	 */
	Map<AgileArchitectureSection, Map<Element, Set<File>>> hierarchy = new EnumMap<>(AgileArchitectureSection.class);
	@Override
	public boolean isParallel() {
		return false;
	}

	@Override
	public int priority() {
		return Integer.MAX_VALUE-1;
	}

	@Override
	public boolean startVisit(Workspace workspace, OutputBuilder builder) {
		// Initialize map with all sections
		for(AgileArchitectureSection section : AgileArchitectureSection.values()) {
			hierarchy.put(section, 
					new TreeMap<Element, 
						Set<File>>(Comparator.comparing(element -> StructurizrUtils.getCanonicalPath(element))));
		}
		return true;
	}

	/**
	 * Once workspace has been visited, we write one file per section which is simply an asciidoc list of 
	 * includes with relative paths.
	 */
	@Override
	public void endVisit(Workspace workspace, OutputBuilder builder) {
		hierarchy.entrySet().stream()
//			.filter(entry -> !entry.getValue().isEmpty())
			.forEach(entry -> writeIncludeFor(entry.getKey(), entry.getValue()));
	}

	/**
	 * Write an incldue file for the given section containing includes (with the good level offset)
	 * for each mentionned component
	 * @param key
	 * @param value
	 */
	void writeIncludeFor(AgileArchitectureSection section, Map<Element, Set<File>> enhancements) {
		File target = new File(enhancementsBase, String.format("_%02d-%s.adoc", section.index(), section.name()));
		try {
			FileUtils.write(target, generateContent(section, enhancements, target.getParentFile()), "UTF-8");
		} catch (IOException e) {
			throw new CantCollectEnhancements(
					String.format("can't create include file %s", target.getAbsolutePath()),
					e);
		}
	}

	/**
	 * Generate from hierarchy infos the content that will be generated
	 * @param section 
	 * @param enhancements
	 * @param target
	 * @return
	 */
	String generateContent(AgileArchitectureSection section, Map<Element, Set<File>> enhancements, File target) {
		String content = enhancements.entrySet().stream()
				// TODO potentially test for header generation based upon name deepness
			.map(entry -> generateElementContent(section, target, entry.getKey(), entry.getValue()))
			.collect(Collectors.joining("\n", 
					String.format("// Generated by %s\n", getClass().getName()), ""))
			;
		return content;
	}

	String generateElementContent(AgileArchitectureSection section, File target, Element element, Set<File> generated) {
		int deepness = StringUtils.countMatches(StructurizrUtils.getCanonicalPath(element), '/');
		return String.format("%s\n"
				+ "%s\n",
			elementAsTitle(section, deepness, element),
			generated.stream()
				.map(file -> target.toPath().relativize(file.toPath()).toString())
				.map(path -> String.format("include::%s[leveloffset=+%d]",
						path,
						deepness
						))
				.collect(Collectors.joining("\n"))
			);
	}

	/**
	 * Generate a title from an element.
	 * This title will also include an anchor allowing redirection to this precise section.
	 * This anchor should contain both section and element identifiers.
	 * As a consequence, we use section number-element name (with key latinized and non ascii characters replaced by minus)
	 * @param section 
	 * @param key element for which we want a title
	 * @return an Asciidoc title
	 */
	private String elementAsTitle(AgileArchitectureSection section, int deeepness, Element key) {
		String elementId = String.format("%s-%s", section.name(), key.getName());
	    // convert diacritics to a two-character form (NFD)
	    // http://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html
	    elementId = Normalizer.normalize(elementId, Normalizer.Form.NFD);

	    // remove all characters that combine with the previous character
	    // to form a diacritic.  Also remove control characters.
	    // http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html
	    elementId = elementId.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{Cntrl}]", "");
	    String refText = String.format("%s %s", key.getName(), section.name());
		return String.format("[[#%s, reftext=%s]]\n%s %s",
				elementId,
				refText,
				StringUtils.repeat('=', deeepness),
				key.getName());
	}

	@Override
	public boolean startVisit(Model model) {
		return true;
	}

	@Override
	public boolean startVisit(SoftwareSystem softwareSystem) {
		return true;
	}

	@Override
	public boolean startVisit(Container container) {
		return true;
	}

	@Override
	public boolean startVisit(Component component) {
		return true;
	}

	/**
	 * Collect elements of all sections produced for the given component
	 * @param element the element for which we want the output
	 * @param builder output generator used
	 */
	public void collect(Element element, OutputBuilder builder) {
		for(AgileArchitectureSection section : AgileArchitectureSection.values()) {
			File sectionFolderFor = builder.outputDirectoryFor(section, element);
			File[] filesArray = sectionFolderFor.listFiles((dir, name) -> name.toLowerCase().endsWith(".adoc"));
			if(filesArray!=null && filesArray.length>0) {
				Set<File> files = Stream.of(filesArray)
						.map(file -> file.getAbsoluteFile())
						.collect(Collectors.toCollection(() -> new TreeSet<File>()));
				hierarchy.get(section).put(element, files);
			}
		}
	}

	@Override
	public void endVisit(Component component, OutputBuilder builder) {
		collect(component, builder);
	}

	@Override
	public void endVisit(Container container, OutputBuilder builder) {
		collect(container, builder);
	}

	@Override
	public void endVisit(SoftwareSystem softwareSystem, OutputBuilder builder) {
		collect(softwareSystem, builder);
	}

	@Override
	public void endVisit(Model model, OutputBuilder builder) {
		// TODO Auto-generated method stub

	}

}

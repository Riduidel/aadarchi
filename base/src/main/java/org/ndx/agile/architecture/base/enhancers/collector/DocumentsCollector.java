package org.ndx.agile.architecture.base.enhancers.collector;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.agile.architecture.base.AgileArchitectureSection;
import org.ndx.agile.architecture.base.ModelEnhancer;
import org.ndx.agile.architecture.base.OutputBuilder;

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
public class DocumentsCollector implements ModelEnhancer {
	/**
	 * Injecting enhancements base to have a folder where to put our documents.
	 */
	@Inject @ConfigProperty(name="agile.architecture.enhancements") File enhancementsBase;
	/**
	 * Map that contains all elements that should be automagically included.
	 * First level keys are the enums.
	 * Second level keys are the components, sorted by their canonical name
	 * And value is a sorted set of file names.
	 */
	Map<AgileArchitectureSection, Map<String, Set<File>>> hierarchy = new EnumMap<>(AgileArchitectureSection.class);
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
					new TreeMap<String, 
						Set<File>>());
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
	void writeIncludeFor(AgileArchitectureSection section, Map<String, Set<File>> enhancements) {
		File target = new File(enhancementsBase, String.format("_%02d-%s.adoc", section.index(), section.name()));
		String content = generateContent(enhancements, target);
		try {
			FileUtils.write(target, content, "UTF-8");
		} catch (IOException e) {
			throw new CantCollectEnhancements(
					String.format("can't create include file %s", target.getAbsolutePath()),
					e);
		}
	}

	/**
	 * Generate from hierarchy infos the content that will be generated
	 * @param enhancements
	 * @param target
	 * @return
	 */
	String generateContent(Map<String, Set<File>> enhancements, File target) {
		String content = enhancements.entrySet().stream()
				// TODO potentially test for header generation based upon name deepness
			.map(entry -> String.format("%s\n%s",
					String.format("// %s", entry.getKey()),
					entry.getValue().stream()
						.map(file -> target.toPath().relativize(file.toPath()).toString())
						.map(path -> String.format("include::%s[[leveloffset=+1]]", path))
						.collect(Collectors.joining("\n"))
					))
			.collect(Collectors.joining("\n"))
			;
		return content;
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
	 * @param component
	 * @param builder
	 */
	public void collect(Element element, OutputBuilder builder) {
		for(AgileArchitectureSection section : AgileArchitectureSection.values()) {
			File sectionFolderFor = builder.outputFor(section, element);
			File[] filesArray = sectionFolderFor.listFiles((dir, name) -> name.toLowerCase().endsWith(".adoc"));
			if(filesArray!=null && filesArray.length>0) {
				Set<File> files = new TreeSet<>( 
						Arrays.asList(
								filesArray));
				hierarchy.get(section).put(element.getCanonicalName(), files);
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

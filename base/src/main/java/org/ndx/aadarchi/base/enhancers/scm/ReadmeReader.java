package org.ndx.aadarchi.base.enhancers.scm;

import com.kodcu.asciidocfx.MarkdownToAsciidoc;
import com.structurizr.annotation.Component;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;
import org.apache.commons.io.IOUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.filter.RegexFileFilter;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.OutputBuilder.Format;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.utils.FileContentCache;
import org.ndx.aadarchi.base.utils.StructurizrUtils;
import org.ndx.aadarchi.base.utils.commonsvfs.FileObjectDetector;
import org.ndx.aadarchi.cdi.deltaspike.ConfigProperty;

import jakarta.inject.Inject;
import java.util.logging.Logger;

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
	
	@Inject ReadOneFileFromSource readOneFile;

	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS + 2;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		readOneFile.read(element, 
				new RegexFileFilter("(readme|README)\\.(adoc|md)"), 
				AgileArchitectureSection.code, builder, this, force);
	}
}

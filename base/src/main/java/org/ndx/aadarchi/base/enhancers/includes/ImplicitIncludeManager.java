package org.ndx.aadarchi.base.enhancers.includes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.ndx.aadarchi.base.AgileArchitectureSection;
import org.ndx.aadarchi.base.OutputBuilder;
import org.ndx.aadarchi.base.enhancers.ModelElementAdapter;
import org.ndx.aadarchi.base.enhancers.ModelElementKeys.ConfigProperties.AsciidocSourceDir;
import org.ndx.aadarchi.base.utils.CantAccessPath;
import org.ndx.aadarchi.base.utils.SimpleOutputBuilder;
import org.ndx.aadarchi.base.utils.StructurizrUtils;

import com.structurizr.annotation.Component;
import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

/**
 * Enhancer allowing includes to be auto-generated for each element having interesting content.
 * 
 * Let's take an example.
 * Suppose you want to add some documentation for a component code.
 * Then you just have to write the documentation in the file ${asciidoc.source.docs.directory}/07-code/${component name}.adoc
 * and it will be automagically included in documentation.
 * 
 * @author nicolas-delsaux
 *
 */
@Component(technology = "Java, CDI")
public class ImplicitIncludeManager extends ModelElementAdapter {
	
	private FileObject sourceDir;

	@Inject public void setDocumentsFolder(@ConfigProperty(name=AsciidocSourceDir.NAME, defaultValue = AsciidocSourceDir.VALUE) FileObject sourceDir) {
		if(sourceDir==null) {
			throw new CantCreateImplicitInclude(
					String.format("To have implicit includes working, you have to define the system property %s", 
							AsciidocSourceDir.NAME)
					);
		}
		this.sourceDir = sourceDir;
	}
	/**
	 * We set it at first element to have those text before all enhancers
	 */
	@Override
	public int priority() {
		return TOP_PRIORITY_FOR_INTERNAL_ENHANCERS+1;
	}

	@Override
	protected void processElement(StaticStructureElement element, OutputBuilder builder) {
		for(AgileArchitectureSection section : AgileArchitectureSection.values()) {
			generateLinkFor(section, element, builder);
		}
	}

	private void generateLinkFor(AgileArchitectureSection section, Element element, OutputBuilder builder) {
		String sectionFolder = String.format(SimpleOutputBuilder.SECTION_PATTERN, section.index(), section.name());
		String elementName = String.format("%s.adoc", StructurizrUtils.getCanonicalPath(element).replace('/', '_'));
		try {
			FileObject potentialFile = sourceDir.resolveFile(sectionFolder).resolveFile(elementName);
			FileObject targetFile = builder.outputFor(section, element, this, OutputBuilder.Format.adoc);
			if(potentialFile.exists()) {
				try {
					Path relativePath = targetFile.getParent().getPath().relativize(potentialFile.getPath());
					builder.writeToOutput(section, element, this, OutputBuilder.Format.adoc, 
							String.format("include::%s[leveloffset=+1]\n",
									relativePath.toString()));
				} catch (IOException e) {
					throw new CantCreateImplicitInclude(String.format("Can't create file %s", targetFile), e);
				}
			}
		} catch (FileSystemException e) {
			throw new CantAccessPath(String.format("Cant't acces %s/%s/%s", sourceDir, sectionFolder, elementName), e);
		}
	}

}

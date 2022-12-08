package org.ndx.aadarchi.inferer.maven.enhancers;

import java.util.Optional;
import java.util.function.Function;

import org.apache.maven.project.MavenProject;

import com.structurizr.model.StaticStructureElement;

/**
 * Base class allowing enhancement of a Structurizr model element
 * @author Nicolas
 *
 * @param <Enhanced> type of element to enhance
 */
abstract class ModelElementMavenEnhancer<Enhanced extends StaticStructureElement> {

	/**
	 * Model element that we enhance
	 */
	protected final Enhanced enhanced;

	public ModelElementMavenEnhancer(Enhanced enhanced) {
		this.enhanced = enhanced;
	}

	public void startEnhance(Function<Enhanced, Optional<MavenProject>> modelExtractor) {
		modelExtractor.apply(enhanced).ifPresent(this::startEnhanceWithMavenProject);
	}

	public void endEnhance(Function<Enhanced, Optional<MavenProject>> modelExtractor) {
		modelExtractor.apply(enhanced).ifPresent(this::endEnhanceWithMavenProject);
	}

	protected abstract void startEnhanceWithMavenProject(MavenProject mavenProject);

	protected abstract void endEnhanceWithMavenProject(MavenProject mavenProject);

}
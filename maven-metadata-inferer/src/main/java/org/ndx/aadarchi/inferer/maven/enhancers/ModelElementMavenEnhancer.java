package org.ndx.aadarchi.inferer.maven.enhancers;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;

import com.structurizr.model.StaticStructureElement;

/**
 * Base class allowing enhancement of a Structurizr model element
 * @author Nicolas
 *
 * @param <Enhanced> type of element to enhance
 */
abstract class ModelElementMavenEnhancer<Enhanced extends StaticStructureElement> {

	protected final MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer;
	/**
	 * Model element that we enhance
	 */
	protected final Enhanced enhanced;

	public ModelElementMavenEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, Enhanced enhanced) {
		this.mavenDetailsInfererEnhancer = mavenDetailsInfererEnhancer;
		this.enhanced = enhanced;
	}

	public void startEnhance() {
		this.mavenDetailsInfererEnhancer.processModelElement(enhanced).ifPresent(this::startEnhanceWithMavenProject);
	}

	public void endEnhance() {
		this.mavenDetailsInfererEnhancer.processModelElement(enhanced).ifPresent(this::endEnhanceWithMavenProject);
	}

	protected abstract void startEnhanceWithMavenProject(MavenProject mavenProject);

	protected abstract void endEnhanceWithMavenProject(MavenProject mavenProject);

}
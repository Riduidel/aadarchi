package org.ndx.aadarchi.inferer.maven.enhancers;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenDetailsInfererEnhancer;

import com.structurizr.model.Element;
import com.structurizr.model.StaticStructureElement;

/**
 * Provides base features and code organization for all model enhancers having a POM provided
 * @author nicolas-delsaux
 *
 * @param <Enhanced>
 */
abstract class ModelElementMavenEnhancer<Enhanced extends StaticStructureElement> {

	private final MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer;
	protected final Enhanced enhanced;

	public ModelElementMavenEnhancer(MavenDetailsInfererEnhancer mavenDetailsInfererEnhancer, Enhanced enhanced) {
		this.mavenDetailsInfererEnhancer = mavenDetailsInfererEnhancer;
		this.enhanced = enhanced;
	}

	/**
	 * When an element has a pom, we call the {@link #startEnhanceWithMavenProject(MavenProject)}
	 * method on the enhanced element
	 * @see MavenDetailsInfererEnhancer#processModelElement(Element)
	 */
	public void startEnhance() {
		mavenDetailsInfererEnhancer.processModelElement(enhanced).ifPresent(this::startEnhanceWithMavenProject);
	}

	/**
	 * When an element has a pom, we call the {@link #endEnhanceWithMavenProject(MavenProject)}
	 * method on the enhanced element
	 * @see MavenDetailsInfererEnhancer#processModelElement(Element)
	 */
	public void endEnhance() {
		mavenDetailsInfererEnhancer.processModelElement(enhanced).ifPresent(this::endEnhanceWithMavenProject);
	}

	protected abstract void startEnhanceWithMavenProject(MavenProject mavenProject);

	protected abstract void endEnhanceWithMavenProject(MavenProject mavenProject);

}
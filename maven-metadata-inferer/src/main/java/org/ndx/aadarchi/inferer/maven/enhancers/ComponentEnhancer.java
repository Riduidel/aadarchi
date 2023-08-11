package org.ndx.aadarchi.inferer.maven.enhancers;

import org.apache.maven.project.MavenProject;
import org.ndx.aadarchi.inferer.maven.MavenPomDecorator;
import org.ndx.aadarchi.inferer.maven.MavenPomReader;

import com.structurizr.model.Component;

public class ComponentEnhancer extends ModelElementMavenEnhancer<Component> {

	public ComponentEnhancer(MavenPomReader mavenPomReader, Component enhanced) {
		super(enhanced);
	}

	@Override
	protected void startEnhanceWithMavenProject(MavenProject mavenProject) {
	}

	@Override
	protected void endEnhanceWithMavenProject(MavenProject mavenProject) {

	}
}
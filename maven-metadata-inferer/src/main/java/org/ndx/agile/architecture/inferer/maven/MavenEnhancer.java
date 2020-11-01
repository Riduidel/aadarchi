package org.ndx.agile.architecture.inferer.maven;

import org.ndx.agile.architecture.base.enhancers.ModelElementKeys;

public interface MavenEnhancer {
	/**
	 * URL of the maven pom the model element represents.
	 * This URL can be
	 * <ul>
	 * <li>a local file</li>
	 * <li>a http(s)? url</li>
	 * <li>a resource in jar url</li>
	 *  </ul>
	 */
	String AGILE_ARCHITECTURE_MAVEN_POM = ModelElementKeys.PREFIX+"maven.pom";
	/**
	 * Name of a class we want to load the Maven POM for.
	 */
	String AGILE_ARCHITECTURE_MAVEN_CLASS = ModelElementKeys.PREFIX+"maven.class";

	/**
	 * Maven coordinates for an element linked to a maven module.
	 * This allows dependency inference
	 */
	String AGILE_ARCHITECTURE_MAVEN_COORDINATES = ModelElementKeys.PREFIX+"maven.coordinates";
	/**
	 * When set, this allows users to enter a list of profiles names separated by ";"
	 */
	String AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES = ModelElementKeys.PREFIX+"maven.profiles";
}

package org.ndx.agile.architecture.inferer.maven;

import java.io.File;
import java.util.Arrays;

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
	String AGILE_ARCHITECTURE_MAVEN_POM = "agile.architecture.maven.pom";
	/**
	 * Name of a class we want to load the Maven POM for.
	 */
	String AGILE_ARCHITECTURE_MAVEN_CLASS = "agile.architecture.maven.class";

	/**
	 * Maven coordinates for an element linked to a maven module.
	 * This allows dependency inference
	 */
	String AGILE_ARCHITECTURE_MAVEN_COORDINATES = "agile.architecture.maven.coordinates";
	/**
	 * When set, this allows users to enter a list of profiles names separated by ";"
	 */
	String AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES = "agile.architecture.maven.profiles";
}

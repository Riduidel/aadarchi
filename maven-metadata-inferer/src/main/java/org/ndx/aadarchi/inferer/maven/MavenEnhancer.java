package org.ndx.aadarchi.inferer.maven;

import java.util.Map;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.fasterxml.jackson.core.type.TypeReference;

public interface MavenEnhancer {
	/**
	 * URL of the maven pom the model element represents.
	 * This URL can be
	 * <ul>
	 * <li>a local file</li>
	 * <li>a http(s)? url</li>
	 * <li>a resource in jar url</li>
	 *  </ul>
	 *  This property is only written by this enhancer, never read.
	 *  YOu can use it to identify the element as a maven module.
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

	String AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES = ModelElementKeys.PREFIX+"maven.technologies";
	TypeReference<Map<String, String>> AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES_TYPE = new TypeReference<Map<String, String>>() {};
}

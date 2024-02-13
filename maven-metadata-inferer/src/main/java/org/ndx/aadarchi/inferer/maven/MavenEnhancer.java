package org.ndx.aadarchi.inferer.maven;

import java.util.Map;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

import com.fasterxml.jackson.core.type.TypeReference;

public interface MavenEnhancer {
	String PREFIX = ModelElementKeys.PREFIX+"maven.";
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
	String AGILE_ARCHITECTURE_MAVEN_POM = PREFIX+"pom";
	/**
	 * Name of a class we want to load the Maven POM for.
	 */
	String AGILE_ARCHITECTURE_MAVEN_CLASS = PREFIX+"class";

	/**
	 * Maven coordinates for an element linked to a maven module.
	 * This allows dependency inference
	 */
	String AGILE_ARCHITECTURE_MAVEN_COORDINATES = PREFIX+"coordinates";
	/**
	 * When set, this allows users to enter a list of profiles names separated by ";"
	 */
	String AGILE_ARCHITECTURE_MAVEN_ADDITIONAL_PROFILES = PREFIX+"profiles";

	String AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES = PREFIX+"technologies";
	TypeReference<Map<String, String>> AGILE_ARCHITECTURE_MAVEN_TECHNOLOGIES_TYPE = new TypeReference<Map<String, String>>() {};
	/**
	 * List of ignored submodules. Allows to hide architecture documentation from production-like code
	 */
	String IGNORED_SUBMODULES = PREFIX+"ignored.submodules";
	/**
	 * When set, dependencies tagged with one of the tags in this list will be filtered out from signifiant dependencies
	 */
	 public interface FilterDpendenciesTagged {
		 String NAME = PREFIX+"filter.dependencies.tagged";
		 String VALUE = "testing";
	 }
}

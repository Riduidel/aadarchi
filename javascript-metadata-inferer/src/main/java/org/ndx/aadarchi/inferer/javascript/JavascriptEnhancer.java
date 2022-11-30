package org.ndx.aadarchi.inferer.javascript;

import org.ndx.aadarchi.base.enhancers.ModelElementKeys;

public interface JavascriptEnhancer {

	String AGILE_ARCHITECTURE_NPM_PACKAGE = ModelElementKeys.PREFIX+"npm.package";
	/**
	 * Name of a class we want to load the NPM package for.
	 */
	String AGILE_ARCHITECTURE_NPM_CLASS = ModelElementKeys.PREFIX+"npm.class";

	String AGILE_ARCHITECTURE_NPM_COORDINATES = ModelElementKeys.PREFIX+"npm.coordinates";

	String AGILE_ARCHITECTURE_NPM_ADDITIONAL_PROFILES = ModelElementKeys.PREFIX+"npm.profiles";
}

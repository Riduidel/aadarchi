package org.ndx.agile.architecture.base;

/**
 * List of sections for agile architecture documentation.
 * This allows us to output thing "where they belong" without having to worry too much
 * @author nicolas-delsaux
 */
public enum AgileArchitectureSection {
	context,
	functional_overview,
	quality_attributes,
	constraints,
	principles,
	software_architecture,
	code,
	data,
	infrastructure_architecture,
	deployment,
	development_environment,
	operation_and_support,
	decision_log,;

	/**
	 * I want 1-based section indexing (to be consistent with what I have in the src/docs/asciidoc).
	 * @return {@link #ordinal()}+1
	 */
	public int index() {
		return ordinal()+1;
	}
}

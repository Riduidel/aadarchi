package org.ndx.aadarchi.sequence.generator;

import org.ndx.aadarchi.base.utils.StructurizrUtils;

public interface SequenceGenerator {

	/**
	 * When set, the container having this property will have all its components code elements
	 * scanned and sequence diagrams built for them.
	 * This property also defines which other model elements should be scanned during source code reading.
	 * Associated value should be a ";" separated list of canonical names of containers to also scan.
	 * The canonical name should be obtained by a call to {@link StructurizrUtils#getCanonicalPath(com.structurizr.model.Element)}
	 * and not structurizr method.
	 */
	String GENERATES_WITH = "agile.architecture.sequence.generator.with";

}

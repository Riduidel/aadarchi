package org.ndx.agile.architecture.base;

import org.apache.commons.configuration2.Configuration;

/**
 * All elements having init code should implement that interface
 * @author nicolas-delsaux
 *
 */
public interface Configurable {
	void configure(Configuration configuration);
}

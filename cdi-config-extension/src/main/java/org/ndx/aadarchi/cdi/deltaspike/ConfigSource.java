
package org.ndx.aadarchi.cdi.deltaspike;

import java.util.Map;

/**
 * Classes implementing that interface are responsible for providing
 * values.
 */
public interface ConfigSource {
	/**
	 * Get the config name, allowing to distinguish various configuration sources
	 * @return
	 */
	String getConfigName();

	/**
	 * Get the list of properties resolved at startup.
	 * Those properties won't be reloaded.
	 * @return
	 */
	Map<String, String> getProperties();

	/**
	 * Get the value of the property having the given key
	 * @param key property key
	 * @return property value if present, null otherwise
	 */
	String getPropertyValue(String key);

	/**
	 * Get priority level of config source
	 * @return any integer value will do. But higher ordinal values will be read first
	 */
	int getOrdinal();

	/**
	 * I don't yet know what it does
	 * @return
	 */
	boolean isScannable();

}

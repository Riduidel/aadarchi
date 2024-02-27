package org.ndx.aadarchi.cdi.deltaspike.commons;

import java.util.Map;

import org.apache.commons.configuration2.Configuration;
import org.ndx.aadarchi.cdi.deltaspike.ConfigSource;

/**
 * A bridge to allow usage of commons configurations config as Deltaspike-like one
 */
public class CommonsConfigurationConfigSource implements ConfigSource {
	private String name;
	private int ordinal;
	private Configuration configuration;

	public CommonsConfigurationConfigSource(String name, int ordinal, Configuration configuration) {
		super();
		this.name = name;
		this.ordinal = ordinal;
		this.configuration = configuration;
	}

	@Override
	public String getConfigName() {
		return name;
	}

	@Override
	public Map<String, String> getProperties() {
		return null;
	}

	@Override
	public String getPropertyValue(String key) {
		return configuration.getString(key);
	}

	@Override
	public int getOrdinal() {
		return ordinal;
	}

	@Override
	public boolean isScannable() {
		return false;
	}

}

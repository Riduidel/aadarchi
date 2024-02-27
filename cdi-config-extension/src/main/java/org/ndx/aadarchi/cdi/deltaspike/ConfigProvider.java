package org.ndx.aadarchi.cdi.deltaspike;

import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@Singleton
public class ConfigProvider {
	private static final Logger logger = Logger.getLogger(ConfigProvider.class.getName());
	private Set<ConfigSource> loadedSources = new TreeSet<>(
			new ByOrdinalComparator()
			);
	
	public static class ByOrdinalComparator implements Comparator<ConfigSource> {

		@Override
		public int compare(ConfigSource o1, ConfigSource o2) {
			int returned = 0;
			returned = -1 * Integer.compare(o1.getOrdinal(), o2.getOrdinal());
			if(returned==0) 
				returned = o1.getConfigName().compareTo(o2.getConfigName());
			return returned;
		}
		
	}
	@Inject Instance<ConfigFilter> configFilterList;
	/**
	 * Load each configuration in its ordinal order
	 * @param configSources
	 */
	@Inject
	public void loadConfigSources(Instance<ConfigSource> configSources) {
		configSources.stream()
			.filter(Objects::nonNull)
			.forEach(loadedSources::add);
	}
	public String getValueOf(ConfigProperty property) {
		String foundValue = findValueOf(property);
		return resolve(property.name(), foundValue);
	}
	public String resolve(String name, String value) {
		return filterValueOf(name, value);
	}
	private String filterValueOf(String name, String foundValue) {
		String filtered = foundValue;
		for(ConfigFilter filter : configFilterList) {
			logger.info(String.format("Filtering %s=%s", name, filtered));
			filtered = filter.filterValue(name, filtered);
		}
		logger.info(String.format("Filtered %s=%s", name, filtered));
		return filtered;
	}
	private String findValueOf(ConfigProperty property) {
		return loadedSources.stream()
				.map(source -> source.getPropertyValue(property.name()))
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(property.defaultValue());
	}
}

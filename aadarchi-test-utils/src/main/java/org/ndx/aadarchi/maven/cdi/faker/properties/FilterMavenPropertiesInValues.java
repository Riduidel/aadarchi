package org.ndx.aadarchi.maven.cdi.faker.properties;

import org.apache.commons.lang3.StringUtils;
import org.ndx.aadarchi.cdi.deltaspike.ConfigFilter;

import jakarta.inject.Inject;

public class FilterMavenPropertiesInValues implements ConfigFilter {
	
	@Inject ExposeMavenPropertiesAsConfigProperties configSource;

	@Override
	public String filterValue(String key, String value) {
		String updatedValue = value;
		for(ExposeMavenPropertiesAsConfigProperties.Keys k : ExposeMavenPropertiesAsConfigProperties.Keys.values()) {
			String initial = String.format("${%s}", k.getPropertyKey());
			String replaced = k.getComputedValue();
			updatedValue = updatedValue.replace(initial, replaced);
		}
		return updatedValue;
	}

	@Override
	public String filterValueForLog(String key, String value) {
		String updatedValue = value;
		for(ExposeMavenPropertiesAsConfigProperties.Keys k : ExposeMavenPropertiesAsConfigProperties.Keys.values()) {
			String initial = String.format("${%s}", k.getPropertyKey());
			String replaced = k.getComputedValue();
			if(k.getPropertyKey().contains("password")) {
				replaced = StringUtils.repeat('*', replaced.length());
			}
			updatedValue = updatedValue.replace(initial, replaced);
		}
		return updatedValue;
	}

}

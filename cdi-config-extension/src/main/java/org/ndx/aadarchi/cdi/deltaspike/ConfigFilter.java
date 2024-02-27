package org.ndx.aadarchi.cdi.deltaspike;

public interface ConfigFilter {

	String filterValue(String key, String value);

	String filterValueForLog(String key, String value);

}

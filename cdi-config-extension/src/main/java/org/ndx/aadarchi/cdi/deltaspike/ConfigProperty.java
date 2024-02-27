package org.ndx.aadarchi.cdi.deltaspike;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * This annotation is an extraction from Deltaspike, because that's the only
 * part of Deltaspike we use here.
 * 
 * @see
 */
@Target(value={PARAMETER,FIELD,METHOD,CONSTRUCTOR,ANNOTATION_TYPE})
@Retention(value=RUNTIME)
@Documented
@Qualifier
public @interface ConfigProperty {
	public static final String NULL = "org.apache.deltaspike.NullValueMarker";
	/**
	 * @return Name of the property to get value for
	 */
	@Nonbinding
	String name();

	/**
	 * @return the default value of the property
	 */
	@Nonbinding
	String defaultValue() default NULL;
}

package org.ndx.aadarchi.base.utils.icon;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Qualifier;

/**
 * Describes the font icon as an annotation for content to be injected later
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface FontIcon {
	@Nonbinding
	public String name() default "";
	
	/**
	 * Icon set from which the icon will be read
	 */
	@Nonbinding
	public String set() default "fab";
}

package org.ndx.aadarchi.base.utils;

import com.structurizr.model.Element;

/**
 * Some utility classes about structurizr
 * @author nicolas-delsaux
 *
 */
public class StructurizrUtils {
	public static String getCanonicalPath(Element element) {
		String returned = "";
		if(element.getParent()!=null) {
			returned = getCanonicalPath(element.getParent());
		}
		returned += "/" + element.getName();
		return returned;
	}
}

package com.structurizr.model;

public class CodeElementHack {

	/**
	 * Old-school hack to call a package-protected method from elsewhere.
	 * I wonder how this will work in module world ...
	 * @param element
	 * @param primary
	 */
	public static void setRole(CodeElement element, CodeElementRole primary) {
		element.setRole(primary);
	}

}

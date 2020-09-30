package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.util.Map;
import java.util.TreeMap;

import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

/**
 * Local representation of a class. It simply contains the class name and list of method representations
 */
class ClassRepresentation {
	
	private Map<String, MethodRepresentation> signaturesToMethods = new TreeMap<String, MethodRepresentation>();
	private final String name;

	public ClassRepresentation(String className) {
		this.name = className;
	}

	public MethodRepresentation getMethodFor(ResolvedMethodDeclaration resolved) {
		return getMethodFor(resolved.declaringType().getQualifiedName(), resolved.getName(), resolved.getSignature());
	}

	public MethodRepresentation getMethodFor(String className, String name, String signature) {
		if(!signaturesToMethods.containsKey(signature)) {
			signaturesToMethods.put(signature, new MethodRepresentation(className, name, signature));
		}
		return signaturesToMethods.get(signature);
	}
}
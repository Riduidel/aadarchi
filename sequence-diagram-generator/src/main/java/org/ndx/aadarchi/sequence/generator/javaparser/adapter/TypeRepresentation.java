package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import java.util.Map;
import java.util.TreeMap;

import org.ndx.aadarchi.sequence.generator.javaparser.Utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;

/**
 * Local representation of a class. It simply contains the class name and list of method representations
 */
public class TypeRepresentation extends AbstractCodeRepresentation {
	
	private Map<String, MethodDeclarationRepresentation> signaturesToMethods = new TreeMap<String, MethodDeclarationRepresentation>();
	final String name;
	@Override
	public String toString() {
		return "TypeRepresentation [name=" + name + "]";
	}

	final CallGraphModel callGraphModel;

	public TypeRepresentation(CallGraphModel callGraphModel, String className) {
		super(null);
		this.callGraphModel = callGraphModel;
		this.name = className;
	}

	public MethodDeclarationRepresentation getMethodFor(ResolvedMethodDeclaration resolved) {
		String className =resolved.declaringType().getQualifiedName();
		String methodName = resolved.getName();
		String signature = Utils.toSignature(resolved);
		if(!signaturesToMethods.containsKey(signature)) {
			signaturesToMethods.put(signature, new MethodDeclarationRepresentation(this, className, 
					methodName, signature, Utils.methodToFileName(resolved)));
		}
		return signaturesToMethods.get(signature);
	}

	public MethodDeclarationRepresentation getMethodFor(MethodCallRepresentation methodCallRepresentation) {
		return signaturesToMethods.get(methodCallRepresentation.calledSignature);
	}

	@Override
	public CodeRepresentation inMethodDeclaration(MethodDeclaration n) {
		return getMethodFor(n.resolve());
	}

	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		for(MethodDeclarationRepresentation method : signaturesToMethods.values()) {
			method.accept(visitor);
		}
		visitor.endVisit(this);
	}
}
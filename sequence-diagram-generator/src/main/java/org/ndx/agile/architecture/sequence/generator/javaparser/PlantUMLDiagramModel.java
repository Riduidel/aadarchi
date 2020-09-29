package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.util.Map;
import java.util.TreeMap;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

public class PlantUMLDiagramModel {
	private Map<String, ClassRepresentation> namesToClasses = new TreeMap<>();
	/**
	 * As we have an external visitor, we have to maintain the currently visited method to add
	 * outgoing edges onto it.
	 */
	private MethodRepresentation analyzed;
	private static class ClassRepresentation {
		
		private Map<String, MethodRepresentation> signaturesToMethods = new TreeMap<String, PlantUMLDiagramModel.MethodRepresentation>();
		private final String name;

		public ClassRepresentation(String className) {
			this.name = className;
		}

		public MethodRepresentation getMethodFor(ResolvedMethodDeclaration resolved) {
			String signature = resolved.getSignature();
			if(!signaturesToMethods.containsKey(signature)) {
				signaturesToMethods.put(signature, new MethodRepresentation(resolved.getName(), signature));
			}
			return signaturesToMethods.get(signature);
		}
	}
	private static class MethodRepresentation {
		private final String name;
		private final String signature;
		public MethodRepresentation(String name, String signature) {
			super();
			this.name = name;
			this.signature = signature;
		}
	}

	private ClassRepresentation getClassFor(String className) {
		if(!namesToClasses.containsKey(className)) {
			namesToClasses.put(className, new ClassRepresentation(className));
		}
		return namesToClasses.get(className);
	}

	public void addCall(MethodCallExpr methodCall) {
		ResolvedMethodDeclaration resolved = methodCall.resolve();
		// Go up to method declaration
		Node methodDeclarationNode = null;
		do {
			methodDeclarationNode = methodCall.getParentNode().get();
		} while(methodDeclarationNode!=null && !(methodDeclarationNode instanceof MethodDeclaration));
		if(methodDeclarationNode!=null) {
			MethodDeclaration methodDeclaration = (MethodDeclaration) methodDeclarationNode;
			ResolvedMethodDeclaration resolvedMethodDeclaration = methodDeclaration.resolve();
			ResolvedReferenceTypeDeclaration callerType = resolvedMethodDeclaration.declaringType();
			String callerName = resolvedMethodDeclaration.getName();
			String callerSignature = resolvedMethodDeclaration.getSignature();
			// Now let's call that method!
			ResolvedReferenceTypeDeclaration calledType = resolved.declaringType();
			String calledName = resolved.getName();
			String calledSignature = resolved.getSignature();
//			getClassFor(callerType.getQualifiedName()).getMethodFor(callerName, callerSignature)
//				.call()
		}
	}
}

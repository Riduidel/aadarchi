package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.structurizr.model.Component;

public class CallGraphModel {
	private final Map<String, Component> classesToComponents;
	public CallGraphModel(Map<String, Component> classesToComponents) {
		super();
		this.classesToComponents = classesToComponents;
	}

	private Map<String, ClassRepresentation> namesToClasses = new TreeMap<>();
	
	/**
	 * List of classes that should be scanned
	 */
	public SortedSet<String> unknownClasses = new TreeSet<>();

	private static class ClassRepresentation {
		
		private Map<String, MethodRepresentation> signaturesToMethods = new TreeMap<String, CallGraphModel.MethodRepresentation>();
		private final String name;

		public ClassRepresentation(String className) {
			this.name = className;
		}

		public MethodRepresentation getMethodFor(ResolvedMethodDeclaration resolved) {
			return getMethodFor(resolved.getName(), resolved.getSignature());
		}

		public MethodRepresentation getMethodFor(String name, String signature) {
			if(!signaturesToMethods.containsKey(signature)) {
				signaturesToMethods.put(signature, new MethodRepresentation(name, signature));
			}
			return signaturesToMethods.get(signature);
		}
	}
	private static class CallInstance {
		private final String name;
		private final MethodRepresentation called;
		public CallInstance(String name, MethodRepresentation called) {
			super();
			this.name = name;
			this.called = called;
		}
	}
	private static class MethodRepresentation {
		private final String name;
		private final String signature;
		/**
		 * Calls are set in a list, to make sure they're ordered
		 */
		private final List<CallInstance> calls = new ArrayList<>();
		public MethodRepresentation(String name, String signature) {
			super();
			this.name = name;
			this.signature = signature;
		}
		public void call(String string, MethodRepresentation methodFor) {
			calls.add(new CallInstance(string, methodFor));
		}
	}

	private ClassRepresentation getClassFor(String className) {
		if(!namesToClasses.containsKey(className)) {
			namesToClasses.put(className, new ClassRepresentation(className));
			// If class is new, it is unknown as a default
		}
		return namesToClasses.get(className);
	}

	/**
	 * Add the call if meaningfull
	 * That's to say the call is only added if the method call has for destination a component type
	 * @param methodCall
	 */
	public void addCall(MethodCallExpr methodCall) {
		ResolvedMethodDeclaration resolved = methodCall.resolve();
		// Go up to method declaration
		Node methodDeclarationNode = methodCall;
		do {
			methodDeclarationNode = methodDeclarationNode.getParentNode().get();
		} while(!(methodDeclarationNode instanceof MethodDeclaration));
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
			String calledTypeName = calledType.getQualifiedName();
			String callerTypeName = callerType.getQualifiedName();
			
			getClassFor(callerTypeName)
				.getMethodFor(callerName, callerSignature)
				.call(methodCall.toString(), getClassFor(calledTypeName).getMethodFor(calledName, calledSignature));
			// If we added a call from a class, this is because class is already known, 
			// So remove it from unknown classes
			unknownClasses.remove(callerTypeName);
		}
	}

	public String toSequenceDiagram(Method method) {
		// First, convert method to method representation
		MethodRepresentation methodRepresentation = getClassFor(method.getDeclaringClass().getName())
			.getMethodFor(method.getName(), toSignature(method));
		return toSequenceDiagram(methodRepresentation, new TreeSet<String>());
	}

	private String toSequenceDiagram(MethodRepresentation methodRepresentation, TreeSet<String> treeSet) {
		return "TODO";
	}

	private String toSignature(Method method) {
		return method.toGenericString();
	}
}

package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.lang.reflect.Method;
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
	final Map<String, Component> classesToComponents;
	Map<String, ClassRepresentation> namesToClasses = new TreeMap<>();
	
	/**
	 * List of classes that should be scanned
	 */
	public SortedSet<String> unknownClasses = new TreeSet<>();

	public CallGraphModel(Map<String, Component> classesToComponents) {
		super();
		this.classesToComponents = classesToComponents;
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
				.getMethodFor(callerTypeName, callerName, callerSignature)
				.call(methodCall.toString(), getClassFor(calledTypeName).getMethodFor(calledTypeName, calledName, calledSignature));
			// If we added a call from a class, this is because class is already known, 
			// So remove it from unknown classes
			unknownClasses.remove(callerTypeName);
		}
	}

	public String toSequenceDiagram(Method method) {
		// First, convert method to method representation
		MethodRepresentation methodRepresentation = getClassFor(method.getDeclaringClass().getName())
			.getMethodFor(method.getDeclaringClass().getName(), method.getName(), toSignature(method));
		return new SequenceDiagramModel.Builder()
					.startsWith(methodRepresentation)
					.build(this)
					.toString();
	}

	private String toSignature(Method method) {
		return method.toGenericString();
	}
}

package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ndx.agile.architecture.sequence.generator.SequenceGeneratorException;
import org.ndx.agile.architecture.sequence.generator.javaparser.SequenceDiagramGenerator;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedArrayType;
import com.github.javaparser.resolution.types.ResolvedPrimitiveType;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.resolution.types.ResolvedTypeVariable;
import com.structurizr.model.Component;

public class CallGraphModel {
	private static final Logger logger = Logger.getLogger(CallGraphModel.class.getName());
	
	public final Map<String, Component> classesToComponents;
	Map<String, ClassRepresentation> namesToClasses = new TreeMap<>();
	
	/**
	 * List of classes that should be scanned
	 */
	public SortedSet<String> unknownClasses = new TreeSet<>();

	public CallGraphModel(Map<String, Component> classesToComponents) {
		super();
		this.classesToComponents = classesToComponents;
	}
	
	public ClassRepresentation getClassFor(String className) {
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
		try {
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
				String callerSignature = toSignature(resolvedMethodDeclaration);
				// Now let's call that method!
				ResolvedReferenceTypeDeclaration calledType = resolved.declaringType();
				String calledName = resolved.getName();
				String calledSignature = toSignature(resolved);
				String calledTypeName = calledType.getQualifiedName();
				String callerTypeName = callerType.getQualifiedName();

				// Notice that to limit model size, e only add calls between components
//				if(classesToComponents.containsKey(calledType)) {
					getClassFor(callerTypeName)
						.getMethodFor(callerTypeName, callerName, callerSignature)
						.call(methodCall.toString(), getClassFor(calledTypeName).getMethodFor(calledTypeName, calledName, calledSignature));
//				}
				// If we added a call from a class, this is because class is already known, 
				// So remove it from unknown classes
				unknownClasses.remove(callerTypeName);
			}
		} catch(UnsupportedOperationException e) {
			logger.severe("Unable to resolve call to "+methodCall);
		}
	}

	public String toSequenceDiagram(Method method) {
		// First, convert method to method representation
		MethodRepresentation methodRepresentation = getClassFor(method.getDeclaringClass().getName())
			.getMethodFor(method.getDeclaringClass().getName(), method.getName(), toSignature(method));
		return new SequenceDiagramGenerator.Builder()
					.startsWith(methodRepresentation)
					.build(this)
					.toString();
	}

	private String toSignature(ResolvedMethodDeclaration method) {
		StringBuilder returned = new StringBuilder(method.getName());
		returned.append('(');
		for (int index = 0; index < method.getNumberOfParams(); index++) {
			ResolvedParameterDeclaration parameter = method.getParam(index);
			if(index>0) {
				returned.append(", ");
			}
			ResolvedType type = parameter.getType();
			returned.append(tryToResolveType(type));
		}
		returned.append(')');
		return returned.toString();
	}

	private String tryToResolveType(ResolvedType type) {
		if(type instanceof ResolvedReferenceType) {
			ResolvedReferenceType resolved = (ResolvedReferenceType) type;
			return resolved.getTypeDeclaration().map(resolvedType ->
				resolvedType.getQualifiedName()
			).get();
		} else if(type instanceof ResolvedPrimitiveType) {
			ResolvedPrimitiveType primitiveType = (ResolvedPrimitiveType) type;
			return primitiveType.name();
		} else if(type instanceof ResolvedArrayType) {
			ResolvedArrayType arrayType = (ResolvedArrayType) type;
			return tryToResolveType(arrayType.getComponentType())+"[]";
		} else if(type instanceof ResolvedTypeVariable) {
			ResolvedTypeVariable resolved = (ResolvedTypeVariable) type;
			return resolved.qualifiedName();
		} else {
			throw new SequenceGeneratorException(String.format("We don't know yet how to transform a %s type into a valid signature element."
							+ "Pleae report a bug at https://github.com/Riduidel/agile-architecture-documentation-system/issues", 
							type.getClass().getName()));
		}
	}

	private String toSignature(Method method) {
		return String.format("%s(%s)", method.getName(),
				Stream.of(method.getParameterTypes())
					.map(clazz -> clazz.getName())
					.collect(Collectors.joining(", "))
				);
	}
}

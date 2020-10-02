package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ndx.agile.architecture.sequence.generator.SequenceGeneratorException;
import org.ndx.agile.architecture.sequence.generator.javaparser.Utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

public abstract class AbstractCodeRepresentation implements CodeRepresentation {
	/**
	 * Ordered list of subelements
	 */
	protected final List<CodeRepresentation> children = new ArrayList<CodeRepresentation>();
	private Optional<CodeRepresentation> parent;

	public AbstractCodeRepresentation() {
		this.parent = Optional.empty();
	}

	public AbstractCodeRepresentation(CodeRepresentation parent) {
		this.parent = Optional.of(parent);
	}

	@Override
	public <Type extends CodeRepresentation> Optional<Type> containerOfType(Class<Type> clazz) {
		if(clazz.isInstance(this))
			return Optional.of(clazz.cast(this));
		if(parent.isPresent()) {
			CodeRepresentation parentRepresentation = parent.get();
			if(clazz.isInstance(clazz)) {
				return Optional.of(clazz.cast(parentRepresentation));
			} else {
				return parentRepresentation.containerOfType(clazz);
			}
		} else {
			return Optional.empty();
		}
	}

	public List<CodeRepresentation> getChildren() {
		return children;
	}
	

	@Override
	public CodeRepresentation inClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration n) {
		Optional<TypeRepresentation> parentType = containerOfType(TypeRepresentation.class);
		ResolvedReferenceTypeDeclaration resolvedTypeDeclaration = n.resolve();
		String typeName = resolvedTypeDeclaration.getQualifiedName();
		// There SHOULD be a typeRepresentation due to initialization mode
		if(parentType.isEmpty()) {
			throw new SequenceGeneratorException(String.format("Seems like there is no parent for declaration of %s.", typeName));
		} else {
			TypeRepresentation parentTypeDeclaration = parentType.get();
			if(parentTypeDeclaration.name.equals(typeName)) {
				return parentTypeDeclaration;
			} else {
				// Mmmh, a subclass.
				// I guess this class should be added to link between classes and components
				return parentTypeDeclaration.callGraphModel.getSubClassFor(parentTypeDeclaration.name, typeName);
			}
		}
	}


	/**
	 * Add the call if meaningfull That's to say the call is only added if the
	 * method call has for destination a component type
	 * 
	 * @param methodCall
	 */
	public CodeRepresentation inMethodCall(MethodCallExpr methodCall) {
		ResolvedMethodDeclaration resolved = methodCall.resolve();
		// Go up to method declaration
		// Now let's call that method!
		String calledName = resolved.getName();
		String calledSignature = Utils.toSignature(resolved);
		String calledTypeName = resolved.declaringType().getQualifiedName();

		MethodCallRepresentation returned = new MethodCallRepresentation(this, calledName, calledSignature,
				calledTypeName);
		children.add(returned);
		return returned;
	}

	/**
	 * Visit all children of this node
	 * @param visitor
	 */
	protected void visitChildren(CodeRepresentationVisitor visitor) {
		for(CodeRepresentation child : children) {
			child.accept(visitor);
		}
	}
}

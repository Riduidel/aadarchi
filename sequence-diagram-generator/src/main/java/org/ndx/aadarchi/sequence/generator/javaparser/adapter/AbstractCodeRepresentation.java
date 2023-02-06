package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;
import org.ndx.aadarchi.sequence.generator.javaparser.Utils;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

public abstract class AbstractCodeRepresentation implements CodeRepresentation {
	/**
	 * Ordered list of subelements
	 */
	protected final List<CodeRepresentation> children = new ArrayList<CodeRepresentation>();
	private Optional<CodeRepresentation> parent = Optional.empty();

	public AbstractCodeRepresentation(CodeRepresentation parent) {
		setParent(parent);
	}
	
	protected void setParent(CodeRepresentation parent) {
		this.parent= Optional.ofNullable(parent);
	}

	@Override
	public <Type extends CodeRepresentation> Optional<Type> containerOfType(Class<Type> clazz) {
		if(clazz.isInstance(this))
			return Optional.of(clazz.cast(this));
		if(parent.isPresent()) {
			CodeRepresentation parentRepresentation = parent.get();
			if(clazz.isInstance(parentRepresentation)) {
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
	
	/**
	 * A one-liner making sure children are added
	 * @param representation
	 * @return
	 */
	protected CodeRepresentation addChild(CodeRepresentation representation) {
		children.add(representation);
		return representation;
	}
	

	@Override
	public CodeRepresentation inMethodDeclaration(MethodDeclaration n) {
		throw new SequenceGeneratorException("method declarations can't happen anywhere.");
	}

	@Override
	public CodeRepresentation inClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration n) {
		ResolvedReferenceTypeDeclaration resolvedTypeDeclaration = n.resolve();
		return createTypeRepresentationOf(resolvedTypeDeclaration.getQualifiedName());
	}

	CodeRepresentation createTypeRepresentationOf(String typeName) {
		Optional<TypeRepresentation> parentType = containerOfType(TypeRepresentation.class);
		// There SHOULD be a typeRepresentation due to initialization mode
		if(parentType.isEmpty()) {
			throw new NoParentMethodException(String.format("Seems like there is no parent for declaration of %s.", typeName));
		} else {
			TypeRepresentation parentTypeDeclaration = parentType.get();
			if(parentTypeDeclaration.name.equals(typeName)) {
				return parentTypeDeclaration;
			} else {
				// Mmmh, a subclass.
				// I guess this class should be added to link between classes and components
				return parentTypeDeclaration.callGraphModel.getSubClassFor(parentTypeDeclaration, typeName);
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

		return addChild(new MethodCallRepresentation(this, calledName, calledSignature,
				calledTypeName));
	}
	
	/**
	 * Creates an object creation representation.
	 * In order to handle anonymous subclasses, the object creation representation is 
	 * created with a type representation immediatly attached to.
	 */
	public CodeRepresentation inObjectCreation(ObjectCreationExpr objectCreation) {
		if(objectCreation.getAnonymousClassBody().isPresent()) {
			// And this is a new inner class!
			// inner classes have their structure created
			ResolvedConstructorDeclaration resolvedCreation = objectCreation.resolve();
			ObjectCreationRepresentation representation = new ObjectCreationRepresentation(this, resolvedCreation.declaringType().getQualifiedName());
			children.add(representation);
			return representation.createTypeRepresentationOf(representation.className);
		} else {
			return addChild(new ObjectCreationRepresentation(this, 
					objectCreation.getType().resolve().getQualifiedName()));
		}
	}

	public CodeRepresentation inForEach(ForEachStmt n) {
		return addChild(new ForEachRepresentation(this));
	}
	
	public CodeRepresentation inForLoop(ForStmt n) {
		return addChild(new ForLoopRepresentation(this));
	}
	
	public CodeRepresentation inIf(IfStmt n) {
		return addChild(new IfRepresentation(this, n.getCondition()));
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

package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;

public interface CodeRepresentation {
	<Type extends CodeRepresentation> Optional<Type> containerOfType(Class<Type> clazz);
	
	void accept(CodeRepresentationVisitor visitor);

	/**
	 * Invoked when entering in a method
	 * @param n
	 * @return
	 */
	CodeRepresentation inMethodDeclaration(MethodDeclaration n);

	/**
	 * Invoked when entering in a method call
	 * @param methodCall
	 * @return
	 */
	CodeRepresentation inMethodCall(MethodCallExpr methodCall);

	CodeRepresentation inClassOrInterfaceDeclaration(ClassOrInterfaceDeclaration n);

	CodeRepresentation inObjectCreation(ObjectCreationExpr n);

}

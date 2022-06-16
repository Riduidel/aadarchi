package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import java.util.Optional;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;

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

	default CodeRepresentation inBlock(BlockStmt n) {
		return this;
	}

	default CodeRepresentation inBinaryExpression(BinaryExpr n) {
		return this;
	}

	CodeRepresentation inForEach(ForEachStmt n);

	CodeRepresentation inForLoop(ForStmt n);

	CodeRepresentation inIf(IfStmt n);

}

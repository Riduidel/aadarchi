package org.ndx.agile.architecture.sequence.generator.javaparser.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.CodeRepresentation;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;

public class JavaParserVisitorForBuildingCallGraph extends GenericVisitorAdapter<CodeRepresentation, CodeRepresentation> {
	private static final Logger logger =Logger.getLogger(JavaParserVisitorForBuildingCallGraph.class.getName());
	
	@Override
	public CodeRepresentation visit(ClassOrInterfaceDeclaration n, CodeRepresentation arg) {
		return super.visit(n, arg.inClassOrInterfaceDeclaration(n));
	}

	@Override
	public CodeRepresentation visit(MethodDeclaration n, CodeRepresentation representation) {
		return super.visit(n, representation.inMethodDeclaration(n));
	}
	
	@Override
	public CodeRepresentation visit(ObjectCreationExpr objectCreation, CodeRepresentation representation) {
		try {
		return super.visit(objectCreation, representation.inObjectCreation(objectCreation));
		} catch(RuntimeException e) {
			logger.log(Level.SEVERE, String.format("Unable to resolve object creation %s due to exception \"%s\". We give up on that one.", objectCreation, e.getMessage()));
			return super.visit(objectCreation, representation);
		}
	}
	
	@Override
	public CodeRepresentation visit(MethodCallExpr methodCall, CodeRepresentation representation) {
		try {
			return super.visit(methodCall, representation.inMethodCall(methodCall));
		} catch(RuntimeException e) {
			logger.log(Level.SEVERE, String.format("Unable to resolve method call %s due to exception \"%s\". We give up on that one.", methodCall, e.getMessage()));
			return super.visit(methodCall, representation);
		}
	}
	
	@Override
	public CodeRepresentation visit(BlockStmt n, CodeRepresentation representation) {
		return super.visit(n, representation.inBlock(n));
	}
	
	@Override
	public CodeRepresentation visit(BinaryExpr n, CodeRepresentation representation) {
		return super.visit(n, representation.inBinaryExpression(n));
	}
	
	@Override
	public CodeRepresentation visit(ForEachStmt n, CodeRepresentation representation) {
		return super.visit(n, representation.inForEach(n));
	}
	@Override
	public CodeRepresentation visit(ForStmt n, CodeRepresentation representation) {
		return super.visit(n, representation.inForLoop(n));
	}
	
	@Override
	public CodeRepresentation visit(IfStmt n, CodeRepresentation representation) {
		return super.visit(n, representation.inIf(n));
	}
}

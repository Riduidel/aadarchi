package org.ndx.agile.architecture.sequence.generator.javaparser.visitor;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.ndx.agile.architecture.sequence.generator.javaparser.adapter.CodeRepresentation;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
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
	public CodeRepresentation visit(MethodCallExpr methodCall, CodeRepresentation representation) {
		try {
			return super.visit(methodCall, representation.inMethodCall(methodCall));
		} catch(RuntimeException e) {
			logger.log(Level.SEVERE, String.format("Unable to resolve method call %s due to exception \"%s\". We give up on that one.", methodCall, e.getMessage()));
			return super.visit(methodCall, representation);
		}
	}
}

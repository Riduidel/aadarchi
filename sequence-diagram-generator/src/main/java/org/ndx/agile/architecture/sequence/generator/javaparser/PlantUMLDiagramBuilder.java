package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.util.Map;
import java.util.logging.Logger;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.structurizr.model.Component;

public class PlantUMLDiagramBuilder extends GenericVisitorAdapter<PlantUMLDiagramModel, PlantUMLDiagramModel> {
	private static final Logger logger =Logger.getLogger(PlantUMLDiagramBuilder.class.getName());
	@Override
	public PlantUMLDiagramModel visit(MethodDeclaration n, PlantUMLDiagramModel model) {
		return super.visit(n, model);
	}
	
	@Override
	public PlantUMLDiagramModel visit(MethodCallExpr methodCall, PlantUMLDiagramModel model) {
		logger.info("Found method call "+methodCall);
		ResolvedMethodDeclaration resolvedMethodCall = methodCall.resolve();
		ResolvedReferenceTypeDeclaration declaringType = resolvedMethodCall.declaringType();
		String declaringTypeName = declaringType.getQualifiedName();
		model.addCall(methodCall);
		return super.visit(methodCall, model);
	}
}

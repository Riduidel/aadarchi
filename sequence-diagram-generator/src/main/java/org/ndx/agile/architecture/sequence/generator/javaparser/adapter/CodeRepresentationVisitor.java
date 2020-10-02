package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

public interface CodeRepresentationVisitor {

	void startVisit(TypeRepresentation classRepresentation);

	void endVisit(TypeRepresentation classRepresentation);

	void startVisit(MethodDeclarationRepresentation methodDeclarationRepresentation);

	void endVisit(MethodDeclarationRepresentation methodDeclarationRepresentation);

	void startVisit(MethodCallRepresentation methodCallRepresentation);

	void endVisit(MethodCallRepresentation methodCallRepresentation);

}

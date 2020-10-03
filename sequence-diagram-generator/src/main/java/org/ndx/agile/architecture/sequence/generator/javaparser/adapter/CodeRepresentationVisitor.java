package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

public interface CodeRepresentationVisitor {

	void startVisit(TypeRepresentation classRepresentation);

	void endVisit(TypeRepresentation classRepresentation);

	void startVisit(MethodDeclarationRepresentation methodDeclarationRepresentation);

	void endVisit(MethodDeclarationRepresentation methodDeclarationRepresentation);

	void startVisit(MethodCallRepresentation methodCallRepresentation);

	void endVisit(MethodCallRepresentation methodCallRepresentation);

	void startVisit(ObjectCreationRepresentation objectCreationRepresentation);

	void endVisit(ObjectCreationRepresentation objectCreationRepresentation);

	void startVisit(BlockRepresentation blockRepresentation);

	void endVisit(BlockRepresentation blockRepresentation);

	void startVisit(ForEachRepresentation forEachRepresentation);

	void endVisit(ForEachRepresentation forEachRepresentation);

	void startVisit(ForLoopRepresentation forLoopRepresentation);

	void endVisit(ForLoopRepresentation forLoopRepresentation);

	void startVisit(IfRepresentation ifRepresentation);

	void endVisit(IfRepresentation ifRepresentation);

}

package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import org.ndx.agile.architecture.sequence.generator.SequenceGeneratorException;

import com.github.javaparser.ast.body.MethodDeclaration;

public class ObjectCreationRepresentation extends AbstractCodeRepresentation {

	public final String className;

	public ObjectCreationRepresentation(AbstractCodeRepresentation abstractCodeRepresentation, String qualifiedName) {
		super(abstractCodeRepresentation);
		this.className = qualifiedName;
	}

	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		visitChildren(visitor);
		visitor.endVisit(this);
	}

	@Override
	public CodeRepresentation inMethodDeclaration(MethodDeclaration n) {
		throw new SequenceGeneratorException("inner method declarations are not yet supported");
	}

}

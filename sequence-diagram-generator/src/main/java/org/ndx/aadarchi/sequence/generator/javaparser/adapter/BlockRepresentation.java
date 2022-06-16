package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;

import com.github.javaparser.ast.body.MethodDeclaration;

/**
 * A block representation that should allow us to handle alternatives and other concepts
 */
public class BlockRepresentation extends AbstractCodeRepresentation {

	public BlockRepresentation(CodeRepresentation parent) {
		super(parent);
	}

	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		visitChildren(visitor);
		visitor.endVisit(this);
	}

	@Override
	public CodeRepresentation inMethodDeclaration(MethodDeclaration n) {
		throw new SequenceGeneratorException("There is no method declaration in a block");
	}

}

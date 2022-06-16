package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;

import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodCallRepresentation extends AbstractCodeRepresentation {

	public final String calledName;
	public final String calledSignature;
	public final String calledTypeName;

	public MethodCallRepresentation(CodeRepresentation parent, String calledName,
			String calledSignature, String calledTypeName) {
		super(parent);
		this.calledName = calledName;
		this.calledSignature = calledSignature;
		this.calledTypeName = calledTypeName;
	}

	@Override
	public CodeRepresentation inMethodDeclaration(MethodDeclaration n) {
		throw new SequenceGeneratorException("inner method declarations are not yet supported");
	}
	
	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		visitChildren(visitor);
		visitor.endVisit(this);
	}

}

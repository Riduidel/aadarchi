package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import org.ndx.aadarchi.sequence.generator.SequenceGeneratorException;

import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodCallRepresentation extends AbstractCodeRepresentation {

	public final String methodName;
	public final String methodSignature;
	public final String methodTypeName;

	public MethodCallRepresentation(CodeRepresentation parent, String methodName,
			String methodSignature, String methodTypeName) {
		super(parent);
		this.methodName = methodName;
		this.methodSignature = methodSignature;
		this.methodTypeName = methodTypeName;
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

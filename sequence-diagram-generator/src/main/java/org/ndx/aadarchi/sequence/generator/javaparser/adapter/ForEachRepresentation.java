package org.ndx.aadarchi.sequence.generator.javaparser.adapter;

import com.github.javaparser.ast.body.MethodDeclaration;

public class ForEachRepresentation extends AbstractCodeRepresentation implements LoopRepresentation {

	public ForEachRepresentation(CodeRepresentation parent) {
		super(parent);
	}

	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		visitChildren(visitor);
		visitor.endVisit(this);
	}

	@Override
	public String getGroupName() {
		return "for each";
	}

}

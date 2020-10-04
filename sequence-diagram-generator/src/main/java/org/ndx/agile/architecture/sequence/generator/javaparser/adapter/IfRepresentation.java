package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

public class IfRepresentation extends AbstractCodeRepresentation implements GroupRepresentation {

	public IfRepresentation(CodeRepresentation parent) {
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
		return "if";
	}

}

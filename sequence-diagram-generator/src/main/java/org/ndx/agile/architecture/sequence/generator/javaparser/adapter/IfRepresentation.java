package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;

public class IfRepresentation extends AbstractCodeRepresentation implements GroupRepresentation {

	private String condition;

	public IfRepresentation(CodeRepresentation parent, Expression expression) {
		super(parent);
		this.condition = expression.toString();
	}

	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		visitChildren(visitor);
		visitor.endVisit(this);
	}

	@Override
	public String getGroupName() {
		return "if "+condition;
	}

	@Override
	public CodeRepresentation inBlock(BlockStmt n) {
		return addChild(new BlockRepresentation(this));
	}
}

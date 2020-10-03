package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import com.github.javaparser.ast.body.MethodDeclaration;

public class MethodDeclarationRepresentation extends BlockRepresentation{
	public final String className;
	public final String name;
	public final String signature;
	public final String filename;
	public MethodDeclarationRepresentation(CodeRepresentation typeRepresentation, String className, String name, String signature,
			String filename) {
		super(typeRepresentation);
		this.className = className;
		this.name = name;
		this.signature = signature;
		this.filename = filename;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodDeclarationRepresentation other = (MethodDeclarationRepresentation) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (signature == null) {
			if (other.signature != null)
				return false;
		} else if (!signature.equals(other.signature))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((signature == null) ? 0 : signature.hashCode());
		return result;
	}
	@Override
	public String toString() {
		return "MethodRepresentation [signature=" + signature + "]";
	}
	
	@Override
	public CodeRepresentation inMethodDeclaration(MethodDeclaration n) {
		return this;
	}
	
	@Override
	public void accept(CodeRepresentationVisitor visitor) {
		visitor.startVisit(this);
		super.visitChildren(visitor);
		visitor.endVisit(this);
	}
}
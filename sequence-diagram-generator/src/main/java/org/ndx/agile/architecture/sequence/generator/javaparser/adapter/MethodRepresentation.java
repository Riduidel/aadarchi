package org.ndx.agile.architecture.sequence.generator.javaparser.adapter;

import java.util.ArrayList;
import java.util.List;

import org.ndx.agile.architecture.sequence.generator.javaparser.CallInstance;

public class MethodRepresentation {
	/**
	 * Calls are set in a list, to make sure they're ordered
	 */
	public final List<CallInstance> calls = new ArrayList<>();
	public final String className;
	public final String name;
	public final String signature;
	public MethodRepresentation(String className, String name, String signature) {
		super();
		this.className = className;
		this.name = name;
		this.signature = signature;
	}
	public void call(String callText, MethodRepresentation methodFor) {
		calls.add(new CallInstance(this, callText, methodFor));
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodRepresentation other = (MethodRepresentation) obj;
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
}
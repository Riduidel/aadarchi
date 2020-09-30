package org.ndx.agile.architecture.sequence.generator.javaparser;

import java.util.Optional;

/**
 * Instance of a call to a method
 */
class CallInstance {
	public final MethodRepresentation called;
	public Optional<MethodRepresentation> caller = Optional.empty();
	public final String name;
	public CallInstance(MethodRepresentation caller, String name, MethodRepresentation called) {
		super();
		this.name = name;
		this.called = called;
		this.caller = Optional.of(caller);
	}
	public CallInstance(String name, MethodRepresentation called) {
		super();
		this.name = name;
		this.called = called;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CallInstance other = (CallInstance) obj;
		if (called == null) {
			if (other.called != null)
				return false;
		} else if (!called.equals(other.called))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((called == null) ? 0 : called.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
}
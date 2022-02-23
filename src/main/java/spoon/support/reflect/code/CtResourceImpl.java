package spoon.support.reflect.code;

import spoon.reflect.code.CtResource;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtElementImpl;

import java.io.Closeable;

public class CtResourceImpl extends CtElementImpl implements CtResource {
	CtVariable _variable;
	public CtResourceImpl(CtVariable v) {
		_variable = v;
	}
	@Override
	public CtVariable getVariable() {
		return _variable;
	}

	@Override
	public void accept(CtVisitor visitor) {
		// this is a shadow element
		// the local variable already exists
		// and has been visited
	}

	@Override
	public CtTypeReference<Closeable> getType() {
		return getFactory().createCtTypeReference(Closeable.class);
	}

	@Override
	public <C extends CtTypedElement> C setType(CtTypeReference<Closeable> type) {
		throw new UnsupportedOperationException();
	}
}

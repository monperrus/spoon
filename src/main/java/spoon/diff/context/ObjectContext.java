package spoon.diff.context;

import spoon.reflect.declaration.CtElement;

public class ObjectContext extends Context {
	private final CtElement ctElement;
	private final String fieldName;

	public ObjectContext(CtElement ctElement, String fieldName) {
		this.ctElement = ctElement;
		this.fieldName = fieldName;
	}

/*
	try {
		final Field field = ctElement.getClass().getField(fieldName);
	} catch (NoSuchFieldException e) {
		throw new IllegalArgumentException("Can't find the field named by " + fieldName, e);
	}
*/
}

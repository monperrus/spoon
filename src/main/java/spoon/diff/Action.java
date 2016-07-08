package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public abstract class Action {
	private final Context context;
	private final CtElement newElement;
	private final Object newValue;

	Action(Context context, CtElement newElement) {
		this.context = context;
		this.newElement = newElement;
		this.newValue = null;
	}

	Action(Context context, Object newValue) {
		this.context = context;
		this.newValue = newValue;
		this.newElement = null;
	}

	public abstract void rollback();

	public Context getContext() {
		return context;
	}

	public CtElement getNewElement() {
		return newElement;
	}

	public Object getNewValue() {
		return newValue;
	}
}

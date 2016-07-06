package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public abstract class Action {
	private final Context context;
	private final CtElement newElement;

	Action(Context context, CtElement newElement) {
		this.context = context;
		this.newElement = newElement;
	}

	public abstract void rollback();

	public Context getContext() {
		return context;
	}

	public CtElement getNewElement() {
		return newElement;
	}
}

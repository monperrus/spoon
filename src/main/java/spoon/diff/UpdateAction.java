package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public class UpdateAction extends Action {
	private final CtElement oldElement;
	private final Object oldValue;

	public UpdateAction(Context context, CtElement newElement, CtElement oldElement) {
		super(context, newElement);
		this.oldElement = oldElement;
		this.oldValue = null;
	}

	public UpdateAction(Context context, Object newValue, Object oldValue) {
		super(context, newValue);
		this.oldValue = oldValue;
		this.oldElement = null;
	}

	@Override
	public void rollback() {

	}

	public CtElement getOldElement() {
		return oldElement;
	}

	public Object getOldValue() {
		return oldValue;
	}
}

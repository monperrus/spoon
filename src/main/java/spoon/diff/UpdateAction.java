package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public class UpdateAction extends Action {
	private final CtElement oldElement;

	public UpdateAction(Context context, CtElement newElement, CtElement oldElement) {
		super(context, newElement);
		this.oldElement = oldElement;
	}

	@Override
	public void rollback() {

	}

	public CtElement getOldElement() {
		return oldElement;
	}
}

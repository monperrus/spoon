package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public class AddAction extends Action {
	public AddAction(Context context, CtElement newElement) {
		super(context, newElement);
	}

	public AddAction(Context context, Object newValue) {
		super(context, newValue);
	}

	@Override
	public void rollback() {
	}
}

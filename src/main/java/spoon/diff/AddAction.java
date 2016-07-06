package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public class AddAction extends Action {
	public AddAction(Context context, CtElement newElement) {
		super(context, newElement);
	}

	@Override
	public void rollback() {
	}
}

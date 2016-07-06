package spoon.diff;

import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

public class DeleteAction extends Action {
	public DeleteAction(Context context, CtElement oldElement) {
		super(context, oldElement);
	}

	@Override
	public void rollback() {

	}
}

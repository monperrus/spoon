package spoon.diff;

import spoon.diff.context.CollectionContext;
import spoon.diff.context.Context;
import spoon.reflect.declaration.CtElement;

import java.util.Collection;

public class DeleteAllAction extends DeleteAction {
	public DeleteAllAction(Context context, CtElement oldElement) {
		super(context, oldElement);
	}

	public DeleteAllAction(CollectionContext context, Collection<?> copy) {
		super(context, null);
	}
}

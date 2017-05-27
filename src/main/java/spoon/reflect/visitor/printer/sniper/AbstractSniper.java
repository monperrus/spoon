package spoon.reflect.visitor.printer.sniper;

import spoon.diff.Action;
import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;
import spoon.reflect.declaration.CtElement;

public abstract class AbstractSniper<T extends CtElement> implements SniperElement {
	private SniperWriter writer;
	private T element;

	public AbstractSniper(SniperWriter writer, T element) {
		this.writer = writer;
		this.element = element;
	}

	public T getElement() {
		return element;
	}

	public SniperWriter getWriter() {
		return writer;
	}

	@Override
	public AddAction onAdd(AddAction action) {
		return action;
	}

	@Override
	public DeleteAction onDelete(DeleteAction action) {
		return action;
	}

	@Override
	public DeleteAllAction onDeleteAll(DeleteAllAction action) {
		return action;
	}

	@Override
	public UpdateAction onUpdate(UpdateAction action) {
		return action;
	}

	@Override
	public Action onAction(Action action) {
		return action;
	}
}

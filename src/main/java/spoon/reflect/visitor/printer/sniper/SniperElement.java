package spoon.reflect.visitor.printer.sniper;

import spoon.diff.Action;
import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;

public interface SniperElement {
	AddAction onAdd(AddAction action);

	DeleteAction onDelete(DeleteAction action);

	DeleteAllAction onDeleteAll(DeleteAllAction action);

	UpdateAction onUpdate(UpdateAction action);

	Action onAction(Action action);
}

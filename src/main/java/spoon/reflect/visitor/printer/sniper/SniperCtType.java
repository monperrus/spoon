package spoon.reflect.visitor.printer.sniper;

import spoon.diff.AddAction;
import spoon.reflect.code.CtComment;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;

public class SniperCtType extends AbstractSniper<CtType> {

	public SniperCtType(SniperWriter writer, CtType element) {
		super(writer, element);
	}

	@Override
	public AddAction onAdd(AddAction action) {
		CtType<?> type = getElement();
		int position = 0;
		if (action.getNewElement() instanceof CtField) {
			for (int i = getElement().getFields().size() - 1; i >= 0; i--) {
				CtField<?> ctField = type.getFields().get(i);
				if (ctField.getPosition() != null) {
					position = ctField.getPosition().getSourceEnd() + 2;
					break;
				}
			}
		} else if (action.getNewElement() instanceof CtMethod) {
			for (CtMethod<?> method : type.getMethods()) {
				if (method.getPosition() != null) {
					position = method.getPosition().getSourceEnd() + 2;
					break;
				}
			}
		} else if (action.getNewElement() instanceof CtComment) {
			position = action.getNewElement().getParent().getPosition().getSourceStart();
		} else if (action.getNewValue() instanceof ModifierKind) {
			return action;
		} else {
			throw new RuntimeException("Action Type not handled " + action.getNewElement());
		}
		if (position == 0) {
			position = type.getPosition().getSourceEnd() - 2;
		}
		getWriter().write(action.getNewElement(), position);
		return null;
	}
}

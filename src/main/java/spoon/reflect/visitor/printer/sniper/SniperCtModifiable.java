package spoon.reflect.visitor.printer.sniper;

import spoon.SpoonException;
import spoon.diff.Action;
import spoon.reflect.cu.position.BodyHolderSourcePosition;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.ModifierKind;

public class SniperCtModifiable extends AbstractSniper<CtModifiable> {

	public SniperCtModifiable(SniperWriter writer, CtModifiable element) {
		super(writer, element);
	}

	@Override
	public Action onAction(Action action) {
		if (action.getNewValue() instanceof ModifierKind) {
			CtElement element = action.getContext().getElement();
			if (element.getPosition() instanceof BodyHolderSourcePosition) {
				BodyHolderSourcePosition position = (BodyHolderSourcePosition) element.getPosition();
				getWriter().replaceModifiers(position.getModifierSourceStart(), position.getModifierSourceEnd(), (CtModifiable) element);
				return null;
			} else if (!(element.getPosition() instanceof NoSourcePosition)) {
				throw new SpoonException("Position is not correct" + element.getPosition());
			}
		}
		return action;
	}
}

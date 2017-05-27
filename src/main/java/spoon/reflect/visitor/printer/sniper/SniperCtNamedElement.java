package spoon.reflect.visitor.printer.sniper;

import spoon.diff.UpdateAction;
import spoon.reflect.cu.position.DeclarationSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtNamedElement;

public class SniperCtNamedElement extends AbstractSniper<CtNamedElement> {

	public SniperCtNamedElement(SniperWriter writer, CtNamedElement element) {
		super(writer, element);
	}

	@Override
	public UpdateAction onUpdate(UpdateAction action) {
		CtElement element = action.getContext().getElement();
		if (element.getPosition() instanceof DeclarationSourcePosition) {
			DeclarationSourcePosition position = (DeclarationSourcePosition) element.getPosition();
			getWriter().replace(position.getNameStart(), position.getNameEnd(), action.getNewValue() + "");
			return null;
		}
		return action;
	}
}

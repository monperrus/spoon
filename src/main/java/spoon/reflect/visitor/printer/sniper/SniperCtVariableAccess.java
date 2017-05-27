package spoon.reflect.visitor.printer.sniper;

import spoon.diff.UpdateAction;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtReference;

public class SniperCtVariableAccess extends AbstractSniper<CtVariableAccess> {

	public SniperCtVariableAccess(SniperWriter writer, CtVariableAccess element) {
		super(writer, element);
	}

	@Override
	public UpdateAction onUpdate(UpdateAction action) {
		CtElement element = action.getContext().getElement();
		// rename the variable
		if (element instanceof CtReference) {
			element = element.getParent();
		}
		SourcePosition position = element.getPosition();
		getWriter().replace(position.getSourceStart(), position.getSourceEnd(), action.getNewValue() + "");
		return null;
	}
}

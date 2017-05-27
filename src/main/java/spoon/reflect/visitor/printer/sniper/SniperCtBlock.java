package spoon.reflect.visitor.printer.sniper;

import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;

import java.util.List;

public class SniperCtBlock extends AbstractSniper<CtBlock> {

	public SniperCtBlock(SniperWriter writer, CtBlock element) {
		super(writer, element);
	}

	@Override
	public AddAction onAdd(AddAction action) {
		CtBlock block = getElement();

		CtElement element = action.getNewElement();
		int position;
		int index = block.getStatements().indexOf(element);
		// add at the beginning
		if (index == 0) {
			position = block.getPosition().getSourceStart();
		} else {
			position = block.getStatement(index - 1).getPosition().getSourceEnd();
		}
		position += getWriter().getPositionNewLine(position);

		getWriter().write(element, position);
		return null;
	}

	@Override
	public DeleteAction onDelete(DeleteAction action) {
		CtElement statement = action.getNewElement();
		getWriter().remove(statement.getPosition().getSourceStart(), statement.getPosition().getSourceEnd());
		return null;
	}

	@Override
	public DeleteAllAction onDeleteAll(DeleteAllAction action) {
		List<CtStatement> statements = (List<CtStatement>) action.getOldContent();
		if (!statements.isEmpty()) {
			getWriter().remove(statements.get(0).getPosition().getSourceStart(), statements.get(statements.size() - 1).getPosition().getSourceEnd());
		}
		return null;
	}
}

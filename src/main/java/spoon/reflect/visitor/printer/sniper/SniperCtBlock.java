/**
 * Copyright (C) 2006-2017 INRIA and contributors
 * Spoon - http://spoon.gforge.inria.fr/
 *
 * This software is governed by the CeCILL-C License under French law and
 * abiding by the rules of distribution of free software. You can use, modify
 * and/or redistribute the software under the terms of the CeCILL-C license as
 * circulated by CEA, CNRS and INRIA at http://www.cecill.info.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the CeCILL-C License for more details.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 */
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

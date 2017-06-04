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

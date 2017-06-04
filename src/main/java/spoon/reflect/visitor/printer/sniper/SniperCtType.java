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

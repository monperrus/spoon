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
package spoon.reflect.factory;

import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;
import spoon.diff.context.ListContext;
import spoon.diff.context.MapContext;
import spoon.diff.context.ObjectContext;
import spoon.diff.context.SetContext;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.ModifierKind;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ChangeFactory extends SubFactory {

	public ChangeFactory(Factory factory) {
		super(factory);
	}

	public void onObjectUpdate(CtElement currentElement, FieldName fieldName, String changedField, CtElement newValue, CtElement oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new UpdateAction(new ObjectContext(currentElement, changedField), newValue, oldValue));
		}
	}

	public void onObjectUpdate(CtElement currentElement, FieldName fieldName, String changedField, Object newValue, Object oldValue) {
		if (newValue instanceof CtElement && (oldValue instanceof CtElement || oldValue == null)) {
			onObjectUpdate(currentElement, fieldName, changedField, (CtElement) newValue, (CtElement) oldValue);
		} else if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new UpdateAction(new ObjectContext(currentElement, changedField), newValue, oldValue));
		}
	}

	public void onObjectDelete(CtElement currentElement, FieldName fieldName, String changedField, CtElement oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAction(new ObjectContext(currentElement, changedField), oldValue));
		}
	}

	public void onListAdd(CtElement currentElement, FieldName fieldName, List field, CtElement newValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new AddAction(new ListContext(currentElement, field), newValue));
		}
	}

	public void onListAdd(CtElement currentElement, FieldName fieldName, List field, int index, CtElement newValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new AddAction(new ListContext(currentElement, field, index), newValue));
		}
	}


	public void onListDelete(CtElement currentElement, FieldName fieldName, List field, Collection<? extends CtElement> oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			for (CtElement ctElement : oldValue) {
				onListDelete(currentElement, fieldName, field, field.indexOf(ctElement), ctElement);
			}
		}
	}

	public void onListDelete(CtElement currentElement, FieldName fieldName, List field, int index, CtElement oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAction(new ListContext(currentElement, field, index), oldValue));
		}
	}


	public void onListDeleteAll(CtElement currentElement, FieldName fieldName, List field, List oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAllAction(new ListContext(currentElement, field), oldValue));
		}
	}


	public void onMapAdd(CtElement currentElement, FieldName fieldName, Map field, Object key, CtElement newValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new AddAction(new MapContext(currentElement, field, key), newValue));
		}
	}

	public void onMapDeleteAll(CtElement currentElement, FieldName fieldName, Map field, Map oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAllAction(new MapContext(currentElement, field), oldValue));
		}
	}

	public void onSetAdd(CtElement currentElement, FieldName fieldName, Set field, CtElement newValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new AddAction(new SetContext(currentElement, field), newValue));
		}
	}

	public void onSetAdd(CtElement currentElement, FieldName fieldName, Set field, ModifierKind newValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new AddAction(new SetContext(currentElement, field), newValue));
		}
	}


	public void onSetDelete(CtElement currentElement, FieldName fieldName, Set field, CtElement oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAction(new SetContext(currentElement, field), oldValue));
		}
	}

	public void onSetDelete(CtElement currentElement, FieldName fieldName, Set field, ModifierKind oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAction(new SetContext(currentElement, field), oldValue));
		}
	}

	public void onSetDeleteAll(CtElement currentElement, FieldName fieldName, Set field, Set oldValue) {
		if (factory.getEnvironment().buildStackChanges()) {
			factory.getEnvironment().pushToStack(new DeleteAllAction(new SetContext(currentElement, field), oldValue));
		}
	}

	public enum FieldName {
		PARENT,
		NAME,
		TYPE,
		BODY,
		IS_FINAL,
		IS_SHADOW,
		IS_STATIC,
		IS_IMPLICIT,
		IS_DEFAULT,
		IS_VARARGS,
		DEFAULT_EXPRESSION,
		THEN,
		ELSE,
		PACKAGE,
		CONDITION,
		SUPER_TYPE,
		POSITION,
		RIGHT_OPERAND,
		LEFT_OPERAND,
		LABEL,
		CASE,
		KIND,
		PARAMETER,
		EXPRESSION,
		TARGET,
		OPERAND,
		VARIABLE,
		FINALIZER,
		UPPER,
		THROW,
		EXECUTABLE,
		ASSIGNMENT,
		ASSIGNED,
		MODIFIERS,
		COMMENTS,
		TYPES,
		INTERFACES,
		ANNOTATIONS,
		STATEMENTS,
		ARGUMENTS,
		MEMBERS,
		CASTS,
		VALUES,
		FOR_UPDATE,
		FOR_INIT,
		RESOURCES,
		DIMENSIONS,
		BOUNDS,
		CATCHERS,
		ANONYMOUS_CLASS,
		TARGET_LABEL,
		TYPE_PARAMETERS,
		CONTENT,
		TAGS;

		public static FieldName fromString(String name) {
			name = name.toLowerCase();
			for (int i = 0; i < FieldName.values().length; i++) {
				if (FieldName.values()[i].getTitleName().toLowerCase()
						.equals(name)) {
					return FieldName.values()[i];
				}
			}
			if ("implicit".equals(name)) {
				return IS_IMPLICIT;
			}
			if ("fina".equals(name)) {
				return IS_FINAL;
			}
			if ("stat".equals(name)) {
				return IS_STATIC;
			}
			if ("varargs".equals(name)) {
				return IS_VARARGS;
			}
			if ("defaultmethod".equals(name)) {
				return IS_DEFAULT;
			}
			if ("block".equals(name)) {
				return BODY;
			}
			if ("param".equals(name)) {
				return PARAMETER;
			}
			if ("dimensionexpressions".equals(name)) {
				return DIMENSIONS;
			}
			if ("actualtypearguments".equals(name)) {
				return TYPE_PARAMETERS;
			}
			if ("formalcttypeparameters".equals(name)) {
				return TYPE_PARAMETERS;
			}
			if ("typecasts".equals(name)) {
				return CASTS;
			}
			if ("cases".equals(name)) {
				return CASE;
			}
			if ("labelledstatement".equals(name)) {
				return LABEL;
			}
			if ("enumvalues".equals(name) || "elementvalues".equals(name)) {
				return VALUES;
			}
			if ("throwntypes".equals(name)) {
				return THROW;
			}
			if ("value".equals(name) || "returnedexpression".equals(name) || "expressions".equals(name)) {
				return EXPRESSION;
			}
			if ("asserted".equals(name)) {
				return CONDITION;
			}
			if ("parameters".equals(name)) {
				return PARAMETER;
			}
			if ("typemembers".equals(name)) {
				return MEMBERS;
			}
			if ("throwexpression".equals(name)) {
				return THROW;
			}
			if ("returntype".equals(name)
					|| "componenttype".equals(name)
					|| "declaringtype".equals(name)
					|| "annotationtype".equals(name)
					|| "declaringtype".equals(name)) {
				return TYPE;
			}
			if ("caseexpression".equals(name)) {
				return CASE;
			}
			if ("elseexpression".equals(name) || "elsestatement".equals(name)) {
				return ELSE;
			}
			if ("thenexpression".equals(name) || "thenstatement".equals(name)) {
				return THEN;
			}
			if ("righthandoperand".equals(name)) {
				return RIGHT_OPERAND;
			}
			if ("lefthandoperand".equals(name)) {
				return LEFT_OPERAND;
			}
			if ("pack".equals(name) || "packs".equals(name)) {
				return PACKAGE;
			}
			if ("superclass".equals(name)) {
				return SUPER_TYPE;
			}
			if ("name".equals(name) || "simplename".equals(name)) {
				return NAME;
			}
			return null;
		}

		public String getTitleName() {
			String s = name().toLowerCase();
			s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
			int i = s.indexOf("_");
			if (i != -1) {
				s = s.substring(0, i) + Character.toUpperCase(s.charAt(i + 1))
						+ s.substring(i + 2);
			}
			return s;
		}
	}
}

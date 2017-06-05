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

import org.apache.commons.io.FileUtils;
import spoon.compiler.Environment;
import spoon.diff.Action;
import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;
import spoon.reflect.reference.CtReference;
import spoon.reflect.visitor.CtInheritanceScanner;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.PrettyPrinter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A visitor for generating Java code from the program compile-time model.
 */
public class SniperJavaPrettyPrinter extends CtScanner
		implements PrettyPrinter {

	private Deque<Action> actions;
	private Environment env;
	private HashMap<CtElement, Deque<Action>> actionsOnElement;
	private spoon.reflect.visitor.printer.sniper.SniperWriter writer;

	public SniperJavaPrettyPrinter(Environment env) {
		this.actions = env.getActionChanges();
		this.env = env;
	}

	@Override
	public String getPackageDeclaration() {
		return null;
	}

	@Override
	public String printPackageInfo(CtPackage pack) {
		return "";
	}

	@Override
	public String getResult() {
		return writer.toString();
	}

	@Override
	public void reset() {
		writer.clear();
	}

	@Override
	public void calculate(CompilationUnit sourceCompilationUnit, List<CtType<?>> types) {
		actionsOnElement = new HashMap<>();
		Deque<Action> actionOnTypes = new ArrayDeque<>();
		for (CtType<?> ctType : types) {
			for (Action action : actions) {
				CtElement element = action.getContext().getElement();
				if (element instanceof CtReference && element.isParentInitialized()) {
					element = element.getParent();
				}
				try {
					if (element.hasParent(ctType) || ctType.equals(element)) {
						if (!actionsOnElement.containsKey(element)) {
							actionsOnElement.put(element, new ArrayDeque<Action>());
						}
						actionsOnElement.get(element).addFirst(action);
						actionOnTypes.add(action);
					}
				} catch (ParentNotInitializedException e) {
					System.out.println(element);
					e.printStackTrace();
				}
			}
			for (CtElement ctElement : new HashSet<>(actionsOnElement.keySet())) {
				if (ctElement.getPosition() == null || ctElement.getPosition() instanceof NoSourcePosition) {
					actionsOnElement.remove(ctElement);
				} else {
					cleanDeleteAll(actionsOnElement.get(ctElement));
					removeMultiModifiersActions(actionsOnElement.get(ctElement));
				}
			}

			writer = new spoon.reflect.visitor.printer.sniper.SniperWriter(sourceCompilationUnit.getOriginalSourceCode(), env);
			scan(ctType);
		}
	}

	private void cleanDeleteAll(Deque<Action> actions) {
		DeleteAllAction currentDeleteAllAction = null;
		for (Action action : new ArrayDeque<>(actions)) {
			if (action instanceof DeleteAllAction) {
				currentDeleteAllAction = (DeleteAllAction) action;
				continue;
			}
			if (currentDeleteAllAction != null && action instanceof AddAction) {
				Object oldContent = currentDeleteAllAction.getOldContent();
				if (oldContent instanceof List) {
					List<?> list = (List) oldContent;
					if (list.contains(action.getNewElement())) {
						List<?> elementToRemove = new ArrayList<>(list);
						List<Action> addToDelete = new ArrayList<>();
						for (Object o : list) {
							for (Action a : new ArrayDeque<>(actions)) {
								if (o.equals(a.getNewElement())) {
									elementToRemove.remove(o);
									addToDelete.add(a);
								}
							}
						}
						actions.removeFirstOccurrence(currentDeleteAllAction);
						for (Object o : elementToRemove) {
							if (o instanceof CtElement) {
								actions.addFirst(new DeleteAction(currentDeleteAllAction.getContext(), (CtElement) o));
							} else {
								actions.addFirst(new DeleteAction(currentDeleteAllAction.getContext(), o));
							}
						}
						for (Action action1 : addToDelete) {
							actions.removeFirstOccurrence(action1);
						}
						break;
					}
				}
			}
		}
	}

	private void removeMultiModifiersActions(Deque<Action> actions) {
		boolean isModifiersAlreadyPresent = false;
		for (Action action : new ArrayDeque<>(actions)) {
			if (action.getNewValue() instanceof ModifierKind) {
				if (isModifiersAlreadyPresent) {
					actions.remove(action);
				}
				isModifiersAlreadyPresent = true;
			}
		}
	}

	@Override
	public Map<Integer, Integer> getLineNumberMapping() {
		return null;
	}

	private String getFileContent(File file) {
		try {
			return FileUtils.readFileToString(file);
		} catch (IOException e) {
			throw new RuntimeException("File not found");
		}
	}

	@Override
	protected void enter(CtElement e) {
		if (actionsOnElement.containsKey(e)) {
			SniperWriter s = new SniperWriter(actionsOnElement.get(e));
			if (e.getPosition() != null && !(e.getPosition() instanceof NoSourcePosition)) {
				s.scan(e);
				if (!s.getActions().isEmpty()) {
					throw new RuntimeException("All actions are not applied (" + s.getActions().size() + ")");
				}
			}
		}
		super.enter(e);
	}

	class SniperWriter extends CtInheritanceScanner {
		private Deque<Action> actions;

		SniperWriter(Deque<Action> actions) {
			this.actions = new ArrayDeque<>(actions);
		}

		public Deque<Action> getActions() {
			return actions;
		}

		private Action applyAction(SniperElement sniper, Action action) {
			Action consumed = sniper.onAction(action);
			if (consumed == null) {
				return null;
			}
			if (action instanceof AddAction) {
				return sniper.onAdd((AddAction) action);
			}
			if (action instanceof UpdateAction) {
				return sniper.onUpdate((UpdateAction) action);
			}
			if (action instanceof DeleteAllAction) {
				return sniper.onDeleteAll((DeleteAllAction) action);
			}
			if (action instanceof DeleteAction) {
				return sniper.onDelete((DeleteAction) action);
			}
			return action;
		}

		private void applyActions(SniperElement sniper) {
			Iterator<Action> actionIterator = actions.iterator();
			while (actionIterator.hasNext()) {
				Action action = actionIterator.next();
				Action consumed = applyAction(sniper, action);
				if (consumed == null) {
					actions.removeFirstOccurrence(action);
				}
			}
		}

		@Override
		public <T> void scanCtType(CtType<T> type) {
			applyActions(new SniperCtType(writer, type));
		}

		@Override
		public <R> void visitCtBlock(CtBlock<R> block) {
			applyActions(new SniperCtBlock(writer, block));
		}

		@Override
		public void scanCtStatement(CtStatement s) {
			if (s instanceof CtType) {
				super.scanCtStatement(s);
				return;
			}

			super.scanCtStatement(s);
		}

		@Override
		public <T> void scanCtVariableAccess(CtVariableAccess<T> e) {
			applyActions(new SniperCtVariableAccess(writer, e));
			super.scanCtVariableAccess(e);
		}

		@Override
		public void scanCtNamedElement(CtNamedElement e) {
			applyActions(new SniperCtNamedElement(writer, e));
			super.scanCtNamedElement(e);
		}

		@Override
		public void scanCtModifiable(CtModifiable m) {
			applyActions(new SniperCtModifiable(writer, m));
			super.scanCtModifiable(m);
		}
	}
}


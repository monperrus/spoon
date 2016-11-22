/**
 * Copyright (C) 2006-2016 INRIA and contributors
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
package spoon.reflect.visitor;

import org.apache.commons.io.FileUtils;
import spoon.compiler.Environment;
import spoon.diff.Action;
import spoon.diff.AddAction;
import spoon.diff.DeleteAction;
import spoon.diff.DeleteAllAction;
import spoon.diff.UpdateAction;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtModifiable;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.declaration.ParentNotInitializedException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A visitor for generating Java code from the program compile-time model.
 */
public class SniperJavaPrettyPrinter extends CtScanner implements PrettyPrinter {

	private Deque<Action> actions;
	private Environment env;
	private HashMap<CtElement, Deque<Action>> actionsOnElement;
	private Writer writer;

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
		actionsOnElement = new HashMap();
		Deque<Action> actionOnTypes = new ArrayDeque();
		for (int i = 0; i < types.size(); i++) {
			CtType<?> ctType = types.get(i);
			for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
				Action action = iterator.next();
				CtElement element = action.getContext().getElement();
				try {
					if (element.hasParent(ctType) || ctType.equals(element)) {
						if (!actionsOnElement.containsKey(element)) {
							actionsOnElement.put(element, new ArrayDeque<Action>());
						}
						actionsOnElement.get(element).add(action);
						actionOnTypes.add(action);
					}
				} catch (ParentNotInitializedException e) {
					System.out.println(element);
					e.printStackTrace();
				}
			}
			writer = new Writer(getFileContent(ctType.getPosition().getFile()));
			scan(ctType);
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
			if (e.getPosition() != null) {
				s.scan(e);
			}
		}
		super.enter(e);
	}

	class SniperWriter extends CtInheritanceScanner {
		private Deque<Action> actions;

		SniperWriter(Deque<Action> actions) {
			this.actions = actions;
		}

		@Override
		public <T> void scanCtType(CtType<T> type) {
			for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
				Action action = iterator.next();
				if (action instanceof AddAction) {
					int position = 0;
					if (action.getNewElement() instanceof CtField) {
						for (int i = type.getFields().size() - 1; i >= 0; i--) {
							CtField<?> ctField = type.getFields().get(i);
							if (ctField.getPosition() != null) {
								position = ctField.getPosition().getSourceEnd() + 2;
								break;
							}
						}
					} else if (action.getNewElement() instanceof CtMethod) {
						for (Iterator<CtMethod<?>> actionIterator = type
								.getMethods()
								.iterator(); actionIterator.hasNext();) {
							CtMethod<?> method = actionIterator.next();
							if (method.getPosition() != null) {
								position = method.getPosition().getSourceEnd() + 2;
								break;
							}
						}

					} else {
						throw new RuntimeException("balbal");
					}
					if (position == 0) {
						position = type.getPosition().getSourceEnd() - 2;
					}
					writer.write(action.getNewElement(), position);
				} else if (action instanceof DeleteAction) {

				} else if (action instanceof DeleteAllAction) {

				}
			}
			super.scanCtType(type);
		}

		@Override
		public void scanCtNamedElement(CtNamedElement e) {
			for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
				Action action = iterator.next();
				if (action instanceof UpdateAction) {
					CtElement element = action.getContext().getElement();
					if (element instanceof CtType) {
						String firstLine = element.clone().setComments(Collections.EMPTY_LIST).toString().split("\n")[0];
						writer.replace(element.getPosition().getSourceStart(), element.getPosition().getSourceStart() + 1, firstLine);
					}
				}
			}
			super.scanCtNamedElement(e);
		}

		@Override
		public void scanCtModifiable(CtModifiable m) {
			for (Iterator<Action> iterator = actions.iterator(); iterator.hasNext();) {
				Action action = iterator.next();
				if (action instanceof UpdateAction) {
					if (action.getNewValue() instanceof ModifierKind) {
						// TODO
					}
				} else if (action instanceof DeleteAllAction) {
					if (action.getNewValue() instanceof ModifierKind) {
						// TODO
					}
				} else if (action instanceof DeleteAction) {
					if (action.getNewValue() instanceof ModifierKind) {
						// TODO
					}
				} else if (action instanceof AddAction) {
					if (action.getNewValue() instanceof ModifierKind) {
						// TODO
					}
				}
			}
			super.scanCtModifiable(m);
		}
	}
}


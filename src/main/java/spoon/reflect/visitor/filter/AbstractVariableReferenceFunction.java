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
package spoon.reflect.visitor.filter;

import spoon.SpoonException;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtVariableReference;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.chain.CtConsumableFunction;
import spoon.reflect.visitor.chain.CtConsumer;
import spoon.reflect.visitor.chain.CtQuery;
import spoon.reflect.visitor.chain.CtScannerListener;
import spoon.reflect.visitor.chain.ScanningMode;

/**
 * This mapping function expects a {@link CtVariable} as input
 * and returns all {@link CtVariableReference}s, which refers this input.
 * It is used to implement {@link LocalVariableReferenceFunction}, {@link ParameterReferenceFunction}, {@link CatchVariableReferenceFunction}
 */
abstract class AbstractVariableReferenceFunction implements CtConsumableFunction<CtElement> {

	final CtVariable<?> targetVariable;
	final Class<?> variableClass;
	final Class<?> variableReferenceClass;

	protected AbstractVariableReferenceFunction(Class<?> variableClass, Class<?> variableReferenceClass) {
		this.variableClass = variableClass;
		this.variableReferenceClass = variableReferenceClass;
		this.targetVariable = null;
	}

	/**
	 * This constructor allows to define target variable - the one for which this function will search for.
	 * In such case the input of mapping function represents the searching scope
	 * @param variable - the parameter declaration which is searched in scope of input element
	 */
	protected AbstractVariableReferenceFunction(Class<?> variableClass, Class<?> variableReferenceClass, CtVariable<?> variable) {
		this.variableClass = variableClass;
		this.variableReferenceClass = variableReferenceClass;
		this.targetVariable = variable;
	}

	@Override
	public void apply(final CtElement scope, CtConsumer<Object> outputConsumer) {
		CtVariable<?> var = targetVariable;
		if (var == null) {
			if (variableClass.isInstance(scope)) {
				var = (CtVariable<?>) scope;
			} else {
				throw new SpoonException("The input of " + getClass().getSimpleName() + " must be a " + variableClass.getSimpleName() + " but is " + scope.getClass().getSimpleName());
			}
		}
		final CtVariable<?> variable = var;
		final String simpleName = variable.getSimpleName();
		//the context which knows whether we are scanning in scope of local type or not
		final Context context = new Context();
		CtQuery scopeQuery;
		if (scope == variable) {
			//we are starting search from local variable declaration
			scopeQuery = createScopeQuery(scope, context);
		} else {
			//we are starting search later, somewhere deep in scope of variable declaration
			final CtElement variableParent = variable.getParent();
			/*
			 * search in parents of searching scope for the variableParent
			 * 1) to check that scope is a child of variableParent
			 * 2) to detect if there is an local class between variable declaration and scope
			 */
			if (scope.map(new ParentFunction()).select(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					if (element instanceof CtType) {
						//detected that the search scope is in local class declared in visibility scope of variable
						context.nrTypes++;
					}
					return variableParent == element;
				}
			}).first() == null) {
				//the scope is not under children of localVariable
				throw new SpoonException("Cannot search for references of variable in wrong scope.");
			}
			//search in all children of the scope element
			scopeQuery = scope.map(new CtScannerFunction().setListener(context));
		}
		scopeQuery.select(new Filter<CtElement>() {
				@Override
				public boolean matches(CtElement element) {
					if (variableReferenceClass.isInstance(element)) {
						CtVariableReference<?> varRef = (CtVariableReference<?>) element;
						if (simpleName.equals(varRef.getSimpleName())) {
							//we have found a variable reference of required type in visibility scope of targetVariable
							if (context.hasLocalType()) {
								//there exists a local type in visibility scope of this variable declaration
								//another variable declarations in scope of this local class may shadow input localVariable
								//so finally check that found variable reference is really a reference to target variable
								return variable == varRef.getDeclaration();
							}
							//else we can be sure that found reference is reference to variable
							return true;
						}
					}
					return false;
				}
			})
			.forEach(outputConsumer);
	}

	protected static class Context implements CtScannerListener {
		int nrTypes = 0;

		@Override
		public ScanningMode enter(CtElement element) {
			if (element instanceof CtType) {
				nrTypes++;
			}
			return ScanningMode.NORMAL;
		}

		@Override
		public void exit(CtElement element) {
			if (element instanceof CtType) {
				nrTypes--;
			}
		}
		boolean hasLocalType() {
			return nrTypes > 0;
		}
	}

	protected abstract CtQuery createScopeQuery(CtElement scope, Context context);
}

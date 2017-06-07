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
package spoon.reflect.code;

import spoon.support.PropertyGetter;
import spoon.support.PropertySetter;

import static spoon.reflect.path.CtRole.KIND;
import static spoon.reflect.path.CtRole.OPERAND;


/**
 * This code element represents a unary operator.
 * For example :
 * <pre>
 *     int x=3;
 *     --x; // &lt;-- unary --
 * </pre>
 *
 * @param <T>
 * 		"Return" type of this expression
 */
public interface CtUnaryOperator<T> extends CtExpression<T>, CtStatement {

	/**
	 * Gets the expression to which the operator is applied.
	 */
	@PropertyGetter(role = OPERAND)
	CtExpression<T> getOperand();

	/**
	 * Sets the expression to which the operator is applied.
	 */
	@PropertySetter(role = OPERAND)
	<C extends CtUnaryOperator> C setOperand(CtExpression<T> expression);

	/**
	 * Sets the kind of this operator.
	 */
	@PropertySetter(role = KIND)
	<C extends CtUnaryOperator> C setKind(UnaryOperatorKind kind);

	/**
	 * Gets the kind of this operator.
	 */
	@PropertyGetter(role = KIND)
	UnaryOperatorKind getKind();

	@Override
	CtUnaryOperator<T> clone();
}

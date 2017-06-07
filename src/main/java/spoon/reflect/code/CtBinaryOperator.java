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
import static spoon.reflect.path.CtRole.LEFT_OPERAND;
import static spoon.reflect.path.CtRole.RIGHT_OPERAND;

/**
 * This interface defines a binary operator.
 *
 * Example:
 * <pre>
 *     // 3+4 is the binary expression
 *     int x = 3 + 4;
 * </pre>
 * @param <T>
 * 		Type of this expression
 */
public interface CtBinaryOperator<T> extends CtExpression<T> {

	/**
	 * Returns the left-hand operand.
	 */
	@PropertyGetter(role = LEFT_OPERAND)
	CtExpression<?> getLeftHandOperand();

	/**
	 * Returns the right-hand operand.
	 */
	@PropertyGetter(role = RIGHT_OPERAND)
	CtExpression<?> getRightHandOperand();

	/**
	 * Sets the left-hand operand.
	 */
	@PropertySetter(role = LEFT_OPERAND)
	<C extends CtBinaryOperator<T>> C setLeftHandOperand(CtExpression<?> expression);

	/**
	 * Sets the right-hand operand.
	 */
	@PropertySetter(role = RIGHT_OPERAND)
	<C extends CtBinaryOperator<T>> C setRightHandOperand(CtExpression<?> expression);

	/**
	 * Sets the kind of this binary operator.
	 */
	@PropertySetter(role = KIND)
	<C extends CtBinaryOperator<T>> C setKind(BinaryOperatorKind kind);

	/**
	 * Gets the kind of this binary operator.
	 */
	@PropertyGetter(role = KIND)
	BinaryOperatorKind getKind();

	@Override
	CtBinaryOperator<T> clone();
}

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
package spoon.reflect.visitor.chain;

import spoon.reflect.declaration.CtElement;
import spoon.reflect.visitor.Filter;

/**
 * Represents an object on which one can make queries.
 * It is implemented 1) by {@link CtElement} to allow creation of a new query
 * 2) by {@link CtQuery} to allow chaining query steps
 */
public interface CtQueryable {

	/**
	 * Appends a queryStep to the query.
	 * When this query is executed then this query sends input to the queryStep and the queryStep
	 * sends the result element(s) of this queryStep by calling out output.accept(result)
	 *
	 * @param queryStep
	 * @return the created QueryStep, which is the new last step of the query
	 */
	<T> CtQuery<T> map(CtQueryStep<?, T> queryStep);

	/**
	 * Appends a function to the query, that is executed according to the return type of function.
	 * <table>
	 * <tr><td><b>Return type of `function`</b><td><b>Behavior</b>
	 * <tr><td>{@link Boolean}<td>Sends input of this step to the next step if returned value of `function`is true
	 * <tr><td>{@link Iterable}<td>Sends each item of the collection to the next step
	 * <tr><td>{@link Object[]}<td>Sends each item of the array to the next step
	 * <tr><td>? extends {@link Object}<td>Sends the returned value of `function` to the next step
	 * </table><br>
	 *
	 * @param function a Function with one parameter of type I returning value of type R
	 * @return the create QueryStep, which is now the last step of the query
	 */
	<I, R> CtQuery<R> map(CtFunction<I, R> function);

	/**
	 * Recursively scans alR child elements of an input element.
	 * The matched child element (filter.matches(element)==true) are sent to the next step.
	 *
	 * Note: the input element (the root of the query, this if you're in {@link CtElement}) is also checked and may thus be also sent to the next step.
	 * The elements which throw {@link ClassCastException} during {@link Filter#matches(CtElement)}
	 * are considered as not matching.
	 *
	 * @param filter used to filter scanned children elements of the AST tree
	 * @return the created QueryStep, which is now the last step of the query
	 */
	<T extends CtElement> CtQuery<T> filterChildren(Filter<T> filter);
}

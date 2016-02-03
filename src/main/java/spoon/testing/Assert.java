/**
 * Copyright (C) 2006-2015 INRIA and contributors
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
package spoon.testing;

import spoon.reflect.declaration.CtElement;

import java.io.File;

import static spoon.testing.utils.Check.exists;
import static spoon.testing.utils.Check.isNotNull;

/**
 * Entry point for assertion methods for different data types.
 * Each method in this class is a static factory for the type-specific
 * assertion objects. The purpose of this class is to make test code
 * more readable.
 */
public class Assert {
	/**
	 * Create a new instance of <code>{@link FileAssert}</code>.
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractFileAssert<?> assertThat(String actual) {
		return assertThat(new File(actual));
	}

	/**
	 * Create a new instance of <code>{@link FileAssert}</code>.
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractFileAssert<?> assertThat(File actual) {
		isNotNull(actual);
		exists(actual);
		return new FileAssert(actual);
	}

	/**
	 * Create a new instance of <code>{@link CtElementAssert}</code>.
	 *
	 * @param actual
	 * 		The actual value.
	 * @return the created assertion object.
	 */
	public static AbstractCtElementAssert<?> assertThat(CtElement actual) {
		isNotNull(actual);
		return new CtElementAssert(actual);
	}
}

	/**
	* Copyright (C) 2006-2018 INRIA and contributors Spoon - http://spoon.gforge.inria.fr/
	*
	* <p>This software is governed by the CeCILL-C License under French law and abiding by the rules of
	* distribution of free software. You can use, modify and/or redistribute the software under the
	* terms of the CeCILL-C license as circulated by CEA, CNRS and INRIA at http://www.cecill.info.
	*
	* <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
	* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
	* CeCILL-C License for more details.
	*
	* <p>The fact that you are presently reading this means that you have had knowledge of the CeCILL-C
	* license and that you accept its terms.
	*/
	package spoon.javadoc.internal;

	/**
	* An element of a description: either an inline tag or a piece of text.
	*
	* <p>So for example <code>a text</code> or <code>{@link String}</code> could be valid description
	* elements.
	*/
	public interface JavadocDescriptionElement {
	/** pretty-prints the Javadoc fragment */
	String toText();
	}

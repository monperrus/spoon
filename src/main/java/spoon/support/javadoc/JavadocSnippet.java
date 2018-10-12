/**
 * Copyright (C) 2006-2018 INRIA and contributors
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

package spoon.support.javadoc;

/**
 * A piece of text inside a Javadoc description.
 * <p>
 * For example in <code>A class totally unrelated to {@link String}, I swear!</code> we would have two snippets: one
 * before and one after the inline tag (<code>{@link String}</code>).
 */
public class JavadocSnippet implements JavadocDescriptionElement {
    private String text;

    public JavadocSnippet(String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        this.text = text;
    }

    @Override
    public String toText() {
        return this.text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavadocSnippet that = (JavadocSnippet) o;

        return text.equals(that.text);

    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public String toString() {
        return "JavadocSnippet{" +
                "text='" + text + '\'' +
                '}';
    }
}

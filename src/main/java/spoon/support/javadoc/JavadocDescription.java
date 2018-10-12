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

import java.util.LinkedList;
import java.util.List;

/**
 * A javadoc text, potentially containing inline tags.
 *
 * For example <code>This class is totally unrelated to {@link com.github.javaparser.Range}</code>
 */
public class JavadocDescription {

    private List<JavadocDescriptionElement> elements;

    public static JavadocDescription parseText(String text) {
        JavadocDescription instance = new JavadocDescription();
        int index = 0;
        Pair<Integer, Integer> nextInlineTagPos;
        while ((nextInlineTagPos = indexOfNextInlineTag(text, index)) != null) {
            if (nextInlineTagPos.a != index) {
                instance.addElement(new JavadocSnippet(text.substring(index, nextInlineTagPos.a)));
            }
            instance.addElement(JavadocInlineTag.fromText(text.substring(nextInlineTagPos.a, nextInlineTagPos.b + 1)));
            index = nextInlineTagPos.b + 1;
        }
        if (index < text.length()) {
            instance.addElement(new JavadocSnippet(text.substring(index)));
        }
        return instance;
    }

    private static Pair<Integer, Integer> indexOfNextInlineTag(String text, int start) {
        int index = text.indexOf("{@", start);
        if (index == -1) {
            return null;
        }
        // we are interested only in complete inline tags
        int closeIndex = text.indexOf("}", index);
        if (closeIndex == -1) {
            return null;
        }
        return new Pair<>(index, closeIndex);
    }

    public JavadocDescription() {
        elements = new LinkedList<>();
    }

    public JavadocDescription(List<JavadocDescriptionElement> elements) {
        this();

        this.elements.addAll(elements);
    }

    public boolean addElement(JavadocDescriptionElement element) {
        return this.elements.add(element);
    }

    public List<JavadocDescriptionElement> getElements() {
        return this.elements;
    }

    public String toText() {
        StringBuilder sb = new StringBuilder();
        elements.forEach(e -> sb.append(e.toText()));
        return sb.toString();
    }

    public boolean isEmpty() {
        return toText().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavadocDescription that = (JavadocDescription) o;

        return elements.equals(that.elements);

    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    @Override
    public String toString() {
        return "JavadocDescription{" +
                "elements=" + elements +
                '}';
    }

}

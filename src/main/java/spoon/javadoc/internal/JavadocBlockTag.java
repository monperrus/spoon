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

package spoon.javadoc.internal;


import java.util.Optional;

/**
 * A block tag.
 * <p>
 * Typically they are found at the end of Javadoc comments.
 * <p>
 * Examples:
 * <code>@see AnotherClass</code>
 * <code>@since v0.0.1</code>
 * <code>@author Jim O'Java</code>
 */
public class JavadocBlockTag {

    /**
     * The type of tag: it could either correspond to a known tag (param, return, etc.) or represent
     * an unknown tag.
     */
    public enum Type {
        AUTHOR,
        DEPRECATED,
        EXCEPTION,
        PARAM,
        RETURN,
        SEE,
        SERIAL,
        SERIAL_DATA,
        SERIAL_FIELD,
        SINCE,
        THROWS,
        VERSION,
        UNKNOWN;

        Type() {
            this.keyword = name();
        }

        private String keyword;

        boolean hasName() {
            return this == PARAM || this == THROWS;
        }

        static Type fromName(String tagName) {
            for (Type t : Type.values()) {
                if (t.keyword.equals(tagName.toUpperCase())) {
                    return t;
                }
            }
            return UNKNOWN;
        }

    }

    private Type type;
    private JavadocDescription content;
    private Optional<String> name = Optional.empty();
    private String tagName;

    public JavadocBlockTag(Type type, String content) {
        this.type = type;
        this.tagName = type.keyword;
        if (type.hasName()) {
            this.name = Optional.of(JavadocInlineTag.nextWord(content));
            content = content.substring(this.name.get().length()).trim();
        }
        this.content = Javadoc.parseText(content);
    }

    public JavadocBlockTag(String tagName, String content) {
        this(Type.fromName(tagName), content);
        this.tagName = tagName;
    }

    public static JavadocBlockTag createParamBlockTag(String paramName, String content) {
        return new JavadocBlockTag(Type.PARAM, paramName + " " + content);
    }

    public Type getType() {
        return type;
    }

    public JavadocDescription getContent() {
        return content;
    }

    public Optional<String> getName() {
        return name;
    }

    public String getTagName() {
        return tagName;
    }

    /** pretty-prints the Javadoc tag */
    public String toText() {
        StringBuilder sb = new StringBuilder();
        sb.append("@");
        sb.append(tagName);
        name.ifPresent(s -> sb.append(" ").append(s));
        if (!content.isEmpty()) {
            sb.append(" ");
            sb.append(content.toText());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JavadocBlockTag that = (JavadocBlockTag) o;

        if (type != that.type) return false;
        if (!content.equals(that.content)) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JavadocBlockTag{" +
                "type=" + type +
                ", content='" + content + '\'' +
                ", name=" + name +
                '}';
    }
}

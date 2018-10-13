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

import org.eclipse.jdt.internal.compiler.parser.JavadocParser;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static spoon.support.javadoc.JavadocInlineTag.nextWord;


/**
 * The structured content of a single Javadoc comment.
 * <p>
 * It is composed by a description and a list of block tags.
 * <p>
 * An example would be the text contained in this very Javadoc comment. At the moment
 * of this writing this comment does not contain any block tags (such as <code>@see AnotherClass</code>)
 */
public class Javadoc {

    private JavadocDescription description;
    private List<JavadocBlockTag> blockTags;

    public Javadoc(JavadocDescription description) {
        this.description = description;
        this.blockTags = new LinkedList<>();
    }

    public Javadoc addBlockTag(JavadocBlockTag blockTag) {
        this.blockTags.add(blockTag);
        return this;
    }

    /**
     * For tags like "@return good things" where
     * tagName is "return",
     * and the rest is content.
     */
    public Javadoc addBlockTag(String tagName, String content) {
        return addBlockTag(new JavadocBlockTag(tagName, content));
    }

    /**
     * For tags like "@param abc this is a parameter" where
     * tagName is "param",
     * parameter is "abc"
     * and the rest is content.
     */
    public Javadoc addBlockTag(String tagName, String parameter, String content) {
        return addBlockTag(tagName, parameter + " " + content);
    }

    public Javadoc addBlockTag(String tagName) {
        return addBlockTag(tagName, "");
    }

    /**
     * Return the text content of the document. It does not containing trailing spaces and asterisks
     * at the start of the line.
     */
    public String toText() {
        StringBuilder sb = new StringBuilder();
        if (!description.isEmpty()) {
            sb.append(description.toText());
            sb.append(System.lineSeparator());
        }
        if (!blockTags.isEmpty()) {
            sb.append(System.lineSeparator());
        }
        blockTags.forEach(bt -> {
            sb.append(bt.toText());
            sb.append(System.lineSeparator());
        });
        return sb.toString();
    }

    public JavadocDescription getDescription() {
        return description;
    }

    /**
     * @return the current List of associated JavadocBlockTags
     */
    public List<JavadocBlockTag> getBlockTags() {
        return this.blockTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Javadoc document = (Javadoc) o;

        return description.equals(document.description) && blockTags.equals(document.blockTags);

    }

    @Override
    public int hashCode() {
        int result = description.hashCode();
        result = 31 * result + blockTags.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Javadoc{" +
                "description=" + description +
                ", blockTags=" + blockTags +
                '}';
    }

    private static String BLOCK_TAG_PREFIX = "@";
    private static Pattern BLOCK_PATTERN = Pattern.compile("^\\s*" + BLOCK_TAG_PREFIX, Pattern.MULTILINE);

    private final static String EOL = System.lineSeparator();

    public static Javadoc parse(String commentContent) {
        List<String> cleanLines;
		//cleanLines = cleanLines(commentContent);
		cleanLines = Arrays.asList(commentContent.split(EOL));
        int indexOfFirstBlockTag = cleanLines.stream()
                .filter(Javadoc::isABlockLine)
                .map(cleanLines::indexOf)
                .findFirst()
                .orElse(-1);
        List<String> blockLines;
        String descriptionText;
        if (indexOfFirstBlockTag == -1) {
            descriptionText = trimRight(String.join(EOL, cleanLines));
            blockLines = Collections.emptyList();
        } else {
            descriptionText = trimRight(String.join(EOL, cleanLines.subList(0, indexOfFirstBlockTag)));

            //Combine cleaned lines, but only starting with the first block tag till the end
            //In this combined string it is easier to handle multiple lines which actually belong together
            String tagBlock = cleanLines.subList(indexOfFirstBlockTag, cleanLines.size())
                    .stream()
                    .collect(Collectors.joining(EOL));

            //Split up the entire tag back again, considering now that some lines belong to the same block tag.
            //The pattern splits the block at each new line starting with the '@' symbol, thus the symbol
            //then needs to be added again so that the block parsers handles everything correctly.
            blockLines = BLOCK_PATTERN
                    .splitAsStream(tagBlock)
                    .filter(x -> !x.isEmpty())
                    .map(s -> BLOCK_TAG_PREFIX + s)
                    .collect(Collectors.toList());
        }
        Javadoc document = new Javadoc(JavadocDescription.parseText(descriptionText));
        blockLines.forEach(l -> document.addBlockTag(parseBlockTag(l)));
        return document;
    }

    private static JavadocBlockTag parseBlockTag(String line) {
        line = line.trim().substring(1);
        String tagName = nextWord(line);
        String rest = line.substring(tagName.length()).trim();
        return new JavadocBlockTag(tagName, rest);
    }

    private static boolean isABlockLine(String line) {
        return line.trim().startsWith(BLOCK_TAG_PREFIX);
    }

    private static String trimRight(String string) {
        while (!string.isEmpty() && Character.isWhitespace(string.charAt(string.length() - 1))) {
            string = string.substring(0, string.length() - 1);
        }
        return string;
    }

    private static List<String> cleanLines(String content) {
        String[] lines = content.split(EOL);
        List<String> cleanedLines = Arrays.stream(lines).map(l -> {
            int asteriskIndex = startsWithAsterisk(l);
            if (asteriskIndex == -1) {
                return l;
            } else {
                // if a line starts with space followed by an asterisk drop to the asterisk
                // if there is a space immediately after the asterisk drop it also
                if (l.length() > (asteriskIndex + 1)) {

                    char c = l.charAt(asteriskIndex + 1);
                    if (c == ' ' || c == '\t') {
                        return l.substring(asteriskIndex + 2);
                    }
                }
                return l.substring(asteriskIndex + 1);
            }
        }).collect(Collectors.toList());
        // lines containing only whitespace are normalized to empty lines
        cleanedLines = cleanedLines.stream().map(l -> l.trim().isEmpty() ? "" : l).collect(Collectors.toList());
        // if the first starts with a space, remove it
        if (!cleanedLines.get(0).isEmpty() && (cleanedLines.get(0).charAt(0) == ' ' || cleanedLines.get(0).charAt(0) == '\t')) {
            cleanedLines.set(0, cleanedLines.get(0).substring(1));
        }
        // drop empty lines at the beginning and at the end
        while (cleanedLines.size() > 0 && cleanedLines.get(0).trim().isEmpty()) {
            cleanedLines = cleanedLines.subList(1, cleanedLines.size());
        }
        while (cleanedLines.size() > 0 && cleanedLines.get(cleanedLines.size() - 1).trim().isEmpty()) {
            cleanedLines = cleanedLines.subList(0, cleanedLines.size() - 1);
        }
        return cleanedLines;
    }

    // Visible for testing
    static int startsWithAsterisk(String line) {
        if (line.startsWith("*")) {
            return 0;
        } else if ((line.startsWith(" ") || line.startsWith("\t")) && line.length() > 1) {
            int res = startsWithAsterisk(line.substring(1));
            if (res == -1) {
                return -1;
            } else {
                return 1 + res;
            }
        } else {
            return -1;
        }
    }

}

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
package spoon.reflect.visitor;

import org.apache.commons.lang3.StringUtils;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Computes source code representation of the Comment literal
 */
public class CommentHelper {

	/**
	 * RegExp which matches all possible line separators
	 */
	private static final Pattern LINE_SEPARATORS_RE = Pattern.compile("\\n\\r|\\n|\\r");

	private CommentHelper() {
	}

	public static void printCommentContent(PrinterHelper printer, CtComment comment) {
		printer.write(comment.getContent());
		switch (comment.getCommentType()) {
			case JAVADOC:
				printer.writeln();
				// we also have the tags
				List<CtJavaDocTag> javaDocTags = null;
				if (comment instanceof CtJavaDoc) {
					javaDocTags = ((CtJavaDoc) comment).getTags();
				}
				if (javaDocTags != null && javaDocTags.isEmpty() == false) {
					printer.writeln();
					for (CtJavaDocTag docTag : javaDocTags) {
						printJavaDocTag(printer, docTag);
					}
				}
				break;
		}
	}

	/** Utility method to print a complete comment */
	public static void printComment(PrinterHelper printer, CtComment comment) {
		CtComment.CommentType commentType = comment.getCommentType();
		switch (commentType) {
		case FILE:
			printer.write(DefaultJavaPrettyPrinter.JAVADOC_START).writeln();
			break;
		case JAVADOC:
			printer.write(DefaultJavaPrettyPrinter.JAVADOC_START).writeln();
			break;
		case INLINE:
			printer.write(DefaultJavaPrettyPrinter.INLINE_COMMENT_START);
			break;
		case BLOCK:
			printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_START);
			break;
		}
		printCommentContent(printer, comment);
		switch (commentType) {
			case BLOCK:
				printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_END);
				break;
			case FILE:
				printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_END);
				break;
			case JAVADOC:
				printer.write(DefaultJavaPrettyPrinter.BLOCK_COMMENT_END);
				break;
		}
	}

	static void printJavaDocTag(PrinterHelper printer, CtJavaDocTag docTag) {
		printer.write(CtJavaDocTag.JAVADOC_TAG_PREFIX);
		printer.write(docTag.getType().name().toLowerCase());
		printer.write(" ");
		if (docTag.getType().hasParam()) {
			printer.write(docTag.getParam()).write(" ");
		}

		printer.write(docTag.getContent()).writeln();

	}
}

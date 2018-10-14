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
package spoon.support.reflect.code;

import spoon.reflect.annotations.MetamodelPropertyField;
import spoon.reflect.code.CtComment;
import spoon.reflect.code.CtJavaDoc;
import spoon.reflect.code.CtJavaDocTag;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.path.CtRole;
import spoon.reflect.visitor.CtVisitor;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.javadoc.internal.Javadoc;
import spoon.javadoc.internal.JavadocBlockTag;
import spoon.javadoc.internal.JavadocDescription;
import spoon.javadoc.internal.JavadocDescriptionElement;
import spoon.javadoc.internal.JavadocInlineTag;
import spoon.support.util.ModelList;

import java.util.List;

import static spoon.support.compiler.jdt.JDTCommentBuilder.cleanComment;

public class CtJavaDocImpl extends CtCommentImpl implements CtJavaDoc {

	@MetamodelPropertyField(role = CtRole.COMMENT_TAG)
	private final ModelList<CtJavaDocTag> tags = new ModelList<CtJavaDocTag>() {
		@Override
		protected CtElement getOwner() {
			return CtJavaDocImpl.this;
		}
		@Override
		protected CtRole getRole() {
			return CtRole.COMMENT_TAG;
		}
		@Override
		protected int getDefaultCapacity() {
			return 2;
		}
	};

	/** parsed version of the javadoc */
	private transient Javadoc javadoc;

	@Override
	public <E extends CtComment> E setContent(String content) {
		javadoc = Javadoc.parse(cleanComment(content));
		for (JavadocBlockTag tag : javadoc.getBlockTags()) {
			CtJavaDocTag ctTag = getFactory().createJavaDocTag("", CtJavaDocTag.TagType.tagFromName(tag.getTagName().toUpperCase()));
			ctTag.setParam(tag.getName().orElse(""));
			ctTag.setContent(tag.getContent().toText());
			addTag(ctTag);
		}
		return super.setContent(content);
	}

	@Override
	public String getContent() {
		// for deserialization, we must rebuild the Javadoc object
		if (javadoc == null && getRawContent() != null) {
			javadoc = Javadoc.parse(cleanComment(getRawContent()));
		}

		if (getRawContent() == null || javadoc == null) {
			return "";
		}

		return getJavadocWithUpdatedInlineLinks().getDescription().toText().trim();
		//return cleanComment(getRawContent());
	}

	private Javadoc getJavadocWithUpdatedInlineLinks() {
		for(JavadocDescriptionElement fragment : getJavadocDescription().getElements()) {
			if (fragment instanceof JavadocInlineTag) {
				JavadocInlineTag tag = (JavadocInlineTag) fragment;
				if (tag.getType().equals(JavadocInlineTag.Type.LINK)
						&& this.getFactory().getEnvironment().isAutoImports() == false) {
					String stype = tag.getContent();
					// we write it fully qualified
					CtType type = this.getFactory().getModel().filterChildren(new NamedElementFilter<>(CtType.class, tag.getContent())).first(CtType.class);
					if (type != null) {
						tag.setContent(type.getQualifiedName());
					}
				}
			}
		}
		return javadoc;
	}

	private JavadocDescription getJavadocDescription() {
		if (javadoc == null) {
			return new JavadocDescription();
		}
		JavadocDescription description = javadoc.getDescription();
		if (description != null) {
			return description;
		}
		return new JavadocDescription();
	}

	public CtJavaDocImpl() {
		super(CommentType.JAVADOC);
	}

	@Override
	public List<CtJavaDocTag> getTags() {
		return tags;
	}

	@Override
	public <E extends CtJavaDoc> E setTags(List<CtJavaDocTag> tags) {
		this.tags.set(tags);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E addTag(CtJavaDocTag tag) {
		this.tags.add(tag);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E addTag(int index, CtJavaDocTag tag) {
		this.tags.add(index, tag);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E removeTag(int index) {
		this.tags.remove(index);
		return (E) this;
	}

	@Override
	public <E extends CtJavaDoc> E removeTag(CtJavaDocTag tag) {
		this.tags.remove(tag);
		return (E) this;
	}

	@Override
	public String getShortDescription() {
		int indexEndSentence = this.getContent().indexOf('.');
		if (indexEndSentence == -1) {
			indexEndSentence = this.getContent().indexOf('\n');
		}
		if (indexEndSentence != -1) {
			return this.getContent().substring(0, indexEndSentence + 1).trim();
		} else {
			return this.getContent().trim();
		}
	}

	@Override
	public String getLongDescription() {
		int indexStartLongDescription = getShortDescription().length();

		if (indexStartLongDescription < this.getContent().trim().length()) {
			return this.getContent().substring(indexStartLongDescription).trim();
		} else {
			return this.getContent().trim();
		}

	}

	@Override
	public void accept(CtVisitor visitor) {
		visitor.visitCtJavaDoc(this);
	}

	@Override
	public CtJavaDoc clone() {
		return (CtJavaDoc) super.clone();
	}
}

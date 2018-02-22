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
package spoon.support.reflect.reference;

import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtType;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.UnsettableProperty;

/** a type reference to the type containing the executable corresponding to the given executable reference */
public class CtDynamicLoopupTypeReferenceImpl extends CtTypeReferenceImpl  {

	private CtExecutableReference ref;

	public CtDynamicLoopupTypeReferenceImpl(CtExecutableReference ref) {
		super();
		this.ref = ref;
		// required for cloning afterwards
		setSimpleName(getSimpleName());
	}

	@Override
	public String getSimpleName() {
		CtType<Object> declaration = getDeclaration();
		if (declaration == null) {
			return super.getSimpleName();
		}
		return declaration.getSimpleName();
	}

	@Override
	public String getQualifiedName() {
		CtType<Object> declaration = getDeclaration();
		if (declaration == null) {
			return super.getQualifiedName();
		}
		return declaration.getQualifiedName();
	}

	@Override
	public CtType<Object> getDeclaration() {
		return getTypeDeclaration();
	}

	@Override
	public CtType<Object> getTypeDeclaration() {
		if (ref == null) {
			return null;
		}
		CtExecutable declaration = ref.getDeclaration();
		if (declaration == null || !declaration.isParentInitialized()) {
			// is a dynamic lookup, yet it is not in a type
			return null;
		}
		return (CtType<Object>) declaration.getParent();
	}

	public CtDynamicLoopupTypeReferenceImpl cloneSpecial() {
		return new CtDynamicLoopupTypeReferenceImpl(null);
	}
}

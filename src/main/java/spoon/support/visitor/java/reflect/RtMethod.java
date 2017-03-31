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
package spoon.support.visitor.java.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;

public class RtMethod {
	private Class<?> clazz;
	private String name;
	private Class<?> returnType;
	private TypeVariable<Method>[] typeParameters;
	private Class<?>[] parameterTypes;
	private Class<?>[] exceptionTypes;
	private int modifiers;
	private Annotation[] annotations;
	private Annotation[][] parameterAnnotations;
	private boolean isVarArgs;
	private boolean isDefault;

	public RtMethod(Class<?> clazz, String name, Class<?> returnType, TypeVariable<Method>[] typeParameters, Class<?>[] parameterTypes, Class<?>[] exceptionTypes, int modifiers, Annotation[] annotations,
			Annotation[][] parameterAnnotations, boolean isVarArgs, boolean isDefault) {
		this.clazz = clazz;
		this.name = name;
		this.returnType = returnType;
		this.typeParameters = typeParameters;
		this.parameterTypes = parameterTypes;
		this.exceptionTypes = exceptionTypes;
		this.modifiers = modifiers;
		this.annotations = annotations;
		this.parameterAnnotations = parameterAnnotations;
		this.isVarArgs = isVarArgs;
		this.isDefault = isDefault;
	}

	public Class<?> getDeclaringClass() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public Class<?> getReturnType() {
		return returnType;
	}

	public TypeVariable<Method>[] getTypeParameters() {
		return typeParameters;
	}

	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public Class<?>[] getExceptionTypes() {
		return exceptionTypes;
	}

	public int getModifiers() {
		return modifiers;
	}

	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	public Annotation[][] getParameterAnnotations() {
		return parameterAnnotations;
	}

	public boolean isVarArgs() {
		return isVarArgs;
	}

	public boolean isDefault() {
		return isDefault;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		RtMethod rtMethod = (RtMethod) o;

		if (name != null ? !name.equals(rtMethod.name) : rtMethod.name != null) {
			return false;
		}
		if (returnType != null ? !returnType.equals(rtMethod.returnType) : rtMethod.returnType != null) {
			return false;
		}
		if (!Arrays.equals(parameterTypes, rtMethod.parameterTypes)) {
			return false;
		}
		return Arrays.equals(exceptionTypes, rtMethod.exceptionTypes);
	}

	@Override
	public int hashCode() {
		return getDeclaringClass().getName().hashCode() ^ getName().hashCode();
	}

	public boolean isLightEquals(RtMethod rtMethod) {
		if (this == rtMethod) {
			return true;
		}
		if (rtMethod == null || getClass() != rtMethod.getClass()) {
			return false;
		}

		if (name != null ? !name.equals(rtMethod.name) : rtMethod.name != null) {
			return false;
		}
		return Arrays.equals(parameterTypes, rtMethod.parameterTypes);
	}
}

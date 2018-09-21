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
package spoon.support.compiler;

import spoon.SpoonModelBuilder;
import spoon.compiler.ModelBuildingException;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCodeSnippetExpression;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.compiler.jdt.JDTSnippetCompiler;
import spoon.support.reflect.declaration.CtElementImpl;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Helper class for working with snippets */
public class SnippetCompilationHelper {

	private SnippetCompilationHelper() { }

	private static final String WRAPPER_CLASS_NAME = "Wrapper";
	private static final String WRAPPER_METHOD_NAME = "wrap";

	public static void compileAndReplaceSnippetsIn(CtType<?> initialClass) {

		Factory f = initialClass.getFactory();

		// we need to slightly play with the modifiers
		Set<ModifierKind> backup = EnumSet.noneOf(ModifierKind.class);
		backup.addAll(initialClass.getModifiers());
		initialClass.removeModifier(ModifierKind.PUBLIC);

		// we need to delete the current class from its package
		// otherwsise the new type is not added because it has the same fully qualified name
		initialClass.delete();

		try {
			build(f, "package " + initialClass.getPackage().getQualifiedName() + ";" + initialClass.toString());
		} finally {
			// restore modifiers
			initialClass.setModifiers(backup);
		}

		// now we get the new class
		CtType<?> newClass = f.Type().get(initialClass.getQualifiedName());

		// we put all the members of the new class in the old class
		for (CtTypeMember m : new ArrayList<>(initialClass.getTypeMembers())) {
			initialClass.removeTypeMember(m);
		}
		for (CtTypeMember m : newClass.getTypeMembers()) {
			initialClass.addTypeMember(m);
		}

		// and we replace the new class in the factory by the previous one
		newClass.replace(initialClass);

	}

	public static CtStatement compileStatement(CtCodeSnippetStatement st)
			throws SnippetCompilationError {
		return internalCompileStatement(st, st.getFactory().Type().VOID_PRIMITIVE);
	}

	public static CtStatement compileStatement(CtCodeSnippetStatement st, CtTypeReference returnType)
			throws SnippetCompilationError {
		return internalCompileStatement(st, returnType);
	}

	private static CtStatement internalCompileStatement(CtElement st, CtTypeReference returnType) {
		Factory f = st.getFactory();

		String contents = createWrapperContent(st, f, returnType);

		build(f, contents);

		CtType<?> c = f.Type().get(WRAPPER_CLASS_NAME);

		// Get the part we want

		CtMethod<?> wrapper = c.getMethod(WRAPPER_METHOD_NAME);

		List<CtStatement> statements = wrapper.getBody().getStatements();
		CtStatement ret = statements.get(statements.size() - 1);

		// Clean up
		c.getPackage().removeType(c);

		//disconnect element from the parent, so it can be added to another model
		ret.delete();

		if (ret instanceof CtClass) {
			CtClass klass = (CtClass) ret;
			ret.getFactory().Package().getRootPackage().addType(klass);
			klass.setSimpleName(klass.getSimpleName().replaceAll("^[0-9]*", ""));
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static <T> CtExpression<T> compileExpression(
			CtCodeSnippetExpression<T> expr) throws SnippetCompilationError {

		CtReturn<T> ret = (CtReturn<T>) internalCompileStatement(expr, expr.getFactory().Type().OBJECT);

		return ret.getReturnedExpression();
	}

	private static void build(Factory f, String contents) {
		// Build contents
		SpoonModelBuilder builder = new JDTSnippetCompiler(f, contents);
		try {
			builder.build();
		} catch (Exception e) {
			throw new ModelBuildingException("snippet compilation error while compiling: " + contents, e);
		}
	}

	private static String createWrapperContent(final CtElement element, final Factory f, final CtTypeReference returnType) {
		CtClass<?> w = f.Class().create(WRAPPER_CLASS_NAME);

		CtBlock body = f.Core().createBlock();

		if (element instanceof CtStatement) {
			body.addStatement((CtStatement) element);
		} else if (element instanceof CtExpression) {
			CtReturn ret = f.Core().createReturn();
			ret.setReturnedExpression((CtExpression) element);
			body.addStatement(ret);
		}

		Set<ModifierKind> modifiers = EnumSet.of(ModifierKind.STATIC);

		Set<CtTypeReference<? extends Throwable>> thrownTypes = new HashSet<>();
		thrownTypes.add(f.Class().<Throwable>get(Throwable.class).getReference());
		f.Method().create(
				w,
				modifiers,
				returnType,
				WRAPPER_METHOD_NAME,
				CtElementImpl.<CtParameter<?>>emptyList(),
				thrownTypes,
				body);

		String contents = w.toString();
		// Clean up (delete wrapper from factory) after it is printed. The DefaultJavaPrettyPrinter needs w in model to be able to print it correctly
		w.getPackage().removeType(w);

		return contents;
	}

}

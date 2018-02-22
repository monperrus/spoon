/*
 * Copyright (C) 2006-2015 INRIA and contributors
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

package spoon.test.method;

import org.junit.Assert;
import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtParameterReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.NamedElementFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.reference.CtDynamicLoopupTypeReferenceImpl;
import spoon.test.delete.testclasses.Adobada;
import spoon.test.method.testclasses.Tacos;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.createFactory;

public class MethodTest {
	@Test
	public void testClone() throws Exception {
		final Factory factory = build(Adobada.class);
		final CtClass<Adobada> adobada = factory.Class().get(Adobada.class);
		final CtMethod<?> m2 = adobada.getMethod("m2");

		CtMethod<?> clone = m2.clone();
		clone.setVisibility(ModifierKind.PRIVATE);

		assertEquals(ModifierKind.PUBLIC, m2.getModifiers().iterator().next());
	}

	@Test
	public void testCloneMethod() throws Exception {
		// contract: dynamic lookup of executable references is preserved after cloning
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("src/test/resources/noclasspath/A2.java");
		l.buildModel();
		CtClass<Object> a2 = l.getFactory().Class().get("A2");
		CtMethod<?> method = a2.getMethodsByName("c").get(0);
		// the lookup is OK in the original node
		CtExecutableReference ctExecutableReference = method.getElements(new TypeFilter<>(CtExecutableReference.class)).get(0);
		assertSame(method,  ctExecutableReference.getDeclaration());
		assertSame(method, ctExecutableReference.getDeclaration());
		assertEquals("A2", ctExecutableReference.getDeclaringType().getSimpleName());
		assertSame(a2, ctExecutableReference.getDeclaringType().getDeclaration());

		CtTypeReference declaringType = ctExecutableReference.getDeclaringType();
		assertEquals(((CtDynamicLoopupTypeReferenceImpl)declaringType).clone(), declaringType);
		//assertEquals(((CtDynamicLoopupTypeReferenceImpl)declaringType).cloneSpecial(), declaringType);
		// cloning (and modifying for debug with toString)
		CtMethod<?> methodClone = method.clone();
		methodClone.getBody().insertBegin(l.getFactory().createCodeSnippetStatement("// debug info"));

		// the lookup is OK in the clone as well
		CtExecutableReference reference = methodClone.getElements(new TypeFilter<>(CtExecutableReference.class)).get(0);

		reference.setDeclaringType(new CtDynamicLoopupTypeReferenceImpl(reference));

		assertEquals("c", reference.getSimpleName());
		assertSame(methodClone, reference.getDeclaration());
		// a2.addMethod(methodClone);
		// assertEquals("<DYNAMIC_LOOKUP_REFERENCE>", reference.getDeclaringType().getSimpleName()); // not yet in a type
		// assertSame(a2, reference.getDeclaringType().getDeclaration()); // now we are in a type
	}

	@Test
	public void testSearchMethodWithGeneric() throws Exception {
		CtType<Tacos> aTacos = buildClass(Tacos.class);
		CtMethod<Object> method1 = aTacos.getMethod("method1", aTacos.getFactory().Type().integerType());
		assertEquals("public <T extends java.lang.Integer> void method1(T t) {" + System.lineSeparator() + "}", method1.toString());
		method1 = aTacos.getMethod("method1", aTacos.getFactory().Type().stringType());
		assertEquals("public <T extends java.lang.String> void method1(T t) {" + System.lineSeparator() + "}", method1.toString());
		method1 = aTacos.getMethod("method1", aTacos.getFactory().Type().objectType());
		assertEquals("public <T> void method1(T t) {" + System.lineSeparator() + "}", method1.toString());
	}

	@Test
	public void testAddSameMethodsTwoTimes() throws Exception {
		final Factory factory = createFactory();
		final CtClass<Object> tacos = factory.Class().create("Tacos");
		final CtMethod<Void> method = factory.Method().create(tacos, new HashSet<>(), factory.Type().voidType(), "m", new ArrayList<>(), new HashSet<>());
		try {
			tacos.addMethod(method.clone());
		} catch (ConcurrentModificationException e) {
			fail();
		}
	}

	@Test
	public void testGetAllMethods() throws Exception {
		/* getAllMethods must not throw Exception in no classpath mode */
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("src/test/resources/noclasspath/A3.java");
		l.buildModel();
		Set<CtMethod<?>> methods = l.getFactory().Class().get("A3").getAllMethods();
		assertEquals(1, methods.stream().filter(method -> "foo".equals(method.getSimpleName())).count());
	}

	@Test
	public void testGetAllMethodsAdaptingType() throws Exception {
		// contract: AbstractTypingContext should not enter in recursive calls when resolving autoreferenced bounding type
		// such as T extends Comparable<? super T>
		Launcher l = new Launcher();
		l.getEnvironment().setNoClasspath(true);
		l.addInputResource("src/test/resources/noclasspath/spring/PropertyComparator.java");
		l.buildModel();

		CtType<?> propertyComparator = l.getModel().getElements(new NamedElementFilter<CtType>(CtType.class, "PropertyComparator")).get(0);
		Set<CtMethod<?>> allMethods = propertyComparator.getAllMethods();

		boolean compareFound = false;
		for (CtMethod<?> method : allMethods) {
			if (method.getSimpleName().equals("compare")) {
				assertEquals("compare(T,T)", method.getSignature());
				compareFound = true;
			}
		}

		assertTrue(compareFound);
	}

}

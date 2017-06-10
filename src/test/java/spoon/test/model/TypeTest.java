package spoon.test.model;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtLambda;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtNewClass;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeInformation;
import spoon.reflect.declaration.CtTypeParameter;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtIntersectionTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;
import spoon.test.model.testclasses.Mole;
import spoon.test.model.testclasses.Pozole;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;
import static spoon.testing.utils.ModelUtils.canBeBuilt;
import static spoon.testing.utils.ModelUtils.createFactory;


public class TypeTest {

	@Test
	public void testGetAllExecutables() throws Exception {
		CtClass<?> type = build("spoon.test.model", "Foo");
		assertEquals(1, type.getDeclaredFields().size());
		assertEquals(3, type.getMethods().size());
		assertEquals(4, type.getDeclaredExecutables().size());
		assertEquals(2, type.getAllFields().size());
		assertEquals(1, type.getConstructors().size());
		assertEquals(16, type.getAllMethods().size());
		assertEquals(12, type.getFactory().Type().get(Object.class).getAllMethods().size());

		// we have 3  methods in Foo + 2 in Baz - 1 common in Foo.bar (m) + 12 in Object + 1 explicit constructor in Foo
		Collection<CtExecutableReference<?>> allExecutables = type.getAllExecutables();
		assertEquals(17, allExecutables.size());
	}

	@Test
	public void testGetUsedTypes() throws Exception {
		CtType<?> type = build("spoon.test.model", "Foo");
		TypeFactory tf = type.getFactory().Type();

		Set<CtTypeReference<?>> usedTypes = type.getUsedTypes(true);
		assertEquals(3, usedTypes.size());
		assertTrue(usedTypes.contains(tf.createReference(Bar.class)));
		assertTrue(usedTypes.contains(tf.createReference(Baz.class)));
		assertTrue(usedTypes.contains(tf.createReference(Baz.Inner.class)));

		assertEquals(0, type.getUsedTypes(false).size());
	}

	@Test
	public void superclassTest() throws Exception {
		CtType<?> type = build("spoon.test.model", "InterfaceSuperclass");

		Set<CtTypeReference<?>> interfaces = type.getSuperInterfaces();
		assertEquals(1, interfaces.size());

		CtTypeReference<?> inface = interfaces.iterator().next();
		assertNull(inface.getSuperclass());
	}

	@Test
	public void testGetUsedTypesForTypeInRootPackage() throws Exception {
		CtClass<?> cl = createFactory().Code().createCodeSnippetStatement("class X { X x; }").compile();
		assertEquals(0, cl.getUsedTypes(false).size());
	}

	@Test
	public void testGetDeclaredOrIheritedFieldOnType() throws Exception {
		CtType<?> type = build("spoon.test.model", "ClassWithSuperAndIFace");

		assertEquals("classField", type.getDeclaredOrInheritedField("classField").getSimpleName());
		assertEquals("i", type.getDeclaredOrInheritedField("i").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("fooMethod"));
		assertEquals("j", type.getDeclaredOrInheritedField("j").getSimpleName());
		assertEquals("IFACE_FIELD_1", type.getDeclaredOrInheritedField("IFACE_FIELD_1").getSimpleName());
		assertEquals("IFACE_FIELD_2", type.getDeclaredOrInheritedField("IFACE_FIELD_2").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("notExists"));
	}

	@Test
	public void testGetDeclaredOrIheritedFieldOnTypeRef() throws Exception {
		CtTypeReference<?> type = build("spoon.test.model", "ClassWithSuperAndIFace").getReference();

		assertEquals("classField", type.getDeclaredOrInheritedField("classField").getSimpleName());
		assertEquals("i", type.getDeclaredOrInheritedField("i").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("fooMethod"));
		assertEquals("j", type.getDeclaredOrInheritedField("j").getSimpleName());
		assertEquals("IFACE_FIELD_1", type.getDeclaredOrInheritedField("IFACE_FIELD_1").getSimpleName());
		assertEquals("IFACE_FIELD_2", type.getDeclaredOrInheritedField("IFACE_FIELD_2").getSimpleName());
		assertNull(type.getDeclaredOrInheritedField("notExists"));
	}

	@Test
	public void testGetDeclaredOrIheritedFieldByReflection() throws Exception {
		CtTypeReference<?> type = build("spoon.test.model", "ClassWithSuperOutOfModel").getReference();

		assertEquals("buf", type.getDeclaredOrInheritedField("buf").getSimpleName());
		assertEquals("count", type.getDeclaredOrInheritedField("count").getSimpleName());
		
	}

	@Test
	public void testTypeInfoIsInterface() throws Exception {
		//contract: isInterface returns true only for interfaces
		CtType<?> clazz = build("spoon.test.model", "ClassWithSuperOutOfModel");
		checkIsSomething("class", clazz);
		CtType<?> type = build("spoon.test.model", "InterfaceWrithFields");
		checkIsSomething("interface", type);
		checkIsSomething("enum", type.getFactory().Enum().create(type.getPackage(), "someEnum"));
		CtType<?> ctAnnotation = type.getFactory().Annotation().create(type.getPackage(), "someAnnotation");
		checkIsSomething("annotation", ctAnnotation);
		CtTypeParameter ctTypeParam = type.getFactory().Core().createTypeParameter();
		ctTypeParam.setSimpleName("T");
		clazz.addFormalCtTypeParameter(ctTypeParam);
		checkIsSomething("generics", ctTypeParam);
	}
	
	@Test
	public void testTypeAccessForDotClass() throws Exception {
		// contract: When we use .class on a type, this must be a CtTypeAccess.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/model/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> make = aPozole.getMethodsByName("make").get(0);

		final List<CtFieldRead<?>> fieldClasses = make.getElements(new TypeFilter<CtFieldRead<?>>(CtFieldRead.class) {
			@Override
			public boolean matches(CtFieldRead<?> element) {
				return "class".equals(element.getVariable().getSimpleName()) && super.matches(element);
			}
		});
		assertEquals(4, fieldClasses.size());
		for (CtFieldRead<?> fieldClass : fieldClasses) {
			assertTrue(fieldClass.getTarget() instanceof CtTypeAccess);
		}

		canBeBuilt(target, 8, true);
	}

	@Test
	public void testTypeAccessOnPrimitive() throws Exception {
		Factory factory = createFactory();
		CtClass<?> clazz = factory.Code().createCodeSnippetStatement( //
				"class X {" //
						+ "public void foo() {" //
						+ " Class klass=null;" //
						+ "  boolean x= (klass == short.class);" //
						+ "}};").compile();
		CtMethod<?> foo = (CtMethod<?>) clazz.getMethods().toArray()[0];

		CtBlock<?> body = foo.getBody();
		CtLocalVariable<?> ass = body.getStatement(1);
		CtBinaryOperator<?> op = (CtBinaryOperator<?>) ass.getDefaultExpression();
		assertEquals("Class", op.getLeftHandOperand().getType().getSimpleName());
		assertFalse(op.getLeftHandOperand().getType().isPrimitive());
		assertEquals("Class", op.getRightHandOperand().getType().getSimpleName());
		assertTrue(op.getRightHandOperand() instanceof CtFieldRead);
		assertFalse(op.getRightHandOperand().getType().isPrimitive());
	}

	@Test
	public void testTypeAccessForTypeAccessInInstanceOf() throws Exception {
		// contract: the right hand operator must be a CtTypeAccess.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/model/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> eat = aPozole.getMethodsByName("eat").get(0);

		final List<CtTypeAccess<?>> typeAccesses = eat.getElements(new TypeFilter<CtTypeAccess<?>>(CtTypeAccess.class));
		assertEquals(2, typeAccesses.size());

		assertTrue(typeAccesses.get(0).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(0).getParent()).getKind());
		assertEquals("a instanceof java.lang.String", typeAccesses.get(0).getParent().toString());

		assertTrue(typeAccesses.get(1).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(1).getParent()).getKind());
		assertEquals("a instanceof java.util.Collection<?>", typeAccesses.get(1).getParent().toString());
	}

	@Test
	public void testTypeAccessOfArrayObjectInFullyQualifiedName() throws Exception {
		// contract: A type access in fully qualified name must to rewrite well.
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/model/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> season = aPozole.getMethodsByName("season").get(0);

		final List<CtTypeAccess<?>> typeAccesses = season.getElements(new TypeFilter<CtTypeAccess<?>>(CtTypeAccess.class));
		assertEquals(2, typeAccesses.size());

		assertTrue(typeAccesses.get(0).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(0).getParent()).getKind());
		assertEquals("a instanceof java.lang.@spoon.test.annotation.testclasses.TypeAnnotation(integer = 1)" + System.lineSeparator() + "Object[]", typeAccesses.get(0).getParent().toString());

		assertTrue(typeAccesses.get(1).getParent() instanceof CtBinaryOperator);
		assertEquals(BinaryOperatorKind.INSTANCEOF, ((CtBinaryOperator) typeAccesses.get(1).getParent()).getKind());
		assertEquals("a instanceof java.lang.Object[]", typeAccesses.get(1).getParent().toString());

		canBeBuilt(target, 8, true);
	}

	@Test
	public void test() throws Exception {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/TorIntegration.java");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtType<?> ctType = launcher.getFactory().Class().getAll().get(0);
		List<CtNewClass> elements = ctType.getElements(new TypeFilter<>(CtNewClass.class));
		assertEquals(4, elements.size());
		for (int i = 0; i < elements.size(); i++) {
			CtNewClass ctNewClass = elements.get(i);
			assertEquals("android.content.DialogInterface$OnClickListener", ctNewClass.getAnonymousClass().getSuperclass().getQualifiedName());
		}
	}

	@Test
	public void testIntersectionTypeReferenceInGenericsAndCasts() throws Exception {
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/model/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> prepare = aPozole.getMethodsByName("prepare").get(0);

		// Intersection type in generic types.
		final List<CtClass> localTypes = prepare.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(1, localTypes.size());

		// New type parameter declaration.
		final CtTypeParameter typeParameter = localTypes.get(0).getFormalCtTypeParameters().get(0);
		assertNotNull(typeParameter);
		assertEquals("T", typeParameter.getSimpleName());
		assertIntersectionTypeForPozolePrepareMethod(aPozole, typeParameter.getSuperclass());

		// Intersection type in casts.
		final List<CtLambda<?>> lambdas = prepare.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class));
		assertEquals(1, lambdas.size());

		assertEquals(1, lambdas.get(0).getTypeCasts().size());
		assertTrue(lambdas.get(0).getTypeCasts().get(0) instanceof CtIntersectionTypeReference);
		final CtIntersectionTypeReference<?> intersectionType = lambdas.get(0).getTypeCasts().get(0).asCtIntersectionTypeReference();
		assertEquals("java.lang.Runnable & java.io.Serializable", intersectionType.toString());
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), intersectionType.getBounds().stream().collect(Collectors.toList()).get(0));
		assertEquals(aPozole.getFactory().Type().createReference(Serializable.class), intersectionType.getBounds().stream().collect(Collectors.toList()).get(1));

		canBeBuilt(target, 8, true);
	}

	private void assertIntersectionTypeForPozolePrepareMethod(CtClass<Pozole> aPozole, CtTypeReference<?> boundingType) {
		assertNotNull(boundingType);
		assertTrue(boundingType instanceof CtIntersectionTypeReference);
		assertEquals("java.lang.Runnable & java.io.Serializable", boundingType.toString());
		final CtIntersectionTypeReference<?> superType = boundingType.asCtIntersectionTypeReference();
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), superType.getBounds().stream().collect(Collectors.toList()).get(0));
		assertEquals(aPozole.getFactory().Type().createReference(Serializable.class), superType.getBounds().stream().collect(Collectors.toList()).get(1));
	}

	@Test
	public void testTypeReferenceInGenericsAndCasts() throws Exception {
		final String target = "./target/type";
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/java/spoon/test/model/testclasses");
		launcher.setSourceOutputDirectory(target);
		launcher.getEnvironment().setNoClasspath(true);
		launcher.run();

		final CtClass<Pozole> aPozole = launcher.getFactory().Class().get(Pozole.class);
		final CtMethod<?> prepare = aPozole.getMethodsByName("finish").get(0);

		// Intersection type in generic types.
		final List<CtClass> localTypes = prepare.getElements(new TypeFilter<>(CtClass.class));
		assertEquals(1, localTypes.size());

		// New type parameter declaration.
		final CtTypeParameter typeParameter = localTypes.get(0).getFormalCtTypeParameters().get(0);
		assertNotNull(typeParameter);
		assertEquals("T", typeParameter.getSimpleName());
		assertIntersectionTypeForPozoleFinishMethod(aPozole, typeParameter.getSuperclass());

		// Intersection type in casts.
		final List<CtLambda<?>> lambdas = prepare.getElements(new TypeFilter<CtLambda<?>>(CtLambda.class));
		assertEquals(1, lambdas.size());

		assertEquals(1, lambdas.get(0).getTypeCasts().size());
		assertEquals("java.lang.Runnable", lambdas.get(0).getTypeCasts().get(0).toString());
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), lambdas.get(0).getTypeCasts().get(0));

		canBeBuilt(target, 8, true);
	}

	private void assertIntersectionTypeForPozoleFinishMethod(CtClass<Pozole> aPozole, CtTypeReference<?> boundingType) {
		assertNotNull(boundingType);
		assertEquals("java.lang.Runnable", boundingType.toString());
		assertEquals(aPozole.getFactory().Type().createReference(Runnable.class), boundingType);
	}

	@Test
	public void testIntersectionTypeOnTopLevelType() throws Exception {
		final CtType<Mole> aMole = buildClass(Mole.class);

		assertEquals(1, aMole.getFormalCtTypeParameters().size());

		// New type parameter declaration.
		final CtTypeParameter typeParameter = aMole.getFormalCtTypeParameters().get(0);
		assertIntersectionTypeForMole(aMole, typeParameter.getSuperclass());
	}

	private void assertIntersectionTypeForMole(CtType<Mole> aMole, CtTypeReference<?> boundingType) {
		assertNotNull(boundingType);
		assertTrue(boundingType instanceof CtIntersectionTypeReference);
		assertEquals(2, boundingType.asCtIntersectionTypeReference().getBounds().size());
		assertEquals(Number.class, boundingType.asCtIntersectionTypeReference().getBounds().stream().collect(Collectors.toList()).get(0).getActualClass());
		assertEquals(Comparable.class, boundingType.asCtIntersectionTypeReference().getBounds().stream().collect(Collectors.toList()).get(1).getActualClass());
		assertEquals("public class Mole<NUMBER extends java.lang.Number & java.lang.Comparable<NUMBER>> {}", aMole.toString());
	}

	@Test
	public void testUnboxingTypeReference() throws Exception {
		// contract: When you call CtTypeReference#unbox on a class which doesn't exist
		// in the spoon path, the method return the type reference itself.
		final Factory factory = createFactory();
		final CtTypeReference<Object> aReference = factory.Type().createReference("fr.inria.Spoon");
		try {
			final CtTypeReference<?> unbox = aReference.unbox();
			assertEquals(aReference, unbox);
		} catch (SpoonClassNotFoundException e) {
			fail("Should never throw a SpoonClassNotFoundException.");
		}
	}

	@Test
	public void testDeclarationCreatedByFactory() throws Exception {
		final Factory factory = createFactory();
		assertNotNull(factory.Interface().create("fr.inria.ITest").getReference().getDeclaration());
		assertNotNull(factory.Enum().create("fr.inria.ETest").getReference().getDeclaration());
	}

	@Test
	public void testPolyTypBindingInTernaryExpression() throws Exception {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/noclasspath/ternary-bug");
		launcher.getEnvironment().setNoClasspath(true);
		launcher.buildModel();

		CtType<Object> aType = launcher.getFactory().Type().get("de.uni_bremen.st.quide.persistence.transformators.IssueTransformator");
		CtConstructorCall ctConstructorCall = aType.getElements(new TypeFilter<CtConstructorCall>(CtConstructorCall.class) {
			@Override
			public boolean matches(CtConstructorCall element) {
				return "TOIssue".equals(element.getExecutable().getType().getSimpleName()) && super.matches(element);
			}
		}).get(0);
		assertEquals(launcher.getFactory().Type().objectType(), ctConstructorCall.getExecutable().getParameters().get(9));
	}

	@Test
	public void testShadowType() throws Exception {

		/* Objects and factory have to be the sames */

		Launcher launcher = new Launcher();
		launcher.buildModel();

		final CtClass<Object> objectCtClass = launcher.getFactory().Class().get(Object.class);
		final CtClass<Object> objectCtClass1 = launcher.getFactory().Class().get(Object.class);

		assertSame(objectCtClass, objectCtClass1);

		assertSame(launcher.getFactory().Class(), objectCtClass.getFactory().Class());
		assertSame(launcher.getFactory(), objectCtClass.getFactory());

		assertSame(launcher.getFactory().Class(), objectCtClass1.getFactory().Class());
		assertSame(launcher.getFactory(), objectCtClass1.getFactory());

		assertSame(objectCtClass.getFactory().Class().get(objectCtClass.getActualClass()), objectCtClass);
		assertSame(objectCtClass.getFactory().Class().get(Object.class), objectCtClass);

		assertSame(objectCtClass1.getFactory().Class().get(objectCtClass1.getActualClass()), objectCtClass1);
		assertSame(objectCtClass1.getFactory().Class().get(Object.class), objectCtClass1);

		assertTrue(objectCtClass.isShadow());
		assertEquals("java.lang.Object", objectCtClass.getQualifiedName());

		final CtType<Object> objectCtType = launcher.getFactory().Type().get(Object.class);
		final CtType<Object> objectCtType1 = launcher.getFactory().Type().get(Object.class);

		assertSame(objectCtType, objectCtType1);

		assertSame(launcher.getFactory().Type(), objectCtType.getFactory().Type());
		assertSame(launcher.getFactory(), objectCtType.getFactory());

		assertSame(launcher.getFactory().Type(), objectCtType1.getFactory().Type());
		assertSame(launcher.getFactory(), objectCtType1.getFactory());

		assertSame(objectCtType.getFactory().Type().get(objectCtType.getActualClass()), objectCtType);
		assertSame(objectCtType.getFactory().Type().get(Object.class), objectCtType);

		assertSame(objectCtType1.getFactory().Type().get(objectCtType1.getActualClass()), objectCtType1);
		assertSame(objectCtType1.getFactory().Type().get(Object.class), objectCtType1);

		assertTrue(objectCtClass.isShadow());
		assertEquals("java.lang.Object", objectCtClass.getQualifiedName());

		final List<String> methodNameList = Arrays.asList(Object.class.getDeclaredMethods()).stream().map(Method::getName).collect(Collectors.toList());

		for (CtMethod<?> ctMethod : objectCtClass.getMethods()) {
			assertTrue(methodNameList.contains(ctMethod.getSimpleName()));
			assertTrue(ctMethod.getBody().getStatements().isEmpty());
		}

	}

	private void checkIsSomething(String expectedType, CtType type) {
		_checkIsSomething(expectedType, type);
		_checkIsSomething(expectedType, type.getReference());
	}
	private void _checkIsSomething(String expectedType, CtTypeInformation type) {
		assertEquals("interface".equals(expectedType), type.isInterface());
		assertEquals("class".equals(expectedType), type.isClass());
		assertEquals("annotation".equals(expectedType), type.isAnnotationType());
		assertEquals("anonymous".equals(expectedType), type.isAnonymous());
		assertEquals("enum".equals(expectedType), type.isEnum());
		assertEquals("generics".equals(expectedType), type.isGenerics());
	}
}

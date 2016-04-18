package spoon.test.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static spoon.testing.utils.ModelUtils.build;
import static spoon.testing.utils.ModelUtils.buildClass;

import org.junit.Test;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.processing.AbstractProcessor;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.filter.AbstractFilter;
import spoon.reflect.visitor.filter.NameFilter;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.reflect.declaration.CtMethodImpl;
import spoon.test.factory.testclasses.Foo;

public class FactoryTest {

	@Test
	public void testClone() throws Exception {
		CtClass<?> type = build("spoon.test", "SampleClass");
		CtMethod<?> m = type.getMethodsByName("method3").get(0);
		int i = m.getBody().getStatements().size();

		m = m.getFactory().Core().clone(m);

		assertEquals(i, m.getBody().getStatements().size());
		// cloned elements must not have an initialized parent
		assertFalse(m.isParentInitialized());
	}

	@Test
	public void testFactoryOverriding()  throws Exception {

		@SuppressWarnings("serial")
		class MyCtMethod<T> extends CtMethodImpl<T>{};

		@SuppressWarnings("serial")
		final CoreFactory specialCoreFactory = new DefaultCoreFactory() {
			@Override
			public <T> CtMethod<T> createMethod() {
				return new MyCtMethod<T>();
			}
		};

		Launcher launcher = new Launcher() {
			@Override
			public Factory createFactory() {
				return new FactoryImpl(specialCoreFactory, new StandardEnvironment());
			}
		};

		CtClass<?> type = build("spoon.test", "SampleClass", launcher.getFactory());

		CtMethod<?> m = type.getMethodsByName("method3").get(0);

		assertTrue(m instanceof MyCtMethod);
	}

	@Test
	public void testClassAccessCreatedFromFactories() throws Exception {
		final CtType<Foo> foo = buildClass(Foo.class);

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(0, foo.getAnnotations().get(0).getElementValues().size());

		foo.getFactory().Annotation().annotate(foo, Foo.Bar.class, "clazz", Foo.class);

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(1, foo.getAnnotations().get(0).getElementValues().size());
		assertTrue(foo.getAnnotations().get(0).getElementValues().get("clazz") instanceof CtFieldRead);
		assertEquals("spoon.test.factory.testclasses.Foo.class", foo.getAnnotations().get(0).getElementValues().get("clazz").toString());

		foo.getFactory().Annotation().annotate(foo, Foo.Bar.class, "classes", new Class[] { Foo.class });

		assertEquals(1, foo.getAnnotations().size());
		assertEquals(2, foo.getAnnotations().get(0).getElementValues().size());
		assertTrue(foo.getAnnotations().get(0).getElementValues().get("classes") instanceof CtNewArray);
		assertEquals(1, ((CtNewArray) foo.getAnnotations().get(0).getElementValues().get("classes")).getElements().size());
		assertEquals("spoon.test.factory.testclasses.Foo.class", ((CtNewArray) foo.getAnnotations().get(0).getElementValues().get("classes")).getElements().get(0).toString());
	}
	
	@Test
	public void testCtModel() throws Exception {
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/test/java/spoon/test/factory/testclasses");
		spoon.buildModel();
		
		CtModel model = spoon.getModel();
		
		// contains Foo and Foo.@Bar
		assertEquals(1, model.getAllTypes().size());
		
		// [, spoon, spoon.test, spoon.test.factory, spoon.test.factory.testclasses]
		assertEquals(5, model.getAllPackages().size());
		
		// add to itself is fine
		model.getRootPackage().addPackage(model.getRootPackage());
		assertEquals(1, model.getAllTypes().size());
		assertEquals(5, model.getAllPackages().size());

		model.getRootPackage().getPackage("spoon").addPackage(model.getRootPackage().getPackage("spoon"));
		assertEquals(1, model.getAllTypes().size());
		assertEquals(5, model.getAllPackages().size());

		model.getRootPackage().addPackage(model.getRootPackage().getPackage("spoon"));
		assertEquals(1, model.getAllTypes().size());
		assertEquals(5, model.getAllPackages().size());

		
		CtPackage p = spoon.getFactory().Core().clone(model.getRootPackage().getElements(new NameFilter<CtPackage>("spoon")).get(0));
		// if we change the implem, merge is impossible
		CtField f = spoon.getFactory().Core().createField();
		f.setSimpleName("foo");
		f.setType(spoon.getFactory().Type().BYTE);
		p.getElements(new NameFilter<CtPackage>("testclasses")).get(0).getType("Foo").addField(f);
		try {
			model.getRootPackage().addPackage(p);
			fail("no exception thrown");
		} catch (IllegalStateException success) {}
	}

	@Test
	public void testIncrementalModel() throws Exception {

		// Feed some inputResources to a spoon compiler
		SpoonAPI spoon = new Launcher();
		spoon.addInputResource("src/test/java/spoon/test/factory/testclasses");

		// Build model
		spoon.buildModel();
		assertEquals(1, spoon.getModel().getAllTypes().size());
		
		// Do something with that model..
		CtModel model = spoon.getModel();
		model.processWith(new AbstractProcessor<CtMethod>() {
			@Override
			public void process(CtMethod element) {
				element.setDefaultMethod(false);
			}			
		});

		// Feed some new inputResources 
		SpoonAPI spoon2 = new Launcher();
		spoon2.addInputResource("src/test/java/spoon/test/factory/testclasses2");
		
		// Build models of newly added classes/packages 
		spoon2.buildModel();
		assertEquals(1, spoon2.getModel().getAllTypes().size());

		// attach them to the existing model.
		model.getRootPackage().addPackage(spoon2.getModel().getRootPackage());

		// checking the results
		assertEquals(6, model.getAllPackages().size());		
		assertEquals(2, model.getAllTypes().size());
		assertEquals(1, model.getRootPackage().getElements(new AbstractFilter<CtPackage>() {
			@Override
			public boolean matches(CtPackage element) {
				return "spoon.test.factory.testclasses2".equals(element.getQualifiedName());
			}
		}).size());
	}

}

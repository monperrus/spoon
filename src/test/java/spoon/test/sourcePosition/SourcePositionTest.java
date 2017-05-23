package spoon.test.sourcePosition;

import org.junit.Test;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtTypeMember;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.Filter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.test.SampleClass;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static spoon.testing.utils.ModelUtils.build;

public class SourcePositionTest {

	@Test
	public void equalPositionsHaveSameHashcode() throws Exception {
		String packageName = "spoon.test";
		String sampleClassName = "SampleClass";
		String qualifiedName = packageName + "." + sampleClassName;

		Filter<CtMethod<?>> methodFilter = new TypeFilter<CtMethod<?>>(CtMethod.class);

		Factory aFactory = factoryFor(packageName, sampleClassName);
		List<CtMethod<?>> methods = aFactory.Class().get(qualifiedName).getElements(methodFilter);

		Factory newInstanceOfSameFactory = factoryFor(packageName, sampleClassName);
		List<CtMethod<?>> newInstanceOfSameMethods = newInstanceOfSameFactory.Class().get(qualifiedName).getElements(methodFilter);

		assertEquals(methods.size(), newInstanceOfSameMethods.size());
		for (int i = 0; i < methods.size(); i += 1) {
			SourcePosition aPosition = methods.get(i).getPosition();
			SourcePosition newInstanceOfSamePosition = newInstanceOfSameMethods.get(i).getPosition();
			assertTrue(aPosition.equals(newInstanceOfSamePosition));
			assertEquals(aPosition.hashCode(), newInstanceOfSamePosition.hashCode());
		}
	}

	private Factory factoryFor(String packageName, String className) throws Exception {
		return build(packageName, className).getFactory();
	}

	@Test
	public void testGetColumn() throws Exception {
		// contract: source position can compute the column
		String packageName = "spoon.test";
		String sampleClassName = "SampleClass";
		Factory aFactory = factoryFor(packageName, sampleClassName);


		CtType<Object> klass = aFactory.Type().get(SampleClass.class);
		assertEquals(1, klass.getPosition().getColumn());

		List<CtTypeMember> elements = klass.getTypeMembers();

		CtElement el = elements.get(0); // the first constructor
		assertTrue(el instanceof CtConstructor);
		assertEquals(el.toString(), 2, el.getPosition().getColumn());

		CtMethod m = (CtMethod) elements.get(4); // the second method
		assertEquals(el.toString(), 2, m.getPosition().getColumn());

		// now the first statement
		assertEquals(el.toString(), 3, m.getBody().getStatement(0).getPosition().getColumn());

	}
}
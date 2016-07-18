package spoon.test.prettyprinter;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.SniperJavaPrettyPrinter;
import spoon.test.prettyprinter.testclasses.AClass;

import java.util.Arrays;

public class SniperTest {

	public Launcher createSpoon() throws Exception {
	    Launcher spoon = new Launcher();
		spoon.addInputResource("./src/test/java/spoon/test/prettyprinter/");
		spoon.getEnvironment().setBuildStackChanges(true);
		spoon.buildModel();
		return spoon;
	}

	@Test
	public void testPrettyPrinter() throws Exception {
		Launcher spoon = createSpoon();
		Factory factory = spoon.getFactory();
		CtClass<AClass> aClass = factory.Class().get(AClass.class);

		CtMethod method = factory.Core().createMethod();
		method.setSimpleName("m");
		method.setType(factory.Type().VOID_PRIMITIVE);
		method.addModifier(ModifierKind.PUBLIC);
		method.setBody(factory.Core().createBlock());

		aClass.addMethod(method);

		aClass.setSimpleName("Blabla");

		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		System.out.println(sniper.getResult());
	}

}

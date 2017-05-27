package spoon.test.prettyprinter;

import org.junit.Test;
import spoon.Launcher;
import spoon.refactoring.Refactoring;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.printer.sniper.SniperJavaPrettyPrinter;
import spoon.reflect.visitor.filter.TypeFilter;
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
		aClass.removeModifier(ModifierKind.PUBLIC);
		aClass.addModifier(ModifierKind.PRIVATE);
		aClass.addComment(factory.createInlineComment("blabla"));

		CtLocalVariable param = aClass.getMethodsByName("param").get(0).getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class)).get(0);
		Refactoring.changeLocalVariableName(param, "g");

		aClass.getMethod("aMethod").getBody().addStatement(aClass.getFactory().Code().createCodeSnippetStatement("System.out.println(\"test\")"));

		CtStatement statement = aClass.getMethod("aMethodWithGeneric").getBody().getStatement(0);
		statement.replace(aClass.getFactory().Code().createCodeSnippetStatement("System.out.println(\"test\")"));

		aClass.getMethod("aMethodWithGeneric").getBody().addStatement(statement);

		//aClass.getMethod("aMethodWithGeneric").getBody().removeStatement(aClass.getMethod("aMethodWithGeneric").getBody().getStatement(0));
		SniperJavaPrettyPrinter sniper = new SniperJavaPrettyPrinter(spoon.getEnvironment());
		sniper.calculate(aClass.getPosition().getCompilationUnit(), Arrays.asList(aClass));
		System.out.println(sniper.getResult());
	}

}

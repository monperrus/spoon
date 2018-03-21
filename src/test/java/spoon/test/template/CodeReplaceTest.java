package spoon.test.template;

import org.junit.Test;

import spoon.Launcher;
import spoon.OutputType;
import spoon.SpoonModelBuilder;
import spoon.pattern.ParametersBuilder;
import spoon.pattern.Pattern;
import spoon.pattern.PatternBuilder;
import spoon.pattern.TemplateModelBuilder;
import spoon.pattern.matcher.Match;
import spoon.pattern.parameter.ParameterValueProvider;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.meta.ContainerKind;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;
import spoon.test.template.testclasses.replace.DPPSample1;
import spoon.test.template.testclasses.replace.NewPattern;
import spoon.test.template.testclasses.replace.OldPattern;
import spoon.testing.utils.ModelUtils;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

public class CodeReplaceTest {


	@Test
	public void testMatchSample1() throws Exception {
		// contract: a complex pattern is well matched twice
		Factory f = ModelUtils.build(
				new File("./src/test/java/spoon/test/template/testclasses/replace/DPPSample1.java"),
				new File("./src/test/java/spoon/test/template/testclasses/replace")
			);
		CtClass<?> classDJPP = f.Class().get(DPPSample1.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());

		CtType<Object> type = f.Type().get(OldPattern.class);
		Pattern p = PatternBuilder
				//Create a pattern from all statements of OldPattern_ParamsInNestedType#patternModel
				.create(type, new TemplateModelBuilder(type).setBodyOfMethod("patternModel").getTemplateModels())
				.configureParameters((ParametersBuilder pb) -> pb
						// creating patterns parameters for all references to "params" and "items"
						.createPatternParameterForVariable("params", "item")
						.parameter("statements").setContainerKind(ContainerKind.LIST)
				)
				.createPatternParameters()
				.configureInlineStatements(ls -> ls.byVariableName("useStartKeyword"))
				.build();

		// so let's try to match this complex pattern  on DJPP
		List<Match> matches = p.getMatches(classDJPP);

		// there are two results (the try-with-resource in each method
		assertEquals(2, matches.size());
		ParameterValueProvider params = matches.get(0).getParameters();
		assertEquals("\"extends\"", params.getValue("startKeyword").toString());
		assertEquals(Boolean.TRUE, params.getValue("useStartKeyword"));

		params = matches.get(1).getParameters();
		// all method arguments to createListPrinter have been matched
		assertEquals("false", params.getValue("startPrefixSpace").toString());
		assertEquals("null", params.getValue("start").toString());
		assertEquals("false", params.getValue("startSuffixSpace").toString());
		assertEquals("false", params.getValue("nextPrefixSpace").toString());
		assertEquals("\",\"", params.getValue("next").toString());
		assertEquals("true", params.getValue("nextSuffixSpace").toString());
		assertEquals("false", params.getValue("endPrefixSpace").toString());
		assertEquals("\";\"", params.getValue("end").toString());
		assertEquals("ctEnum.getEnumValues()", params.getValue("getIterable").toString());
		assertEquals("[scan(enumValue)]", params.getValue("statements").toString());
	}

	@Test
	public void testTemplateReplace() throws Exception {
		Launcher launcher = new Launcher();
		final Factory factory = launcher.getFactory();
		factory.getEnvironment().setComplianceLevel(8);
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(true);
		factory.getEnvironment().setAutoImports(true);
		final SpoonModelBuilder compiler = launcher.createCompiler(factory);
		compiler.addInputSource(new File("./src/main/java/spoon/reflect/visitor"));
		compiler.addInputSource(new File("./src/test/java/spoon/test/template/testclasses/replace"));
		compiler.build();
		CtClass<?> classDJPP = factory.Class().get(DefaultJavaPrettyPrinter.class);
		assertNotNull(classDJPP);
		assertFalse(classDJPP.isShadow());
		NewPattern.replaceOldByNew(classDJPP);

		launcher.setSourceOutputDirectory(new File("./target/spooned-template-replace/"));
		launcher.getModelBuilder().generateProcessedSourceFiles(OutputType.CLASSES);
	}

}

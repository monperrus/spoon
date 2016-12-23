package spoon.test.template.gui;

import org.junit.Test;
import spoon.Launcher;
import spoon.support.gui.SpoonModelTree;

import static spoon.test.limits.StaticFieldAccesOnInstance.test;

public class GuiTest {
	@Test
	public void test() {
		Launcher spoon = new Launcher();
		spoon.addInputResource("src/test/java/spoon/test/gui/");
		spoon.buildModel();
		SpoonModelTree gui = new SpoonModelTree(spoon.getFactory());
		gui.getMenu();
		// we only check the absence of crash
	}
}

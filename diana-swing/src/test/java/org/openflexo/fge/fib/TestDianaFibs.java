package org.openflexo.fge.fib;

import java.io.File;

import org.junit.Test;
import org.openflexo.fib.utils.GenericFIBTestCase;

public class TestDianaFibs extends GenericFIBTestCase {

	public static void main(String[] args) {
		System.out.println(generateFIBTestCaseClass(new File(System.getProperty("user.dir") + "/src/main/resources/Fib"), "Fib/"));
	}

	@Test
	public void testBackgroundStylePanel() {
		validateFIB("Fib/BackgroundStylePanel.fib");
	}

	@Test
	public void testForegroundStylePanel() {
		validateFIB("Fib/ForegroundStylePanel.fib");
	}

	@Test
	public void testShadowStylePanel() {
		validateFIB("Fib/ShadowStylePanel.fib");
	}

	@Test
	public void testShapeSelectorPanel() {
		validateFIB("Fib/ShapeSelectorPanel.fib");
	}

	@Test
	public void testTextStylePanel() {
		validateFIB("Fib/TextStylePanel.fib");
	}
}

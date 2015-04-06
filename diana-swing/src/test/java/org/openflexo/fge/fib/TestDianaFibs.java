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
	public void testBackgroundStyleSelector() {
		validateFIB("Fib/BackgroundStyleSelector.fib");
	}

	@Test
	public void testConnectorSelectorPanel() {
		validateFIB("Fib/ConnectorSelectorPanel.fib");
	}

	@Test
	public void testForegroundStylePanel() {
		validateFIB("Fib/ForegroundStylePanel.fib");
	}

	@Test
	public void testForegroundStyleSelector() {
		validateFIB("Fib/ForegroundStyleSelector.fib");
	}

	@Test
	public void testLocationSizePanel() {
		validateFIB("Fib/LocationSizePanel.fib");
	}

	@Test
	public void testShadowStylePanel() {
		validateFIB("Fib/ShadowStylePanel.fib");
	}

	@Test
	public void testShadowStyleSelector() {
		validateFIB("Fib/ShadowStyleSelector.fib");
	}

	@Test
	public void testShapeSelectorPanel() {
		validateFIB("Fib/ShapeSelectorPanel.fib");
	}

	@Test
	public void testTextStylePanel() {
		validateFIB("Fib/TextStylePanel.fib");
	}

	@Test
	public void testTextStyleSelector() {
		validateFIB("Fib/TextStyleSelector.fib");
	}
}

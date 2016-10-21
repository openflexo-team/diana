/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
 * developed at Openflexo.
 * 
 * 
 * Openflexo is dual-licensed under the European Union Public License (EUPL, either 
 * version 1.1 of the License, or any later version ), which is available at 
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 * and the GNU General Public License (GPL, either version 3 of the License, or any 
 * later version), which is available at http://www.gnu.org/licenses/gpl.html .
 * 
 * You can redistribute it and/or modify under the terms of either of these licenses
 * 
 * If you choose to redistribute it and/or modify under the terms of the GNU GPL, you
 * must include the following additional permission.
 *
 *          Additional permission under GNU GPL version 3 section 7
 *
 *          If you modify this Program, or any covered work, by linking or 
 *          combining it with software containing parts covered by the terms 
 *          of EPL 1.0, the licensors of this Program grant you additional permission
 *          to convey the resulting work. * 
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. 
 *
 * See http://www.openflexo.org/license.html for details.
 * 
 * 
 * Please contact Openflexo (openflexo-contacts@openflexo.org)
 * or visit www.openflexo.org if you need additional information.
 * 
 */

package org.openflexo.fge.fib;

import java.io.File;

import org.junit.Test;
import org.openflexo.gina.utils.GenericFIBTestCase;

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
	public void testLayoutManagersPanel() {
		validateFIB("Fib/LayoutManagersPanel.fib");
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
	public void testTextPropertiesPanel() {
		validateFIB("Fib/TextPropertiesPanel.fib");
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

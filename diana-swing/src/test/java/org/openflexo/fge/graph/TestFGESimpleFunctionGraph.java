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

package org.openflexo.fge.graph;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.test.OrderedRunner;
import org.openflexo.test.TestOrder;

@RunWith(OrderedRunner.class)
public class TestFGESimpleFunctionGraph extends AbstractTestFGEGraph {

	private static final Logger logger = FlexoLogger.getLogger(TestFGESimpleFunctionGraph.class.getPackage().getName());

	@Test
	@TestOrder(1)
	public void testContinuousFunctionGraphDrawing() {

		JPanel example1Panel = new JPanel(new BorderLayout());

		final ExampleFGEContinuousSimpleFunctionGraphDrawing d = new ExampleFGEContinuousSimpleFunctionGraphDrawing(new Object(), factory);
		final TestDrawingController dc = new TestDrawingController(d);
		example1Panel.add(new JScrollPane(dc.getDrawingView()), BorderLayout.CENTER);
		example1Panel.add(dc.scaleSelector.getComponent(), BorderLayout.NORTH);

		gcDelegate.addTab("Continuous function", example1Panel);
	}

	@Test
	@TestOrder(2)
	public void testDiscreteFunctionGraphDrawing() {

		JPanel example2Panel = new JPanel(new BorderLayout());

		final ExampleFGEDiscreteSimpleFunctionGraphDrawing d2 = new ExampleFGEDiscreteSimpleFunctionGraphDrawing(new Object(), factory);
		final TestDrawingController dc2 = new TestDrawingController(d2);
		example2Panel.add(new JScrollPane(dc2.getDrawingView()), BorderLayout.CENTER);
		example2Panel.add(dc2.scaleSelector.getComponent(), BorderLayout.NORTH);

		gcDelegate.addTab("Discrete function", example2Panel);
	}

}

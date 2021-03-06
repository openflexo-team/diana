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

package org.openflexo.diana.graph;

import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.test.UITest;

public class TestDianaPolarFunctionGraph extends AbstractTestDianaGraph {

	private static final Logger logger = FlexoLogger.getLogger(TestDianaPolarFunctionGraph.class.getPackage().getName());

	@Test
	@Category(UITest.class)
	public void testContinuousPolarFunctionGraphDrawing() {

		JPanel panel = new JPanel(new BorderLayout());

		final ExampleDianaContinuousPolarFunctionGraphDrawing drawing = new ExampleDianaContinuousPolarFunctionGraphDrawing(new Object(),
				factory);
		final TestDrawingController controller = new TestDrawingController(drawing);
		panel.add(new JScrollPane(controller.getDrawingView()), BorderLayout.CENTER);
		panel.add(controller.scaleSelector.getComponent(), BorderLayout.NORTH);

		gcDelegate.addTab("Continuous function", panel);
	}

	@Test
	@Category(UITest.class)
	public void testDiscretePolarFunctionGraphDrawing() {

		JPanel panel = new JPanel(new BorderLayout());

		final ExampleDianaDiscretePolarFunctionGraphDrawing drawing = new ExampleDianaDiscretePolarFunctionGraphDrawing(new Object(),
				factory);
		final TestDrawingController controller = new TestDrawingController(drawing);
		panel.add(new JScrollPane(controller.getDrawingView()), BorderLayout.CENTER);
		panel.add(controller.scaleSelector.getComponent(), BorderLayout.NORTH);

		gcDelegate.addTab("Discrete function", panel);
	}

}

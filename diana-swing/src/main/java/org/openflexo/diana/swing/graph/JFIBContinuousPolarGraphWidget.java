/**
 * 
 * Copyright (c) 2013-2016, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Gina-core, a component of the software infrastructure 
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

package org.openflexo.diana.swing.graph;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.diana.graph.FGEContinuousPolarFunctionGraph;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBContinuousPolarFunctionGraph;

/**
 * Swing implementation for a {@link FIBContinuousPolarFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public class JFIBContinuousPolarGraphWidget extends JFIBPolarGraphWidget<FIBContinuousPolarFunctionGraph> {

	private static final Logger logger = Logger.getLogger(JFIBContinuousPolarGraphWidget.class.getPackage().getName());

	public JFIBContinuousPolarGraphWidget(FIBContinuousPolarFunctionGraph model, FIBController controller) {
		super(model, controller);
	}

	@Override
	protected FGEContinuousPolarFunctionGraphDrawing makeGraphDrawing() {
		return new FGEContinuousPolarFunctionGraphDrawing(getWidget());
	}

	public class FGEContinuousPolarFunctionGraphDrawing
			extends FGEPolarFunctionGraphDrawing<FIBContinuousPolarFunctionGraph, FGEContinuousPolarFunctionGraph> {

		public FGEContinuousPolarFunctionGraphDrawing(FIBContinuousPolarFunctionGraph fibGraph) {
			super(fibGraph, JFIBContinuousPolarGraphWidget.this);
		}

		@Override
		protected FGEContinuousPolarFunctionGraph makeGraph(FIBContinuousPolarFunctionGraph fibGraph) {

			// System.out.println("Type=" + TypeUtils.getBaseClass(fibGraph.getParameterType()));

			// Create the FGEGraph
			graph = new FGEContinuousPolarFunctionGraph();
			graph.setBindingFactory(fibGraph.getBindingFactory());

			// Sets borders
			graph.setBorderTop(fibGraph.getBorderTop());
			graph.setBorderBottom(fibGraph.getBorderBottom());
			graph.setBorderLeft(fibGraph.getBorderLeft());
			graph.setBorderRight(fibGraph.getBorderRight());

			// Set parameter name and type
			// System.out.println("Parameter " + fibGraph.getParameterName() + " type=" + fibGraph.getParameterType());
			graph.setParameter(fibGraph.getParameterName(), fibGraph.getParameterType());

			graph.setDisplayAngleTicks(fibGraph.getDisplayAngleTicks());
			graph.setDisplayReferenceMarks(fibGraph.getDisplayReferenceMarks());
			graph.setDisplayLabels(fibGraph.getDisplayLabels());
			graph.setDisplayGrid(fibGraph.getDisplayGrid());

			// Sets step number
			int stepsNumber = FIBContinuousPolarFunctionGraph.DEFAULT_STEPS_NUMBER;
			if (fibGraph.getStepsNumber() != null && fibGraph.getStepsNumber().isSet() && fibGraph.getStepsNumber().isValid()) {
				try {
					stepsNumber = fibGraph.getStepsNumber().getBindingValue(JFIBContinuousPolarGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("stepsNumber=" + stepsNumber);
			graph.setStepsNumber(stepsNumber);

			// Sets angle tick spacing
			Double angleTickSpacing = FIBContinuousPolarFunctionGraph.DEFAULT_ANGLE_TICK_SPACING;
			if (fibGraph.getAngleTickSpacing() != null && fibGraph.getAngleTickSpacing().isSet()
					&& fibGraph.getAngleTickSpacing().isValid()) {
				try {
					angleTickSpacing = fibGraph.getAngleTickSpacing().getBindingValue(JFIBContinuousPolarGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("majorTickSpacing=" + majorTickSpacing);
			graph.setAngleTickSpacing(angleTickSpacing);

			appendFunctions(fibGraph, graph, getController());

			return graph;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(FIBContinuousPolarFunctionGraph.ANGLE_TICK_SPACING_KEY)
					|| evt.getPropertyName().equals(FIBContinuousPolarFunctionGraph.STEPS_NUMBER_KEY)
					|| evt.getPropertyName().equals(FIBContinuousPolarFunctionGraph.DISPLAY_ANGLE_TICKS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousPolarFunctionGraph.DISPLAY_REFERENCE_MARKS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousPolarFunctionGraph.DISPLAY_LABELS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousPolarFunctionGraph.DISPLAY_GRID_KEY)) {
				System.out.println("---------------> On reconstruit le graphe entierement a cause de " + evt.getPropertyName());
				updateGraph();
			}
		}

	}

}

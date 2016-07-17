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

package org.openflexo.fge.swing.graph;

import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.connie.type.TypeUtils;
import org.openflexo.fge.graph.FGEContinuousSimpleFunctionGraph;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;

/**
 * Swing implementation for a {@link FIBContinuousSimpleFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public class JFIBContinuousSimpleGraphWidget extends JFIBSimpleGraphWidget<FIBContinuousSimpleFunctionGraph> {

	private static final Logger logger = Logger.getLogger(JFIBContinuousSimpleGraphWidget.class.getPackage().getName());

	public JFIBContinuousSimpleGraphWidget(FIBContinuousSimpleFunctionGraph model, FIBController controller) {
		super(model, controller);
	}

	@Override
	protected FGEContinuousSimpleFunctionGraphDrawing makeGraphDrawing() {
		return new FGEContinuousSimpleFunctionGraphDrawing(getWidget());
	}

	public class FGEContinuousSimpleFunctionGraphDrawing extends FGESimpleFunctionGraphDrawing<FIBContinuousSimpleFunctionGraph> {

		public FGEContinuousSimpleFunctionGraphDrawing(FIBContinuousSimpleFunctionGraph fibGraph) {
			super(fibGraph, JFIBContinuousSimpleGraphWidget.this);
		}

		@Override
		protected FGEContinuousSimpleFunctionGraph<?> makeGraph(FIBContinuousSimpleFunctionGraph fibGraph) {

			// System.out.println("Type=" + TypeUtils.getBaseClass(fibGraph.getParameterType()));

			// Create the FGEGraph
			FGEContinuousSimpleFunctionGraph<Number> returned = new FGEContinuousSimpleFunctionGraph<Number>(
					(Class) TypeUtils.getBaseClass(fibGraph.getParameterType()));
			returned.setBindingFactory(fibGraph.getBindingFactory());

			// Sets borders
			returned.setBorderTop(fibGraph.getBorderTop());
			returned.setBorderBottom(fibGraph.getBorderBottom());
			returned.setBorderLeft(fibGraph.getBorderLeft());
			returned.setBorderRight(fibGraph.getBorderRight());

			// Set parameter name and type
			// System.out.println("Parameter " + fibGraph.getParameterName() + " type=" + fibGraph.getParameterType());
			returned.setParameter(fibGraph.getParameterName(), fibGraph.getParameterType());

			returned.setDisplayMajorTicks(fibGraph.getDisplayMajorTicks());
			returned.setDisplayMinorTicks(fibGraph.getDisplayMinorTicks());
			returned.setDisplayReferenceMarks(fibGraph.getDisplayReferenceMarks());
			returned.setDisplayLabels(fibGraph.getDisplayLabels());
			returned.setDisplayGrid(fibGraph.getDisplayGrid());

			// Sets parameter range
			Number minValue = FIBContinuousSimpleFunctionGraph.DEFAULT_MIN_VALUE;
			Number maxValue = FIBContinuousSimpleFunctionGraph.DEFAULT_MAX_VALUE;
			if (fibGraph.getMinValue() != null && fibGraph.getMinValue().isSet() && fibGraph.getMinValue().isValid()) {
				try {
					minValue = fibGraph.getMinValue().getBindingValue(JFIBContinuousSimpleGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fibGraph.getMaxValue() != null && fibGraph.getMaxValue().isSet() && fibGraph.getMaxValue().isValid()) {
				try {
					maxValue = fibGraph.getMaxValue().getBindingValue(JFIBContinuousSimpleGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("minValue=" + minValue + " maxValue=" + maxValue);
			returned.setParameterRange(minValue, maxValue);

			// Sets step number
			int stepsNumber = FIBContinuousSimpleFunctionGraph.DEFAULT_STEPS_NUMBER;
			if (fibGraph.getStepsNumber() != null && fibGraph.getStepsNumber().isSet() && fibGraph.getStepsNumber().isValid()) {
				try {
					stepsNumber = fibGraph.getStepsNumber().getBindingValue(JFIBContinuousSimpleGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("stepsNumber=" + stepsNumber);
			returned.setStepsNumber(stepsNumber);

			// Sets major tick spacing
			Number majorTickSpacing = FIBContinuousSimpleFunctionGraph.DEFAULT_MAJOR_TICK_SPACING;
			if (fibGraph.getMajorTickSpacing() != null && fibGraph.getMajorTickSpacing().isSet()
					&& fibGraph.getMajorTickSpacing().isValid()) {
				try {
					majorTickSpacing = fibGraph.getMajorTickSpacing().getBindingValue(JFIBContinuousSimpleGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("majorTickSpacing=" + majorTickSpacing);
			returned.setParameterMajorTickSpacing(majorTickSpacing);

			// Sets minor tick spacing
			Number minorTickSpacing = FIBContinuousSimpleFunctionGraph.DEFAULT_MINOR_TICK_SPACING;
			if (fibGraph.getMinorTickSpacing() != null && fibGraph.getMinorTickSpacing().isSet()
					&& fibGraph.getMinorTickSpacing().isValid()) {
				try {
					minorTickSpacing = fibGraph.getMinorTickSpacing().getBindingValue(JFIBContinuousSimpleGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("minorTickSpacing=" + minorTickSpacing);
			returned.setParameterMinorTickSpacing(minorTickSpacing);

			appendFunctions(fibGraph, returned, getController());

			return returned;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.MIN_VALUE_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.MAX_VALUE_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.MAJOR_TICK_SPACING_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.MINOR_TICK_SPACING_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.STEPS_NUMBER_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.DISPLAY_MAJOR_TICKS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.DISPLAY_MINOR_TICKS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.DISPLAY_REFERENCE_MARKS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.DISPLAY_LABELS_KEY)
					|| evt.getPropertyName().equals(FIBContinuousSimpleFunctionGraph.DISPLAY_GRID_KEY)) {
				System.out.println("---------------> On reconstruit le graphe entierement a cause de " + evt.getPropertyName());
				updateGraph();
			}
		}

	}

}

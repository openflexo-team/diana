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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.graph.FGEDiscreteSimpleFunctionGraph;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBDiscreteSimpleFunctionGraph;
import org.openflexo.gina.model.graph.FIBDiscreteSimpleFunctionGraph;

/**
 * Swing implementation for a {@link FIBDiscreteSimpleFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public class JFIBDiscreteSimpleGraphWidget extends JFIBSimpleGraphWidget<FIBDiscreteSimpleFunctionGraph> {

	private static final Logger logger = Logger.getLogger(JFIBDiscreteSimpleGraphWidget.class.getPackage().getName());

	public JFIBDiscreteSimpleGraphWidget(FIBDiscreteSimpleFunctionGraph model, FIBController controller) {
		super(model, controller);
	}

	@Override
	protected FGEDiscreteSimpleFunctionGraphDrawing makeGraphDrawing() {
		return new FGEDiscreteSimpleFunctionGraphDrawing(getWidget());
	}

	public class FGEDiscreteSimpleFunctionGraphDrawing
			extends FGESimpleFunctionGraphDrawing<FIBDiscreteSimpleFunctionGraph, FGEDiscreteSimpleFunctionGraph<?>> {

		public FGEDiscreteSimpleFunctionGraphDrawing(FIBDiscreteSimpleFunctionGraph fibGraph) {
			super(fibGraph, JFIBDiscreteSimpleGraphWidget.this);
		}

		@Override
		protected FGEDiscreteSimpleFunctionGraph<?> makeGraph(FIBDiscreteSimpleFunctionGraph fibGraph) {

			// Create the FGEGraph
			FGEDiscreteSimpleFunctionGraph<?> returned = new FGEDiscreteSimpleFunctionGraph<Object>();
			returned.setBindingFactory(fibGraph.getBindingFactory());

			// Sets borders
			returned.setBorderTop(fibGraph.getBorderTop());
			returned.setBorderBottom(fibGraph.getBorderBottom());
			returned.setBorderLeft(fibGraph.getBorderLeft());
			returned.setBorderRight(fibGraph.getBorderRight());

			// Set parameter name and type
			// System.out.println("Parameter " + fibGraph.getParameterName() + " type=" + fibGraph.getParameterType());
			returned.setParameter(fibGraph.getParameterName(), fibGraph.getParameterType());

			// Set discrete values
			List<?> values = new ArrayList<>();
			if (fibGraph.getValues() != null && fibGraph.getValues().isSet() && fibGraph.getValues().isValid()) {
				try {
					values = fibGraph.getValues().getBindingValue(JFIBDiscreteSimpleGraphWidget.this);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			// System.out.println("values=" + values);
			returned.setDiscreteValues((List) values);

			// Labels of discrete values
			if (fibGraph.getLabels() != null && fibGraph.getLabels().isSet() && fibGraph.getLabels().isValid()) {
				returned.setDiscreteValuesLabel(fibGraph.getLabels());
			}

			appendFunctions(fibGraph, returned, getController());

			return returned;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(FIBDiscreteSimpleFunctionGraph.VALUES_KEY)
					|| evt.getPropertyName().equals(FIBDiscreteSimpleFunctionGraph.LABELS_KEY)) {
				System.out.println("---------------> On reconstruit le graphe entierement a cause de " + evt.getPropertyName());
				updateGraph();
			}
		}

	}

}

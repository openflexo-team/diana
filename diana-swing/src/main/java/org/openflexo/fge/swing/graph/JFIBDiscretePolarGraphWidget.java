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

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingValueListChangeListener;
import org.openflexo.fge.graph.FGEDiscretePolarFunctionGraph;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBDiscretePolarFunctionGraph;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Swing implementation for a {@link FIBDiscretePolarFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public class JFIBDiscretePolarGraphWidget extends JFIBPolarGraphWidget<FIBDiscretePolarFunctionGraph> {

	private static final Logger logger = Logger.getLogger(JFIBDiscretePolarGraphWidget.class.getPackage().getName());

	private BindingValueListChangeListener<Object, List<Object>> valuesBindingValueChangeListener;

	public JFIBDiscretePolarGraphWidget(FIBDiscretePolarFunctionGraph model, FIBController controller) {
		super(model, controller);
	}

	@Override
	protected void componentBecomesVisible() {
		super.componentBecomesVisible();
		listenDiscreteValuesChange();
		getGraphDrawing().updateGraph();
	}

	@Override
	protected void componentBecomesInvisible() {
		stopListenDiscreteValuesChange();
		super.componentBecomesInvisible();
	}

	private void listenDiscreteValuesChange() {
		if (valuesBindingValueChangeListener != null) {
			valuesBindingValueChangeListener.stopObserving();
			valuesBindingValueChangeListener.delete();
		}
		if (getComponent().getValues() != null && getComponent().getValues().isValid()) {
			valuesBindingValueChangeListener = new BindingValueListChangeListener<Object, List<Object>>(
					((DataBinding) getComponent().getValues()), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<Object> newValues) {
					// System.out.println(" bindingValueChanged() detected for values=" + getComponent().getValues() + " with newValue="
					// + newValues + " source=" + source);
					getGraphDrawing().updateDiscreteValues(newValues);
				}
			};
		}
	}

	private void stopListenDiscreteValuesChange() {
		if (valuesBindingValueChangeListener != null) {
			valuesBindingValueChangeListener.stopObserving();
			valuesBindingValueChangeListener.delete();
			valuesBindingValueChangeListener = null;
		}
	}

	@Override
	protected FGEDiscretePolarFunctionGraphDrawing makeGraphDrawing() {
		return new FGEDiscretePolarFunctionGraphDrawing(getWidget());
	}

	@Override
	public FGEDiscretePolarFunctionGraphDrawing getGraphDrawing() {
		return (FGEDiscretePolarFunctionGraphDrawing) super.getGraphDrawing();
	}

	public class FGEDiscretePolarFunctionGraphDrawing
			extends FGEPolarFunctionGraphDrawing<FIBDiscretePolarFunctionGraph, FGEDiscretePolarFunctionGraph<?>> {

		public FGEDiscretePolarFunctionGraphDrawing(FIBDiscretePolarFunctionGraph fibGraph) {
			super(fibGraph, JFIBDiscretePolarGraphWidget.this);
		}

		private List<HasPropertyChangeSupport> discreteValuesBeeingListened;

		protected void updateDiscreteValues(List<?> values) {
			stopListenDiscreteValues();
			if (values != null) {
				for (Object o : values) {
					if (o instanceof HasPropertyChangeSupport) {
						discreteValuesBeeingListened.add((HasPropertyChangeSupport) o);
						((HasPropertyChangeSupport) o).getPropertyChangeSupport().addPropertyChangeListener(this);
					}
				}
			}

			// System.out.println("values=" + values);
			getGraph().setDiscreteValues((List) values);

		}

		private void stopListenDiscreteValues() {
			if (discreteValuesBeeingListened == null) {
				discreteValuesBeeingListened = new ArrayList<>();
			}
			for (HasPropertyChangeSupport o : discreteValuesBeeingListened) {
				o.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			discreteValuesBeeingListened.clear();
		}

		@Override
		public void delete() {
			stopListenDiscreteValues();
			super.delete();
		}

		@Override
		protected FGEDiscretePolarFunctionGraph<?> makeGraph(FIBDiscretePolarFunctionGraph fibGraph) {

			// Create the FGEGraph
			graph = new FGEDiscretePolarFunctionGraph<Object>();
			graph.setBindingFactory(fibGraph.getBindingFactory());

			// Sets borders
			graph.setBorderTop(fibGraph.getBorderTop());
			graph.setBorderBottom(fibGraph.getBorderBottom());
			graph.setBorderLeft(fibGraph.getBorderLeft());
			graph.setBorderRight(fibGraph.getBorderRight());

			// Set parameter name and type
			// System.out.println("Parameter " + fibGraph.getParameterName() + " type=" + fibGraph.getParameterType());
			graph.setParameter(fibGraph.getParameterName(), fibGraph.getParameterType());

			// Set discrete values
			List<?> values = new ArrayList<>();
			if (fibGraph.getValues() != null && fibGraph.getValues().isSet() && fibGraph.getValues().isValid()) {
				try {
					values = fibGraph.getValues().getBindingValue(JFIBDiscretePolarGraphWidget.this);
					updateDiscreteValues(values);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			// Labels of discrete values
			if (fibGraph.getLabels() != null && fibGraph.getLabels().isSet() && fibGraph.getLabels().isValid()) {
				graph.setDiscreteValuesLabel(fibGraph.getLabels());
			}

			// Angle extent for discrete values
			if (fibGraph.getAngleExtent() != null && fibGraph.getAngleExtent().isSet() && fibGraph.getAngleExtent().isValid()) {
				graph.setWeight(fibGraph.getAngleExtent());
			}

			appendFunctions(fibGraph, graph, getController());

			return graph;
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(FIBDiscretePolarFunctionGraph.VALUES_KEY)
					|| evt.getPropertyName().equals(FIBDiscretePolarFunctionGraph.LABELS_KEY)
					|| evt.getPropertyName().equals(FIBDiscretePolarFunctionGraph.ANGLE_EXTENT_KEY)) {
				// System.out.println("---------------> On reconstruit le graphe entierement a cause de " + evt.getPropertyName());
				updateGraph();
			}
			if (discreteValuesBeeingListened.contains(evt.getSource())) {
				if (!evt.getPropertyName().equals("serializing")) {
					System.out.println("Updating graph because property " + evt.getPropertyName() + " changed for " + evt.getSource());
					updateGraph();
				}
			}
		}

	}

}

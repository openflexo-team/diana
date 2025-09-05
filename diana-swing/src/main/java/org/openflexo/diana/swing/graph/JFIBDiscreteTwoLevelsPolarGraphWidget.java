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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.connie.binding.BindingPathListChangeListener;
import org.openflexo.diana.graph.DianaDiscreteTwoLevelsPolarFunctionGraph;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBDiscretePolarFunctionGraph;
import org.openflexo.gina.model.graph.FIBDiscreteTwoLevelsPolarFunctionGraph;
import org.openflexo.toolbox.HasPropertyChangeSupport;

/**
 * Swing implementation for a {@link FIBDiscretePolarFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public class JFIBDiscreteTwoLevelsPolarGraphWidget extends JFIBPolarGraphWidget<FIBDiscreteTwoLevelsPolarFunctionGraph> {

	private static final Logger logger = Logger.getLogger(JFIBDiscreteTwoLevelsPolarGraphWidget.class.getPackage().getName());

	private BindingPathListChangeListener<Object, List<Object>> valuesBindingValueChangeListener;

	public JFIBDiscreteTwoLevelsPolarGraphWidget(FIBDiscreteTwoLevelsPolarFunctionGraph model, FIBController controller) {
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
			valuesBindingValueChangeListener = new BindingPathListChangeListener<Object, List<Object>>(
					((DataBinding) getComponent().getValues()), getBindingEvaluationContext()) {

				@Override
				public void bindingValueChanged(Object source, List<Object> newValues) {
					// System.out.println(" bindingValueChanged() detected for values=" + getComponent().getValues() + " with newValue="
					// + newValues + " source=" + source);
					getGraphDrawing().updateDiscreteValues();
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
	protected DianaDiscretePolarFunctionGraphDrawing makeGraphDrawing() {
		return new DianaDiscretePolarFunctionGraphDrawing(getWidget());
	}

	@Override
	public DianaDiscretePolarFunctionGraphDrawing getGraphDrawing() {
		return (DianaDiscretePolarFunctionGraphDrawing) super.getGraphDrawing();
	}

	public class DianaDiscretePolarFunctionGraphDrawing
			extends DianaPolarFunctionGraphDrawing<FIBDiscreteTwoLevelsPolarFunctionGraph, DianaDiscreteTwoLevelsPolarFunctionGraph<?, ?>> {

		public DianaDiscretePolarFunctionGraphDrawing(FIBDiscreteTwoLevelsPolarFunctionGraph fibGraph) {
			super(fibGraph, JFIBDiscreteTwoLevelsPolarGraphWidget.this);
		}

		private List<HasPropertyChangeSupport> discreteValuesBeeingListened;

		protected <T1, T2> void updateDiscreteValues() {
			stopListenDiscreteValues();

			Map<T1, List<T2>> values = retrieveValues(getWidget());
			if (values != null) {
				for (Object o1 : values.keySet()) {
					if (o1 instanceof HasPropertyChangeSupport) {
						discreteValuesBeeingListened.add((HasPropertyChangeSupport) o1);
						((HasPropertyChangeSupport) o1).getPropertyChangeSupport().addPropertyChangeListener(this);
					}
					if (values.get(o1) != null) {
						for (Object o2 : values.get(o1)) {
							if (o2 instanceof HasPropertyChangeSupport) {
								discreteValuesBeeingListened.add((HasPropertyChangeSupport) o2);
								((HasPropertyChangeSupport) o2).getPropertyChangeSupport().addPropertyChangeListener(this);
							}
						}
					}
				}
			}
			getGraph().setSecondaryDiscreteValues((Map) values);

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

		private <T1, T2> Map<T1, List<T2>> retrieveValues(FIBDiscreteTwoLevelsPolarFunctionGraph fibGraph) {

			Map<T1, List<T2>> returned = new LinkedHashMap<>();
			List<T1> primaryValues = null;

			if (fibGraph.getValues() != null && fibGraph.getValues().isSet() && fibGraph.getValues().isValid()
					&& fibGraph.getSecondaryValues() != null && fibGraph.getSecondaryValues().isSet()
					&& fibGraph.getSecondaryValues().isValid()) {
				try {
					primaryValues = (List<T1>) fibGraph.getValues().getBindingValue(JFIBDiscreteTwoLevelsPolarGraphWidget.this);
					if (primaryValues != null) {
						for (T1 primaryValue : primaryValues) {
							graph.getEvaluator().set(graph.getPrimaryParameterName(), primaryValue);
							List<T2> secondaryValues = (List<T2>) fibGraph.getSecondaryValues().getBindingValue(graph.getEvaluator());
							returned.put(primaryValue, secondaryValues);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			return returned;

		}

		@Override
		protected DianaDiscreteTwoLevelsPolarFunctionGraph<?, ?> makeGraph(FIBDiscreteTwoLevelsPolarFunctionGraph fibGraph) {

			// Create the DianaGraph
			graph = new DianaDiscreteTwoLevelsPolarFunctionGraph<Object, Object>();
			graph.setBindingFactory(fibGraph.getBindingFactory());

			// Sets borders
			graph.setBorderTop(fibGraph.getBorderTop());
			graph.setBorderBottom(fibGraph.getBorderBottom());
			graph.setBorderLeft(fibGraph.getBorderLeft());
			graph.setBorderRight(fibGraph.getBorderRight());

			graph.setDisplayReferenceMarks(fibGraph.getDisplayReferenceMarks());
			graph.setDisplayLabels(fibGraph.getDisplayLabels());
			graph.setDisplayGrid(fibGraph.getDisplayGrid());
			graph.setDisplaySecondaryLabels(fibGraph.getDisplaySecondaryLabels());

			// Set parameter name and type
			// System.out.println("Parameter " + fibGraph.getParameterName() + " type=" + fibGraph.getParameterType());
			graph.setParameter(fibGraph.getParameterName(), fibGraph.getParameterType());
			graph.setPrimaryParameterName(fibGraph.getParameterName());
			graph.setParameter(fibGraph.getSecondaryParameterName(), fibGraph.getSecondaryParameterType());
			graph.setSecondaryParameterName(fibGraph.getSecondaryParameterName());

			// Set discrete values
			updateDiscreteValues();

			/*List<?> values = new ArrayList<>();
			if (fibGraph.getValues() != null && fibGraph.getValues().isSet() && fibGraph.getValues().isValid()) {
				try {
					values = fibGraph.getValues().getBindingValue(JFIBDiscreteTwoLevelsPolarGraphWidget.this);
					updateDiscreteValues(values);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}*/

			// Labels of discrete values
			updateDiscreteValuesLabel();

			// Angle extent for discrete values
			if (fibGraph.getAngleExtent() != null && fibGraph.getAngleExtent().isSet() && fibGraph.getAngleExtent().isValid()) {
				graph.setWeight(fibGraph.getAngleExtent());
			}

			appendFunctions(fibGraph, graph, getController());

			return graph;
		}

		private void updateDiscreteValuesLabel() {
			if (getModel().getLabels() != null && getModel().getLabels().isSet() && getModel().getLabels().isValid()) {
				graph.setDiscreteValuesLabel(getModel().getLabels());
			}
			else {
				if (getModel().getLabels() != null && getModel().getLabels().isSet() && !getModel().getLabels().isValid()) {
					logger.warning("Invalid discrete values label : invalid binding " + getModel().getLabels() + " reason: "
							+ getModel().getLabels().invalidBindingReason());
					// System.out.println("bindable: " + getModel().getLabels().getOwner());
					// System.out.println("bm: " + getModel().getLabels().getOwner().getBindingModel());
				}
			}
			if (getModel().getSecondaryLabels() != null && getModel().getSecondaryLabels().isSet()
					&& getModel().getSecondaryLabels().isValid()) {
				graph.setSecondaryDiscreteValuesLabel(getModel().getSecondaryLabels());
			}
			else {
				if (getModel().getSecondaryLabels() != null && getModel().getSecondaryLabels().isSet()
						&& !getModel().getSecondaryLabels().isValid()) {
					logger.warning("Invalid discrete values label : invalid binding " + getModel().getSecondaryLabels() + " reason: "
							+ getModel().getSecondaryLabels().invalidBindingReason());
					// System.out.println("bindable: " + getModel().getLabels().getOwner());
					// System.out.println("bm: " + getModel().getLabels().getOwner().getBindingModel());
				}
			}
		}

		@Override
		protected void performUpdateGraph() {
			super.performUpdateGraph();
			updateDiscreteValuesLabel();
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(FIBDiscretePolarFunctionGraph.VALUES_KEY)
					|| (evt.getPropertyName().equals(FIBDiscretePolarFunctionGraph.LABELS_KEY))
					|| evt.getPropertyName().equals(FIBDiscretePolarFunctionGraph.ANGLE_EXTENT_KEY)) {
				System.out.println("Rebuilding graph because of " + evt.getPropertyName() + " changed for " + evt.getSource());
				updateGraph();
			}
			else if (evt.getPropertyName().equals(FIBDiscreteTwoLevelsPolarFunctionGraph.SECONDARY_PARAMETER_NAME_KEY)) {
				// System.out.println("parameter name changed from " + evt.getOldValue() + " to " + evt.getNewValue() + " property="
				// + evt.getPropertyName());
				getGraph().clearParameter((String) evt.getOldValue());
				getGraph().setParameter(getModel().getSecondaryParameterName(), getModel().getSecondaryParameterType());
				updateGraph();
			}
			else if (evt.getPropertyName().equals(FIBDiscreteTwoLevelsPolarFunctionGraph.DISPLAY_SECONDARY_LABELS_KEY)) {
				getGraph().setDisplaySecondaryLabels(getModel().getDisplaySecondaryLabels());
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

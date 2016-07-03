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

import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;
import org.openflexo.gina.model.graph.FIBPolarFunctionGraph;
import org.openflexo.gina.model.graph.FIBSimpleFunctionGraph;

/**
 * Swing implementation for a {@link FIBContinuousSimpleFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public abstract class JFIBPolarGraphWidget<W extends FIBPolarFunctionGraph> extends JFIBGraphWidget<W> {

	private static final Logger logger = Logger.getLogger(JFIBPolarGraphWidget.class.getPackage().getName());

	public JFIBPolarGraphWidget(W model, FIBController controller) {
		super(model, controller);
	}

	public abstract static class FGEPolarFunctionGraphDrawing<W extends FIBPolarFunctionGraph> extends GraphDrawing<W> {

		public FGEPolarFunctionGraphDrawing(W fibGraph, JFIBPolarGraphWidget<W> widget) {
			super(fibGraph, widget);
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			super.propertyChange(evt);
			if (evt.getPropertyName().equals(FIBSimpleFunctionGraph.PARAMETER_NAME_KEY)) {
				System.out.println("---------------> On reconstruit le graphe entierement a cause de " + evt.getPropertyName());
				updateGraph();
			}
		}

	}

}

/**
 * 
 * Copyright (c) 2016, Openflexo
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

package org.openflexo.fge.swing.graph;

import java.util.logging.Logger;

import org.openflexo.fge.swing.SwingViewFactory;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.FIBContainer;
import org.openflexo.gina.model.FIBWidget;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;
import org.openflexo.gina.model.graph.FIBGraph;
import org.openflexo.gina.view.FIBContainerView;
import org.openflexo.gina.view.FIBWidgetView;
import org.openflexo.gina.view.GinaViewFactory;
import org.openflexo.gina.view.GinaViewFactoryExtension;

/**
 * Extension for {@link SwingViewFactory} providing support for FIBGraph
 * 
 * @author sylvain
 * 
 */
public class GraphGinaViewFactoryExtension implements GinaViewFactoryExtension {

	private static final Logger logger = Logger.getLogger(GraphGinaViewFactoryExtension.class.getPackage().getName());

	@Override
	public void install(GinaViewFactory<?> viewFactory) {
		logger.info("Installing GraphGinaViewFactoryExtension");
	}

	@Override
	public boolean handleContainer(FIBContainer fibContainer) {
		return false;
	}

	@Override
	public <F extends FIBContainer> FIBContainerView<F, ?, ?> makeContainer(F fibContainer, FIBController controller, boolean updateNow) {
		return null;
	}

	@Override
	public boolean handleWidget(FIBWidget fibWidget) {
		if (fibWidget instanceof FIBGraph) {
			return true;
		}
		return false;
	}

	@Override
	public <W extends FIBWidget> FIBWidgetView<W, ?, ?> makeWidget(W fibWidget, FIBController controller) {
		if (fibWidget instanceof FIBContinuousSimpleFunctionGraph) {
			return (FIBWidgetView<W, ?, ?>) new JFIBContinuousSimpleGraphWidget((FIBContinuousSimpleFunctionGraph) fibWidget, controller);
		}
		return null;
	}
}

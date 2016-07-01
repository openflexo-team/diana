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

import java.awt.Color;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.graph.FGEContinuousSimpleFunctionGraph;
import org.openflexo.fge.graph.FGEFunction.GraphType;
import org.openflexo.fge.graph.FGENumericFunction;
import org.openflexo.gina.controller.FIBController;
import org.openflexo.gina.model.graph.FIBContinuousSimpleFunctionGraph;

/**
 * Swing implementation for a {@link FIBContinuousSimpleFunctionGraph} view<br>
 * 
 * @author sylvain
 */
public class JFIBContinuousSimpleGraphWidget extends JFIBGraphWidget<FIBContinuousSimpleFunctionGraph> {

	private static final Logger logger = Logger.getLogger(JFIBContinuousSimpleGraphWidget.class.getPackage().getName());

	public JFIBContinuousSimpleGraphWidget(FIBContinuousSimpleFunctionGraph model, FIBController controller) {
		super(model, controller);
	}

	@Override
	protected FGEContinuousSimpleFunctionGraphDrawing makeGraphDrawing() {
		return new FGEContinuousSimpleFunctionGraphDrawing();
	}

	public static class FGEContinuousSimpleFunctionGraphDrawing extends GraphDrawing<FGEContinuousSimpleFunctionGraph<Double>> {

		@Override
		protected FGEContinuousSimpleFunctionGraph<Double> makeGraph() {
			FGEContinuousSimpleFunctionGraph<Double> returned = new FGEContinuousSimpleFunctionGraph<Double>(Double.class);
			returned.setParameter("x", Double.class);
			returned.setParameterRange(-10.0, 10.0);
			returned.setStepsNumber(100);

			FGENumericFunction<Double> y1Function = returned.addNumericFunction("y1", Double.class, new DataBinding<Double>("x*x-2*x+1"),
					GraphType.POLYLIN);
			y1Function.setRange(0.0, 100.0);
			y1Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLUE, 1.0f));

			FGENumericFunction<Double> y2Function = returned.addNumericFunction("y2", Double.class, new DataBinding<Double>("cos(x)"),
					GraphType.CURVE);
			y2Function.setRange(-1.0, 1.0);
			y2Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.RED, 1.0f));
			y2Function.setBackgroundStyle(
					getFactory().makeColorGradientBackground(Color.BLUE, Color.WHITE, ColorGradientDirection.NORTH_SOUTH));

			FGENumericFunction<Integer> y3Function = returned.addNumericFunction("y3", Integer.class,
					new DataBinding<Integer>("($java.lang.Integer)(x*x/2+1)"), GraphType.POINTS);
			y3Function.setRange(0, 12);
			y3Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.BLACK, 1.0f));

			FGENumericFunction<Double> y4Function = returned.addNumericFunction("y", Double.class,
					new DataBinding<Double>("-3*x*(x-10)+20"), GraphType.RECT_POLYLIN);
			y4Function.setRange(0.0, 100.0);
			y4Function.setForegroundStyle(getFactory().makeForegroundStyle(Color.PINK, 1.0f));

			return returned;
		}

	}

}

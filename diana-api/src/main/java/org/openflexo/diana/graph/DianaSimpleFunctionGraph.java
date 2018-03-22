/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaGeneralShape.Closure;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graph.DianaFunction.DianaGraphType;
import org.openflexo.diana.graph.DianaFunction.FunctionSample;
import org.openflexo.diana.graphics.DianaShapeGraphics;

/**
 * Represents a 2D-base graph [y=f(x)] representing functions where:
 * <ul>
 * <li>a coordinate is iterated over continuous or discrete values</li>
 * <li>a coordinates is based on an expression using opposite coordinate (iterated value, which can be discrete or continuous)</li><br>
 * </ul>
 * 
 * Such graphs allows only one parameter.
 * 
 * @author sylvain
 * 
 * @param <X>
 *            type of value which plays iterator role
 */
public abstract class DianaSimpleFunctionGraph<X> extends DianaSingleParameteredGraph<X> {

	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	private Orientation parameterOrientation = Orientation.HORIZONTAL;

	public DianaSimpleFunctionGraph() {
		super();
	}

	public Orientation getParameterOrientation() {
		return parameterOrientation;
	}

	public void setParameterOrientation(Orientation orientation) {
		this.parameterOrientation = orientation;
	}

	@Override
	protected abstract Iterator<X> iterateParameter();

	protected abstract Double getNormalizedPosition(X value);

	/**
	 * Called for graph painting
	 * 
	 * @param g
	 */
	@Override
	public void paint(DianaShapeGraphics g) {

		// System.out.println("Painting graph");
		// System.out.println("width = " + g.getViewWidth());
		// System.out.println("height = " + g.getViewHeight());

		super.paint(g);

		// Paint parameters
		paintParameters(g);

		for (DianaFunction<?> f : getFunctions()) {

			f.paint(g);

		}

	}

	public abstract void paintParameters(DianaShapeGraphics g);

	@Override
	protected <T> FunctionRepresentation buildRepresentationForFunction(DianaFunction<T> function) {

		if (function.getFunctionExpression() == null || !function.getFunctionExpression().isSet()
				|| !function.getFunctionExpression().isValid()) {
			return new FunctionRepresentation(new DianaEmptyArea(), function.getForegroundStyle(), function.getBackgroundStyle());
		}

		List<FunctionSample<X, T>> samples = function.retrieveSamples(this);

		List<DianaPoint> points = new ArrayList<>();

		for (FunctionSample<X, T> s : samples) {
			DianaPoint pt;
			if (getParameterOrientation() == Orientation.HORIZONTAL) {
				pt = new DianaPoint(getNormalizedPosition(s.x), function.getNormalizedPosition(s.value));
			}
			else {
				pt = new DianaPoint(function.getNormalizedPosition(s.value), getNormalizedPosition(s.x));
			}

			// System.out.println("Sampling function " + getFunctionName() + "(" + s.p + ") = " + s.value + " normalizedValue="
			// + getNormalizedPosition(s.value));

			points.add(pt);
		}

		switch (function.getGraphType()) {
			case POINTS:
				return new FunctionRepresentation(DianaUnionArea.makeUnion(points), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case POLYLIN:
				return new FunctionRepresentation(new DianaPolylin(points), function.getForegroundStyle(), function.getBackgroundStyle());
			case RECT_POLYLIN:
				List<DianaPoint> rectPoints = new ArrayList<>();
				double delta = (double) 1 / points.size() / 2;
				for (DianaPoint pt : points) {
					if (getParameterOrientation() == Orientation.HORIZONTAL) {
						rectPoints.add(new DianaPoint(pt.x - delta, pt.y));
						rectPoints.add(new DianaPoint(pt.x + delta, pt.y));
					}
					else { // Vertical
						rectPoints.add(new DianaPoint(pt.x, pt.y - delta));
						rectPoints.add(new DianaPoint(pt.x, pt.y + delta));
					}
				}
				return new FunctionRepresentation(new DianaPolylin(rectPoints), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case CURVE:
				return new FunctionRepresentation(new DianaComplexCurve(Closure.OPEN_NOT_FILLED, points), function.getForegroundStyle(),
						function.getBackgroundStyle());
			case BAR_GRAPH:
				List<DianaRectangle> rectangles = new ArrayList<>();
				double sampleSize = (double) 1 / points.size();
				double barWidth = 0.8 * sampleSize / getNumberOfFunctionsOfType(DianaGraphType.BAR_GRAPH);
				double barSpacing = sampleSize / 10;
				int index = getIndexOfFunctionsOfType(function);
				for (DianaPoint pt : points) {
					if (getParameterOrientation() == Orientation.HORIZONTAL) {
						DianaRectangle r = new DianaRectangle(new DianaPoint(pt.x - sampleSize / 2 + barSpacing + (index * barWidth), 0),
								new DianaDimension(barWidth, pt.y), Filling.FILLED);
						rectangles.add(r);
					}
					else {
						DianaRectangle r = new DianaRectangle(new DianaPoint(0, pt.y - sampleSize / 2 + barSpacing + (index * barWidth)),
								new DianaDimension(pt.x, barWidth), Filling.FILLED);
						rectangles.add(r);
					}
				}
				return new FunctionRepresentation(DianaUnionArea.makeUnion(rectangles), function.getForegroundStyle(),
						function.getBackgroundStyle());

			default:
				break;
		}
		return null;
	}

}

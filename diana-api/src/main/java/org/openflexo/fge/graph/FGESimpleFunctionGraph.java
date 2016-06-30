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

package org.openflexo.fge.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.openflexo.fge.geom.FGEComplexCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolylin;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEUnionArea;
import org.openflexo.fge.graph.FGEFunction.FunctionSample;
import org.openflexo.fge.graphics.FGEShapeGraphics;

/**
 * Represents a 2D-base graph representing functions where:
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
public abstract class FGESimpleFunctionGraph<X> extends FGESingleParameteredGraph<X> {

	public static enum Orientation {
		HORIZONTAL, VERTICAL;
	}

	private Orientation parameterOrientation = Orientation.HORIZONTAL;

	public FGESimpleFunctionGraph() {
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
	public void paint(FGEShapeGraphics g) {

		// System.out.println("Painting graph");
		// System.out.println("width = " + g.getViewWidth());
		// System.out.println("height = " + g.getViewHeight());

		super.paint(g);

		// Paint parameters
		paintParameters(g);

		for (FGEFunction<?> f : getFunctions()) {

			f.paint(g);

		}

	}

	public abstract void paintParameters(FGEShapeGraphics g);

	@Override
	protected <T> FGEArea buildRepresentationForFunction(FGEFunction<T> function) {

		List<FunctionSample<X, T>> samples = function.retrieveSamples(this);

		List<FGEPoint> points = new ArrayList<FGEPoint>();

		for (FunctionSample<X, T> s : samples) {
			FGEPoint pt;
			if (getParameterOrientation() == Orientation.HORIZONTAL) {
				pt = new FGEPoint(getNormalizedPosition(s.x), function.getNormalizedPosition(s.value));
			}
			else {
				pt = new FGEPoint(function.getNormalizedPosition(s.value), getNormalizedPosition(s.x));
			}

			// System.out.println("Sampling function " + getFunctionName() + "(" + s.p + ") = " + s.value + " normalizedValue="
			// + getNormalizedPosition(s.value));

			points.add(pt);
		}

		switch (function.getGraphType()) {
			case POINTS:
				return FGEUnionArea.makeUnion(points);
			case POLYLIN:
				return new FGEPolylin(points);
			case RECT_POLYLIN:
				List<FGEPoint> rectPoints = new ArrayList<FGEPoint>();
				double delta = (double) 1 / points.size() / 2;
				for (FGEPoint pt : points) {
					if (getParameterOrientation() == Orientation.HORIZONTAL) {
						rectPoints.add(new FGEPoint(pt.x - delta, pt.y));
						rectPoints.add(new FGEPoint(pt.x + delta, pt.y));
					}
					else { // Vertical
						rectPoints.add(new FGEPoint(pt.x, pt.y - delta));
						rectPoints.add(new FGEPoint(pt.x, pt.y + delta));
					}
				}
				return new FGEPolylin(rectPoints);
			case CURVE:
				return new FGEComplexCurve(Closure.OPEN_NOT_FILLED, points);
			case BAR_GRAPH:
				List<FGERectangle> rectangles = new ArrayList<FGERectangle>();
				double sampleSize = (double) 1 / points.size();
				double barWidth = 0.8 * sampleSize / getNumberOfFunctionsOfType(GraphType.BAR_GRAPH);
				double barSpacing = sampleSize / 10;
				int index = getIndexOfFunctionsOfType(function);
				for (FGEPoint pt : points) {
					if (getParameterOrientation() == Orientation.HORIZONTAL) {
						FGERectangle r = new FGERectangle(new FGEPoint(pt.x - sampleSize / 2 + barSpacing + (index * barWidth), 0),
								new FGEDimension(barWidth, pt.y), Filling.FILLED);
						rectangles.add(r);
					}
					else {
						FGERectangle r = new FGERectangle(new FGEPoint(0, pt.y - sampleSize / 2 + barSpacing + (index * barWidth)),
								new FGEDimension(pt.x, barWidth), Filling.FILLED);
						rectangles.add(r);
					}
				}
				return FGEUnionArea.makeUnion(rectangles);

			default:
				break;
		}
		return null;
	}

}

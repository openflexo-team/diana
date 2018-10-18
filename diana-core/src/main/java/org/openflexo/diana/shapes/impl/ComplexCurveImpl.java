/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.diana.shapes.impl;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.DianaComplexCurve;
import org.openflexo.diana.geom.DianaGeneralShape.Closure;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.shapes.ComplexCurve;

public abstract class ComplexCurveImpl extends ShapeSpecificationImpl implements ComplexCurve {

	private List<DianaPoint> points;
	private Closure closure = Closure.CLOSED_FILLED;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ComplexCurveImpl() {
		super();
		this.points = new ArrayList<>();
	}

	public ComplexCurveImpl(List<DianaPoint> points) {
		this();
		for (DianaPoint pt : points) {
			// this.points.add(pt);
			addToPoints(pt);
		}
	}

	public ComplexCurveImpl(DianaComplexCurve curve) {
		this();
		for (DianaPoint pt : curve.getPoints()) {
			// points.add(pt);
			addToPoints(pt);
		}
	}

	@Override
	public List<DianaPoint> getPoints() {
		return points;
	}

	@Override
	public void setPoints(List<DianaPoint> points) {
		if (points != null) {
			this.points = new ArrayList<>(points);
		}
		else {
			this.points = null;
		}
		notifyChange(POINTS);
	}

	@Override
	public void addToPoints(DianaPoint aPoint) {
		points.add(aPoint);
		notifyChange(POINTS);
	}

	@Override
	public void removeFromPoints(DianaPoint aPoint) {
		points.remove(aPoint);
		notifyChange(POINTS);
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.COMPLEX_CURVE;
	}

	@Override
	public Closure getClosure() {
		return closure;
	}

	@Override
	public void setClosure(Closure aClosure) {
		DianaAttributeNotification<?> notification = requireChange(CLOSURE, aClosure);
		if (notification != null) {
			closure = aClosure;
			hasChanged(notification);
		}
	}

	@Override
	public DianaComplexCurve makeNormalizedDianaShape(ShapeNode<?> node) {
		return new DianaComplexCurve(getClosure(), points);
	}

}

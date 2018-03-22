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
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaRegularPolygon;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.diana.shapes.RegularPolygon;

public abstract class RegularPolygonImpl extends PolygonImpl implements RegularPolygon {

	private int npoints = -1;
	private int startAngle = 90;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public RegularPolygonImpl() {
		super();
		setNPoints(5);
	}

	public RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, List<DianaPoint> points) {
		this();
		setPoints(new ArrayList<>(points));
	}

	public RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		this();
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
	}

	/*@Deprecated
	private RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
	}
	
	@Deprecated
	private RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, List<DianaPoint> points) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		setPoints(new ArrayList<DianaPoint>(points));
	}
	
	@Deprecated
	private RegularPolygonImpl(ShapeGraphicalRepresentation aGraphicalRepresentation, int pointsNb) {
		this();
		setGraphicalRepresentation(aGraphicalRepresentation);
		if (pointsNb < 3) {
			throw new IllegalArgumentException("Cannot build polygon with less then 3 points (" + pointsNb + ")");
		}
		npoints = pointsNb;
		updateShape();
	}*/

	@Override
	public DianaShape<?> makeDianaShape(ShapeNode<?> node) {
		if (getPoints() != null && getPoints().size() > 0) {
			return new DianaPolygon(Filling.FILLED, getPoints());
		}
		else if (getNPoints() > 2) {
			return new DianaRegularPolygon(0, 0, 1, 1, Filling.FILLED, getNPoints(), startAngle);
		}
		return null;
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.POLYGON;
	}

	@Override
	public int getNPoints() {
		return npoints;
	}

	@Override
	public void setNPoints(int pointsNb) {
		DianaAttributeNotification<?> notification = requireChange(N_POINTS, pointsNb);
		if (notification != null) {
			npoints = pointsNb;
			hasChanged(notification);
		}
	}

	@Override
	public int getStartAngle() {
		return startAngle;
	}

	@Override
	public void setStartAngle(int anAngle) {
		DianaAttributeNotification<?> notification = requireChange(START_ANGLE, anAngle);
		if (notification != null) {
			startAngle = anAngle;
			hasChanged(notification);
		}
	}

}

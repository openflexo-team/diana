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

package org.openflexo.diana.connectors.rpc;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.connectors.RectPolylinConnectorSpecification.RectPolylinAdjustability;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaPlane;

public class AdjustableIntermediateControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger LOGGER = Logger.getLogger(AdjustableIntermediateControlPoint.class.getPackage().getName());

	private int index;

	public AdjustableIntermediateControlPoint(DianaPoint point, int index, RectPolylinConnector connector) {
		super(point, connector);
		this.index = index;
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		return new DianaPlane();
	}

	@Override
	public Cursor getDraggingCursor() {
		if (getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
			return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
		}
		return super.getDraggingCursor();
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {

		System.out.println("AdjustableIntermediateControlPoint dragged to " + newRelativePoint);

		if (getConnectorSpecification().getAdjustability() == RectPolylinAdjustability.BASICALLY_ADJUSTABLE) {
			getConnectorSpecification().setCrossedControlPoint(newRelativePoint);
			getConnector()._connectorChanged(true);
			getNode().notifyConnectorModified();
			return true;
		}
		DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		if (pt == null) {
			LOGGER.warning("Cannot nearest point for point " + newRelativePoint + " and area " + getDraggingAuthorizedArea());
			return false;
		}
		// Following little hack is used here to prevent some equalities that may
		// lead to inconsistent orientations
		pt.x += DianaGeometricObject.EPSILON;
		pt.y += DianaGeometricObject.EPSILON;
		DianaPoint oldPoint = getPoint();
		setPoint(pt);
		getPolylin().updatePointAt(index, pt);
		boolean continueDragging = movedIntermediateCP(index, oldPoint, true);
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return continueDragging;
	}

	/**
	 * This method is internally called to notify that intermediate control point at supplied index has just been moved.
	 * 
	 * Previous and next point are updated accordingly
	 * 
	 * Return boolean indicating if dragging lead to major layout modification where current dragged control point was suppressed: - When
	 * true: control point still exist - When false: control point has disappeared and dragging should stop
	 * 
	 * @param index
	 */
	private boolean movedIntermediateCP(int index, DianaPoint oldCPLocation, boolean simplifyLayout) {
		if (LOGGER.isLoggable(Level.FINE)) {
			LOGGER.fine("Moved intermediate point at index: " + index);
		}

		// First, obtain location of Control Point being moved
		DianaPoint newCPLocation = getPolylin().getPointAt(index);

		// Then obtain locations of previous and following control points
		DianaPoint previousCPLocation = getPolylin().getPointAt(index - 1);
		DianaPoint previousCPOldLocation = previousCPLocation.clone();
		DianaPoint nextCPLocation = getPolylin().getPointAt(index + 1);
		DianaPoint nextCPOldLocation = nextCPLocation.clone();

		// And their orientations, that will be usefull too
		SimplifiedCardinalDirection previousSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index - 1);
		SimplifiedCardinalDirection nextSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index);
		// SimplifiedCardinalDirection startSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index-2);
		// SimplifiedCardinalDirection endSegmentOrientation = getPolylin().getApproximatedOrientationOfSegment(index+1);

		DianaSegment intermediateCPStartSegment = getPolylin().getSegmentAt(index - 2);
		DianaSegment intermediateCPEndSegment = getPolylin().getSegmentAt(index + 1);
		// DianaSegment intermediateCPFirstSegment = polylin.getSegmentAt(index-1);
		// DianaSegment intermediateCPNextSegment = polylin.getSegmentAt(index);

		// Declare new locations
		DianaPoint previousCPNewLocation = new DianaPoint(previousCPLocation);
		DianaPoint nextCPNewLocation = new DianaPoint(nextCPLocation);

		// Update previous control point location according to orientation of related segment
		if (previousSegmentOrientation.isHorizontal()) {
			previousCPNewLocation.y = newCPLocation.y;
		}
		else if (previousSegmentOrientation.isVertical()) {
			previousCPNewLocation.x = newCPLocation.x;
		}
		else {
			LOGGER.warning("Inconsistent data: segment not horizontal nor vertical");
		}

		// If we don't modify general layout of connector,
		// control point will move from location previousCPLocation to previousCPNewLocation

		if (simplifyLayout) {
			// But meanwhile, we can also decide to change general shape by deleting some points.
			// This will happen if oldCPLocation and newCPLocation are each other
			// From both side of half-plane formed by intermediateCPStartSegment
			DianaHalfPlane intermediateCPStartSegmentHalfPlane = new DianaHalfPlane(intermediateCPStartSegment, oldCPLocation);
			if (!intermediateCPStartSegmentHalfPlane.containsPoint(newCPLocation)) {
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.info("Two points will be removed (pattern 1) at index=" + (index - 1));
				}
				getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index - 1, newCPLocation);
				/*
				getPolylin().removePointAtIndex(index-1);
				getPolylin().removePointAtIndex(index-1);
				getConnector()._getControlPoints().remove(index-1);
				getConnector()._getControlPoints().remove(index-1);
				if (previousSegmentOrientation.isHorizontal()) {
				getPolylin().updatePointAt(index-2, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index-2).y));
				getPolylin().updatePointAt(index-1, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index-1).y));
				}
				else if (previousSegmentOrientation.isVertical()) {
				getPolylin().updatePointAt(index-2, new DianaPoint(getPolylin().getPointAt(index-2).x,newCPLocation.y));
				getPolylin().updatePointAt(index-1, new DianaPoint(getPolylin().getPointAt(index-1).x,newCPLocation.y));
				}
				getConnector().updateWithNewPolylin(getPolylin());*/
				return false;
			}

			if (index > 2) {
				// This may also happen if previousCPOldLocation and previousCPNewLocation are each other
				// From both side of half-plane formed by intermediateCPBeforeStartSegment
				DianaSegment intermediateCPBeforeStartSegment = getPolylin().getSegmentAt(index - 3);
				DianaHalfPlane intermediateCPBeforeStartSegmentHalfPlane = new DianaHalfPlane(intermediateCPBeforeStartSegment,
						previousCPOldLocation);
				if (!intermediateCPBeforeStartSegmentHalfPlane.containsPoint(previousCPNewLocation)) {
					if (LOGGER.isLoggable(Level.INFO)) {
						LOGGER.info("Two points will be removed (pattern 2) at index=" + (index - 2));
					}
					getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index - 2, newCPLocation);
					/*
					getPolylin().removePointAtIndex(index-2);
					getPolylin().removePointAtIndex(index-2);
					getConnector()._getControlPoints().remove(index-2);
					getConnector()._getControlPoints().remove(index-2);
					if (startSegmentOrientation.isVertical()) {
					getPolylin().updatePointAt(index-2, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index-2).y));
					getPolylin().updatePointAt(index-1, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index-1).y));
					}
					else if (startSegmentOrientation.isHorizontal()) {
					getPolylin().updatePointAt(index-2, new DianaPoint(getPolylin().getPointAt(index-2).x,newCPLocation.y));
					getPolylin().updatePointAt(index-1, new DianaPoint(getPolylin().getPointAt(index-1).x,newCPLocation.y));
					}
					getConnector().updateWithNewPolylin(getPolylin());*/
					return false;
				}
			}
		}

		getPolylin().updatePointAt(index - 1, previousCPNewLocation);
		getConnector().getControlPoints().get(index - 1).setPoint(previousCPNewLocation);

		// Update next control point location according to orientation of related segment
		if (nextSegmentOrientation.isHorizontal()) {
			nextCPNewLocation.y = newCPLocation.y;
		}
		else if (nextSegmentOrientation.isVertical()) {
			nextCPNewLocation.x = newCPLocation.x;
		}
		else {
			LOGGER.warning("Inconsistent data: segment not horizontal nor vertical");
		}

		// If we don't modify general layout of connector,
		// control point will move from location nextCPLocation to nextCPNewLocation

		if (simplifyLayout) {

			// But meanwhile, we can also decide to change general shape by deleting some points.
			// This will happen if oldCPLocation and newCPLocation are each other
			// From both side of half-plane formed by intermediateCPEndSegment
			DianaHalfPlane intermediateCPEndSegmentHalfPlane = new DianaHalfPlane(intermediateCPEndSegment, oldCPLocation);
			if (!intermediateCPEndSegmentHalfPlane.containsPoint(newCPLocation)) {
				if (LOGGER.isLoggable(Level.INFO)) {
					LOGGER.info("Two points will be removed (pattern 3) at index=" + index);
				}
				getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index, newCPLocation);
				/*
				getPolylin().removePointAtIndex(index);
				getPolylin().removePointAtIndex(index);
				getConnector()._getControlPoints().remove(index);
				getConnector()._getControlPoints().remove(index);
				if (nextSegmentOrientation.isHorizontal()) {
				getPolylin().updatePointAt(index-1, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index-1).y));
				getPolylin().updatePointAt(index, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index).y));
				}
				else if (nextSegmentOrientation.isVertical()) {
				getPolylin().updatePointAt(index-1, new DianaPoint(getPolylin().getPointAt(index-1).x,newCPLocation.y));
				getPolylin().updatePointAt(index, new DianaPoint(getPolylin().getPointAt(index).x,newCPLocation.y));
				}
				getConnector().updateWithNewPolylin(getPolylin());*/
				return false;
			}

			if (getPolylin().getSegmentNb() > index + 2) {
				// This may also happen if nextCPOldLocation and nextCPNewLocation are each other
				// From both side of half-plane formed by intermediateCPAfterEndSegment
				DianaSegment intermediateCPAfterEndSegment = getPolylin().getSegmentAt(index + 2);
				DianaHalfPlane intermediateCPAfterEndSegmentHalfPlane = new DianaHalfPlane(intermediateCPAfterEndSegment,
						nextCPOldLocation);
				if (!intermediateCPAfterEndSegmentHalfPlane.containsPoint(nextCPNewLocation)) {
					if (LOGGER.isLoggable(Level.INFO)) {
						LOGGER.info("Two points will be removed (pattern 4) at index=" + (index + 1));
					}
					getConnector()._simplifyLayoutOfCurrentPolylinByDeletingTwoPoints(index + 1, newCPLocation);
					/*
					getPolylin().removePointAtIndex(index+1);
					getPolylin().removePointAtIndex(index+1);
					getConnector()._getControlPoints().remove(index+1);
					getConnector()._getControlPoints().remove(index+1);
					if (endSegmentOrientation.isHorizontal()) {
					getPolylin().updatePointAt(index, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index).y));
					getPolylin().updatePointAt(index+1, new DianaPoint(newCPLocation.x,getPolylin().getPointAt(index+1).y));
					}
					else if (endSegmentOrientation.isVertical()) {
					getPolylin().updatePointAt(index, new DianaPoint(getPolylin().getPointAt(index).x,newCPLocation.y));
					getPolylin().updatePointAt(index+1, new DianaPoint(getPolylin().getPointAt(index+1).x,newCPLocation.y));
					}
					getConnector().updateWithNewPolylin(getPolylin());*/
					return false;
				}
			}
		}

		getPolylin().updatePointAt(index + 1, nextCPNewLocation);
		getConnector().getControlPoints().get(index + 1).setPoint(nextCPNewLocation);

		/*if (!getPolylin().isNormalized()) {
			getConnector().updateWithNewPolylin(getPolylin(), true);
		}*/

		return true;
	}

}

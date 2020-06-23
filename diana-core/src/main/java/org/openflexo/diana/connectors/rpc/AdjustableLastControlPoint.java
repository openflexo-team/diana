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

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.geom.area.DianaSubstractionArea;

public class AdjustableLastControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger LOGGER = Logger.getLogger(AdjustableLastControlPoint.class.getPackage().getName());

	private SimplifiedCardinalDirection currentEndOrientation = null;

	public AdjustableLastControlPoint(DianaPoint point, RectPolylinConnector connector) {
		super(point, connector);
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		AffineTransform at2 = DianaUtils.convertNormalizedCoordinatesAT(getNode().getEndNode(), getNode());
		DianaArea endArea = getNode().getEndNode().getDianaShape().transform(at2);
		return new DianaSubstractionArea(new DianaPlane(), endArea, false);
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
		if (pt == null) {
			LOGGER.warning("Cannot nearest point for point " + newRelativePoint + " and area " + getDraggingAuthorizedArea());
			return false;
		}
		// Following little hack is used here to prevent some equalities that may
		// lead to inconsistent orientations
		// pt.x += DianaPoint.EPSILON;
		// pt.y += DianaPoint.EPSILON;
		setPoint(pt);
		getPolylin().updatePointAt(getPolylin().getPointsNb() - 2, pt);
		movedLastCP();
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return true;
	}

	/**
	 * This method is internally called when last control point has been detected to be moved.
	 * 
	 */
	private void movedLastCP() {
		DianaArea endArea = getConnector().retrieveAllowedEndArea(false);

		if (getConnectorSpecification().getIsEndingLocationFixed() && !getConnectorSpecification().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = getConnector().retrieveEndArea();
		}

		DianaPoint newLastCPLocation = getPoint();
		DianaPoint previousCPLocation = initialPolylin.getPointAt(initialPolylin.getPointsNb() - 3);
		SimplifiedCardinalDirection initialEndOrientation = initialPolylin.getApproximatedOrientationOfSegment(
				initialPolylin.getSegmentNb() - 1).getOpposite();
		SimplifiedCardinalDirection initialLastOrientation = initialPolylin.getApproximatedOrientationOfSegment(
				initialPolylin.getSegmentNb() - 2).getOpposite();
		SimplifiedCardinalDirection initialPreviousOrientation = initialPolylin.getSegmentNb() > 2 ? initialPolylin
				.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb() - 3).getOpposite() : null;

		if (endArea.getOrthogonalPerspectiveArea(initialEndOrientation).containsPoint(newLastCPLocation)) {
			// OK, the new location will not modify general structure of connector
			DianaPoint newEndPoint = endArea.nearestPointFrom(newLastCPLocation, initialEndOrientation.getOpposite());
			if (newEndPoint == null) {
				LOGGER.warning("Could not find nearest point from " + newLastCPLocation + " on " + endArea + " following orientation "
						+ initialEndOrientation.getOpposite());
				newEndPoint = endArea.getNearestPoint(newLastCPLocation);
			}
			getPolylin().updatePointAt(getPolylin().getPointsNb() - 1, newEndPoint);
			getConnector().getEndControlPoint().setPoint(newEndPoint);
			if (getConnectorSpecification().getIsEndingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedEndLocation(DianaUtils.convertNormalizedPoint(getNode(), newEndPoint, getNode().getEndNode()));
			}

			if (initialPolylin.getSegmentNb() > 3) {
				DianaSegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3);
				DianaRectPolylin appendingPath1 = new DianaRectPolylin(oppositeSegment.getP1(), initialPolylin
						.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb() - 3).getOpposite(), newLastCPLocation,
						initialLastOrientation, true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector()
								.getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath2 = new DianaRectPolylin(oppositeSegment.getP1(),
						initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb() - 3), newLastCPLocation,
						initialLastOrientation, true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector()
								.getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath = appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ? appendingPath1
						: appendingPath2;

				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2,
						getPolylin(), getPolylin().getPointsNb() - 2, getPolylin().getPointsNb() - 1);

				mergedPolylin = getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 5, mergedPolylin, 0,
						mergedPolylin.getPointsNb() - 1);

				getConnector().updateWithNewPolylin(mergedPolylin, true);
			}

			else { // We go directely to end point, we have to preserve direction
				DianaRectPolylin appendingPath = new DianaRectPolylin(initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3).getP1(),
						initialPreviousOrientation.getOpposite(), newLastCPLocation, initialLastOrientation, true, getConnector()
								.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 2,
						getPolylin(), getPolylin().getPointsNb() - 2, getPolylin().getPointsNb() - 1);

				getConnector().updateWithNewPolylin(mergedPolylin, true);

			}

			currentEndOrientation = initialEndOrientation;

		}

		else {
			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			// SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : getConnector().getPrimitiveAllowedEndOrientations()) {
				if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(newLastCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.
				// I don't want to hide the thuth: this is NOT good...

				// We keep here initial start orientation
				if (currentEndOrientation == null) {
					currentEndOrientation = initialEndOrientation;
				}
				orientation = currentEndOrientation;

				if (!getConnector().getAllowedEndOrientations().contains(orientation)
						&& getConnector().getAllowedEndOrientations().size() > 0) {
					orientation = getConnector().getAllowedEndOrientations().firstElement();
				}

				/*if (endArea.containsPoint(newLastCPLocation)) {
						orientation = currentEndOrientation;
						alternativeOrientation = currentEndOrientation;
					}
					else {
						CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(getPolylin().getLastPoint(),newLastCPLocation);
						orientation = quadrant.getHorizonalComponent();
						alternativeOrientation = quadrant.getVerticalComponent();
					}*/

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of end area regarding orientation
				DianaPoint newEndPosition = endArea.getAnchorAreaFrom(orientation).getNearestPoint(newLastCPLocation);

				// Compute path to append
				/*DianaRectPolylin appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(
						previousCPLocation, newEndPosition, newLastCPLocation,
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(initialPreviousOrientation.getOpposite()),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation));*/

				DianaRectPolylin appendingPath;

				if (initialPolylin.getSegmentNb() > 3) {
					DianaSegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3);
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(oppositeSegment.getP1(), newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				} else {
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(previousCPLocation, newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				}
				// debugPolylin = appendingPath;

				// Merge polylin
				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 4,
						appendingPath, 0, appendingPath.getPointsNb() - 1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin, true);

			} else {
				// OK, we have found a cardinal direction in which it is possible to
				// project dragged control point

				// Compute new end position by projecting dragged control point
				// related to orientation
				DianaPoint newEndPosition = endArea.nearestPointFrom(newLastCPLocation, orientation.getOpposite());
				if (newEndPosition == null) {
					LOGGER.warning("Could not find nearest point from " + newLastCPLocation + " on " + endArea + " following orientation "
							+ initialEndOrientation.getOpposite());
					newEndPosition = endArea.getNearestPoint(newLastCPLocation);
				}
				getPolylin().updatePointAt(getPolylin().getPointsNb() - 1, newEndPosition);
				getConnector().getEndControlPoint().setPoint(newEndPosition);
				if (getConnectorSpecification().getIsEndingLocationFixed()) { // Don't forget this !!!
					getConnectorSpecification().setFixedEndLocation(
							DianaUtils.convertNormalizedPoint(getNode(), newEndPosition, getNode().getEndNode()));
				}

				// Compute path to append
				DianaRectPolylin appendingPath;

				if (initialPolylin.getSegmentNb() > 3) {
					DianaSegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb() - 3);
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(oppositeSegment.getP1(), newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				} else {
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(previousCPLocation, newEndPosition, newLastCPLocation,
							initialPreviousOrientation.getOpposite(), orientation, true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				}

				// getConnector().debugPolylin = appendingPath;

				// Merge polylin
				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb() - 4,
						appendingPath, 0, appendingPath.getPointsNb() - 1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin, true);

				currentEndOrientation = orientation;
			}
		}
	}

	/**
	 * This method is internally called when last control point has been detected to be moved.
	 * 
	 */
	/*private void movedLastCP()
	{

		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getEndObject(), getGraphicalRepresentation());
		DianaArea endArea = getConnector().getEndObject().getShape().getOutline().transform(at2);

		DianaPoint lastCPLocation = getPoint();
		DianaPoint previousCPLocation = initialPolylin.getPointAt(initialPolylin.getPointsNb()-3);
		SimplifiedCardinalDirection initialEndOrientation = initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-1).getOpposite();
		SimplifiedCardinalDirection initialLastOrientation = initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-2).getOpposite();
		SimplifiedCardinalDirection initialPreviousOrientation = (initialPolylin.getSegmentNb() > 2 ? initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-3).getOpposite() : null);


		if (endArea.getOrthogonalPerspectiveArea(initialEndOrientation).containsPoint(lastCPLocation)) {
			// OK, the new location will not modify general structure of connector
			DianaPoint newPoint = new DianaPoint(getPolylin().getLastPoint());
			if (initialEndOrientation.isHorizontal()) {
				newPoint.setY(lastCPLocation.y);
			}
			else if (initialEndOrientation.isVertical()) {
				newPoint.setX(lastCPLocation.x);
			}
			getPolylin().updatePointAt(getPolylin().getPointsNb()-1, newPoint);
			getConnector().getEndControlPoint().setPoint(newPoint);

			if (initialPolylin.getSegmentNb() > 3) {
				DianaSegment oppositeSegment = initialPolylin.getSegmentAt(initialPolylin.getSegmentNb()-3);
				DianaRectPolylin appendingPath1 = new DianaRectPolylin(
						oppositeSegment.getP1(),initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-3).getOpposite(),
						lastCPLocation,initialLastOrientation,
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath2 = new DianaRectPolylin(
						oppositeSegment.getP1(),initialPolylin.getApproximatedOrientationOfSegment(initialPolylin.getSegmentNb()-3),
						lastCPLocation,initialLastOrientation,
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath = (appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ?
						appendingPath1 : appendingPath2);


				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-2, getPolylin(), getPolylin().getPointsNb()-2, getPolylin().getPointsNb()-1 );

				mergedPolylin = getConnector().mergePolylins(
						initialPolylin, 0, initialPolylin.getPointsNb()-5,
						mergedPolylin, 0, mergedPolylin.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);
			}		

			else { // We go directely to end point, we have to preserve direction
				DianaRectPolylin appendingPath = new DianaRectPolylin(
						initialPolylin.getSegmentAt(initialPolylin.getSegmentNb()-3).getP1(),initialPreviousOrientation.getOpposite(),
						lastCPLocation,initialLastOrientation,
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-2, getPolylin(), getPolylin().getPointsNb()-2,getPolylin().getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);

			}

			currentEndOrientation = initialEndOrientation;

		}

		else {
			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
				if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(lastCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.

				// We keep here initial start orientation
				if (currentEndOrientation == null) currentEndOrientation = initialEndOrientation;
				orientation = currentEndOrientation;

				if (endArea.containsPoint(lastCPLocation)) {
					orientation = currentEndOrientation;
					alternativeOrientation = currentEndOrientation;
				}
				else {
					CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(getPolylin().getLastPoint(),lastCPLocation);
					orientation = quadrant.getHorizonalComponent();
					alternativeOrientation = quadrant.getVerticalComponent();
				}

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of end area regarding orientation
				DianaPoint newEndPosition = endArea.getAnchorAreaFrom(orientation).getNearestPoint(lastCPLocation);

				// Compute path to append
				DianaRectPolylin appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(
						previousCPLocation, newEndPosition, lastCPLocation,
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(initialPreviousOrientation.getOpposite()),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation,alternativeOrientation));

				//debugPolylin = appendingPath;

				// Merge polylin
				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb()-4,appendingPath, 0, appendingPath.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

			}
			else {
				// OK, we have found a cardinal direction in which it is possible to 
				// project dragged control point

				// Compute new end position by projecting dragged control point
				// related to orientation
				DianaPoint newEndPosition = endArea.getAnchorAreaFrom(orientation).getNearestPoint(lastCPLocation);

				// Compute path to append
				DianaRectPolylin appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(
						previousCPLocation, newEndPosition, lastCPLocation, initialPreviousOrientation.getOpposite(), orientation, true, 
						getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				//debugPolylin = appendingPath;

				// Merge polylin
				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(initialPolylin, 0, initialPolylin.getPointsNb()-4,appendingPath, 0, appendingPath.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

				currentEndOrientation = orientation;
			}
		}
	}*/

}

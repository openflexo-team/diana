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

public class AdjustableFirstControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger LOGGER = Logger.getLogger(AdjustableFirstControlPoint.class.getPackage().getName());

	private SimplifiedCardinalDirection currentStartOrientation = null;

	public AdjustableFirstControlPoint(DianaPoint point, RectPolylinConnector connector) {
		super(point, connector);
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		AffineTransform at1 = DianaUtils.convertNormalizedCoordinatesAT(getNode().getStartNode(), getNode());
		DianaArea startArea = getNode().getStartNode().getDianaShape().transform(at1);
		return new DianaSubstractionArea(new DianaPlane(), startArea, false);
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
		getPolylin().updatePointAt(1, pt);
		movedFirstCP();
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return true;
	}

	/**
	 * This method is internally called when first control point has been detected to be moved.
	 * 
	 */
	private void movedFirstCP() {
		DianaArea startArea = getConnector().retrieveAllowedStartArea(false);

		if (getConnectorSpecification().getIsStartingLocationFixed() && !getConnectorSpecification().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = getConnector().retrieveStartArea();
		}

		DianaPoint newFirstCPLocation = getPoint(); // polylin.getPointAt(1);
		DianaPoint nextCPLocation = initialPolylin.getPointAt(2);
		SimplifiedCardinalDirection initialStartOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
		SimplifiedCardinalDirection initialFirstOrientation = initialPolylin.getApproximatedOrientationOfSegment(1);
		SimplifiedCardinalDirection initialNextOrientation = initialPolylin.getSegmentNb() > 2 ? initialPolylin
				.getApproximatedOrientationOfSegment(2) : null;

		if (startArea.getOrthogonalPerspectiveArea(initialStartOrientation).containsPoint(newFirstCPLocation)) {

			// OK, the new location will not modify general structure of connector
			DianaPoint newStartPoint = startArea.nearestPointFrom(newFirstCPLocation, initialStartOrientation.getOpposite());
			if (newStartPoint == null) {
				LOGGER.warning("Could not find nearest point from " + newFirstCPLocation + " on " + startArea + " following orientation "
						+ initialStartOrientation.getOpposite());
				newStartPoint = startArea.getNearestPoint(newFirstCPLocation);
			}
			getPolylin().updatePointAt(0, newStartPoint);
			getConnector().getStartControlPoint().setPoint(newStartPoint);
			if (getConnectorSpecification().getIsStartingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedStartLocation(DianaUtils.convertNormalizedPoint(getNode(), newStartPoint, getNode().getStartNode()));
			}

			if (initialPolylin.getSegmentNb() > 3) {
				DianaSegment oppositeSegment = initialPolylin.getSegmentAt(2);
				DianaRectPolylin appendingPath1 = new DianaRectPolylin(newFirstCPLocation, initialFirstOrientation, oppositeSegment.getP2(),
						initialPolylin.getApproximatedOrientationOfSegment(2).getOpposite(), true, getConnector()
								.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath2 = new DianaRectPolylin(newFirstCPLocation, initialFirstOrientation, oppositeSegment.getP2(),
						initialPolylin.getApproximatedOrientationOfSegment(2), true, getConnector().getOverlapXResultingFromPixelOverlap(),
						getConnector().getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath = appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ? appendingPath1
						: appendingPath2;

				// debugPolylin = appendingPath;

				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1,
						appendingPath.getPointsNb() - 1);

				mergedPolylin = getConnector().mergePolylins(mergedPolylin, 0, mergedPolylin.getPointsNb() - 1, initialPolylin, 4,
						initialPolylin.getPointsNb() - 1);

				getConnector().updateWithNewPolylin(mergedPolylin, true);
			}

			else { // We go directely to end point, we have to preserve direction
				DianaRectPolylin appendingPath = new DianaRectPolylin(newFirstCPLocation, initialFirstOrientation, initialPolylin.getSegmentAt(
						2).getP2(), initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(),
						getConnector().getOverlapYResultingFromPixelOverlap());

				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1,
						appendingPath.getPointsNb() - 1);

				getConnector().updateWithNewPolylin(mergedPolylin, true);
			}

			currentStartOrientation = initialStartOrientation;

		}

		else {

			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			// SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : getConnector().getPrimitiveAllowedStartOrientations()) {
				if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(newFirstCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.
				// I don't want to hide the thuth: this is NOT good...

				// We keep here initial start orientation
				if (currentStartOrientation == null) {
					currentStartOrientation = initialStartOrientation;
				}
				orientation = currentStartOrientation;

				if (!getConnector().getAllowedStartOrientations().contains(orientation)
						&& getConnector().getAllowedStartOrientations().size() > 0) {
					orientation = getConnector().getAllowedStartOrientations().firstElement();
				}

				/*if (startArea.containsPoint(newFirstCPLocation)) {
					orientation = currentStartOrientation;
					alternativeOrientation = currentStartOrientation;
				}
				else {
					CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),newFirstCPLocation);
					orientation = quadrant.getHorizonalComponent();
					alternativeOrientation = quadrant.getVerticalComponent();
				}*/

				// CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),newFirstCPLocation);
				// orientation = currentStartOrientation;
				/*if (quadrant.getHorizonalComponent() == currentStartOrientation) {
					alternativeOrientation = quadrant.getVerticalComponent();
				}
				else {
					alternativeOrientation = quadrant.getHorizonalComponent();
				}*/

				// System.out.println("orientation="+orientation+" alternativeOrientation="+alternativeOrientation);

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of start area regarding orientation
				DianaPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(newFirstCPLocation);

				// Compute path to append
				DianaRectPolylin appendingPath;
				/*DianaRectPolylin appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(
						newStartPosition, nextCPLocation, newFirstCPLocation, 
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation),
						SimplifiedCardinalDirection.allDirectionsExcept(initialNextOrientation.getOpposite()));*/
				if (initialPolylin.getSegmentNb() > 3) {
					DianaSegment oppositeSegment = initialPolylin.getSegmentAt(2);
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(newStartPosition, oppositeSegment.getP2(),
							newFirstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				} else {
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(newStartPosition, nextCPLocation, newFirstCPLocation,
							orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(),
							getConnector().getOverlapYResultingFromPixelOverlap());
				}

				// Merge polylin
				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 1,
						initialPolylin, 3, initialPolylin.getPointsNb() - 1);

				// System.out.println("mergedPolylin="+mergedPolylin);
				// System.out.println("L'orientation est: "+mergedPolylin.getSegmentAt(0).getApproximatedOrientation());

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin, true);

			} else {
				// OK, we have found a cardinal direction in which it is possible to
				// project dragged control point

				// Compute new start position by projecting dragged control point
				// related to orientation

				DianaPoint newStartPosition = startArea.nearestPointFrom(newFirstCPLocation, orientation.getOpposite());
				if (newStartPosition == null) {
					LOGGER.warning("Could not find nearest point from " + newFirstCPLocation + " on " + startArea
							+ " following orientation " + initialStartOrientation.getOpposite());
					newStartPosition = startArea.getNearestPoint(newFirstCPLocation);
				}
				getPolylin().updatePointAt(0, newStartPosition);
				getConnector().getStartControlPoint().setPoint(newStartPosition);
				if (getConnectorSpecification().getIsStartingLocationFixed()) { // Don't forget this !!!
					getConnectorSpecification().setFixedStartLocation(
							DianaUtils.convertNormalizedPoint(getNode(), newStartPosition, getNode().getStartNode()));
				}

				// DianaPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(newFirstCPLocation);

				// Compute path to append
				DianaRectPolylin appendingPath;

				if (initialPolylin.getSegmentNb() > 3) {
					DianaSegment oppositeSegment = initialPolylin.getSegmentAt(2);
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(newStartPosition, oppositeSegment.getP2(),
							newFirstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector()
									.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				} else {
					appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(newStartPosition, nextCPLocation, newFirstCPLocation,
							orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(),
							getConnector().getOverlapYResultingFromPixelOverlap());
				}

				// getConnector().debugPolylin = appendingPath;

				// Merge polylin
				DianaRectPolylin mergedPolylin = getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb() - 1,
						initialPolylin, 3, initialPolylin.getPointsNb() - 1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin, true);

				currentStartOrientation = orientation;

			}
		}
	}

	/**
	 * This method is internally called when first control point has been detected to be moved.
	 * 
	 */
	/*private void movedFirstCP()
	{

		AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getStartObject(), getGraphicalRepresentation());
		DianaArea startArea = getConnector().getStartObject().getShape().getOutline().transform(at1);

		DianaPoint firstCPLocation = getPoint(); //polylin.getPointAt(1);
		DianaPoint nextCPLocation = initialPolylin.getPointAt(2);
		SimplifiedCardinalDirection initialStartOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
		SimplifiedCardinalDirection initialFirstOrientation = initialPolylin.getApproximatedOrientationOfSegment(1);
		SimplifiedCardinalDirection initialNextOrientation = (initialPolylin.getSegmentNb() > 2 ? initialPolylin.getApproximatedOrientationOfSegment(2) : null);


		if (startArea.getOrthogonalPerspectiveArea(initialStartOrientation).containsPoint(firstCPLocation)) {
			// OK, the new location will not modify general structure of connector
			DianaPoint newPoint = new DianaPoint(getPolylin().getPointAt(0));
			if (initialStartOrientation.isHorizontal()) {
				newPoint.setY(firstCPLocation.y);
			}
			else if (initialStartOrientation.isVertical()) {
				newPoint.setX(firstCPLocation.x);
			}
			getPolylin().updatePointAt(0, newPoint);
			getConnector().getStartControlPoint().setPoint(newPoint);


			if (initialPolylin.getSegmentNb() > 3) {
				DianaSegment oppositeSegment = initialPolylin.getSegmentAt(2);
				DianaRectPolylin appendingPath1 = new DianaRectPolylin(
						firstCPLocation,initialFirstOrientation,
						oppositeSegment.getP2(),initialPolylin.getApproximatedOrientationOfSegment(2).getOpposite(),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath2 = new DianaRectPolylin(
						firstCPLocation,initialFirstOrientation,
						oppositeSegment.getP2(),initialPolylin.getApproximatedOrientationOfSegment(2),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
				DianaRectPolylin appendingPath = (appendingPath1.getPointsNb() <= appendingPath2.getPointsNb() ?
						appendingPath1 : appendingPath2);

				//debugPolylin = appendingPath;

				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1, appendingPath.getPointsNb()-1);

				mergedPolylin = getConnector().mergePolylins(
						mergedPolylin, 0, mergedPolylin.getPointsNb()-1, 
						initialPolylin, 4, initialPolylin.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);
			}		

			else { // We go directely to end point, we have to preserve direction
				DianaRectPolylin appendingPath = new DianaRectPolylin(
						firstCPLocation,initialFirstOrientation,
						initialPolylin.getSegmentAt(2).getP2(),initialNextOrientation.getOpposite(),
						true,getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(getPolylin(), 0, 1, appendingPath, 1, appendingPath.getPointsNb()-1);

				getConnector().updateWithNewPolylin(mergedPolylin);
			}

			currentStartOrientation = initialStartOrientation;

		}

		else {

			// Try to find a cardinal direction in which it is possible to project
			// dragged control point
			SimplifiedCardinalDirection orientation = null;
			SimplifiedCardinalDirection alternativeOrientation = null;
			for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
				if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(firstCPLocation)) {
					orientation = o;
				}
			}

			if (orientation == null) {
				// Control point has just moved in an area which is not in any
				// orthogonal perspective area of starting shape.


				// We keep here initial start orientation
				if (currentStartOrientation == null) currentStartOrientation = initialStartOrientation;
				orientation = currentStartOrientation;

				if (startArea.containsPoint(firstCPLocation)) {
					orientation = currentStartOrientation;
					alternativeOrientation = currentStartOrientation;
				}
				else {
					CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),firstCPLocation);
					orientation = quadrant.getHorizonalComponent();
					alternativeOrientation = quadrant.getVerticalComponent();
				}

				// Compute new start position by getting nearest point of dragged point
				// located on anchor area of start area regarding orientation
				DianaPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(firstCPLocation);

				// Compute path to append
				DianaRectPolylin appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(
						newStartPosition, nextCPLocation, firstCPLocation, 
						true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(),
						SimplifiedCardinalDirection.allDirectionsExcept(orientation,alternativeOrientation),
						SimplifiedCardinalDirection.allDirectionsExcept(initialNextOrientation.getOpposite()));

				// Merge polylin
				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-1, initialPolylin, 3, initialPolylin.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

			}
			else {
				// OK, we have found a cardinal direction in which it is possible to 
				// project dragged control point

				// Compute new start position by projecting dragged control point
				// related to orientation
				DianaPoint newStartPosition = startArea.getAnchorAreaFrom(orientation).getNearestPoint(firstCPLocation);

				// Compute path to append
				DianaRectPolylin appendingPath = DianaRectPolylin.makeRectPolylinCrossingPoint(
						newStartPosition, nextCPLocation, firstCPLocation, orientation, initialNextOrientation.getOpposite(), true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

				// Merge polylin
				DianaRectPolylin mergedPolylin 
				= getConnector().mergePolylins(appendingPath, 0, appendingPath.getPointsNb()-1, initialPolylin, 3, initialPolylin.getPointsNb()-1);

				// Update with this new polylin
				getConnector().updateWithNewPolylin(mergedPolylin);

				currentStartOrientation = orientation;

			}
		}
	}*/

}

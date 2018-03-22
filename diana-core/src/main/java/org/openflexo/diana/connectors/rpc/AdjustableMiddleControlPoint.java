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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;

public class AdjustableMiddleControlPoint extends RectPolylinAdjustableControlPoint {
	static final Logger LOGGER = Logger.getLogger(AdjustableMiddleControlPoint.class.getPackage().getName());

	public AdjustableMiddleControlPoint(DianaPoint point, RectPolylinConnector connector) {
		super(point, connector);
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		return new DianaPlane();
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
		pt.x += DianaGeometricObject.EPSILON;
		pt.y += DianaGeometricObject.EPSILON;
		setPoint(pt);
		getPolylin().updatePointAt(1, pt);
		movedMiddleCP();
		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return true;
	}

	private void movedMiddleCP() {
		DianaArea startArea = getConnector().retrieveAllowedStartArea(false);

		Vector<SimplifiedCardinalDirection> allowedStartOrientations = getConnector().getPrimitiveAllowedStartOrientations();
		Vector<SimplifiedCardinalDirection> allowedEndOrientations = getConnector().getPrimitiveAllowedEndOrientations();

		if (getConnectorSpecification().getIsStartingLocationFixed() && !getConnectorSpecification().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = getConnector().retrieveStartArea();
			allowedStartOrientations = getConnector().getAllowedStartOrientations();
		}

		DianaArea endArea = getConnector().retrieveAllowedEndArea(false);

		if (getConnectorSpecification().getIsEndingLocationFixed() && !getConnectorSpecification().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = getConnector().retrieveEndArea();
			allowedEndOrientations = getConnector().getAllowedEndOrientations();
		}

		/*System.out.println("startArea="+startArea);
		System.out.println("endArea="+endArea);
		System.out.println("allowedStartOrientations="+allowedStartOrientations);
		System.out.println("allowedEndOrientations="+allowedEndOrientations);*/

		DianaRectPolylin newPolylin;

		DianaPoint middleCPLocation = getPoint();

		newPolylin = DianaRectPolylin.makeRectPolylinCrossingPoint(startArea, endArea, middleCPLocation, true, getConnector()
				.getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap(), SimplifiedCardinalDirection
				.allDirectionsExcept(allowedStartOrientations), SimplifiedCardinalDirection.allDirectionsExcept(allowedEndOrientations));

		if (newPolylin == null) {
			LOGGER.warning("Obtained null polylin allowedStartOrientations=" + allowedStartOrientations);
			return;
		}

		if (getConnectorSpecification().getIsStartingLocationFixed()) { // Don't forget this !!!
			getConnector().setFixedStartLocation(
					DianaUtils.convertNormalizedPoint(getNode(), newPolylin.getFirstPoint(), getNode().getStartNode()));
		}
		if (getConnectorSpecification().getIsEndingLocationFixed()) { // Don't forget this !!!
			getConnector().setFixedEndLocation(
					DianaUtils.convertNormalizedPoint(getNode(), newPolylin.getLastPoint(), getNode().getEndNode()));
		}

		if (newPolylin.isNormalized()) {
			getConnector().updateWithNewPolylin(newPolylin, true, false);
		} else {
			LOGGER.warning("Computed layout returned a non-normalized polylin. Please investigate");
			getConnector().updateWithNewPolylin(newPolylin, false, false);
		}

	}

	/**
	 * This method is internally called when first control point has been detected to be moved.
	 * 
	 */
	/*private void movedMiddleCP()
	{

		DianaArea startArea = getConnector().retrieveAllowedStartArea(false);
		
		if (getConnector().getIsStartingLocationFixed() 
				&& !getConnector().getIsStartingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			startArea = getConnector().retrieveStartArea();
		}
						
		DianaArea endArea = getConnector().retrieveAllowedEndArea(false);
		
		if (getConnector().getIsEndingLocationFixed() 
				&& !getConnector().getIsEndingLocationDraggable()) {
			// If starting location is fixed and not draggable,
			// Then retrieve start area itself (which is here a single point)
			endArea = getConnector().retrieveEndArea();
		}
		
		DianaPoint middleCPLocation = getPoint();
		//SimplifiedCardinalDirection initialStartOrientation = initialPolylin.getApproximatedOrientationOfSegment(0);
		//SimplifiedCardinalDirection initialEndOrientation = initialPolylin.getApproximatedOrientationOfSegment(1).getOpposite();

		// Try to find a cardinal direction in which it is possible to project
		// dragged control point
		SimplifiedCardinalDirection startOrientation = null;
		SimplifiedCardinalDirection altStartOrientation = null;
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (startArea.getOrthogonalPerspectiveArea(o).containsPoint(middleCPLocation)) {
				startOrientation = o;
			}
		}
		if (startOrientation == null) {
			CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(getPolylin().getFirstPoint(),middleCPLocation);			
			startOrientation = quadrant.getHorizonalComponent();
			altStartOrientation = quadrant.getVerticalComponent();
		}

		// Try to find a cardinal direction in which it is possible to project
		// dragged control point
		SimplifiedCardinalDirection endOrientation = null;
		SimplifiedCardinalDirection altEndOrientation = null;
		for (SimplifiedCardinalDirection o : SimplifiedCardinalDirection.values()) {
			if (endArea.getOrthogonalPerspectiveArea(o).containsPoint(middleCPLocation)) {
				endOrientation = o;
			}
		}
		if (endOrientation == null) {
			CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(middleCPLocation, getPolylin().getLastPoint());			
			endOrientation = quadrant.getHorizonalComponent().getOpposite();
			altEndOrientation = quadrant.getVerticalComponent().getOpposite();
		}



		if (startArea.getOrthogonalPerspectiveArea(startOrientation).containsPoint(middleCPLocation)) {
			// OK, the new location will not modify general structure of connector
			DianaPoint newPoint = new DianaPoint(getPolylin().getFirstPoint());
			if (startOrientation.isHorizontal()) {
				newPoint.setY(middleCPLocation.y);
			}
			else if (startOrientation.isVertical()) {
				newPoint.setX(middleCPLocation.x);
			}
			getPolylin().updatePointAt(0, newPoint);
			getConnector().getStartControlPoint().setPoint(newPoint);
			if (getConnector().getIsStartingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedStartLocation(
						GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), newPoint, getGraphicalRepresentation().getStartObject()));
			}
		}

		if (endArea.getOrthogonalPerspectiveArea(endOrientation).containsPoint(middleCPLocation)) {
			// OK, the new location will not modify general structure of connector
			DianaPoint newPoint = new DianaPoint(getPolylin().getLastPoint());
			if (endOrientation.isHorizontal()) {
				newPoint.setY(middleCPLocation.y);
			}
			else if (endOrientation.isVertical()) {
				newPoint.setX(middleCPLocation.x);
			}
			getPolylin().updatePointAt(getPolylin().getPointsNb()-1, newPoint);
			getConnector().getEndControlPoint().setPoint(newPoint);
			if (getConnector().getIsEndingLocationFixed()) { // Don't forget this !!!
				getConnector().setFixedEndLocation(
						GraphicalRepresentation.convertNormalizedPoint(getGraphicalRepresentation(), newPoint, getGraphicalRepresentation().getEndObject()));
			}
		}

		System.out.println("Tiens, je sais pas quoi faire...");
		
		int alternatives = 1;
		if (altStartOrientation != null) alternatives=alternatives*2;
		if (altEndOrientation != null) alternatives=alternatives*2;
		DianaRectPolylin[] newPolylins = new DianaRectPolylin[alternatives]; 

		int n=0;
		newPolylins[n++] 
		            = DianaRectPolylin.makeRectPolylinCrossingPoint(
		            		getPolylin().getFirstPoint(), 
		            		getPolylin().getLastPoint(), 
		            		middleCPLocation, 
		            		startOrientation,
		            		endOrientation,
		            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());

		if (altStartOrientation != null) {
			newPolylins[n++] 
			            = DianaRectPolylin.makeRectPolylinCrossingPoint(
			            		getPolylin().getFirstPoint(), 
			            		getPolylin().getLastPoint(), 
			            		middleCPLocation, 
			            		altStartOrientation,
			            		endOrientation,
			            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
			if (altEndOrientation != null) {
				newPolylins[n++] 
				            = DianaRectPolylin.makeRectPolylinCrossingPoint(
				            		getPolylin().getFirstPoint(), 
				            		getPolylin().getLastPoint(), 
				            		middleCPLocation, 
				            		altStartOrientation,
				            		altEndOrientation,
				            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
			}
		}
		if (altEndOrientation != null) {
			newPolylins[n++] 
			            = DianaRectPolylin.makeRectPolylinCrossingPoint(
			            		getPolylin().getFirstPoint(), 
			            		getPolylin().getLastPoint(), 
			            		middleCPLocation, 
			            		startOrientation,
			            		altEndOrientation,
			            		true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
		}

		DianaRectPolylin newPolylin = null;

		for (int i=0; i<alternatives; i++) {
			if (!newPolylins[i].crossedItSelf() && newPolylin == null) 
				newPolylin = newPolylins[i];
		}
		if (newPolylin == null) newPolylin = newPolylins[0];


		getConnector().updateWithNewPolylin(newPolylin,true);

	}
	*/

}

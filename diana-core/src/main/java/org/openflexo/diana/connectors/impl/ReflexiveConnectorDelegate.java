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

package org.openflexo.diana.connectors.impl;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.connectors.Connector;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.connectors.LineConnectorSpecification;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorControlPoint;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalQuadrant;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolylin;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.graphics.DianaConnectorGraphics;

public class ReflexiveConnectorDelegate {

	private static final Logger LOGGER = Logger.getLogger(LineConnectorSpecification.class.getPackage().getName());

	protected DianaRectangle NORMALIZED_BOUNDS = new DianaRectangle(0, 0, 1, 1, Filling.FILLED);

	private ConnectorNode<?> connectorNode;

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ConnectorAdjustingControlPoint reflexiveConnectorControlPoint;

	private DianaRectangle connectorUsedBounds = NORMALIZED_BOUNDS;

	private CardinalQuadrant orientation; // = CardinalQuadrant.NORTH_EAST;
	private DianaPolylin polylin = null;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public ReflexiveConnectorDelegate(ConnectorNode<?> connectorNode) {
		this.connectorNode = connectorNode;

		DianaPoint cp = DianaUtils.convertNormalizedPoint(connectorNode, getReflexiveConnectorControlPoint().getPoint(), getShapeNode());
		orientation = DianaPoint.getCardinalQuadrant(getShapeNode().getShape().getShape().getCenter(), cp);
	}

	public ShapeNode<?> getShapeNode() {
		// remember that startNode = endNode
		return connectorNode.getStartNode();
	}

	public Connector<?> getConnector() {
		return connectorNode.getConnector();
	}

	public ConnectorSpecification getConnectorSpecification() {
		return connectorNode.getConnectorSpecification();
	}

	public ControlPoint getCp1() {
		return cp1;
	}

	public ControlPoint getCp2() {
		return cp2;
	}

	public ConnectorAdjustingControlPoint getReflexiveConnectorControlPoint() {

		if (reflexiveConnectorControlPoint == null) {

			DianaPoint north = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
					getShapeNode().getShape().getShape().getCenter().y - getShapeNode().getHeight());
			DianaPoint east = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x + getShapeNode().getWidth(),
					getShapeNode().getShape().getShape().getCenter().y);
			DianaPoint northP = getShapeNode().getShape().outlineIntersect(north);
			DianaPoint eastP = getShapeNode().getShape().outlineIntersect(east);

			DianaPoint newCP;
			if (getConnectorSpecification().getReflexiveControlPointLocation() == null) {
				double horizontalOverlap = DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP / (double) getShapeNode().getViewWidth(1.0);
				double verticalOverlap = DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP / (double) getShapeNode().getViewHeight(1.0);

				DianaPoint cp = new DianaPoint(eastP.x + horizontalOverlap, northP.y - verticalOverlap);
				newCP = DianaUtils.convertNormalizedPoint(getShapeNode(), cp, connectorNode);
				getConnectorSpecification().setReflexiveControlPointLocation(newCP);
			}
			else {
				newCP = getConnectorSpecification().getReflexiveControlPointLocation();
			}

			DianaPlane plane = new DianaPlane();
			DianaArea draggingArea = DianaSubstractionArea.makeSubstraction(plane, getShapeNode().getShape().getShape(), true);

			reflexiveConnectorControlPoint = new ConnectorAdjustingControlPoint(connectorNode, newCP) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					return draggingArea;
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					CardinalQuadrant newOrientation = DianaPoint.getCardinalQuadrant(getShapeNode().getShape().getShape().getCenter(),
							newRelativePoint);
					if (newOrientation != orientation) {
						orientation = newOrientation;
					}
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					getConnectorSpecification().setReflexiveControlPointLocation(pt);
					// connectorNode.notifyConnectorModified();
					refreshConnectorUsedBounds();
					connectorNode.refreshConnector();

					return true;
				}
			};

		}

		return reflexiveConnectorControlPoint;
	}

	public DianaRectangle getConnectorUsedBounds() {
		return connectorUsedBounds;
	}

	public void delete() {
	}

	public DianaPolylin updateControlPoints() {

		DianaPoint cp = DianaUtils.convertNormalizedPoint(connectorNode, getReflexiveConnectorControlPoint().getPoint(), getShapeNode());
		DianaPoint north = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
				getShapeNode().getShape().getShape().getCenter().y - getShapeNode().getHeight());
		DianaPoint south = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
				getShapeNode().getShape().getShape().getCenter().y + getShapeNode().getHeight());
		DianaPoint east = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x + getShapeNode().getWidth(),
				getShapeNode().getShape().getShape().getCenter().y);
		DianaPoint west = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x - getShapeNode().getWidth(),
				getShapeNode().getShape().getShape().getCenter().y);
		DianaPoint p1 = null, p2 = null;
		DianaPolylin localPolylin = null;

		switch (orientation) {
			case NORTH_EAST:
				if (getShapeNode().getShape().getAllowedHorizontalConnectorLocationFromEast().containsPoint(cp)) {
					p1 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeHorizontalLine(cp)).getNearestPoint(cp);
					p2 = getShapeNode().getShape().outlineIntersect(east);
					DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
					localPolylin = new DianaPolylin(p1, cp, pp2, p2);
				}
				else {
					p1 = getShapeNode().getShape().outlineIntersect(north);
					if (getShapeNode().getShape().getAllowedVerticalConnectorLocationFromNorth().containsPoint(cp)) {
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						p2 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeVerticalLine(cp)).getNearestPoint(cp);
						localPolylin = new DianaPolylin(p1, pp1, cp, p2);
					}
					else {
						p2 = getShapeNode().getShape().outlineIntersect(east);
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
						localPolylin = new DianaPolylin(p1, pp1, cp, pp2, p2);
					}
				}
				break;
			case SOUTH_EAST:
				if (getShapeNode().getShape().getAllowedHorizontalConnectorLocationFromEast().containsPoint(cp)) {
					p1 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeHorizontalLine(cp)).getNearestPoint(cp);
					p2 = getShapeNode().getShape().outlineIntersect(east);
					DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
					localPolylin = new DianaPolylin(p1, cp, pp2, p2);
				}
				else {
					p1 = getShapeNode().getShape().outlineIntersect(south);
					if (getShapeNode().getShape().getAllowedVerticalConnectorLocationFromSouth().containsPoint(cp)) {
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						p2 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeVerticalLine(cp)).getNearestPoint(cp);
						localPolylin = new DianaPolylin(p1, pp1, cp, p2);
					}
					else {
						p2 = getShapeNode().getShape().outlineIntersect(east);
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
						localPolylin = new DianaPolylin(p1, pp1, cp, pp2, p2);
					}
				}
				break;
			case NORTH_WEST:
				if (getShapeNode().getShape().getAllowedHorizontalConnectorLocationFromWest().containsPoint(cp)) {
					p1 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeHorizontalLine(cp)).getNearestPoint(cp);
					p2 = getShapeNode().getShape().outlineIntersect(west);
					DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
					localPolylin = new DianaPolylin(p1, cp, pp2, p2);
				}
				else {
					p1 = getShapeNode().getShape().outlineIntersect(north);
					if (getShapeNode().getShape().getAllowedVerticalConnectorLocationFromNorth().containsPoint(cp)) {
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						p2 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeVerticalLine(cp)).getNearestPoint(cp);
						localPolylin = new DianaPolylin(p1, pp1, cp, p2);
					}
					else {
						p2 = getShapeNode().getShape().outlineIntersect(west);
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
						localPolylin = new DianaPolylin(p1, pp1, cp, pp2, p2);
					}
				}
				break;
			case SOUTH_WEST:
				if (getShapeNode().getShape().getAllowedHorizontalConnectorLocationFromWest().containsPoint(cp)) {
					p1 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeHorizontalLine(cp)).getNearestPoint(cp);
					p2 = getShapeNode().getShape().outlineIntersect(west);
					DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
					localPolylin = new DianaPolylin(p1, cp, pp2, p2);
				}
				else {
					p1 = getShapeNode().getShape().outlineIntersect(south);
					if (getShapeNode().getShape().getAllowedVerticalConnectorLocationFromSouth().containsPoint(cp)) {
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						p2 = getShapeNode().getShape().getOutline().intersect(DianaLine.makeVerticalLine(cp)).getNearestPoint(cp);
						localPolylin = new DianaPolylin(p1, pp1, cp, p2);
					}
					else {
						p2 = getShapeNode().getShape().outlineIntersect(west);
						DianaPoint pp1 = new DianaPoint(p1.x, cp.y);
						DianaPoint pp2 = new DianaPoint(cp.x, p2.y);
						localPolylin = new DianaPolylin(p1, pp1, cp, pp2, p2);
					}
				}
				break;

			default:
				break;
		}

		cp1 = new ConnectorControlPoint(connectorNode, DianaUtils.convertNormalizedPoint(getShapeNode(), p2, connectorNode));
		cp2 = new ConnectorControlPoint(connectorNode, DianaUtils.convertNormalizedPoint(getShapeNode(), p1, connectorNode));

		AffineTransform at = DianaUtils.convertNormalizedCoordinatesAT(getShapeNode(), connectorNode);
		polylin = localPolylin.transform(at);

		return polylin;

	}

	protected void refreshConnectorUsedBounds() {

		DianaPoint north = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
				getShapeNode().getShape().getShape().getCenter().y - getShapeNode().getHeight());
		DianaPoint south = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
				getShapeNode().getShape().getShape().getCenter().y + getShapeNode().getHeight());
		DianaPoint east = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x + getShapeNode().getWidth(),
				getShapeNode().getShape().getShape().getCenter().y);
		DianaPoint west = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x - getShapeNode().getWidth(),
				getShapeNode().getShape().getShape().getCenter().y);
		DianaPoint northP = getShapeNode().getShape().outlineIntersect(north);
		DianaPoint southP = getShapeNode().getShape().outlineIntersect(south);
		DianaPoint eastP = getShapeNode().getShape().outlineIntersect(east);
		DianaPoint westP = getShapeNode().getShape().outlineIntersect(west);

		double horizontalOverlap = 0;
		double verticalOverlap = 0;

		switch (orientation) {
			case NORTH_EAST:
				horizontalOverlap = (reflexiveConnectorControlPoint.getPoint().getX() - eastP.getX()) * 1.2;
				verticalOverlap = (-reflexiveConnectorControlPoint.getPoint().getY() + northP.getY()) * 1.2;
				connectorUsedBounds = new DianaRectangle(0, -verticalOverlap, 1 + horizontalOverlap, 1 + verticalOverlap, Filling.FILLED);
				break;
			case SOUTH_EAST:
				horizontalOverlap = (reflexiveConnectorControlPoint.getPoint().getX() - eastP.getX()) * 1.2;
				verticalOverlap = (reflexiveConnectorControlPoint.getPoint().getY() - southP.getY()) * 1.2;
				connectorUsedBounds = new DianaRectangle(0, 0, 1 + horizontalOverlap, 1 + verticalOverlap, Filling.FILLED);
				break;
			case NORTH_WEST:
				horizontalOverlap = (-reflexiveConnectorControlPoint.getPoint().getX() + westP.getX()) * 1.2;
				verticalOverlap = (-reflexiveConnectorControlPoint.getPoint().getY() + northP.getY()) * 1.2;
				connectorUsedBounds = new DianaRectangle(-horizontalOverlap, -verticalOverlap, 1 + horizontalOverlap, 1 + verticalOverlap,
						Filling.FILLED);
				break;
			case SOUTH_WEST:
				horizontalOverlap = (-reflexiveConnectorControlPoint.getPoint().getX() + westP.getX()) * 1.2;
				verticalOverlap = (reflexiveConnectorControlPoint.getPoint().getY() - southP.getY()) * 1.2;
				connectorUsedBounds = new DianaRectangle(-horizontalOverlap, 0, 1 + horizontalOverlap, 1 + verticalOverlap, Filling.FILLED);
				break;
			default:
				break;
		}
	}

	public void drawConnector(DianaConnectorGraphics g) {
		g.useDefaultForegroundStyle();

		g.drawPolyline(polylin);

		// Draw eventual symbols
		if (polylin != null && polylin.getSegments() != null && polylin.getSegments().size() > 0) {
			// Segments are here all orthogonal, we can can then rely on getAngle() computation performed on geom layer
			// (we dont need to convert to view first)
			if (getConnector().getStartSymbol() != StartSymbolType.NONE) {
				DianaSegment firstSegment = polylin.getSegments().firstElement();
				if (firstSegment != null) {
					g.drawSymbol(firstSegment.getP1(), getConnector().getStartSymbol(), getConnector().getStartSymbolSize(),
							firstSegment.getAngle());
				}
			}
			if (getConnector().getEndSymbol() != EndSymbolType.NONE) {
				DianaSegment lastSegment = polylin.getSegments().lastElement();
				if (lastSegment != null) {
					g.drawSymbol(lastSegment.getP2(), getConnector().getEndSymbol(), getConnector().getEndSymbolSize(),
							lastSegment.getAngle() + Math.PI);
				}
			}
			if (getConnector().getMiddleSymbol() != MiddleSymbolType.NONE) {
				g.drawSymbol(getConnector().getMiddleSymbolLocation(), getConnector().getMiddleSymbol(),
						getConnector().getMiddleSymbolSize(), getMiddleSymbolAngle());
			}
		}

	}

	public DianaPoint getMiddleSymbolLocation() {
		if (polylin == null) {
			return new DianaPoint(0, 0);
		}
		AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);
		DianaPolylin transformedPolylin = polylin.transform(at);
		DianaPoint point = transformedPolylin.getPointAtRelativePosition(getConnector().getRelativeMiddleSymbolLocation());
		try {
			return point.transform(at.createInverse());
		} catch (NoninvertibleTransformException e) {
			e.printStackTrace();
		}
		return new DianaPoint(0, 0);
	}

	/**
	 * 
	 * @return angle expressed in radians
	 */
	public double getMiddleSymbolAngle() {
		if (polylin == null) {
			return 0;
		}

		DianaPoint middleSymbolLocation = getConnector().getMiddleSymbolLocation();
		DianaSegment relatedSegment = polylin.getNearestSegment(middleSymbolLocation);

		if (relatedSegment == null) {
			return 0;
		}
		return relatedSegment.getAngle() + Math.PI;
	}

	public double distanceToConnector(DianaPoint aPoint, double scale) {

		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		DianaPoint nearestPoint = polylin.getNearestPoint(aPoint);
		Point nearestPointInView = connectorNode.convertNormalizedPointToViewCoordinates(nearestPoint, scale);
		return DianaPoint.distance(nearestPointInView.x, nearestPointInView.y, testPoint.x, testPoint.y);
	}

}

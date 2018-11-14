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
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.connectors.LineConnectorSpecification;
import org.openflexo.diana.connectors.LineConnectorSpecification.LineConnectorType;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorControlPoint;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaGeometricObject.CardinalQuadrant;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.GeomUtils;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.graphics.DianaConnectorGraphics;

public class LineConnector extends ConnectorImpl<LineConnectorSpecification> {

	private static final Logger LOGGER = Logger.getLogger(LineConnectorSpecification.class.getPackage().getName());

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ConnectorAdjustingControlPoint middleSymbolLocationControlPoint;
	private List<ControlPoint> controlPoints;

	private boolean firstUpdated = false;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public LineConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlPoints = new ArrayList<>();
		if (getLineConnectorType() == null) {
			setLineConnectorType(getConnectorSpecification().getLineConnectorType());
		}
	}

	@Override
	public void delete() {
		super.delete();
		cp1 = null;
		cp2 = null;
		middleSymbolLocationControlPoint = null;
		controlPoints.clear();
		controlPoints = null;
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		// TODO: perfs issue : do not update all the time !!!
		// updateControlPoints();
		return controlPoints;
	}

	public DianaPoint getCp1RelativeToStartObject() {
		return getPropertyValue(LineConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT);
	}

	public void setCp1RelativeToStartObject(DianaPoint aPoint) {
		setPropertyValue(LineConnectorSpecification.CP1_RELATIVE_TO_START_OBJECT, aPoint);
	}

	public DianaPoint getCp2RelativeToEndObject() {
		return getPropertyValue(LineConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT);
	}

	public void setCp2RelativeToEndObject(DianaPoint aPoint) {
		setPropertyValue(LineConnectorSpecification.CP2_RELATIVE_TO_END_OBJECT, aPoint);
	}

	public LineConnectorType getLineConnectorType() {
		return getPropertyValue(LineConnectorSpecification.LINE_CONNECTOR_TYPE);
	}

	public void setLineConnectorType(LineConnectorType aLineConnectorType) {
		setPropertyValue(LineConnectorSpecification.LINE_CONNECTOR_TYPE, aLineConnectorType);
	}

	private ConnectorAdjustingControlPoint makeMiddleSymbolLocationControlPoint() {
		middleSymbolLocationControlPoint = new ConnectorAdjustingControlPoint(connectorNode, getMiddleSymbolLocation()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaSegment(cp1.getPoint(), cp2.getPoint());
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				// logger.info("OK, moving to "+point);
				DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
				setPoint(pt);
				DianaSegment segment = new DianaSegment(cp1.getPoint(), cp2.getPoint());
				setRelativeMiddleSymbolLocation(segment.getRelativeLocation(pt));

				/*
				 * cp1RelativeToStartObject = GraphicalRepresentation.convertNormalizedPoint( getGraphicalRepresentation(), pt,
				 * getStartNode());
				 */
				connectorNode.notifyConnectorModified();
				return true;
			}

		};
		return middleSymbolLocationControlPoint;
	}

	private void updateControlPoints() {
		if (getLineConnectorType() == LineConnectorType.CENTER_TO_CENTER) {

			// With this connection type, we try to draw a line joining both center
			// We have to compute the intersection between this line and the outline
			// of joined shapes

			DianaPoint centerOfEndObjectSeenFromStartObject = DianaUtils.convertNormalizedPoint(getEndNode(), new DianaPoint(0.5, 0.5),
					getStartNode());
			DianaPoint pointOnStartObject = getStartNode().getShape().outlineIntersect(centerOfEndObjectSeenFromStartObject);
			if (pointOnStartObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnStartObject = new DianaPoint(0.5, 0.5);
			}
			DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);

			DianaPoint centerOfStartObjectSeenFromEndObject = DianaUtils.convertNormalizedPoint(getStartNode(), new DianaPoint(0.5, 0.5),
					getEndNode());
			DianaPoint pointOnEndObject = getEndNode().getShape().outlineIntersect(centerOfStartObjectSeenFromEndObject);
			if (pointOnEndObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnEndObject = new DianaPoint(0.5, 0.5);
			}
			DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);

			// cp1.setPoint(newP1);
			// cp2.setPoint(newP2);

			cp1 = new ConnectorControlPoint(connectorNode, newP1);
			cp2 = new ConnectorControlPoint(connectorNode, newP2);

			controlPoints.clear();
			if (getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint());
			}
			controlPoints.add(cp2);
			controlPoints.add(cp1);

		}

		else if (getLineConnectorType() == LineConnectorType.MINIMAL_LENGTH) {

			// First obtain the two affine transform allowing to convert from
			// extremity objects coordinates to connector drawable

			AffineTransform at1 = DianaUtils.convertNormalizedCoordinatesAT(getStartNode(), connectorNode);

			AffineTransform at2 = DianaUtils.convertNormalizedCoordinatesAT(getEndNode(), connectorNode);

			// Then compute first order covering area for both extremities

			DianaArea coveringArea = computeCoveringArea(1);

			if (coveringArea instanceof DianaRectangle) {
				// The covering area is a rectangle:
				// This means that the two connector have a common connecting area
				// along x-axis or y-axis: this area is the obtained rectangle

				DianaRectangle r = (DianaRectangle) coveringArea;

				DianaPoint startMiddle = getStartNode().getDianaShape().getCenter().transform(at1);
				DianaPoint endMiddle = getEndNode().getDianaShape().getCenter().transform(at2);
				DianaPoint pointOnStartObject, pointOnEndObject;

				// According to the relative orientation of both objects, compute points on start
				// object and end object, as middle of rectangle covering area

				SimplifiedCardinalDirection orientation = DianaPoint.getSimplifiedOrientation(startMiddle, endMiddle);
				if (orientation == SimplifiedCardinalDirection.NORTH) {
					pointOnStartObject = r.getNorth().getMiddle();
					pointOnEndObject = r.getSouth().getMiddle();
				}
				else if (orientation == SimplifiedCardinalDirection.EAST) {
					pointOnStartObject = r.getEast().getMiddle();
					pointOnEndObject = r.getWest().getMiddle();
				}
				else if (orientation == SimplifiedCardinalDirection.SOUTH) {
					pointOnStartObject = r.getSouth().getMiddle();
					pointOnEndObject = r.getNorth().getMiddle();
				}
				else /* orientation == CardinalDirection.WEST */ {
					pointOnStartObject = r.getWest().getMiddle();
					pointOnEndObject = r.getEast().getMiddle();
				}

				// Now, we still are not sure that obtained points are located on shape
				// So we must project them on shape to find nearest point located on
				// outline (using nearestOutlinePoint(DianaPoint) method)

				pointOnStartObject = DianaUtils.convertNormalizedPoint(connectorNode, pointOnStartObject, getStartNode());
				pointOnStartObject = getStartNode().getShape().nearestOutlinePoint(pointOnStartObject);

				pointOnEndObject = DianaUtils.convertNormalizedPoint(connectorNode, pointOnEndObject, getEndNode());
				pointOnEndObject = getEndNode().getShape().nearestOutlinePoint(pointOnEndObject);

				// Coordinates are expressed in object relative coordinates
				// Convert them to local coordinates

				DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);

				DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);

				// And assign values to existing points.
				// cp1.setPoint(newP1);
				// cp2.setPoint(newP2);

				cp1 = new ConnectorControlPoint(connectorNode, newP1);
				cp2 = new ConnectorControlPoint(connectorNode, newP2);
				controlPoints.clear();
				if (getMiddleSymbol() != MiddleSymbolType.NONE) {
					controlPoints.add(makeMiddleSymbolLocationControlPoint());
				}
				controlPoints.add(cp2);
				controlPoints.add(cp1);

				// That's all folks !
				// (for rectangle)

			}

			else if (coveringArea instanceof DianaEmptyArea) {
				// In this case, we have to join shapes using a line connecting
				// biased cardinal points of embedding rectangle

				DianaPoint startMiddle = getStartNode().getDianaShape().getCenter().transform(at1);
				DianaPoint endMiddle = getEndNode().getDianaShape().getCenter().transform(at2);
				DianaPoint pointOnStartObject, pointOnEndObject;

				CardinalQuadrant orientation = DianaPoint.getCardinalQuadrant(startMiddle, endMiddle);

				if (orientation == CardinalQuadrant.NORTH_WEST) {
					pointOnStartObject = new DianaPoint(0, 0);
					pointOnEndObject = new DianaPoint(1, 1);
				}
				else if (orientation == CardinalQuadrant.SOUTH_WEST) {
					pointOnStartObject = new DianaPoint(0, 1);
					pointOnEndObject = new DianaPoint(1, 0);
				}
				else if (orientation == CardinalQuadrant.NORTH_EAST) {
					pointOnStartObject = new DianaPoint(1, 0);
					pointOnEndObject = new DianaPoint(0, 1);
				}
				else /* orientation == BiasedCardinalDirection.SOUTH_EAST */ {
					pointOnStartObject = new DianaPoint(1, 1);
					pointOnEndObject = new DianaPoint(0, 0);
				}

				// We compute nearest outline point
				pointOnStartObject = getStartNode().getShape().nearestOutlinePoint(pointOnStartObject);
				pointOnEndObject = getEndNode().getShape().nearestOutlinePoint(pointOnEndObject);

				// And then we convert to local coordinates
				DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);
				DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);

				// Finally assign values to existing points.
				// cp1.setPoint(newP1);
				// cp2.setPoint(newP2);

				cp1 = new ConnectorControlPoint(connectorNode, newP1);
				cp2 = new ConnectorControlPoint(connectorNode, newP2);
				controlPoints.clear();
				controlPoints.add(cp2);
				controlPoints.add(cp1);

				if (getMiddleSymbol() != MiddleSymbolType.NONE) {
					controlPoints.add(makeMiddleSymbolLocationControlPoint());
				}
			}

			else {
				LOGGER.warning("Unexpected covering area found : " + coveringArea);
			}
		}

		else if (getLineConnectorType() == LineConnectorType.FUNNY) {

			DianaPoint newP1 = connectorNode.getEndNode().getShape()
					.nearestOutlinePoint(DianaUtils.convertNormalizedPoint(getStartNode(), new DianaPoint(0.5, 0.5), getEndNode()));
			newP1 = DianaUtils.convertNormalizedPoint(getEndNode(), newP1, connectorNode);

			DianaPoint newP2 = connectorNode.getStartNode().getShape()
					.nearestOutlinePoint(DianaUtils.convertNormalizedPoint(getEndNode(), new DianaPoint(0.5, 0.5), getStartNode()));
			newP2 = DianaUtils.convertNormalizedPoint(getStartNode(), newP2, connectorNode);

			// cp1.setPoint(newP1);
			// cp2.setPoint(newP2);

			cp1 = new ConnectorControlPoint(connectorNode, newP1);
			cp2 = new ConnectorControlPoint(connectorNode, newP2);
			controlPoints.clear();
			controlPoints.add(cp2);
			controlPoints.add(cp1);

			if (getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint());
			}
		}

		else if (getLineConnectorType() == LineConnectorType.ADJUSTABLE) {

			if (getCp1RelativeToStartObject() == null || getCp2RelativeToEndObject() == null) {
				// In this case default location is obtained by center_to_center type
				setLineConnectorType(LineConnectorType.CENTER_TO_CENTER);
				updateControlPoints();
				setLineConnectorType(LineConnectorType.ADJUSTABLE);
				setCp1RelativeToStartObject(DianaUtils.convertNormalizedPoint(connectorNode, cp1.getPoint(), getStartNode()));
				setCp2RelativeToEndObject(DianaUtils.convertNormalizedPoint(connectorNode, cp2.getPoint(), getEndNode()));
			}

			// We have either the old position, or the default one
			// We need now to find updated position according to eventual shape move, resize, reshaped, etc...
			// To do that, use outlineIntersect();

			DianaPoint newP1 = null; /* = cp1.getPoint(); */
			if (cp1 != null) {
				newP1 = cp1.getPoint();
			}
			setCp1RelativeToStartObject(getStartNode().getShape().outlineIntersect(getCp1RelativeToStartObject()));
			if (getCp1RelativeToStartObject() != null) {
				newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), getCp1RelativeToStartObject(), connectorNode);
			}

			DianaPoint newP2 = null; /* = cp2.getPoint(); */
			if (cp2 != null) {
				newP2 = cp2.getPoint();
			}
			setCp2RelativeToEndObject(getEndNode().getShape().outlineIntersect(getCp2RelativeToEndObject()));
			if (getCp2RelativeToEndObject() != null) {
				newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), getCp2RelativeToEndObject(), connectorNode);
			}

			cp1 = new ConnectorAdjustingControlPoint(connectorNode, newP1) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					DianaShape<?> shape = getStartNode().getDianaShape();
					return shape.transform(DianaUtils.convertNormalizedCoordinatesAT(getStartNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					// logger.info("OK, moving to "+point);
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					setCp1RelativeToStartObject(DianaUtils.convertNormalizedPoint(connectorNode, pt, getStartNode()));
					connectorNode.notifyConnectorModified();
					return true;
				}

			};

			cp2 = new ConnectorAdjustingControlPoint(connectorNode, newP2) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					DianaShape<?> shape = getEndNode().getDianaShape();
					return shape.transform(DianaUtils.convertNormalizedCoordinatesAT(getEndNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					// logger.info("OK, moving to "+point);
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					setCp2RelativeToEndObject(DianaUtils.convertNormalizedPoint(connectorNode, pt, getEndNode()));
					if (getMiddleSymbol() != MiddleSymbolType.NONE) {
						if (middleSymbolLocationControlPoint != null) {
							middleSymbolLocationControlPoint.setPoint(getMiddleSymbolLocation());
						}
					}
					connectorNode.notifyConnectorModified();
					return true;
				}
			};

			controlPoints.clear();
			controlPoints.add(cp2);
			controlPoints.add(cp1);

			if (getMiddleSymbol() != MiddleSymbolType.NONE) {
				controlPoints.add(makeMiddleSymbolLocationControlPoint());
			}
		}

		else {

			LOGGER.warning("Unexpected lineConnectorType=" + getLineConnectorType());
		}
	}

	@Override
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			return;
		}

		updateControlPoints();

		super.refreshConnector(force);

		firstUpdated = true;

		connectorNode.notifyConnectorModified();

	}

	@Override
	public boolean needsRefresh() {
		if (!firstUpdated) {
			return true;
		}
		return super.needsRefresh();
	}

	@Override
	public void drawConnector(DianaConnectorGraphics g) {
		// FD : seems unused => boolean wasFirstUpdated = firstUpdated;
		if (!firstUpdated) {
			refreshConnector();
		}

		/*
		 * if (DianaConstants.DEBUG || getGraphicalRepresentation().getDebugCoveringArea()) { drawCoveringAreas(g); }
		 */

		/*
		 * if (lineConnectorType == LineConnectorType.ADJUSTABLE) { g.setForeground(fs0); g.setBackground(bs0); DianaShape<?> shape =
		 * getStartNode().getShape().getShape();
		 * shape.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(getStartNode().getDrawable(), getDrawable())) .paint(g);
		 * g.setForeground(fs1); g.setBackground(bs1); DianaShape<?> shape2 = getEndNode().getShape().getShape();
		 * shape2.transform(GraphicalRepresentation.convertNormalizedCoordinatesAT(getEndNode().getDrawable(), getDrawable())) .paint(g);
		 * }
		 */

		if (cp1 == null || cp2 == null) {
			return;
		}

		g.useDefaultForegroundStyle();
		// logger.info("paintConnector() "+cp1.getPoint()+"-"+cp2.getPoint()+" with "+g.getCurrentForeground());
		g.drawLine(cp1.getPoint(), cp2.getPoint());

		Point cp1InView = connectorNode.convertNormalizedPointToViewCoordinates(cp1.getPoint(), 1);
		Point cp2InView = connectorNode.convertNormalizedPointToViewCoordinates(cp2.getPoint(), 1);

		// double angle = Math.atan2(cp2.getPoint().x-cp1.getPoint().x, cp2.getPoint().y-cp1.getPoint().y)+Math.PI/2;
		double angle = Math.atan2(cp2InView.x - cp1InView.x, cp2InView.y - cp1InView.y) + Math.PI / 2;

		// System.out.println("Angle1="+Math.toDegrees(angle));
		// System.out.println("Angle2="+Math.toDegrees(angle+Math.PI));

		if (getStartSymbol() != StartSymbolType.NONE) {
			g.drawSymbol(cp1.getPoint(), getStartSymbol(), getStartSymbolSize(), angle);
		}

		if (getEndSymbol() != EndSymbolType.NONE) {
			g.drawSymbol(cp2.getPoint(), getEndSymbol(), getEndSymbolSize(), angle + Math.PI);
		}

		if (getMiddleSymbol() != MiddleSymbolType.NONE) {
			g.drawSymbol(getMiddleSymbolLocation(), getMiddleSymbol(), getMiddleSymbolSize(), angle + Math.PI);
		}
	}

	@Override
	public double getStartAngle() {
		return GeomUtils.getSlope(cp1.getPoint(), cp2.getPoint());
	}

	@Override
	public double getEndAngle() {
		return GeomUtils.getSlope(cp2.getPoint(), cp1.getPoint());
	}

	@Override
	public DianaPoint getMiddleSymbolLocation() {
		if (cp1 == null || cp2 == null) {
			return new DianaPoint(0, 0);
		}
		return new DianaSegment(cp1.getPoint(), cp2.getPoint()).getScaledPoint(getRelativeMiddleSymbolLocation());
	}

	@Override
	public double distanceToConnector(DianaPoint aPoint, double scale) {
		if (cp1 == null || cp2 == null) {
			LOGGER.warning("Invalid date in LineConnectorSpecification: control points are null");
			return Double.POSITIVE_INFINITY;
		}
		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(cp1.getPoint(), scale);
		Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(cp2.getPoint(), scale);
		return Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
	}

	@Override
	public DianaRectangle getConnectorUsedBounds() {
		return NORMALIZED_BOUNDS;
	}

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public DianaPoint getStartLocation() {
		return getCp1RelativeToStartObject();
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public DianaPoint getEndLocation() {
		return getCp2RelativeToEndObject();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		// If ConnectorNode is either null or deleted, ignore this event
		// (This might happen during some edition phases with inspectors)
		if (getConnectorNode() == null) {
			// logger.warning("Called getPropertyValue() for null ConnectorNode");
			return;
		}
		else if (getConnectorNode().isDeleted()) {
			// logger.warning("Called getPropertyValue() for deleted ConnectorNode");
			return;
		}

		super.propertyChange(evt);

		if (temporaryIgnoredObservables.contains(evt.getSource())) {
			// System.out.println("IGORE NOTIFICATION " + notification);
			return;
		}

		if (evt.getSource() == getConnectorSpecification()) {
			if (evt.getPropertyName().equals(LineConnectorSpecification.LINE_CONNECTOR_TYPE.getName())) {
				refreshConnector(true);
			}
		}

	}

}
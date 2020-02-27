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
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.connectors.CurveConnectorSpecification;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorControlPoint;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaGeometricObject;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaQuadCurve;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.GeomUtils;
import org.openflexo.diana.geom.ParallelLinesException;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.graphics.DianaConnectorGraphics;

public class CurveConnector extends ConnectorImpl<CurveConnectorSpecification> {

	private static final Logger LOGGER = Logger.getLogger(CurveConnectorSpecification.class.getPackage().getName());

	private ControlPoint cp1;
	private ControlPoint cp2;
	private ControlPoint cp;
	private List<ControlPoint> controlPoints;

	private boolean firstUpdated = false;

	private DianaSegment previous;
	private boolean willBeModified = false;

	private DianaQuadCurve curve;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public CurveConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlPoints = new ArrayList<>();
	}

	@Override
	public void delete() {
		super.delete();
		cp1 = null;
		cp2 = null;
		controlPoints.clear();
		controlPoints = null;
	}

	public DianaPoint getCp1RelativeToStartObject() {
		return getPropertyValue(ConnectorSpecification.FIXED_START_LOCATION);
	}

	public void setCp1RelativeToStartObject(DianaPoint aPoint) {
		setPropertyValue(ConnectorSpecification.FIXED_START_LOCATION, aPoint);
	}

	public DianaPoint getCp2RelativeToEndObject() {
		return getPropertyValue(ConnectorSpecification.FIXED_END_LOCATION);
	}

	public void setCp2RelativeToEndObject(DianaPoint aPoint) {
		setPropertyValue(ConnectorSpecification.FIXED_END_LOCATION, aPoint);
	}

	public DianaPoint getCpPosition() {
		return getPropertyValue(CurveConnectorSpecification.CP_POSITION);
	}

	public void setCpPosition(DianaPoint cpPosition) {
		setPropertyValue(CurveConnectorSpecification.CP_POSITION, cpPosition);
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		return controlPoints;
	}

	private ControlPoint makeStartControlPoint(ConnectorNode<?> connectorNode, DianaPoint pt) {

		if (getIsStartingLocationDraggable()) {
			cp1 = new ConnectorAdjustingControlPoint(connectorNode, pt) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					DianaShape<?> shape = getStartNode().getShape().getOutline();
					return shape.transform(DianaUtils.convertNormalizedCoordinatesAT(getStartNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					setCp1RelativeToStartObject(DianaUtils.convertNormalizedPoint(connectorNode, pt, getStartNode()));
					refreshCurve();
					connectorNode.notifyConnectorModified();
					return true;
				}
			};
		}
		else {
			cp1 = new ConnectorControlPoint(connectorNode, pt);
		}
		return cp1;
	}

	private ControlPoint makeEndControlPoint(ConnectorNode<?> connectorNode, DianaPoint pt) {
		if (getIsEndingLocationDraggable()) {
			cp2 = new ConnectorAdjustingControlPoint(connectorNode, pt) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					DianaShape<?> shape = getEndNode().getShape().getOutline();
					return shape.transform(DianaUtils.convertNormalizedCoordinatesAT(getEndNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					setCp2RelativeToEndObject(DianaUtils.convertNormalizedPoint(connectorNode, pt, getEndNode()));
					refreshCurve();
					connectorNode.notifyConnectorModified();
					return true;
				}
			};
		}
		else {
			cp2 = new ConnectorControlPoint(connectorNode, pt);
		}
		return cp2;
	}

	private void updateControlPoints() {

		if (getCpPosition() == null) {
			setCpPosition(new DianaPoint(0.5, 0.4));
		}

		updateCPPositionIfNeeded();

		DianaPoint pointOnStartObject;
		if ((getIsStartingLocationFixed() || getIsStartingLocationDraggable()) && (getStartLocation() != null)) {
			pointOnStartObject = getStartLocation();
		}
		else {
			DianaPoint cpPositionSeenFromStartObject = DianaUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getStartNode());
			pointOnStartObject = getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject);
			if (pointOnStartObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnStartObject = new DianaPoint(0.5, 0.5);
			}
		}
		DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);

		DianaPoint pointOnEndObject;
		if ((getIsEndingLocationFixed() || getIsEndingLocationDraggable()) && (getEndLocation() != null)) {
			pointOnEndObject = getEndLocation();
		}
		else {
			DianaPoint cpPositionSeenFromEndObject = DianaUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getEndNode());
			pointOnEndObject = getEndNode().getShape().outlineIntersect(cpPositionSeenFromEndObject);
			if (pointOnEndObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnEndObject = new DianaPoint(0.5, 0.5);
			}
		}
		DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);

		cp1 = makeStartControlPoint(connectorNode, newP1);
		cp2 = makeEndControlPoint(connectorNode, newP2);

		cp = new ConnectorAdjustingControlPoint(connectorNode, getCpPosition()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(/* pointRelativeToInitialConfiguration */newRelativePoint);
				setPoint(pt);
				setCpPosition(pt);
				updateFromNewCPPosition();
				refreshCurve();
				connectorNode.notifyConnectorModified();
				return true;
			}
		};

		controlPoints.clear();
		controlPoints.add(cp1);
		controlPoints.add(cp);
		controlPoints.add(cp2);

		refreshCurve();
	}

	/**
	 * This method updates the position according to start/end motions. However, this has a small drawback which is caused by the continuous
	 * change of the system coordinates of the connector. Indeed, it is based on the bounds of the start and end node. If one of them moves,
	 * the coordinates should ideally be compared to the same coordinates system.
	 */
	private void updateCPPositionIfNeeded() {
		if (willBeModified && previous != null) {
			DianaSegment newSegment = getCenterToCenterSegment();
			double delta = newSegment.getAngle() - previous.getAngle();
			if (Math.abs(delta) > DianaGeometricObject.EPSILON) {
				DianaPoint inter;
				try {
					inter = DianaAbstractLine.getLineIntersection(previous, newSegment);
				} catch (ParallelLinesException e) {
					return;
				}
				DianaPoint newCPPosition = new DianaPoint();
				AffineTransform at = AffineTransform.getTranslateInstance(inter.x, inter.y);
				at.concatenate(AffineTransform.getRotateInstance(-delta));
				at.concatenate(AffineTransform.getTranslateInstance(-inter.x, -inter.y));
				at.transform(getCpPosition(), newCPPosition);
				setCpPosition(newCPPosition);
				previous = newSegment;
			}
		}
	}

	@Override
	public void connectorWillBeModified() {
		super.connectorWillBeModified();
		willBeModified = true;
		previous = getCenterToCenterSegment();
	}

	private DianaSegment getCenterToCenterSegment() {
		return new DianaSegment(
				DianaUtils.convertNormalizedPoint(getStartNode(), getStartNode().getDianaShape().getCenter(), connectorNode),
				DianaUtils.convertNormalizedPoint(getEndNode(), getEndNode().getDianaShape().getCenter(), connectorNode));
	}

	@Override
	public void connectorHasBeenModified() {
		willBeModified = false;
		previous = null;
		super.connectorHasBeenModified();
	};

	private void updateFromNewCPPosition() {

		DianaPoint pointOnStartObject;
		if ((getIsStartingLocationFixed() || getIsStartingLocationDraggable()) && (getStartLocation() != null)) {
			pointOnStartObject = getStartLocation();
		}
		else {
			DianaPoint cpPositionSeenFromStartObject = DianaUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getStartNode());
			pointOnStartObject = getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject);
			if (pointOnStartObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnStartObject = new DianaPoint(0.5, 0.5);
			}
		}
		DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);

		DianaPoint pointOnEndObject;
		if ((getIsEndingLocationFixed() || getIsEndingLocationDraggable()) && (getEndLocation() != null)) {
			pointOnEndObject = getEndLocation();
		}
		else {
			DianaPoint cpPositionSeenFromEndObject = DianaUtils.convertNormalizedPoint(connectorNode, getCpPosition(), getEndNode());
			pointOnEndObject = getEndNode().getShape().outlineIntersect(cpPositionSeenFromEndObject);
			if (pointOnEndObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnEndObject = new DianaPoint(0.5, 0.5);
			}
		}
		DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);

		cp1.setPoint(newP1);
		cp2.setPoint(newP2);
	}

	private void refreshCurve() {
		if (cp1 != null && cp != null && cp2 != null) {
			curve = DianaQuadCurve.makeCurveFromPoints(cp1.getPoint(), cp.getPoint(), cp2.getPoint());
		}
	}

	@Override
	public void drawConnector(DianaConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		g.useDefaultForegroundStyle();

		if (curve != null) {

			curve.paint(g);

			// Draw eventual symbols
			if (getStartSymbol() != StartSymbolType.NONE) {
				DianaSegment firstSegment = curve.getApproximatedStartTangent();
				DianaSegment viewSegment = firstSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(firstSegment.getP1(), getStartSymbol(), getStartSymbolSize(), viewSegment.getAngle());
			}
			if (getEndSymbol() != EndSymbolType.NONE) {
				DianaSegment lastSegment = curve.getApproximatedEndTangent();
				DianaSegment viewSegment = lastSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(lastSegment.getP2(), getEndSymbol(), getEndSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
			if (getMiddleSymbol() != MiddleSymbolType.NONE) {
				DianaSegment cpSegment = curve.getApproximatedControlPointTangent();
				DianaSegment viewSegment = cpSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
				g.drawSymbol(curve.getP3(), getMiddleSymbol(), getMiddleSymbolSize(), viewSegment.getAngle() + Math.PI);
			}
		}

	}

	@Override
	public double distanceToConnector(DianaPoint aPoint, double scale) {
		if (curve == null) {
			LOGGER.warning("Curve is null");
			return Double.POSITIVE_INFINITY;
		}
		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		DianaPoint nearestPointOnCurve = curve.getNearestPoint(aPoint);
		Point nearestPoint = connectorNode.convertNormalizedPointToViewCoordinates(nearestPointOnCurve, scale);
		return testPoint.distance(nearestPoint);
	}

	@Override
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			return;
		}

		updateControlPoints();

		super.refreshConnector(force);

		firstUpdated = true;

	}

	@Override
	public boolean needsRefresh() {
		if (!firstUpdated) {
			return true;
		}
		return super.needsRefresh();
	}

	@Override
	public DianaPoint getMiddleSymbolLocation() {
		if (getCpPosition() == null) {
			return new DianaPoint(0, 0);
		}
		return getCpPosition();
	}

	@Override
	public DianaPoint getLabelLocation() {
		return getMiddleSymbolLocation();
	}

	@Override
	public DianaRectangle getConnectorUsedBounds() {
		if (curve == null) {
			refreshCurve();
		}
		if (curve == null) {
			return new DianaRectangle(0, 0, 1.0, 1.0);
		}
		DianaRectangle returned = new DianaRectangle(Filling.FILLED);
		Rectangle2D rect = curve.getBounds2D();
		returned.x = rect.getX();
		returned.y = rect.getY();
		returned.width = rect.getWidth();
		returned.height = rect.getHeight();
		return returned;
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
	public double getStartAngle() {
		if (cp1 != null) {
			return GeomUtils.getSlope(DianaPoint.ORIGIN_POINT, cp1.getPoint());
		}
		return 0;
	}

	@Override
	public double getEndAngle() {
		if (cp2 != null) {
			return GeomUtils.getSlope(DianaPoint.ORIGIN_POINT, cp2.getPoint());
		}
		return 0;
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
			if (evt.getPropertyName().equals(ConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE.getName())
					|| evt.getPropertyName().equals(ConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE.getName())
					|| evt.getPropertyName().equals(ConnectorSpecification.FIXED_START_LOCATION.getName())
					|| evt.getPropertyName().equals(ConnectorSpecification.FIXED_END_LOCATION.getName())) {
				refreshConnector(true);
			}
			else if (evt.getPropertyName().equals(ConnectorSpecification.IS_STARTING_LOCATION_FIXED.getName())) {
				if (getIsStartingLocationFixed() && getStartLocation() == null) {
					// In this case, we can initialize fixed start location to its current value
					setCp1RelativeToStartObject(DianaUtils.convertNormalizedPoint(connectorNode, cp1.getPoint(), getStartNode()));
				}
			}
			else if (evt.getPropertyName().equals(ConnectorSpecification.IS_ENDING_LOCATION_FIXED.getName())) {
				if (getIsEndingLocationFixed() && getEndLocation() == null) {
					// In this case, we can initialize fixed start location to its current value
					setCp2RelativeToEndObject(DianaUtils.convertNormalizedPoint(connectorNode, cp2.getPoint(), getEndNode()));
				}
			}
			else {
				updateControlPoints();
			}

		}

	}
}

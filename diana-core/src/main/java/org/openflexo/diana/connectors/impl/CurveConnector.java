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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.connectors.CurveConnectorSpecification;
import org.openflexo.diana.connectors.CurveConnectorSpecification.CurveConnectorType;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorControlPoint;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaAbstractLine;
import org.openflexo.diana.geom.DianaCubicCurve;
import org.openflexo.diana.geom.DianaGeneralShape.GeneralShapePathElement;
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
import org.openflexo.diana.graphics.DianaGraphics;

public class CurveConnector extends ConnectorImpl<CurveConnectorSpecification> {

	private static final Logger LOGGER = Logger.getLogger(CurveConnectorSpecification.class.getPackage().getName());

	private ControlPoint p1;
	private ControlPoint p2;
	private ControlPoint cp;
	private ControlPoint cp1;
	private ControlPoint cp2;
	private List<ControlArea<?>> controlAreas;

	private boolean firstUpdated = false;

	private DianaSegment previous;
	private boolean willBeModified = false;

	private DianaCubicCurve cCurve;
	private DianaQuadCurve qCurve;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	public CurveConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlAreas = new ArrayList<>();
	}

	@Override
	public void delete() {
		super.delete();
		p1 = null;
		p2 = null;
		controlAreas.clear();
		controlAreas = null;
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

	public DianaPoint getCp1Position() {
		return getPropertyValue(CurveConnectorSpecification.CP1_POSITION);
	}

	public void setCp1Position(DianaPoint cp1Position) {
		setPropertyValue(CurveConnectorSpecification.CP1_POSITION, cp1Position);
	}

	public DianaPoint getCp2Position() {
		return getPropertyValue(CurveConnectorSpecification.CP2_POSITION);
	}

	public void setCp2Position(DianaPoint cp2Position) {
		setPropertyValue(CurveConnectorSpecification.CP2_POSITION, cp2Position);
	}

	@Override
	public List<ControlArea<?>> getControlAreas() {
		return controlAreas;
	}

	private ControlPoint makeStartControlPoint(ConnectorNode<?> connectorNode, DianaPoint pt) {

		if (getIsStartingLocationDraggable()) {
			p1 = new ConnectorAdjustingControlPoint(connectorNode, pt) {
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
					if (getConnectorSpecification().getCurveConnectorType() == CurveConnectorType.CUBIC_CURVE) {
						controlLine1.setP1(
								pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
					}
					refreshCurve();
					connectorNode.notifyConnectorModified();
					return true;
				}
			};
		}
		else {
			p1 = new ConnectorControlPoint(connectorNode, pt);
		}
		return p1;
	}

	private ControlPoint makeEndControlPoint(ConnectorNode<?> connectorNode, DianaPoint pt) {
		if (getIsEndingLocationDraggable()) {
			p2 = new ConnectorAdjustingControlPoint(connectorNode, pt) {
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
					if (getConnectorSpecification().getCurveConnectorType() == CurveConnectorType.CUBIC_CURVE) {
						controlLine2.setP1(
								pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
					}
					refreshCurve();
					connectorNode.notifyConnectorModified();
					return true;
				}
			};
		}
		else {
			p2 = new ConnectorControlPoint(connectorNode, pt);
		}
		return p2;
	}

	private DianaSegment controlLine1;
	private DianaSegment controlLine2;

	private void updateControlPoints() {

		switch (getConnectorSpecification().getCurveConnectorType()) {
			case QUAD_CURVE:
				if (getCpPosition() == null) {
					setCpPosition(new DianaPoint(0.5, 0.4));
				}
				updateCPPositionIfNeeded();
				break;
			case CUBIC_CURVE:
				if (getCp1Position() == null) {
					System.out.println("CP1 par defaut");
					setCp1Position(new DianaPoint(0.3, -0.5));
				}
				if (getCp2Position() == null) {
					System.out.println("CP2 par defaut");
					setCp2Position(new DianaPoint(0.7, -0.5));
				}
				break;
		}

		DianaPoint pointOnStartObject = null;
		if ((getIsStartingLocationFixed() || getIsStartingLocationDraggable()) && (getStartLocation() != null)) {
			pointOnStartObject = getStartLocation();
		}
		else {
			switch (getConnectorSpecification().getCurveConnectorType()) {
				case QUAD_CURVE:
					DianaPoint cpPositionSeenFromStartObject = DianaUtils.convertNormalizedPoint(connectorNode, getCpPosition(),
							getStartNode());
					pointOnStartObject = getStartNode().getShape().outlineIntersect(cpPositionSeenFromStartObject);
					break;
				case CUBIC_CURVE:
					DianaPoint cp1PositionSeenFromStartObject = DianaUtils.convertNormalizedPoint(connectorNode, getCp1Position(),
							getStartNode());
					pointOnStartObject = getStartNode().getShape().outlineIntersect(cp1PositionSeenFromStartObject);
					break;
			}

			if (pointOnStartObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnStartObject = new DianaPoint(0.5, 0.5);
			}
		}
		DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);

		DianaPoint pointOnEndObject = null;
		if ((getIsEndingLocationFixed() || getIsEndingLocationDraggable()) && (getEndLocation() != null)) {
			pointOnEndObject = getEndLocation();
		}
		else {
			switch (getConnectorSpecification().getCurveConnectorType()) {
				case QUAD_CURVE:
					DianaPoint cpPositionSeenFromEndObject = DianaUtils.convertNormalizedPoint(connectorNode, getCpPosition(),
							getEndNode());
					pointOnEndObject = getEndNode().getShape().outlineIntersect(cpPositionSeenFromEndObject);
					break;
				case CUBIC_CURVE:
					DianaPoint cp2PositionSeenFromEndObject = DianaUtils.convertNormalizedPoint(connectorNode, getCp2Position(),
							getEndNode());
					pointOnEndObject = getEndNode().getShape().outlineIntersect(cp2PositionSeenFromEndObject);
					break;
			}

			if (pointOnEndObject == null) {
				LOGGER.warning("outlineIntersect() returned null");
				pointOnEndObject = new DianaPoint(0.5, 0.5);
			}
		}
		DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);

		p1 = makeStartControlPoint(connectorNode, newP1);
		p2 = makeEndControlPoint(connectorNode, newP2);

		controlAreas.clear();
		controlAreas.add(p1);
		controlAreas.add(p2);

		switch (getConnectorSpecification().getCurveConnectorType()) {
			case QUAD_CURVE:
				if (getCpPosition() == null) {
					setCpPosition(new DianaPoint(0.5, 0.2));
				}
				updateCPPositionIfNeeded();
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
				controlAreas.add(cp);
				break;
			case CUBIC_CURVE:
				if (getCp1Position() == null) {
					setCp1Position(new DianaPoint(1.0, 1.0));
				}
				cp1 = new ConnectorAdjustingControlPoint(connectorNode, getCp1Position()) {
					@Override
					public DianaArea getDraggingAuthorizedArea() {
						return new DianaPlane();
					}

					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
						setPoint(pt);
						setCp1Position(pt);
						// updateFromNewCPPosition();

						if ((getIsStartingLocationFixed() || getIsStartingLocationDraggable()) && (getStartLocation() != null)) {
						}
						else {
							DianaPoint cp1PositionSeenFromStartObject = DianaUtils.convertNormalizedPoint(connectorNode, getCp1Position(),
									getStartNode());
							DianaPoint pointOnStartObject = getStartNode().getShape().outlineIntersect(cp1PositionSeenFromStartObject);
							DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getStartNode(), pointOnStartObject, connectorNode);
							p1.setPoint(newP1);
							controlLine1.setP1(newP1
									.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
						}

						controlLine1.setP2(
								pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
						refreshCurve();
						connectorNode.notifyConnectorModified();
						return true;
					}
				};
				controlAreas.add(cp1);
				if (getCp2Position() == null) {
					setCp2Position(new DianaPoint(0.9, 0.1));
				}
				cp2 = new ConnectorAdjustingControlPoint(connectorNode, getCp2Position()) {
					@Override
					public DianaArea getDraggingAuthorizedArea() {
						return new DianaPlane();
					}

					@Override
					public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
							DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
						DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
						setPoint(pt);
						setCp2Position(pt);
						// updateFromNewCPPosition();

						if ((getIsEndingLocationFixed() || getIsEndingLocationDraggable()) && (getEndLocation() != null)) {
						}
						else {
							DianaPoint cp2PositionSeenFromEndObject = DianaUtils.convertNormalizedPoint(connectorNode, getCp2Position(),
									getEndNode());
							DianaPoint pointOnEndObject = getEndNode().getShape().outlineIntersect(cp2PositionSeenFromEndObject);
							DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getEndNode(), pointOnEndObject, connectorNode);
							p2.setPoint(newP2);

							controlLine2.setP1(newP2
									.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
						}

						controlLine2.setP2(
								pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
						refreshCurve();
						connectorNode.notifyConnectorModified();
						return true;
					}
				};
				controlAreas.add(cp2);

				controlLine1 = new DianaSegment(p1.getPoint(), getCp1Position())
						.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode()));

				// final DianaSegment line1 = new DianaSegment(new DianaPoint(0.0, 0.0), new DianaPoint(12.0, 12.0));
				// System.out.println("La belle ligne");

				controlAreas.add(new ControlArea<DianaSegment>(connectorNode, controlLine1) {
					@Override
					public boolean isDraggable() {
						return false;
					}

					@Override
					public Rectangle paint(DianaGraphics graphics) {
						graphics.setDefaultForeground(
								graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
						graphics.useDefaultForegroundStyle();
						controlLine1.paint(graphics);
						return null;
					}
				});

				controlLine2 = new DianaSegment(p2.getPoint(), getCp2Position())
						.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode()));

				controlAreas.add(new ControlArea<DianaSegment>(connectorNode, controlLine2) {
					@Override
					public boolean isDraggable() {
						return false;
					}

					@Override
					public Rectangle paint(DianaGraphics graphics) {
						graphics.setDefaultForeground(
								graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
						graphics.useDefaultForegroundStyle();
						controlLine2.paint(graphics);
						return null;
					}
				});

		}

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

		p1.setPoint(newP1);
		p2.setPoint(newP2);
	}

	private void refreshCurve() {
		if (p1 != null && p2 != null) {
			switch (getConnectorSpecification().getCurveConnectorType()) {
				case QUAD_CURVE:
					if (cp != null) {
						qCurve = DianaQuadCurve.makeCurveFromPoints(p1.getPoint(), cp.getPoint(), p2.getPoint());
					}
					break;
				case CUBIC_CURVE:
					if (cp1 != null && cp2 != null) {
						cCurve = new DianaCubicCurve(p1.getPoint(), cp1.getPoint(), cp2.getPoint(), p2.getPoint());
					}
					break;
			}
		}
	}

	@Override
	public void drawConnector(DianaConnectorGraphics g) {
		if (!firstUpdated) {
			refreshConnector();
		}

		g.useDefaultForegroundStyle();

		switch (getConnectorSpecification().getCurveConnectorType()) {
			case QUAD_CURVE:
				if (qCurve != null) {
					qCurve.paint(g);
					// Draw eventual symbols
					if (getStartSymbol() != StartSymbolType.NONE) {
						DianaSegment firstSegment = qCurve.getApproximatedStartTangent();
						DianaSegment viewSegment = firstSegment
								.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
						g.drawSymbol(firstSegment.getP1(), getStartSymbol(), getStartSymbolSize(), viewSegment.getAngle());
					}
					if (getEndSymbol() != EndSymbolType.NONE) {
						DianaSegment lastSegment = qCurve.getApproximatedEndTangent();
						DianaSegment viewSegment = lastSegment
								.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
						g.drawSymbol(lastSegment.getP2(), getEndSymbol(), getEndSymbolSize(), viewSegment.getAngle() + Math.PI);
					}
					if (getMiddleSymbol() != MiddleSymbolType.NONE) {
						DianaSegment cpSegment = qCurve.getApproximatedControlPointTangent();
						DianaSegment viewSegment = cpSegment
								.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
						g.drawSymbol(qCurve.getP3(), getMiddleSymbol(), getMiddleSymbolSize(), viewSegment.getAngle() + Math.PI);
					}
				}
				break;
			case CUBIC_CURVE:
				if (cCurve != null) {
					cCurve.paint(g);
				}
				break;
		}

	}

	private GeneralShapePathElement<?> getCurve() {
		switch (getConnectorSpecification().getCurveConnectorType()) {
			case QUAD_CURVE:
				return qCurve;
			case CUBIC_CURVE:
				return cCurve;
		}
		return null;
	}

	@Override
	public double distanceToConnector(DianaPoint aPoint, double scale) {

		GeneralShapePathElement<?> curve = getCurve();
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
		GeneralShapePathElement<?> curve = getCurve();
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
		if (p1 != null) {
			return GeomUtils.getSlope(DianaPoint.ORIGIN_POINT, p1.getPoint());
		}
		return 0;
	}

	@Override
	public double getEndAngle() {
		if (p2 != null) {
			return GeomUtils.getSlope(DianaPoint.ORIGIN_POINT, p2.getPoint());
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
			if (evt.getPropertyName().equals(CurveConnectorSpecification.CURVE_CONNECTOR_TYPE.getName())) {
				refreshConnector(true);
			}
			else if (evt.getPropertyName().equals(ConnectorSpecification.IS_STARTING_LOCATION_DRAGGABLE.getName())
					|| evt.getPropertyName().equals(ConnectorSpecification.IS_ENDING_LOCATION_DRAGGABLE.getName())
					|| evt.getPropertyName().equals(ConnectorSpecification.FIXED_START_LOCATION.getName())
					|| evt.getPropertyName().equals(ConnectorSpecification.FIXED_END_LOCATION.getName())) {
				refreshConnector(true);
			}
			else if (evt.getPropertyName().equals(ConnectorSpecification.IS_STARTING_LOCATION_FIXED.getName())) {
				if (getIsStartingLocationFixed() && getStartLocation() == null) {
					// In this case, we can initialize fixed start location to its current value
					setCp1RelativeToStartObject(DianaUtils.convertNormalizedPoint(connectorNode, p1.getPoint(), getStartNode()));
				}
			}
			else if (evt.getPropertyName().equals(ConnectorSpecification.IS_ENDING_LOCATION_FIXED.getName())) {
				if (getIsEndingLocationFixed() && getEndLocation() == null) {
					// In this case, we can initialize fixed start location to its current value
					setCp2RelativeToEndObject(DianaUtils.convertNormalizedPoint(connectorNode, p2.getPoint(), getEndNode()));
				}
			}
			else {
				refreshConnector(true);
				// updateControlPoints();
			}

		}

	}
}

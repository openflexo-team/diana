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
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.ForegroundStyle.DashStyle;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.ConnectorSymbol.EndSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.MiddleSymbolType;
import org.openflexo.diana.connectors.ConnectorSymbol.StartSymbolType;
import org.openflexo.diana.connectors.LineConnectorSpecification;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorControlPoint;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaCubicCurve;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.graphics.DianaConnectorGraphics;
import org.openflexo.diana.graphics.DianaGraphics;

/**
 * A component used as a delegate to handle curve reflexive connectors
 * 
 * This component is used both for {@link CurveConnector} and {@link CurvedPolylinConnector}
 * 
 * @author sylvain
 *
 */
public class ReflexiveCurveConnectorDelegate {

	private static final Logger LOGGER = Logger.getLogger(LineConnectorSpecification.class.getPackage().getName());

	protected DianaRectangle NORMALIZED_BOUNDS = new DianaRectangle(0, 0, 1, 1, Filling.FILLED);

	private ConnectorNode<?> connectorNode;

	private ControlPoint p1;
	private ControlPoint p2;
	private ControlPoint cp1;
	private ControlPoint cp2;

	private DianaSegment controlLine1;
	private DianaSegment controlLine2;

	private DianaRectangle connectorUsedBounds = NORMALIZED_BOUNDS;

	private DianaCubicCurve cCurve = null;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public ReflexiveCurveConnectorDelegate(ConnectorNode<?> connectorNode) {
		this.connectorNode = connectorNode;
	}

	public ShapeNode<?> getShapeNode() {
		// remember that startNode = endNode
		return connectorNode.getStartNode();
	}

	public CurveConnector getConnector() {
		return (CurveConnector) connectorNode.getConnector();
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

	public DianaRectangle getConnectorUsedBounds() {
		return connectorUsedBounds;
	}

	public void delete() {
	}

	private ControlPoint makeStartControlPoint(DianaPoint pt) {

		if (getConnector().getIsStartingLocationDraggable()) {
			p1 = new ConnectorAdjustingControlPoint(connectorNode, pt) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					DianaShape<?> shape = getShapeNode().getShape().getOutline();
					return shape.transform(DianaUtils.convertNormalizedCoordinatesAT(getShapeNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					getConnector().setCp1RelativeToStartObject(DianaUtils.convertNormalizedPoint(connectorNode, pt, getShapeNode()));
					controlLine1
							.setP1(pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
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

	private ControlPoint makeEndControlPoint(DianaPoint pt) {
		if (getConnector().getIsEndingLocationDraggable()) {
			p2 = new ConnectorAdjustingControlPoint(connectorNode, pt) {
				@Override
				public DianaArea getDraggingAuthorizedArea() {
					DianaShape<?> shape = getShapeNode().getShape().getOutline();
					return shape.transform(DianaUtils.convertNormalizedCoordinatesAT(getShapeNode(), connectorNode));
				}

				@Override
				public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
						DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					getConnector().setCp2RelativeToEndObject(DianaUtils.convertNormalizedPoint(connectorNode, pt, getShapeNode()));
					controlLine2
							.setP1(pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
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

	public List<ControlArea<?>> updateControlPoints() {

		if (getConnector().getCp1Position() == null) {
			DianaPoint east = new DianaPoint(2.0, 0.5);
			getConnector().setCp1Position(DianaUtils.convertNormalizedPoint(getShapeNode(), east, connectorNode));
		}

		if (getConnector().getCp2Position() == null) {
			DianaPoint north = new DianaPoint(0.5, -1);
			getConnector().setCp2Position(DianaUtils.convertNormalizedPoint(getShapeNode(), north, connectorNode));
		}

		DianaPoint localCP1 = DianaUtils.convertNormalizedPoint(connectorNode, getConnector().getCp1Position(), getShapeNode());
		DianaPoint localCP2 = DianaUtils.convertNormalizedPoint(connectorNode, getConnector().getCp2Position(), getShapeNode());

		DianaPoint localP1 = getShapeNode().getShape().outlineIntersect(localCP1);
		DianaPoint localP2 = getShapeNode().getShape().outlineIntersect(localCP2);

		p1 = makeStartControlPoint(DianaUtils.convertNormalizedPoint(getShapeNode(), localP1, connectorNode));
		p2 = makeEndControlPoint(DianaUtils.convertNormalizedPoint(getShapeNode(), localP2, connectorNode));

		List<ControlArea<?>> returned = new ArrayList<>();
		returned.add(p1);
		returned.add(p2);

		cp1 = new ConnectorAdjustingControlPoint(connectorNode, getConnector().getCp1Position()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
				setPoint(pt);
				getConnector().setCp1Position(pt);

				if ((getConnector().getIsStartingLocationFixed() || getConnector().getIsStartingLocationDraggable())
						&& (getConnector().getStartLocation() != null)) {
				}
				else {
					DianaPoint cp1PositionSeenFromStartObject = DianaUtils.convertNormalizedPoint(connectorNode,
							getConnector().getCp1Position(), getConnector().getStartNode());
					DianaPoint pointOnStartObject = getConnector().getStartNode().getShape()
							.outlineIntersect(cp1PositionSeenFromStartObject);
					DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getConnector().getStartNode(), pointOnStartObject, connectorNode);
					p1.setPoint(newP1);
					controlLine1.setP1(
							newP1.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
				}

				controlLine1.setP2(pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
				refreshCurve();
				connectorNode.notifyConnectorModified();
				return true;
			}
		};
		returned.add(cp1);

		cp2 = new ConnectorAdjustingControlPoint(connectorNode, getConnector().getCp2Position()) {
			@Override
			public DianaArea getDraggingAuthorizedArea() {
				return new DianaPlane();
			}

			@Override
			public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
					DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event) {
				DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
				setPoint(pt);
				getConnector().setCp2Position(pt);

				if ((getConnector().getIsEndingLocationFixed() || getConnector().getIsEndingLocationDraggable())
						&& (getConnector().getEndLocation() != null)) {
				}
				else {
					DianaPoint cp2PositionSeenFromEndObject = DianaUtils.convertNormalizedPoint(connectorNode,
							getConnector().getCp2Position(), getConnector().getEndNode());
					DianaPoint pointOnEndObject = getConnector().getEndNode().getShape().outlineIntersect(cp2PositionSeenFromEndObject);
					DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getConnector().getEndNode(), pointOnEndObject, connectorNode);
					p2.setPoint(newP2);

					controlLine2.setP1(
							newP2.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
				}

				controlLine2.setP2(pt.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode())));
				refreshCurve();
				connectorNode.notifyConnectorModified();
				return true;
			}
		};
		returned.add(cp2);

		controlLine1 = new DianaSegment(p1.getPoint(), getConnector().getCp1Position())
				.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode()));

		returned.add(new ControlArea<DianaSegment>(connectorNode, controlLine1) {
			@Override
			public boolean isDraggable() {
				return false;
			}

			@Override
			public Rectangle paint(DianaGraphics graphics) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
				graphics.useDefaultForegroundStyle();
				controlLine1.paint(graphics);
				return null;
			}
		});

		controlLine2 = new DianaSegment(p2.getPoint(), getConnector().getCp2Position())
				.transform(DianaUtils.convertNormalizedCoordinatesAT(connectorNode, connectorNode.getParentNode()));

		returned.add(new ControlArea<DianaSegment>(connectorNode, controlLine2) {
			@Override
			public boolean isDraggable() {
				return false;
			}

			@Override
			public Rectangle paint(DianaGraphics graphics) {
				graphics.setDefaultForeground(graphics.getFactory().makeForegroundStyle(Color.LIGHT_GRAY, 0.5f, DashStyle.BIG_DASHES));
				graphics.useDefaultForegroundStyle();
				controlLine2.paint(graphics);
				return null;
			}
		});

		refreshCurve();

		return returned;

	}

	private void refreshCurve() {

		cCurve = new DianaCubicCurve(p1.getPoint(), cp1.getPoint(), cp2.getPoint(), p2.getPoint());

		refreshConnectorUsedBounds();

	}

	protected void refreshConnectorUsedBounds() {

		Rectangle bounds = cCurve.getBounds();
		connectorUsedBounds = new DianaRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public void drawConnector(DianaConnectorGraphics g) {
		g.useDefaultForegroundStyle();

		g.drawCurve(cCurve);

		// Draw eventual symbols
		if (getConnector().getStartSymbol() != StartSymbolType.NONE) {
			DianaSegment firstSegment = cCurve.getApproximatedStartTangent();
			DianaSegment viewSegment = firstSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
			g.drawSymbol(firstSegment.getP1(), getConnector().getStartSymbol(), getConnector().getStartSymbolSize(),
					viewSegment.getAngle());
		}
		if (getConnector().getEndSymbol() != EndSymbolType.NONE) {
			DianaSegment lastSegment = cCurve.getApproximatedEndTangent();
			DianaSegment viewSegment = lastSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
			g.drawSymbol(lastSegment.getP2(), getConnector().getEndSymbol(), getConnector().getEndSymbolSize(),
					viewSegment.getAngle() + Math.PI);
		}
		if (getConnector().getMiddleSymbol() != MiddleSymbolType.NONE) {
			DianaPoint pt = getMiddleSymbolLocation();
			DianaSegment cpSegment = cCurve.getApproximatedTangent(pt);
			DianaSegment viewSegment = cpSegment.transform(connectorNode.convertNormalizedPointToViewCoordinatesAT(g.getScale()));
			g.drawSymbol(pt, getConnector().getMiddleSymbol(), getConnector().getMiddleSymbolSize(), viewSegment.getAngle() + Math.PI);
		}

	}

	public DianaPoint getMiddleSymbolLocation() {

		AffineTransform at = connectorNode.convertNormalizedPointToViewCoordinatesAT(1.0);
		if (cCurve != null) {
			DianaCubicCurve transformedCurve = cCurve.transform(at);
			DianaPoint point = transformedCurve.getPointAtRelativePosition(getConnector().getRelativeMiddleSymbolLocation());
			try {
				return point.transform(at.createInverse());
			} catch (NoninvertibleTransformException e) {
				e.printStackTrace();
			}
		}
		return new DianaPoint(0, 0);

	}

	public double distanceToConnector(DianaPoint aPoint, double scale) {

		if (cCurve == null) {
			LOGGER.warning("Curve is null");
			return Double.POSITIVE_INFINITY;
		}

		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		DianaPoint nearestPointOnCurve = cCurve.getNearestPoint(aPoint);
		Point nearestPoint = connectorNode.convertNormalizedPointToViewCoordinates(nearestPointOnCurve, scale);
		return testPoint.distance(nearestPoint);

	}

}

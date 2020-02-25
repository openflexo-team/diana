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
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.DianaUtils;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.connectors.ConnectorSpecification;
import org.openflexo.diana.connectors.LineConnectorSpecification;
import org.openflexo.diana.cp.ConnectorAdjustingControlPoint;
import org.openflexo.diana.cp.ConnectorControlPoint;
import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectangle;
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

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public ReflexiveConnectorDelegate(ConnectorNode<?> connectorNode) {
		this.connectorNode = connectorNode;
	}

	public ShapeNode<?> getShapeNode() {
		// remember that startNode = endNode
		return connectorNode.getStartNode();
	}

	public ConnectorSpecification getConnectorSpecification() {
		return connectorNode.getConnectorSpecification();
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
					// System.out.println("OK, moving to " + newRelativePoint);
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					getConnectorSpecification().setReflexiveControlPointLocation(pt);
					connectorNode.notifyConnectorModified();
					refreshConnectorUsedBounds();
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

	public List<ControlPoint> updateControlPoints() {

		DianaPoint north = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
				getShapeNode().getShape().getShape().getCenter().y - getShapeNode().getHeight());
		DianaPoint east = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x + getShapeNode().getWidth(),
				getShapeNode().getShape().getShape().getCenter().y);
		DianaPoint northP = getShapeNode().getShape().outlineIntersect(north);
		DianaPoint eastP = getShapeNode().getShape().outlineIntersect(east);

		DianaPoint newP1 = DianaUtils.convertNormalizedPoint(getShapeNode(), eastP, connectorNode);
		DianaPoint newP2 = DianaUtils.convertNormalizedPoint(getShapeNode(), northP, connectorNode);
		cp1 = new ConnectorControlPoint(connectorNode, newP1);
		cp2 = new ConnectorControlPoint(connectorNode, newP2);

		/*DianaPoint newCP;
		if (getConnectorSpecification().getReflexiveControlPointLocation() == null) {
			double horizontalOverlap = DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP / (double) getShapeNode().getViewWidth(1.0);
			double verticalOverlap = DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP / (double) getShapeNode().getViewHeight(1.0);
		
			DianaPoint cp = new DianaPoint(eastP.x + horizontalOverlap, northP.y - verticalOverlap);
			newCP = DianaUtils.convertNormalizedPoint(getShapeNode(), cp, connectorNode);
			getConnectorSpecification().setReflexiveControlPointLocation(newCP);
		}
		else {
			newCP = getConnectorSpecification().getReflexiveControlPointLocation();
		}*/

		/*if (reflexiveConnectorControlPoint == null) {
		
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
					System.out.println("OK, moving to " + newRelativePoint);
					DianaPoint pt = getNearestPointOnAuthorizedArea(newRelativePoint);
					setPoint(pt);
					getConnectorSpecification().setReflexiveControlPointLocation(pt);
					connectorNode.notifyConnectorModified();
					refreshConnectorUsedBounds();
					return true;
				}
		
			};
		
		}*/

		List<ControlPoint> controlPoints = new ArrayList<>();
		controlPoints.add(cp1);
		controlPoints.add(cp2);
		controlPoints.add(getReflexiveConnectorControlPoint());
		return controlPoints;

	}

	protected void refreshConnectorUsedBounds() {
		DianaPoint north = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x,
				getShapeNode().getShape().getShape().getCenter().y - getShapeNode().getHeight());
		DianaPoint east = new DianaPoint(getShapeNode().getShape().getShape().getCenter().x + getShapeNode().getWidth(),
				getShapeNode().getShape().getShape().getCenter().y);
		DianaPoint northP = getShapeNode().getShape().outlineIntersect(north);
		DianaPoint eastP = getShapeNode().getShape().outlineIntersect(east);

		double horizontalOverlap = (reflexiveConnectorControlPoint.getPoint().getX() - eastP.getX()) * 1.2;
		double verticalOverlap = (-reflexiveConnectorControlPoint.getPoint().getY() + northP.getY()) * 1.2;

		/*double horizontalOverlap = (double) DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP
				/ (double) getConnectorNode().getStartNode().getViewWidth(1.0);
		double verticalOverlap = (double) DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP
				/ (double) getConnectorNode().getStartNode().getViewHeight(1.0);
		
		DianaPoint cp = new DianaPoint(eastP.x + horizontalOverlap, northP.y - verticalOverlap);
		
		double horizontalOverlap = (double) DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP
				/ (double) getConnectorNode().getStartNode().getViewWidth(1.0);
		double verticalOverlap = (double) DianaConstants.DEFAULT_RECT_POLYLIN_PIXEL_OVERLAP
				/ (double) getConnectorNode().getStartNode().getViewHeight(1.0);*/
		connectorUsedBounds = new DianaRectangle(0, -verticalOverlap, 1 + horizontalOverlap, 1 + verticalOverlap, Filling.FILLED);

	}

	public void drawConnector(DianaConnectorGraphics g) {
		g.useDefaultForegroundStyle();

		DianaPoint i1 = new DianaPoint(reflexiveConnectorControlPoint.getPoint().x, cp1.getPoint().y);
		DianaPoint i2 = new DianaPoint(reflexiveConnectorControlPoint.getPoint().x, reflexiveConnectorControlPoint.getPoint().y);
		DianaPoint i3 = new DianaPoint(cp2.getPoint().x, reflexiveConnectorControlPoint.getPoint().y);
		g.drawLine(cp1.getPoint(), i1);
		g.drawLine(i1, i2);
		g.drawLine(i2, i3);
		g.drawLine(i3, cp2.getPoint());
	}

	public double distanceToConnector(DianaPoint aPoint, double scale) {

		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);

		DianaPoint i1 = new DianaPoint(reflexiveConnectorControlPoint.getPoint().x, cp1.getPoint().y);
		DianaPoint i2 = new DianaPoint(reflexiveConnectorControlPoint.getPoint().x, reflexiveConnectorControlPoint.getPoint().y);
		DianaPoint i3 = new DianaPoint(cp2.getPoint().x, reflexiveConnectorControlPoint.getPoint().y);
		Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(cp1.getPoint(), scale);
		Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(i1, scale);
		Point point3 = connectorNode.convertNormalizedPointToViewCoordinates(i2, scale);
		Point point4 = connectorNode.convertNormalizedPointToViewCoordinates(i3, scale);
		Point point5 = connectorNode.convertNormalizedPointToViewCoordinates(cp2.getPoint(), scale);
		double d1 = Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
		double d2 = Line2D.ptSegDist(point2.x, point2.y, point3.x, point3.y, testPoint.x, testPoint.y);
		double d3 = Line2D.ptSegDist(point3.x, point3.y, point4.x, point4.y, testPoint.x, testPoint.y);
		double d4 = Line2D.ptSegDist(point4.x, point4.y, point5.x, point5.y, testPoint.x, testPoint.y);
		return Math.min(d1, Math.min(d2, Math.min(d3, d4)));
	}

}

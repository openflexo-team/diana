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

package org.openflexo.fge.connectors.impl;

import java.awt.Point;
import java.awt.geom.Line2D;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.connectors.CurvedPolylinConnectorSpecification;
import org.openflexo.fge.cp.ConnectorAdjustingControlPoint;
import org.openflexo.fge.cp.ControlPoint;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.geom.GeomUtils;
import org.openflexo.fge.graphics.FGEConnectorGraphics;

public class CurvedPolylinConnector extends ConnectorImpl<CurvedPolylinConnectorSpecification> {

	private final FGEPoint p1 = new FGEPoint();
	private final FGEPoint p2 = new FGEPoint();
	private List<ControlPoint> controlPoints;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	// Used for deserialization
	public CurvedPolylinConnector(ConnectorNode<?> connectorNode) {
		super(connectorNode);
		controlPoints = new ArrayList<>();
	}

	@Override
	public void delete() {
		super.delete();
		controlPoints.clear();
		controlPoints = null;
	}

	@Override
	public List<ControlPoint> getControlAreas() {
		// TODO: perfs issue : do not update all the time !!!
		updateControlPoints();
		return controlPoints;
	}

	private void updateControlPoints() {

		controlPoints.add(new ConnectorAdjustingControlPoint(connectorNode, p1));
		controlPoints.add(new ConnectorAdjustingControlPoint(connectorNode, p2));

		FGEPoint newP1 = FGEUtils.convertNormalizedPoint(getStartNode(), new FGEPoint(0.5, 0.5), connectorNode);
		FGEPoint newP2 = FGEUtils.convertNormalizedPoint(getEndNode(), new FGEPoint(0.5, 0.5), connectorNode);

		p1.x = newP1.x;
		p1.y = newP1.y;
		p2.x = newP2.x;
		p2.y = newP2.y;
	}

	@Override
	public void drawConnector(FGEConnectorGraphics g) {
		updateControlPoints();

		g.drawLine(p1.x, p1.y, p2.x, p2.y);

	}

	@Override
	public double getStartAngle() {
		return GeomUtils.getSlope(p1, p2);
	}

	@Override
	public double getEndAngle() {
		return GeomUtils.getSlope(p2, p1);
	}

	@Override
	public double distanceToConnector(FGEPoint aPoint, double scale) {
		Point testPoint = connectorNode.convertNormalizedPointToViewCoordinates(aPoint, scale);
		Point point1 = connectorNode.convertNormalizedPointToViewCoordinates(p1, scale);
		Point point2 = connectorNode.convertNormalizedPointToViewCoordinates(p2, scale);
		return Line2D.ptSegDist(point1.x, point1.y, point2.x, point2.y, testPoint.x, testPoint.y);
	}

	@Override
	public void refreshConnector(boolean force) {
		if (!force && !needsRefresh()) {
			return;
		}

		updateControlPoints();

		super.refreshConnector(force);

		// firstUpdated = true;

	}

	@Override
	public boolean needsRefresh() {
		// if (!firstUpdated) return true;
		return super.needsRefresh();
	}

	@Override
	public FGEPoint getMiddleSymbolLocation() {
		return new FGEPoint(0.5, 0.5);
	}

	private final FGERectangle NORMALIZED_BOUNDS = new FGERectangle(0, 0, 1, 1, Filling.FILLED);

	@Override
	public FGERectangle getConnectorUsedBounds() {
		return NORMALIZED_BOUNDS;
	}

	/**
	 * Return start point, relative to start object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getStartLocation() {
		return p1;
	}

	/**
	 * Return end point, relative to end object
	 * 
	 * @return
	 */
	@Override
	public FGEPoint getEndLocation() {
		return p2;
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

		// TODO
		if (evt.getSource() == getConnectorSpecification()) {
		}

		// if (notification instanceof FGENotification && observable == getConnectorSpecification()) {
		// Those notifications are forwarded by the connector specification
		// FGENotification notif = (FGENotification) notification;

		// }

	}

}

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

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.Hashtable;

import org.openflexo.diana.DianaIconLibrary;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.graphics.DianaGraphics;

public class RectPolylinAdjustingArea extends ControlArea<DianaRectPolylin> {

	private static final Hashtable<Integer, Image> PIN_CACHE = new Hashtable<>();
	protected DianaRectPolylin initialPolylin;
	private RectPolylinConnector connector;

	// private DianaRectPolylin newPolylin;

	public RectPolylinAdjustingArea(RectPolylinConnector connector) {
		super(connector.getConnectorNode(), connector.getCurrentPolylin());
		this.connector = connector;
	}

	@Override
	public ConnectorNode<?> getNode() {
		return (ConnectorNode<?>) super.getNode();
	}

	@Override
	public DianaArea getDraggingAuthorizedArea() {
		return new DianaPlane();
	}

	@Override
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		/*AffineTransform at1 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getStartObject(), getGraphicalRepresentation());
		AffineTransform at2 = GraphicalRepresentation.convertNormalizedCoordinatesAT(
				getConnector().getEndObject(), getGraphicalRepresentation());
		DianaArea startArea = getConnector().getStartObject().getShape().getShape().transform(at1);
		DianaArea endArea = getConnector().getEndObject().getShape().getShape().transform(at2);
		
		newPolylin = DianaRectPolylin.makeRectPolylinCrossingPoint(
				startArea, endArea, newRelativePoint,
				getConnector().getStartOrientation(),
				getConnector().getEndOrientation(),
				true, getConnector().getOverlapXResultingFromPixelOverlap(), getConnector().getOverlapYResultingFromPixelOverlap());
		getConnector().getBasicallyAdjustableControlPoint().setPoint(newRelativePoint);
		getConnector().updateWithNewPolylin(newPolylin);*/

		getConnector().setCrossedControlPoint(newRelativePoint);

		getConnector().updateLayout();
		// getConnector()._updateAsBasicallyAdjustable();

		getConnector()._connectorChanged(true);
		getNode().notifyConnectorModified();
		return true;
	}

	protected void notifyConnectorChanged() {
		getNode().notifyConnectorModified();
	}

	@Override
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
		super.startDragging(controller, startPoint);
		if (controller instanceof AbstractDianaEditor && ((AbstractDianaEditor<?, ?, ?>) controller).getDelegate() != null) {
			((AbstractDianaEditor<?, ?, ?>) controller).getDelegate().objectStartMoving(getNode());
		}
		initialPolylin = getPolylin().clone();
		// getConnector().setWasManuallyAdjusted(true);

	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		super.stopDragging(controller, focused);
		if (controller instanceof AbstractDianaEditor && ((AbstractDianaEditor<?, ?, ?>) controller).getDelegate() != null) {
			((AbstractDianaEditor<?, ?, ?>) controller).getDelegate().objectStopMoving(getNode());
		}
		// getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public Cursor getDraggingCursor() {
		return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public Rectangle paint(DianaGraphics graphics) {
		DianaPoint crossedControlPoint = getConnector().getCrossedControlPointOnRoundedArc();
		if (crossedControlPoint != null) {
			int pinSize = graphics.getScale() <= 1 ? 16 : (int) (16.0 / 2 * (1.0 + graphics.getScale()));
			Image PIN = getPinForPinSize(pinSize);
			// int d = (int) (PIN_SIZE * graphics.getScale());
			// g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
			Point p = getNode().convertLocalNormalizedPointToRemoteViewCoordinates(crossedControlPoint, graphics.getNode(), 1.0);
			p.x -= (int) (54.0d / 196.0d * pinSize);
			p.y -= (int) (150.0d / 196.0d * pinSize);
			graphics.drawImage(PIN, new DianaPoint(p.x, p.y));
			// g.drawImage(DianaConstants.PIN_ICON.getImage(), ), , d, d, null);
		}
		return null;
	}

	public Image getPinForPinSize(int pinSize) {
		Image returned = PIN_CACHE.get(pinSize);
		if (returned == null) {
			PIN_CACHE.put(pinSize, returned = DianaIconLibrary.PIN_ICON.getImage().getScaledInstance(pinSize, pinSize, Image.SCALE_SMOOTH));
		}
		return returned;
	}

	/*private Rectangle paintPolylin(Graphics2D g, JDrawingView<?> drawingView, Color mainColor, Color backColor, DianaRectPolylin polylin)
	{
		Rectangle r = new Rectangle();
		Point lastLocation = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(polylin.getFirstPoint(), getGraphicalRepresentation(), drawingView.getScale());
		for (int i=1; i<polylin.getPointsNb(); i++) {
			DianaPoint p = polylin.getPointAt(i);
			Point currentLocation = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(p, getGraphicalRepresentation(), drawingView.getScale());
			g.drawLine(lastLocation.x,lastLocation.y,currentLocation.x,currentLocation.y);
			lastLocation = currentLocation;
		}
		return r;
	}*/

	public RectPolylinConnector getConnector() {
		return connector;
	}

	public RectPolylinConnectorSpecification getConnectorSpecification() {
		return connector.getConnectorSpecification();
	}

	/*@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return connector.getGraphicalRepresentation();
	}*/

	public DianaRectPolylin getPolylin() {
		return getConnector().getCurrentPolylin();
	}
}

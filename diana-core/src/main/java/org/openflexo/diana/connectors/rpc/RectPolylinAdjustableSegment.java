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
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.connectors.RectPolylinConnectorSpecification;
import org.openflexo.diana.connectors.impl.RectPolylinConnector;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.cp.ControlArea;
import org.openflexo.diana.geom.DianaGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaRectPolylin;
import org.openflexo.diana.geom.DianaSegment;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.graphics.DianaGraphics;

public abstract class RectPolylinAdjustableSegment extends ControlArea<DianaSegment> {
	protected DianaRectPolylin initialPolylin;
	private RectPolylinConnector connector;

	public RectPolylinAdjustableSegment(DianaSegment segment, RectPolylinConnector connector) {
		super(connector.getConnectorNode(), segment);
		this.connector = connector;
	}

	@Override
	public ConnectorNode<?> getNode() {
		return (ConnectorNode<?>) super.getNode();
	}

	@Override
	public abstract DianaArea getDraggingAuthorizedArea();

	@Override
	public abstract boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration,
			DianaPoint newAbsolutePoint, DianaPoint initialPoint, MouseEvent event);

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
		getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
		super.stopDragging(controller, focused);
		if (controller instanceof AbstractDianaEditor && ((AbstractDianaEditor<?, ?, ?>) controller).getDelegate() != null) {
			((AbstractDianaEditor<?, ?, ?>) controller).getDelegate().objectStopMoving(getNode());
		}
		getConnector().setWasManuallyAdjusted(true);
	}

	@Override
	public Cursor getDraggingCursor() {
		SimplifiedCardinalDirection orientation = getArea().getApproximatedOrientation();
		if (orientation != null) {
			if (orientation.isHorizontal()) {
				return Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
			}
			if (orientation.isVertical()) {
				return Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
			}
		}
		return null;
	}

	@Override
	public boolean isDraggable() {
		return true;
	}

	@Override
	public Rectangle paint(DianaGraphics graphics) {
		return null;
		/*if (true) return null;
		Point p1 = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(getArea().getP1(), getGraphicalRepresentation(), drawingView.getScale());
		Point p2 = drawingView.getGraphicalRepresentation().convertRemoteNormalizedPointToLocalViewCoordinates(getArea().getP2(), getGraphicalRepresentation(), drawingView.getScale());
		//System.out.println("Peint le segment: "+p1+"-"+p2);
		graphics.setColor(Color.GREEN);
		graphics.drawLine(p1.x,p1.y,p2.x,p2.y);
		return new Rectangle(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y),Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));*/
	}

	public RectPolylinConnectorSpecification getConnectorSpecification() {
		return connector.getConnectorSpecification();
	}

	public RectPolylinConnector getConnector() {
		return connector;
	}

	/*@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return connector.getGraphicalRepresentation();
	}*/

	public DianaRectPolylin getPolylin() {
		return getConnector().getCurrentPolylin();
	}
}

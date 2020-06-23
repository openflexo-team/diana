/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.diana.cp;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.logging.Logger;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.DianaEditor;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.graphics.DianaGraphics;

/**
 * A {@link ControlArea} encodes an interactive area, attached to a DrawingTreeNode<br>
 * 
 * This class is abstract, as it should be subclassed where to define a custom behavioural interaction.<br>
 * The {@link ControlPoint} is the most common specialization of a {@link ControlArea}
 * 
 * @author sylvain
 * 
 * @param <A>
 */
public abstract class ControlArea<A extends DianaArea> implements DianaConstants {

	private static final Logger logger = Logger.getLogger(ControlArea.class.getPackage().getName());

	private DrawingTreeNode<?, ?> node;
	private A area;
	private DianaArea draggingAuthorizedArea = new DianaEmptyArea();

	public ControlArea(DrawingTreeNode<?, ?> node, A area) {
		this.node = node;
		this.area = area;
		if (node == null) {
			logger.warning("ControlArea built for null DrawingTreeNode");
		}

	}

	public void delete() {
		node = null;
		area = null;
		draggingAuthorizedArea = null;
	}

	public DrawingTreeNode<?, ?> getNode() {
		return node;
	}

	public A getArea() {
		return area;
	}

	public void setArea(A point) {
		area = point;
	}

	// Please override when required
	public boolean isClickable() {
		return false;
	}

	public abstract boolean isDraggable();

	// Please override when required
	public Cursor getDraggingCursor() {
		return Cursor.getDefaultCursor();
	}

	public DianaArea getDraggingAuthorizedArea() {
		return draggingAuthorizedArea;
	}

	public final void setDraggingAuthorizedArea(DianaArea area) {
		draggingAuthorizedArea = area;
	}

	protected DianaPoint getNearestPointOnAuthorizedArea(DianaPoint point) {
		return getDraggingAuthorizedArea().getNearestPoint(point);
	}

	// Override when required
	public void startDragging(DianaEditor<?> controller, DianaPoint startPoint) {
	}

	// Override when required
	/**
	 * Drag control area to supplied location Return a flag indicating if dragging should continue<br>
	 * Override this method when required
	 * 
	 */
	public boolean dragToPoint(DianaPoint newRelativePoint, DianaPoint pointRelativeToInitialConfiguration, DianaPoint newAbsolutePoint,
			DianaPoint initialPoint, MouseEvent event) {
		return true;
	}

	// Override when required
	public void stopDragging(DianaEditor<?> controller, DrawingTreeNode<?, ?> focused) {
	}

	// Override when required
	/**
	 * Clicked on control area to supplied location Return a flag indicating if click has been handled
	 */
	public boolean clickOnPoint(DianaPoint clickedPoint, int clickCount) {
		return true;
	}

	public abstract Rectangle paint(DianaGraphics graphics);

	/**
	 * Return distance between a point (normalized) and represented area, asserting that we are working on a view (not normalized), and at a
	 * given scale
	 * 
	 * @param aPoint
	 *            normalized point relative to current GraphicalRepresentation
	 * @param scale
	 * @return
	 */
	public double getDistanceToArea(DianaPoint aPoint, double scale) {
		if (getArea() == null) {
			return Double.POSITIVE_INFINITY;
		}
		DianaPoint nearestPoint = getArea().getNearestPoint(aPoint);
		if (nearestPoint == null) {
			logger.warning("Could not find nearest point for " + aPoint + " on " + getArea());
			return Double.POSITIVE_INFINITY;
		}
		if (node == null) {
			logger.warning("Unexpected null DrawingTreeNode !");
			return Double.POSITIVE_INFINITY;
		}
		Point pt1 = node.convertNormalizedPointToViewCoordinates(nearestPoint, scale);
		Point pt2 = node.convertNormalizedPointToViewCoordinates(aPoint, scale);
		return Point2D.distance(pt1.x, pt1.y, pt2.x, pt2.y);
	}

	/**
	 * Return distance between a point (normalized) and represented area, asserting that we are working on a view (not normalized), and at a
	 * given scale
	 * 
	 * @param aPoint
	 *            view point relative to current GraphicalRepresentation, at correct scale
	 * @param scale
	 * @return
	 */
	public double getDistanceToArea(Point aPoint, double scale) {
		DianaPoint normalizedPoint = node.convertViewCoordinatesToNormalizedPoint(aPoint, scale);
		DianaPoint nearestPoint = getArea().getNearestPoint(normalizedPoint);
		Point pt1 = node.convertNormalizedPointToViewCoordinates(nearestPoint, scale);
		return Point2D.distance(pt1.x, pt1.y, aPoint.x, aPoint.y);
	}

	@Override
	public String toString() {
		return "[" + getClass().getSimpleName() + "," + Integer.toHexString(hashCode()) + "]";
	}
}

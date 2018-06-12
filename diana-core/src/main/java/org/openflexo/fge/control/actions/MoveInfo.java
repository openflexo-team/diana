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

package org.openflexo.fge.control.actions;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Hashtable;
import java.util.Set;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGEUtils;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.view.DrawingView;
import org.openflexo.fge.view.FGEView;
import org.openflexo.model.undo.CompoundEdit;

/**
 * Utility class used to store a move
 * 
 * @author sylvain
 * 
 */
public class MoveInfo {

	private static final Logger logger = Logger.getLogger(MoveInfo.class.getPackage().getName());

	private FGEView<?, ?> view;
	private Point startMovingLocationInDrawingView;
	private final Hashtable<ShapeNode<?>, FGEPoint> movedObjects;
	private final ShapeNode<?> movedObject;

	private boolean moveHasStarted = false;

	private Point currentLocationInDrawingView;

	public MoveInfo(ShapeNode<?> shapeNode, DianaInteractiveViewer<?, ?, ?> controller) {
		view = controller.getDrawingView();

		startMovingLocationInDrawingView = FGEUtils.convertPointFromDrawableToDrawing(shapeNode.getParentNode(),
				new Point(shapeNode.getViewX(controller.getScale()), shapeNode.getViewY(controller.getScale())), controller.getScale());
		currentLocationInDrawingView = new Point(startMovingLocationInDrawingView);

		movedObject = shapeNode;

		if (!controller.getSelectedObjects().contains(movedObject)) {
			controller.setSelectedObject(movedObject);
		}

		movedObjects = new Hashtable<>();
		movedObjects.put(movedObject, movedObject.getLocation());

		// Now see objects coming with
		for (DrawingTreeNode<?, ?> o : controller.getSelectedObjects()) {
			if (o != shapeNode && o instanceof ShapeNode && ((ShapeNode<?>) o).getParentNode() == shapeNode.getParentNode()
					&& !o.getGraphicalRepresentation().getIsReadOnly()
					&& ((ShapeNode<?>) o).getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {
				// OK, o comes with me
				movedObjects.put((ShapeNode<?>) o, ((ShapeNode<?>) o).getLocation());
			}
		}

	}

	public MoveInfo(ShapeNode<?> shapeNode, MouseControlContext context, FGEView<?, ?> view, DianaInteractiveViewer<?, ?, ?> controller) {
		this(shapeNode, controller);

		this.view = view;
		try {
			startMovingLocationInDrawingView = controller.getDelegate().getPointInView(context.getSource(), context.getPoint(),
					getDrawingView());
		} catch (Error ex) {
			ex.printStackTrace();
			logger.warning("Unexpected exception");
			logger.warning("e.getSource()=" + context.getSource());
			logger.warning("view " + Integer.toHexString(view.hashCode()));
			logger.warning("view.isDeleted()=" + view.isDeleted());
			logger.warning("view.getDrawingView()=" + view.getDrawingView());
			// logger.warning("view.getDrawingView().isDeleted()="+view.getDrawingView().isDeleted());
		}
		currentLocationInDrawingView = new Point(startMovingLocationInDrawingView);

	}

	protected DrawingView<?, ?> getDrawingView() {
		return view.getDrawingView();
	}

	private CompoundEdit moveEdit;

	private void startDragging() {

		if (getMovedObject().getUndoManager() != null) {
			moveEdit = getMovedObject().getUndoManager().startRecording("Moving " + movedObjects.size() + " objects");
		}

		for (ShapeNode<?> shapeNode : movedObjects.keySet()) {
			shapeNode.notifyObjectWillMove();
		}

		if (movedObject.isParentLayoutedAsContainer()) {
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectWillMove();
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectWillResize();
			for (DrawingTreeNode<?, ?> child : movedObject.getChildNodes()) {
				if (child instanceof ShapeNode) {
					((ShapeNode<?>) child).notifyObjectWillMove();
				}
			}
		}

		moveHasStarted = true;
	}

	public void moveTo(Point newLocationInDrawingView) {

		if (getMovedObject().getLayoutManager() != null) {
			if (!getMovedObject().getLayoutManager().getDraggingMode().allowsDragging()) {
				return;
			}
		}

		if (!moveHasStarted) {
			startDragging();
		}

		for (ShapeNode<?> d : movedObjects.keySet()) {
			FGEPoint startMovingPoint = movedObjects.get(d);

			FGEPoint desiredLocation = new FGEPoint(
					startMovingPoint.x + (newLocationInDrawingView.x - startMovingLocationInDrawingView.x) / view.getScale(),
					startMovingPoint.y + (newLocationInDrawingView.y - startMovingLocationInDrawingView.y) / view.getScale());

			/*double authorizedRatio = d.getMoveAuthorizedRatio(desiredLocation,startMovingPoint);
			FGEPoint newLocation = new FGEPoint(
					startMovingPoint.x+(desiredLocation.x-startMovingPoint.x)*authorizedRatio,
					startMovingPoint.y+(desiredLocation.y-startMovingPoint.y)*authorizedRatio);
			logger.info("\n>>>>>>>>>>> setLocation() from "+d.getLocation()+" to "+newLocation+" on "+d);
			d.setLocation(newLocation.clone());*/

			d.setLocation(desiredLocation);
			// d.getGraphicalRepresentation().setX(desiredLocation.x);
			// d.getGraphicalRepresentation().setY(desiredLocation.y);

			if (d.isParentLayoutedAsContainer()) {
				FGEPoint resultingLocation = d.getLocation();
				if (!resultingLocation.equals(desiredLocation)) {
					double dx = resultingLocation.x - desiredLocation.x;
					double dy = resultingLocation.y - desiredLocation.y;
					startMovingPoint.x = startMovingPoint.x + dx;
					startMovingPoint.y = startMovingPoint.y + dy;
				}
			}

		}

		currentLocationInDrawingView = newLocationInDrawingView;
	}

	void stopDragging() {
		for (ShapeNode<?> d : movedObjects.keySet()) {
			d.notifyObjectHasMoved();
		}
		if (movedObject.isParentLayoutedAsContainer()) {
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectHasMoved();
			((ShapeNode<?>) movedObject.getParentNode()).notifyObjectHasResized();
			for (DrawingTreeNode<?, ?> child : movedObject.getChildNodes()) {
				if (child instanceof ShapeNode) {
					((ShapeNode<?>) child).notifyObjectHasMoved();
				}
			}
		}

		if (getMovedObject().getUndoManager() != null) {
			getMovedObject().getUndoManager().stopRecording(moveEdit);
		}

	}

	boolean isDnDPattern(Point newLocationInDrawingView, MouseControlContext context) {
		if (movedObjects.size() != 1) {
			return false;
		}

		FGEPoint startMovingPoint = movedObjects.get(movedObject);

		FGEPoint desiredLocation = new FGEPoint(
				startMovingPoint.x + (newLocationInDrawingView.x - startMovingLocationInDrawingView.x) / view.getScale(),
				startMovingPoint.y + (newLocationInDrawingView.y - startMovingLocationInDrawingView.y) / view.getScale());

		if (movedObject.getParentNode() instanceof ShapeNode) {
			ShapeNode<?> container = (ShapeNode<?>) movedObject.getParentNode();
			FGERectangle bounds = new FGERectangle(0, 0, container.getWidth() - movedObject.getWidth(),
					container.getHeight() - movedObject.getHeight(), Filling.FILLED);
			FGEPoint nearestPoint = bounds.getNearestPoint(desiredLocation);
			Point p1 = FGEUtils.convertPointFromDrawableToDrawing(movedObject.getParentNode(), desiredLocation.toPoint(), view.getScale());
			Point p2 = FGEUtils.convertPointFromDrawableToDrawing(movedObject.getParentNode(), nearestPoint.toPoint(), view.getScale());
			if (Point2D.distance(p1.x, p1.y, p2.x, p2.y) > FGEConstants.DND_DISTANCE) {
				return true;
			}
		}

		return false;
	}

	public ShapeNode<?> getMovedObject() {
		return movedObject;
	}

	public Set<ShapeNode<?>> getMovedObjects() {
		return movedObjects.keySet();
	}

	public Hashtable<ShapeNode<?>, FGEPoint> getInitialLocations() {
		return movedObjects;
	}

	public Point getInitialLocationInDrawingView() {
		return startMovingLocationInDrawingView;
	}

	public Point getCurrentLocationInDrawingView() {
		return currentLocationInDrawingView;
	}

}

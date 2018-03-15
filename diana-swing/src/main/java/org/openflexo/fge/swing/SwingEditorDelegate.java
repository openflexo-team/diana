/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.fge.swing;

import java.awt.Component;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditorDelegate;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.actions.MoveInfo;
import org.openflexo.fge.swing.paint.FGEPaintManager;
import org.openflexo.fge.swing.view.JDrawingView;
import org.openflexo.fge.view.FGEView;

public class SwingEditorDelegate implements DianaEditorDelegate {

	private AbstractDianaEditor<?, SwingViewFactory, JComponent> controller;

	private MoveInfo keyDrivenMovingSession;
	private KeyDrivenMovingSessionTimer keyDrivenMovingSessionTimer = null;

	public SwingEditorDelegate(AbstractDianaEditor<?, SwingViewFactory, JComponent> controller) {
		super();
		this.controller = controller;
	}

	public AbstractDianaEditor<?, SwingViewFactory, JComponent> getController() {
		return controller;
	}

	public Drawing<?> getDrawing() {
		return controller.getDrawing();
	}

	public JDrawingView<?> getDrawingView() {
		return (JDrawingView<?>) controller.getDrawingView();
	}

	public FGEPaintManager getPaintManager() {
		if (getDrawingView() != null) {
			return getDrawingView().getPaintManager();
		}
		return null;
	}

	@Override
	public void objectStartMoving(DrawingTreeNode<?, ?> node) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			getPaintManager().addToTemporaryObjects(node);
			getPaintManager().invalidate(node);
		}

	}

	@Override
	public void objectStopMoving(DrawingTreeNode<?, ?> node) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			getPaintManager().removeFromTemporaryObjects(node);
			getPaintManager().invalidate(node);
			getPaintManager().repaint(getDrawingView());
		}
	}

	@Override
	public void objectsStartMoving(Set<? extends DrawingTreeNode<?, ?>> nodes) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			for (DrawingTreeNode<?, ?> node : nodes) {
				getPaintManager().addToTemporaryObjects(node);
				getPaintManager().invalidate(node);
			}
		}
	}

	@Override
	public void objectsStopMoving(Set<? extends DrawingTreeNode<?, ?>> nodes) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			for (DrawingTreeNode<?, ?> node : nodes) {
				getPaintManager().removeFromTemporaryObjects(node);
				getPaintManager().invalidate(node);
			}
			getPaintManager().repaint(getDrawingView());
		}
	}

	@Override
	public void focusedObjectChanged(DrawingTreeNode<?, ?> oldFocusedObject, DrawingTreeNode<?, ?> newFocusedObject) {
		if (getPaintManager().isPaintingCacheEnabled()) {
			// Just repaint old and eventual new focused object
			if (oldFocusedObject != null) {
				getPaintManager().repaint(oldFocusedObject);
			}
			if (newFocusedObject != null) {
				getPaintManager().repaint(newFocusedObject);
			}
		}
		else {
			// @brutal mode
			getPaintManager().repaint(getDrawingView());
		}

	}

	@Override
	public void repaintAll() {
		getPaintManager().repaint(getDrawingView());
	}

	/**
	 * Process 'UP' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean upKeyPressed() {
		// System.out.println("Up");
		return getDrawing().isEditable() && keyDrivenMove(0, -1);
	}

	/**
	 * Process 'DOWN' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean downKeyPressed() {
		// System.out.println("Down");
		return getDrawing().isEditable() && keyDrivenMove(0, 1);
	}

	/**
	 * Process 'LEFT' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean leftKeyPressed() {
		// System.out.println("Left");
		return getDrawing().isEditable() && keyDrivenMove(-1, 0);
	}

	/**
	 * Process 'RIGHT' key pressed
	 * 
	 * @return boolean indicating if event was successfully processed
	 */
	public boolean rightKeyPressed() {
		// System.out.println("Right");
		return getDrawing().isEditable() && keyDrivenMove(1, 0);
	}

	private ShapeNode<?> getMovableShape() {
		if (getController() instanceof DianaInteractiveViewer) {
			return ((DianaInteractiveViewer<?, ?, ?>) getController()).getFirstSelectedShape();
		}
		return null;
	}

	private synchronized boolean keyDrivenMove(int deltaX, int deltaY) {
		if (keyDrivenMovingSessionTimer == null && getMovableShape() != null) {
			// System.out.println("BEGIN to move with keyboard");
			if (startKeyDrivenMovingSession()) {
				doMoveInSession(deltaX, deltaY);
				return true;
			}
			return false;
		}
		else if (keyDrivenMovingSessionTimer != null) {
			doMoveInSession(deltaX, deltaY);
			return true;
		}
		return false;
	}

	public void doMoveInSession(int deltaX, int deltaY) {
		keyDrivenMovingSessionTimer.typed();
		Point newLocation = keyDrivenMovingSession.getCurrentLocationInDrawingView();
		newLocation.x += deltaX;
		newLocation.y += deltaY;
		keyDrivenMovingSession.moveTo(newLocation);
	}

	private synchronized boolean startKeyDrivenMovingSession() {

		if (getMovableShape() != null
				&& getMovableShape().getGraphicalRepresentation().getLocationConstraints() != LocationConstraints.UNMOVABLE) {

			keyDrivenMovingSessionTimer = new KeyDrivenMovingSessionTimer();
			keyDrivenMovingSessionTimer.start();
			ShapeNode<?> movedObject = getMovableShape();
			keyDrivenMovingSession = new MoveInfo(movedObject, (DianaInteractiveViewer<?, ?, ?>) getController());
			// notifyWillMove(keyDrivenMovingSession);
			objectsStartMoving(keyDrivenMovingSession.getMovedObjects());
			return true;
		}
		else {
			return false;
		}
	}

	private synchronized void stopKeyDrivenMovingSession() {
		keyDrivenMovingSessionTimer = null;
		objectsStopMoving(keyDrivenMovingSession.getMovedObjects());
		keyDrivenMovingSession = null;
	}

	private class KeyDrivenMovingSessionTimer extends Thread {
		volatile boolean typed = false;

		public KeyDrivenMovingSessionTimer() {
			typed = true;
		}

		@Override
		public void run() {
			while (typed) {
				typed = false;
				try {
					sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			SwingUtilities.invokeLater(() -> stopKeyDrivenMovingSession());
		}

		public synchronized void typed() {
			typed = true;
			// System.out.println("Tiens on retape sur le clavier");
		}
	}

	@Override
	public Point getPointInView(Object source, Point point, FGEView<?, ?> view) {
		return SwingUtilities.convertPoint((Component) source, point, (Component) view);
	}

	/**
	 * Makes a screenshot of supplied graphical node
	 * 
	 * @param node
	 * @return
	 */
	@Override
	public BufferedImage makeScreenshot(DrawingTreeNode<?, ?> node) {
		return getPaintManager().getScreenshot(node);
	}

}

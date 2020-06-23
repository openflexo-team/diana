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

package org.openflexo.diana.control.actions;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.MouseControlContext;
import org.openflexo.diana.control.MouseDragControl;
import org.openflexo.diana.control.MouseDragControlAction;
import org.openflexo.pamela.factory.EditingContext;

public class MouseDragControlImpl<E extends AbstractDianaEditor<?, ?, ?>> extends MouseControlImpl<E> implements MouseDragControl<E> {

	public MouseDragControlAction<E> action;

	private DrawingTreeNode<?, ?> initialNode;
	private boolean isSignificativeDrag = false;

	public MouseDragControlImpl(String aName, MouseButton button, MouseDragControlAction<E> action, boolean shiftPressed,
			boolean ctrlPressed, boolean metaPressed, boolean altPressed, EditingContext editingContext) {
		super(aName, shiftPressed, ctrlPressed, metaPressed, altPressed, button, editingContext);
		this.action = action;
	}

	@Override
	public MouseDragControlAction<E> getControlAction() {
		return action;
	}

	@Override
	public void setControlAction(MouseDragControlAction<E> action) {
		this.action = action;
	}

	@Override
	public boolean isSignificativeDrag() {
		return isSignificativeDrag;
	}

	@Override
	public DrawingTreeNode<?, ?> getInitialNode() {
		return initialNode;
	}

	/**
	 * Handle mouse pressed event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context) {

		if (action.handleMousePressed(node, controller, context)) {
			initialNode = node;
			// System.out.println("PRESSED initialNode="+initialNode);
			context.consume();
			isSignificativeDrag = false;
			return true;
		}
		return false;
	}

	/**
	 * Handle mouse released event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public void handleMouseReleased(E controller, MouseControlContext context) {
		if (action.handleMouseReleased(initialNode, controller, context, isSignificativeDrag())) {
			initialNode = null;
			// System.out.println("RELEASED initialNode="+initialNode);
			context.consume();
		}
	}

	/**
	 * Handle mouse dragged event, by performing what is required here If event has been correctely handled, consume it.
	 * 
	 * @param graphicalRepresentation
	 * @param controller
	 * @param e
	 *            MouseEvent
	 */
	@Override
	public void handleMouseDragged(E controller, MouseControlContext context) {
		if (action.handleMouseDragged(initialNode, controller, context)) {
			// System.out.println("DRAGGED initialNode="+initialNode);
			isSignificativeDrag = true;
			context.consume();
		}
	}

	@Override
	public String toString() {
		return "MouseDragControlImpl[" + getName() + "," + getModifiersAsString() + "]";
	}

}

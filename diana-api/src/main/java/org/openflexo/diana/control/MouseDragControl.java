/**
 * 
 * Copyright (c) 2014, Openflexo
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


package org.openflexo.diana.control;

import org.openflexo.diana.Drawing.DrawingTreeNode;

public interface MouseDragControl<E extends DianaEditor<?>> extends MouseControl<E> {

	@Override
	public MouseDragControlAction<E> getControlAction();

	public void setControlAction(MouseDragControlAction<E> action);

	public boolean isSignificativeDrag();

	public DrawingTreeNode<?, ?> getInitialNode();

	/**
	 * Handle mouse pressed event
	 * 
	 * @param node
	 *            the node pointer focuses
	 * @param controller
	 *            the related editor
	 * @param context
	 *            the mouse control context (mouse informations)
	 * @return
	 */
	public boolean handleMousePressed(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

	/**
	 * Handle mouse released event
	 * 
	 * @param node
	 *            the node pointer focuses
	 * @param controller
	 *            the related editor
	 * @param context
	 *            the mouse control context (mouse informations)
	 */
	public void handleMouseReleased(E controller, MouseControlContext context);

	/**
	 * Handle mouse dragged event
	 * 
	 * @param node
	 *            the node pointer focuses
	 * @param controller
	 *            the related editor
	 * @param context
	 *            the mouse control context (mouse informations)
	 */
	public void handleMouseDragged(E controller, MouseControlContext context);

}

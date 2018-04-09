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

package org.openflexo.diana.control;

import java.util.logging.Logger;

import org.openflexo.diana.Drawing.DrawingTreeNode;

/**
 * A {@link MouseDragControlAction} represents an action to be applied as a "Drag"-scheme.<br>
 * It is associated to and triggered by a {@link MouseDragControl}
 * 
 * @author sylvain
 * 
 */
public interface MouseDragControlAction<E extends DianaEditor<?>> extends MouseControlAction<E> {

	public static final Logger logger = Logger.getLogger(MouseDragControlAction.class.getPackage().getName());

	// public abstract PredefinedMouseDragControlActionType getActionType();

	/**
	 * Handle mouse pressed event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @return
	 */
	public abstract boolean handleMousePressed(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

	/**
	 * Handle mouse released event, by performing what is required here Return flag indicating if event has been correctely handled and
	 * thus, should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @param isSignificativeDrag
	 *            TODO
	 * @return
	 */
	public abstract boolean handleMouseReleased(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context,
			boolean isSignificativeDrag);

	/**
	 * Handle mouse dragged event, by performing what is required here Return flag indicating if event has been correctely handled and thus,
	 * should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @return
	 */
	public abstract boolean handleMouseDragged(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

}

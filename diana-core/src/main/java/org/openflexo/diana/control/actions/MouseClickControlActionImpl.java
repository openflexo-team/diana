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

import java.util.logging.Logger;

import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.control.AbstractDianaEditor;
import org.openflexo.diana.control.MouseClickControlAction;
import org.openflexo.diana.control.MouseControlContext;

public abstract class MouseClickControlActionImpl<E extends AbstractDianaEditor<?, ?, ?>> extends MouseControlActionImpl<E> implements
		MouseClickControlAction<E> {

	static final Logger logger = Logger.getLogger(MouseClickControlActionImpl.class.getPackage().getName());

	/**
	 * Handle click, by performing what is required.<br>
	 * The implementation of this is technology-specific.<br>
	 * Return flag indicating if event has been correctely handled and thus, should be consumed.
	 * 
	 * @param node
	 *            the node on which this action applies
	 * @param controller
	 *            the editor
	 * @param context
	 *            run-time context of mouse control handling (eg MouseEvent)
	 * @return
	 */
	@Override
	public abstract boolean handleClick(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context);

}

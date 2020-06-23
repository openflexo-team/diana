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

import java.awt.Point;

import org.openflexo.diana.control.MouseControl.MouseButton;

/**
 * This is the abstraction of run-time context of mouse control handling.<br>
 * A possible implementation of this would be for example the {@link MouseEvent} for the Swing Technology Diana Implementation
 * 
 * @author sylvain
 */
public interface MouseControlContext {

	/**
	 * Return a flag indicating if context is still relevant<br>
	 * (Usefull to dismiss consumed events is Swing for example)
	 * 
	 * @return
	 */
	public boolean isConsumed();

	/**
	 * Returns which, if any, of the mouse buttons has changed state.
	 * 
	 * @return
	 */
	public MouseButton getButton();

	public boolean isShiftDown();

	public boolean isControlDown();

	public boolean isMetaDown();

	public boolean isAltDown();

	public int getClickCount();

	public void consume();

	public Object getSource();

	public Point getPoint();

}

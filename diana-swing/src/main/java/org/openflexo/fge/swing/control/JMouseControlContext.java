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

package org.openflexo.fge.swing.control;

import java.awt.Point;
import java.awt.event.MouseEvent;

import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.MouseControlContext;

/**
 * Implements {@link MouseControlContext} for SWING technology<br>
 * This implementation provides a wrapper of a {@link MouseEvent} object
 * 
 * @author sylvain
 * 
 */
public class JMouseControlContext implements MouseControlContext {

	private MouseEvent event;

	public JMouseControlContext(MouseEvent event) {
		super();
		this.event = event;
	}

	public MouseEvent getMouseEvent() {
		return event;
	}

	@Override
	public MouseButton getButton() {

		/*if (ToolBox.getPLATFORM() == ToolBox.MACOS && event.getButton() == MouseEvent.BUTTON1 && event.isControlDown()) {
			return MouseButton.RIGHT;
		}*/

		switch (event.getButton()) {
		case MouseEvent.BUTTON1:
			return MouseButton.LEFT;
		case MouseEvent.BUTTON2:
			return MouseButton.CENTER;
		case MouseEvent.BUTTON3:
			return MouseButton.RIGHT;
		default:
			return null;
		}
	}

	@Override
	public int getClickCount() {
		return event.getClickCount();
	}

	@Override
	public boolean isConsumed() {
		return event.isConsumed();
	}

	@Override
	public void consume() {
		event.consume();
	}

	@Override
	public boolean isShiftDown() {
		return event.isShiftDown();
	}

	@Override
	public boolean isControlDown() {
		return event.isControlDown();
	}

	@Override
	public boolean isMetaDown() {
		return event.isMetaDown();
	}

	@Override
	public boolean isAltDown() {
		return event.isAltDown();
	}

	@Override
	public Object getSource() {
		return event.getSource();
	}

	@Override
	public Point getPoint() {
		return event.getPoint();
	}

}

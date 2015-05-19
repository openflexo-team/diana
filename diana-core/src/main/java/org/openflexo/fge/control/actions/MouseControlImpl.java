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

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.MouseControl;
import org.openflexo.fge.control.MouseControlContext;
import org.openflexo.model.factory.EditingContext;

public abstract class MouseControlImpl<E extends AbstractDianaEditor<?, ?, ?>> implements MouseControl<E> {
	static final Logger logger = Logger.getLogger(MouseControlImpl.class.getPackage().getName());

	private String name;
	private boolean shiftPressed = false;
	private boolean ctrlPressed = false;
	private boolean metaPressed = false;
	private boolean altPressed = false;
	private MouseButton button;

	private final EditingContext editingContext;

	protected MouseControlImpl(String aName, boolean shiftPressed, boolean ctrlPressed, boolean metaPressed, boolean altPressed,
			MouseButton button, EditingContext editingContext) {
		super();
		name = aName;
		this.shiftPressed = shiftPressed;
		this.ctrlPressed = ctrlPressed;
		this.metaPressed = metaPressed;
		this.altPressed = altPressed;
		this.button = button;
		this.editingContext = editingContext;
	}

	public EditingContext getEditingContext() {
		return editingContext;
	}

	@Override
	public boolean isApplicable(DrawingTreeNode<?, ?> node, E controller, MouseControlContext context) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Called isApplicable(MouseEvent) for " + this + " event=" + context);
		}

		if (context.isConsumed()) {
			return false;
		}

		if (button != context.getButton()) {
			return false;
		}

		if (shiftPressed != context.isShiftDown()) {
			return false;
		}
		if (ctrlPressed != context.isControlDown()) {
			return false;
		}

		if (button == MouseButton.RIGHT) {
			// Correction here: on all platforms, it is impossible to
			// distinguish right-click with meta key down from right-click
			// without meta key down (simply because the masks are the same! the
			// same goes for the middle button and alt-down). However, the
			// distinction is that on MacOS, the Meta-key is used to perform
			// multiple non-contiguous selection, when on Windows (and Linux)
			// this is performed with the CTRL key.
			/*
			// Special case for MacOS platform: right-click is emuled by APPLE key (=<META>)
			// cannot distinguish both, so just skip this test
			 */
		} else {
			if (metaPressed != context.isMetaDown()) {
				return false;
			}
		}

		if (button == MouseButton.CENTER) {
		} else if (altPressed != context.isAltDown()) {
			return false;
		}

		// Everything seems ok, now delegate this to the action
		if (getControlAction() != null) {
			return getControlAction().isApplicable(node, controller, context);
		} else { // No action, return false
			return false;
		}
	}

	protected String getModifiersAsString() {
		return button.name() + (shiftPressed ? ",SHIFT" : "") + (ctrlPressed ? ",CTRL" : "") + (metaPressed ? ",META" : "")
				+ (altPressed ? ",ALT" : "");
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean isShiftPressed() {
		return shiftPressed;
	}

	public void setShiftPressed(boolean shiftPressed) {
		this.shiftPressed = shiftPressed;
	}

	@Override
	public boolean isCtrlPressed() {
		return ctrlPressed;
	}

	public void setCtrlPressed(boolean ctrlPressed) {
		this.ctrlPressed = ctrlPressed;
	}

	@Override
	public boolean isMetaPressed() {
		return metaPressed;
	}

	public void setMetaPressed(boolean metaPressed) {
		this.metaPressed = metaPressed;
	}

	@Override
	public boolean isAltPressed() {
		return altPressed;
	}

	public void setAltPressed(boolean altPressed) {
		this.altPressed = altPressed;
	}

	@Override
	public MouseButton getButton() {
		return button;
	}

	public void setButton(MouseButton button) {
		this.button = button;
	}

}

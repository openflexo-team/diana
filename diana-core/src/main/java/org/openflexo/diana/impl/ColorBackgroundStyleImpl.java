/**
 * 
 * Copyright (c) 2014, Openflexo
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

package org.openflexo.diana.impl;

import org.openflexo.diana.ColorBackgroundStyle;
import org.openflexo.diana.notifications.FGEAttributeNotification;

public abstract class ColorBackgroundStyleImpl extends BackgroundStyleImpl implements ColorBackgroundStyle {

	private java.awt.Color color;

	public ColorBackgroundStyleImpl() {
		color = java.awt.Color.WHITE;
	}

	public ColorBackgroundStyleImpl(java.awt.Color aColor) {
		color = aColor;
	}

	@Override
	public java.awt.Color getColor() {
		return color;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.COLOR;
	}

	@Override
	public void setColor(java.awt.Color aColor) {
		if (requireChange(this.color, aColor)) {
			java.awt.Color oldColor = color;
			this.color = aColor;
			notifyObservers(new FGEAttributeNotification<>(COLOR, oldColor, aColor));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.COLOR(" + getColor() + ")";
	}*/

	private static boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			}
			else {
				return true;
			}
		}
		return !oldObject.equals(newObject);
	}

}

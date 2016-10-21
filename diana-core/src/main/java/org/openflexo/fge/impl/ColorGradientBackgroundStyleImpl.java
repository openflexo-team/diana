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

package org.openflexo.fge.impl;

import java.awt.Color;

import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class ColorGradientBackgroundStyleImpl extends BackgroundStyleImpl implements ColorGradientBackgroundStyle {

	private java.awt.Color color1;
	private java.awt.Color color2;
	private ColorGradientBackgroundStyle.ColorGradientDirection direction;

	public ColorGradientBackgroundStyleImpl() {
		this(java.awt.Color.WHITE, java.awt.Color.BLACK,
				org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection.NORTH_WEST_SOUTH_EAST);
	}

	public ColorGradientBackgroundStyleImpl(java.awt.Color aColor1, java.awt.Color aColor2,
			ColorGradientBackgroundStyle.ColorGradientDirection aDirection) {
		super();
		this.color1 = aColor1;
		this.color2 = aColor2;
		this.direction = aDirection;
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.COLOR_GRADIENT;
	}

	@Override
	public java.awt.Color getColor1() {
		return color1;
	}

	@Override
	public void setColor1(java.awt.Color aColor) {
		if (requireChange(this.color1, aColor)) {
			java.awt.Color oldColor = color1;
			this.color1 = aColor;
			setChanged();
			notifyObservers(new FGEAttributeNotification<Color>(COLOR1, oldColor, aColor));
		}
	}

	@Override
	public java.awt.Color getColor2() {
		return color2;
	}

	@Override
	public void setColor2(java.awt.Color aColor) {
		if (requireChange(this.color2, aColor)) {
			java.awt.Color oldColor = color2;
			this.color2 = aColor;
			setChanged();
			notifyObservers(new FGEAttributeNotification<Color>(COLOR2, oldColor, aColor));
		}
	}

	@Override
	public ColorGradientBackgroundStyle.ColorGradientDirection getDirection() {
		return direction;
	}

	@Override
	public void setDirection(ColorGradientBackgroundStyle.ColorGradientDirection aDirection) {
		if (requireChange(this.direction, aDirection)) {
			ColorGradientBackgroundStyle.ColorGradientDirection oldTexture = direction;
			this.direction = aDirection;
			setChanged();
			notifyObservers(new FGEAttributeNotification<ColorGradientDirection>(DIRECTION, oldTexture, aDirection));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.COLOR_GRADIENT(" + getColor1() + "," + getColor2() + "," + getDirection() + ")";
	}*/

	private boolean requireChange(Object oldObject, Object newObject) {
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

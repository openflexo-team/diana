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

package org.openflexo.diana.impl;

import java.awt.Color;
import java.awt.Font;

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.TextStyle;
import org.openflexo.diana.notifications.DianaAttributeNotification;

public abstract class TextStyleImpl extends DianaStyleImpl implements TextStyle {
	private Color color;
	private Color backgroundColor = Color.WHITE;
	private Font font;
	private int orientation = 0; // angle in degree
	private boolean backgroundColored = false;

	public TextStyleImpl() {
		super();
		color = DianaConstants.DEFAULT_TEXT_COLOR;
		font = DianaConstants.DEFAULT_TEXT_FONT;
	}

	/*@Deprecated
	private TextStyleImpl(Color aColor, Font aFont) {
		this();
		color = aColor;
		font = aFont;
	}
	
	@Deprecated
	private static TextStyleImpl makeDefault() {
		return makeTextStyle(DianaConstants.DEFAULT_TEXT_COLOR, DianaConstants.DEFAULT_TEXT_FONT);
	}
	
	@Deprecated
	private static TextStyleImpl makeTextStyle(Color aColor, Font aFont) {
		return new TextStyleImpl(aColor, aFont);
	}
	*/

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color aColor) {
		if (requireChange(this.color, aColor)) {
			Color oldColor = color;
			this.color = aColor;
			notifyObservers(new DianaAttributeNotification<>(COLOR, oldColor, aColor));
		}
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public void setFont(Font aFont) {
		if (requireChange(this.font, aFont)) {
			Font oldFont = this.font;
			this.font = aFont;
			notifyObservers(new DianaAttributeNotification<>(FONT, oldFont, aFont));
		}
	}

	@Override
	public int getOrientation() {
		return orientation;
	}

	@Override
	public void setOrientation(int anOrientation) {
		if (requireChange(this.orientation, anOrientation)) {
			int oldOrientation = this.orientation;
			orientation = anOrientation;
			notifyObservers(new DianaAttributeNotification<>(ORIENTATION, oldOrientation, anOrientation));
		}
	}

	@Override
	public Color getBackgroundColor() {
		return backgroundColor;
	}

	@Override
	public void setBackgroundColor(Color aColor) {
		if (requireChange(this.backgroundColor, aColor)) {
			Color oldColor = backgroundColor;
			this.backgroundColor = aColor;
			notifyObservers(new DianaAttributeNotification<>(BACKGROUND_COLOR, oldColor, aColor));
		}
	}

	@Override
	public boolean getIsBackgroundColored() {
		return backgroundColored;
	}

	@Override
	public void setIsBackgroundColored(boolean aFlag) {
		if (requireChange(this.backgroundColored, aFlag)) {
			boolean oldValue = backgroundColored;
			this.backgroundColored = aFlag;
			notifyObservers(new DianaAttributeNotification<>(IS_BACKGROUND_COLORED, oldValue, aFlag));
		}
	}

	private static boolean requireChange(Object oldObject, Object newObject) {
		if (oldObject == null) {
			if (newObject == null) {
				return false;
			}
			return true;
		}
		return !oldObject.equals(newObject);
	}
}

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

import org.openflexo.diana.DianaConstants;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.notifications.DianaAttributeNotification;

public abstract class ShadowStyleImpl extends DianaStyleImpl implements ShadowStyle {
	private boolean drawShadow;
	private int shadowDarkness;
	private int shadowDepth;
	private int shadowBlur;

	public ShadowStyleImpl() {
		drawShadow = true;
		shadowDarkness = DianaConstants.DEFAULT_SHADOW_DARKNESS;
		shadowDepth = DianaConstants.DEFAULT_SHADOW_DEEP;
		shadowBlur = DianaConstants.DEFAULT_SHADOW_BLUR;
	}

	/*@SuppressWarnings("unused")
	@Deprecated
	private static ShadowStyleImpl makeNone() {
		ShadowStyleImpl returned = new ShadowStyleImpl();
		returned.drawShadow = false;
		returned.shadowDepth = 0;
		return returned;
	}
	
	@SuppressWarnings("unused")
	@Deprecated
	private static ShadowStyleImpl makeDefault() {
		return new ShadowStyleImpl();
	}*/

	@Override
	public boolean getDrawShadow() {
		return drawShadow;
	}

	@Override
	public void setDrawShadow(boolean aFlag) {
		if (requireChange(this.drawShadow, aFlag)) {
			boolean oldValue = drawShadow;
			this.drawShadow = aFlag;
			notifyObservers(new DianaAttributeNotification<>(DRAW_SHADOW, oldValue, aFlag));
		}
	}

	@Override
	public int getShadowDarkness() {
		return shadowDarkness;
	}

	@Override
	public void setShadowDarkness(int aValue) {
		if (requireChange(this.shadowDarkness, aValue)) {
			int oldShadowDarkness = shadowDarkness;
			shadowDarkness = aValue;
			notifyObservers(new DianaAttributeNotification<>(SHADOW_DARKNESS, oldShadowDarkness, aValue));
		}
	}

	@Override
	public int getShadowDepth() {
		return shadowDepth;
	}

	@Deprecated
	public int getShadowDeep() {
		return getShadowDepth();
	}

	@Override
	public void setShadowDepth(int aValue) {
		if (requireChange(this.shadowDepth, aValue)) {
			int oldShadowDeep = shadowDepth;
			shadowDepth = aValue;
			notifyObservers(new DianaAttributeNotification<>(SHADOW_DEPTH, oldShadowDeep, aValue));
		}
	}

	@Override
	public int getShadowBlur() {
		return shadowBlur;
	}

	@Override
	public void setShadowBlur(int aValue) {
		if (requireChange(this.shadowBlur, aValue)) {
			int oldShadowBlur = shadowBlur;
			shadowBlur = aValue;
			notifyObservers(new DianaAttributeNotification<>(SHADOW_BLUR, oldShadowBlur, aValue));
		}
	}

	/*@Override
	public ShadowStyle clone() {
		try {
			return (ShadowStyle) super.clone();
		} catch (CloneNotSupportedException e) {
			// cannot happen, we are clonable
			e.printStackTrace();
			return null;
		}
	}*/

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

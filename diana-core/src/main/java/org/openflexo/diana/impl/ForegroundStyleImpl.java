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
import java.util.logging.Logger;

import org.openflexo.diana.DianaCoreUtils;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.notifications.DianaAttributeNotification;

public abstract class ForegroundStyleImpl extends DianaStyleImpl implements ForegroundStyle {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ForegroundStyle.class.getPackage().getName());

	private boolean noStroke = false;

	private Color color;
	private double lineWidth;
	private JoinStyle joinStyle;
	private CapStyle capStyle;
	private DashStyle dashStyle;

	private boolean useTransparency = false;
	private float transparencyLevel = 0.5f; // Between 0.0 and 1.0

	// private Stroke stroke;
	// private double strokeScale;

	public ForegroundStyleImpl() {
		super();
		noStroke = false;
		color = Color.BLACK;
		lineWidth = 1.0;
		joinStyle = JoinStyle.JOIN_MITER;
		capStyle = CapStyle.CAP_SQUARE;
		dashStyle = DashStyle.PLAIN_STROKE;
	}

	@Override
	public CapStyle getCapStyle() {
		return capStyle;
	}

	@Override
	public void setCapStyle(CapStyle aCapStyle) {
		if (requireChange(this.capStyle, aCapStyle)) {
			CapStyle oldCapStyle = capStyle;
			this.capStyle = aCapStyle;
			// stroke = null;
			notifyObservers(new DianaAttributeNotification<>(CAP_STYLE, oldCapStyle, aCapStyle));
		}
	}

	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color aColor) {
		if (requireChange(this.color, aColor)) {
			java.awt.Color oldColor = color;
			this.color = aColor;
			notifyObservers(new DianaAttributeNotification<>(COLOR, oldColor, aColor));
		}
	}

	@Override
	public void setColorNoNotification(Color aColor) {
		this.color = aColor;
	}

	@Override
	public DashStyle getDashStyle() {
		return dashStyle;
	}

	@Override
	public void setDashStyle(DashStyle aDashStyle) {
		if (requireChange(this.color, aDashStyle)) {
			DashStyle oldDashStyle = dashStyle;
			this.dashStyle = aDashStyle;
			// stroke = null;
			notifyObservers(new DianaAttributeNotification<>(DASH_STYLE, oldDashStyle, dashStyle));
		}
	}

	@Override
	public JoinStyle getJoinStyle() {
		return joinStyle;
	}

	@Override
	public void setJoinStyle(JoinStyle aJoinStyle) {
		if (requireChange(this.joinStyle, aJoinStyle)) {
			JoinStyle oldJoinStyle = joinStyle;
			this.joinStyle = aJoinStyle;
			// stroke = null;
			notifyObservers(new DianaAttributeNotification<>(JOIN_STYLE, oldJoinStyle, aJoinStyle));
		}
	}

	@Override
	public double getLineWidth() {
		return lineWidth;
	}

	@Override
	public void setLineWidth(double aLineWidth) {
		if (requireChange(this.lineWidth, aLineWidth)) {
			double oldLineWidth = lineWidth;
			lineWidth = aLineWidth;
			// stroke = null;
			notifyObservers(new DianaAttributeNotification<>(LINE_WIDTH, oldLineWidth, aLineWidth));
		}
	}

	@Override
	public boolean getNoStroke() {
		return noStroke;
	}

	@Override
	public void setNoStroke(boolean aFlag) {
		if (requireChange(this.noStroke, aFlag)) {
			boolean oldValue = noStroke;
			this.noStroke = aFlag;
			notifyObservers(new DianaAttributeNotification<>(NO_STROKE, oldValue, aFlag));
		}
	}

	@Override
	public float getTransparencyLevel() {
		return transparencyLevel;
	}

	@Override
	public void setTransparencyLevel(float aLevel) {
		if (requireChange(this.transparencyLevel, aLevel)) {
			float oldValue = transparencyLevel;
			this.transparencyLevel = aLevel;
			notifyObservers(new DianaAttributeNotification<>(TRANSPARENCY_LEVEL, oldValue, aLevel));
		}
	}

	@Override
	public boolean getUseTransparency() {
		return useTransparency;
	}

	@Override
	public void setUseTransparency(boolean aFlag) {
		if (requireChange(this.useTransparency, aFlag)) {
			boolean oldValue = useTransparency;
			this.useTransparency = aFlag;
			notifyObservers(new DianaAttributeNotification<>(USE_TRANSPARENCY, oldValue, aFlag));
		}
	}

	@Override
	public String toNiceString() {
		if (getNoStroke()) {
			return DianaCoreUtils.DIANA_LOCALIZATION.localizedForKey("no_stroke");
		}
		return lineWidth + "pt, " + color;
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

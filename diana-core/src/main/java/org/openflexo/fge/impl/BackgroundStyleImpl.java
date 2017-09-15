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

package org.openflexo.fge.impl;

import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.notifications.FGEAttributeNotification;

public abstract class BackgroundStyleImpl extends FGEStyleImpl implements BackgroundStyle {

	static final Logger logger = Logger.getLogger(BackgroundStyle.class.getPackage().getName());

	// private transient GraphicalRepresentation graphicalRepresentation;

	private boolean useTransparency = false;
	private float transparencyLevel = 0.5f; // Between 0.0 and 1.0

	public static GRProperty<?> getParameter(String parameterName) {
		return GRProperty.getGRParameter(BackgroundStyle.class, parameterName);
	}

	public static Collection<GRProperty<?>> getAllParameters() {
		return GRProperty.getGRParameters(BackgroundStyle.class);
	}

	/*@Deprecated
	public static BackgroundStyle makeEmptyBackground() {
		return new NoneBackgroundStyleImpl();
	}
	
	@Deprecated
	public static BackgroundStyle makeColoredBackground(java.awt.Color aColor) {
		return new ColorBackgroundStyleImpl(aColor);
	}
	
	@Deprecated
	public static BackgroundStyle makeColorGradientBackground(java.awt.Color color1, java.awt.Color color2,
			ColorGradientBackgroundStyle.ColorGradientDirection direction) {
		return new ColorGradientBackgroundStyleImpl(color1, color2, direction);
	}
	
	@Deprecated
	public static BackgroundStyle makeTexturedBackground(TextureBackgroundStyle.TextureType type, java.awt.Color aColor1,
			java.awt.Color aColor2) {
		return new TextureBackgroundStyleImpl(type, aColor1, aColor2);
	}
	
	@Deprecated
	public static BackgroundImageBackgroundStyle makeImageBackground(File imageFile) {
		return new BackgroundImageBackgroundStyleImpl(imageFile);
	}
	
	@Deprecated
	public static BackgroundImageBackgroundStyle makeImageBackground(ImageIcon image) {
		return new BackgroundImageBackgroundStyleImpl(image);
	}*/

	// @Deprecated
	/*public static BackgroundStyle makeBackground(BackgroundStyleType type) {
		if (type == BackgroundStyleType.NONE) {
			return makeEmptyBackground();
		} else if (type == BackgroundStyleType.COLOR) {
			return makeColoredBackground(java.awt.Color.WHITE);
		} else if (type == BackgroundStyleType.COLOR_GRADIENT) {
			return makeColorGradientBackground(java.awt.Color.WHITE, java.awt.Color.BLACK,
					org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection.SOUTH_EAST_NORTH_WEST);
		} else if (type == BackgroundStyleType.TEXTURE) {
			return makeTexturedBackground(org.openflexo.fge.TextureBackgroundStyle.TextureType.TEXTURE1, java.awt.Color.RED,
					java.awt.Color.WHITE);
		} else if (type == BackgroundStyleType.IMAGE) {
			return makeImageBackground((File) null);
		}
		return null;
	}*/
	// @Override
	// public abstract Paint getPaint(DrawingTreeNode<?, ?> dtn, double scale);

	@Override
	public abstract BackgroundStyleType getBackgroundStyleType();

	/*public GraphicalRepresentation getGraphicalRepresentation()
	{
		return graphicalRepresentation;
	}
	
	public void setGraphicalRepresentation(
			GraphicalRepresentation graphicalRepresentation)
	{
		this.graphicalRepresentation = graphicalRepresentation;
	}*/

	// @Override
	// public abstract String toString();

	@Override
	public float getTransparencyLevel() {
		return transparencyLevel;
	}

	@Override
	public void setTransparencyLevel(float aLevel) {
		if (requireChange(this.transparencyLevel, aLevel)) {
			float oldValue = transparencyLevel;
			this.transparencyLevel = aLevel;
			setChanged();
			notifyObservers(new FGEAttributeNotification<>(TRANSPARENCY_LEVEL, oldValue, aLevel));
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
			setChanged();
			notifyObservers(new FGEAttributeNotification<>(USE_TRANSPARENCY, oldValue, aFlag));
		}
	}

	/*@Override
	public BackgroundStyle clone() {
		try {
			BackgroundStyle returned = (BackgroundStyle) super.clone();
			// ((BackgroundStyleImpl) returned).graphicalRepresentation = null;
			return returned;
		} catch (CloneNotSupportedException e) {
			// cannot happen since we are clonable
			e.printStackTrace();
			return null;
		}
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

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

import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import org.openflexo.diana.BackgroundImageBackgroundStyle;
import org.openflexo.diana.notifications.DianaAttributeNotification;
import org.openflexo.icon.ImageIconResource;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

public abstract class BackgroundImageBackgroundStyleImpl extends BackgroundStyleImpl implements BackgroundImageBackgroundStyle {

	private Resource imageResource;
	private Image image;

	public BackgroundImageBackgroundStyleImpl() {
		this((ImageIcon) null);
	}

	public BackgroundImageBackgroundStyleImpl(Resource imageResource) {
		super();
		setImageResource(imageResource);
	}

	public BackgroundImageBackgroundStyleImpl(ImageIcon image) {
		super();
		if (image != null) {
			this.image = image.getImage();
		}
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType() {
		return BackgroundStyleType.IMAGE;
	}

	@Override
	public File getImageFile() {
		if (getImageResource() != null) {
			return getImageResource().getLocator().retrieveResourceAsFile(getImageResource());
		}
		return null;
	}

	@Override
	public void setImageFile(File imageFile) {
		if (imageFile == null) {
			setImageResource(null);
		}
		else {
			setImageResource(ResourceLocator.locateResource(imageFile.getPath()));
		}
	}

	@Override
	public Resource getImageResource() {
		return imageResource;
	}

	@Override
	public void setImageResource(Resource anImageResource) {
		if (requireChange(this.imageResource, anImageResource)) {
			Resource oldResource = imageResource;
			imageResource = anImageResource;
			if (anImageResource != null) {
				image = new ImageIconResource(anImageResource).getImage();
			}
			else {
				image = null;
			}
			notifyObservers(new DianaAttributeNotification<>(IMAGE_RESOURCE, oldResource, anImageResource));
			getPropertyChangeSupport().firePropertyChange("image", null, getImage());
		}
	}

	@Override
	public Image getImage() {
		return image;
	}

	@Override
	public void setImage(Image image) {
		this.image = image;
	}

	private BackgroundImageBackgroundStyle.ImageBackgroundType imageBackgroundType = ImageBackgroundType.TRANSPARENT;
	private double scaleX = 1.0;
	private double scaleY = 1.0;
	private double deltaX = 0.0;
	private double deltaY = 0.0;
	private java.awt.Color imageBackgroundColor = java.awt.Color.WHITE;
	private boolean fitToShape = false;

	@Override
	public java.awt.Color getImageBackgroundColor() {
		return imageBackgroundColor;
	}

	@Override
	public void setImageBackgroundColor(java.awt.Color aColor) {
		if (requireChange(this.imageBackgroundColor, aColor)) {
			java.awt.Color oldColor = imageBackgroundColor;
			this.imageBackgroundColor = aColor;
			notifyObservers(new DianaAttributeNotification<>(IMAGE_BACKGROUND_COLOR, oldColor, aColor));
		}
	}

	@Override
	public double getDeltaX() {
		return deltaX;
	}

	@Override
	public void setDeltaX(double aDeltaX) {
		if (requireChange(this.deltaX, aDeltaX)) {
			double oldDeltaX = this.deltaX;
			this.deltaX = aDeltaX;
			notifyObservers(new DianaAttributeNotification<>(DELTA_X, oldDeltaX, deltaX));
		}
	}

	@Override
	public double getDeltaY() {
		return deltaY;
	}

	@Override
	public void setDeltaY(double aDeltaY) {
		if (requireChange(this.deltaY, aDeltaY)) {
			double oldDeltaY = this.deltaY;
			this.deltaY = aDeltaY;
			notifyObservers(new DianaAttributeNotification<>(DELTA_Y, oldDeltaY, deltaY));
		}
	}

	@Override
	public BackgroundImageBackgroundStyle.ImageBackgroundType getImageBackgroundType() {
		return imageBackgroundType;
	}

	@Override
	public void setImageBackgroundType(BackgroundImageBackgroundStyle.ImageBackgroundType anImageBackgroundType) {
		if (requireChange(this.imageBackgroundType, anImageBackgroundType)) {
			BackgroundImageBackgroundStyle.ImageBackgroundType oldImageBackgroundType = this.imageBackgroundType;
			this.imageBackgroundType = anImageBackgroundType;
			notifyObservers(new DianaAttributeNotification<>(IMAGE_BACKGROUND_TYPE, oldImageBackgroundType, anImageBackgroundType));
		}
	}

	@Override
	public double getScaleX() {
		return scaleX;
	}

	@Override
	public void setScaleX(double aScaleX) {
		if (requireChange(this.scaleX, aScaleX)) {
			double oldScaleX = this.scaleX;
			// logger.info(toString()+": Sets scaleX from "+oldScaleX+" to "+aScaleX);
			this.scaleX = aScaleX;
			notifyObservers(new DianaAttributeNotification<>(SCALE_X, oldScaleX, scaleX));
		}
	}

	@Override
	public void setScaleXNoNotification(double aScaleX) {
		if (requireChange(this.scaleX, aScaleX)) {
			this.scaleX = aScaleX;
		}
	}

	@Override
	public double getScaleY() {
		return scaleY;
	}

	@Override
	public void setScaleY(double aScaleY) {
		if (requireChange(this.scaleY, aScaleY)) {
			double oldScaleY = this.scaleY;
			// logger.info(toString()+": Sets scaleY from "+oldScaleY+" to "+aScaleY);
			this.scaleY = aScaleY;
			notifyObservers(new DianaAttributeNotification<>(SCALE_Y, oldScaleY, scaleY));
		}
	}

	@Override
	public void setScaleYNoNotification(double aScaleY) {
		if (requireChange(this.scaleY, aScaleY)) {
			this.scaleY = aScaleY;
		}
	}

	@Override
	public boolean getFitToShape() {
		return fitToShape;
	}

	@Override
	public void setFitToShape(boolean aFlag) {
		if (requireChange(this.fitToShape, aFlag)) {
			boolean oldValue = fitToShape;
			this.fitToShape = aFlag;
			notifyObservers(new DianaAttributeNotification<>(FIT_TO_SHAPE, oldValue, aFlag));
		}
	}

	/*@Override
	public String toString() {
		return "BackgroundStyle.IMAGE(" + getImageFile() + ")";
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

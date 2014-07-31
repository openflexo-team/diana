/*
 * (c) Copyright 2010-2011 AgileBirds
 * (c) Copyright 2012-2013 Openflexo
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.fge;

import java.awt.Color;
import java.awt.Image;
import java.io.File;

import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.rm.Resource;

/**
 * Represents a background filled with an image
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@XMLElement(xmlTag = "ImageBackgroundStyle")
public interface BackgroundImageBackgroundStyle extends BackgroundStyle {

	@PropertyIdentifier(type = Resource.class)
	public static final String IMAGE_RESOURCE_KEY = "imageResource";
	@PropertyIdentifier(type = Double.class)
	public static final String SCALE_X_KEY = "scaleX";
	@PropertyIdentifier(type = Double.class)
	public static final String SCALE_Y_KEY = "scaleY";
	@PropertyIdentifier(type = Double.class)
	public static final String DELTA_X_KEY = "deltaX";
	@PropertyIdentifier(type = Double.class)
	public static final String DELTA_Y_KEY = "deltaY";
	@PropertyIdentifier(type = Boolean.class)
	public static final String FIT_TO_SHAPE_KEY = "fitToShape";
	@PropertyIdentifier(type = ImageBackgroundType.class)
	public static final String IMAGE_BACKGROUND_TYPE_KEY = "imageBackgroundType";
	@PropertyIdentifier(type = Color.class)
	public static final String IMAGE_BACKGROUND_COLOR_KEY = "imageBackgroundColor";

	public static GRProperty<Resource> IMAGE_RESOURCE = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class,
			IMAGE_RESOURCE_KEY, Resource.class);
	public static GRProperty<Double> SCALE_X = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class, SCALE_X_KEY, Double.class);
	public static GRProperty<Double> SCALE_Y = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class, SCALE_Y_KEY, Double.class);
	public static GRProperty<Double> DELTA_X = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class, DELTA_X_KEY, Double.class);
	public static GRProperty<Double> DELTA_Y = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class, DELTA_Y_KEY, Double.class);
	public static GRProperty<Boolean> FIT_TO_SHAPE = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class, FIT_TO_SHAPE_KEY,
			Boolean.class);
	public static GRProperty<ImageBackgroundType> IMAGE_BACKGROUND_TYPE = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class,
			IMAGE_BACKGROUND_TYPE_KEY, ImageBackgroundType.class);
	public static GRProperty<Color> IMAGE_BACKGROUND_COLOR = GRProperty.getGRParameter(BackgroundImageBackgroundStyle.class,
			IMAGE_BACKGROUND_COLOR_KEY, Color.class);

	public static enum ImageBackgroundType {
		OPAQUE, TRANSPARENT
	}

	@Override
	public BackgroundStyleType getBackgroundStyleType();

	@Getter(value = IMAGE_RESOURCE_KEY)
	@XMLAttribute
	public Resource getImageResource();

	@Setter(value = IMAGE_RESOURCE_KEY)
	public void setImageResource(Resource anImageResource);

	@Getter(value = IMAGE_BACKGROUND_COLOR_KEY)
	@XMLAttribute
	public java.awt.Color getImageBackgroundColor();

	@Setter(value = IMAGE_BACKGROUND_COLOR_KEY)
	public void setImageBackgroundColor(java.awt.Color aColor);

	@Getter(value = DELTA_X_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getDeltaX();

	@Setter(value = DELTA_X_KEY)
	public void setDeltaX(double aDeltaX);

	@Getter(value = DELTA_Y_KEY, defaultValue = "0.0")
	@XMLAttribute
	public double getDeltaY();

	@Setter(value = DELTA_Y_KEY)
	public void setDeltaY(double aDeltaY);

	@Getter(value = IMAGE_BACKGROUND_TYPE_KEY)
	@XMLAttribute
	public ImageBackgroundType getImageBackgroundType();

	@Setter(value = IMAGE_BACKGROUND_TYPE_KEY)
	public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType);

	@Getter(value = SCALE_X_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getScaleX();

	@Setter(value = SCALE_X_KEY)
	public void setScaleX(double aScaleX);

	@Getter(value = SCALE_Y_KEY, defaultValue = "1.0")
	@XMLAttribute
	public double getScaleY();

	@Setter(value = SCALE_Y_KEY)
	public void setScaleY(double aScaleY);

	@Getter(value = FIT_TO_SHAPE_KEY, defaultValue = "false")
	@XMLAttribute
	public boolean getFitToShape();

	@Setter(value = FIT_TO_SHAPE_KEY)
	public void setFitToShape(boolean aFlag);

	public Image getImage();

	public void setImage(Image image);

	public void setScaleXNoNotification(double aScaleX);

	public void setScaleYNoNotification(double aScaleY);

	public File getImageFile();

	public void setImageFile(File file);

}

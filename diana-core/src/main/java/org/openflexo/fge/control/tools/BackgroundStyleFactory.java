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

package org.openflexo.fge.control.tools;

import java.awt.Color;
import java.awt.Image;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.GeometricNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGECoreUtils;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.NoneBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.rm.Resource;
import org.openflexo.rm.ResourceLocator;

/**
 * Convenient class used to manipulate BackgroundStyle instances over BackgroundStyle class hierarchy
 * 
 * @author sylvain
 * 
 */
public class BackgroundStyleFactory implements StyleFactory<BackgroundStyle, BackgroundStyleType> {

	private static final Logger logger = Logger.getLogger(BackgroundStyleFactory.class.getPackage().getName());

	private static final String DELETED = "deleted";
	// private AbstractInspectedBackgroundStyle<?> backgroundStyle;

	private BackgroundStyleType backgroundStyleType = BackgroundStyleType.COLOR;

	private InspectedNoneBackgroundStyle noneBackgroundStyle;
	private final InspectedColorBackgroundStyle colorBackgroundStyle;
	private final InspectedColorGradientBackgroundStyle colorGradientBackgroundStyle;
	private final InspectedTextureBackgroundStyle textureBackgroundStyle;
	private final InspectedBackgroundImageBackgroundStyle backgroundImageBackgroundStyle;

	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;

	private final DianaInteractiveViewer<?, ?, ?> controller;

	public BackgroundStyleFactory(DianaInteractiveViewer<?, ?, ?> controller) {
		pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		if (controller != null) {
			fgeFactory = controller.getFactory();
		}
		else {
			fgeFactory = FGECoreUtils.TOOLS_FACTORY;
		}
		noneBackgroundStyle = new InspectedNoneBackgroundStyle(controller, fgeFactory.makeEmptyBackground());
		colorBackgroundStyle = new InspectedColorBackgroundStyle(controller,
				fgeFactory.makeColoredBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR));
		colorGradientBackgroundStyle = new InspectedColorGradientBackgroundStyle(controller, fgeFactory.makeColorGradientBackground(
				FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.WHITE, ColorGradientDirection.NORTH_WEST_SOUTH_EAST));
		textureBackgroundStyle = new InspectedTextureBackgroundStyle(controller,
				fgeFactory.makeTexturedBackground(TextureType.TEXTURE1, FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.WHITE));
		noneBackgroundStyle = new InspectedNoneBackgroundStyle(controller, fgeFactory.makeEmptyBackground());
		backgroundImageBackgroundStyle = new InspectedBackgroundImageBackgroundStyle(controller,
				fgeFactory.makeImageBackground(FGEConstants.DEFAULT_IMAGE));
	}

	@Override
	public FGEModelFactory getFGEFactory() {
		return fgeFactory;
	}

	@Override
	public void setFGEFactory(FGEModelFactory fgeFactory) {
		this.fgeFactory = fgeFactory;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	public void delete() {
		getPropertyChangeSupport().firePropertyChange(DELETED, false, true);
		pcSupport = null;
	}

	@Override
	public String getDeletedProperty() {
		return DELETED;
	}

	@Override
	public AbstractInspectedBackgroundStyle<?> getCurrentStyle() {
		return getBackgroundStyle();
	}

	public AbstractInspectedBackgroundStyle<?> getBackgroundStyle() {
		if (backgroundStyleType != null) {
			switch (backgroundStyleType) {
				case NONE:
					return noneBackgroundStyle;
				case COLOR:
					return colorBackgroundStyle;
				case COLOR_GRADIENT:
					return colorGradientBackgroundStyle;
				case TEXTURE:
					return textureBackgroundStyle;
				case IMAGE:
					return backgroundImageBackgroundStyle;
				default:
					return null;
			}
		}
		else {
			return null;
		}
	}

	/**
	 * Equals method allowing null values
	 * 
	 * @param oldObject
	 * @param newObject
	 * @return
	 */
	protected boolean requireChange(Object oldObject, Object newObject) {
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

	@Override
	public BackgroundStyleType getStyleType() {
		return backgroundStyleType;
	}

	@Override
	public void setStyleType(BackgroundStyleType backgroundStyleType) {
		BackgroundStyleType oldBackgroundStyleType = getStyleType();

		if (oldBackgroundStyleType == backgroundStyleType) {
			return;
		}

		BackgroundStyle oldBS = getBackgroundStyle();

		this.backgroundStyleType = backgroundStyleType;
		pcSupport.firePropertyChange(STYLE_CLASS_CHANGED, oldBackgroundStyleType, getStyleType());
		pcSupport.firePropertyChange("backgroundStyle", oldBS, getBackgroundStyle());
		pcSupport.firePropertyChange("styleType", oldBackgroundStyleType, getStyleType());
	}

	@Override
	public String toString() {
		return "[BackgroundStyleFactory " + Integer.toHexString(hashCode()) + " backgroundStyleType=" + backgroundStyleType + "]";
	}

	@Override
	public BackgroundStyle makeNewStyle(BackgroundStyle oldStyle) {
		BackgroundStyle returned = null;
		switch (backgroundStyleType) {
			case NONE:
				returned = noneBackgroundStyle.cloneStyle();
				break;
			case COLOR:
				ColorBackgroundStyle returnedColor = colorBackgroundStyle.cloneStyle();
				if (oldStyle instanceof ColorGradientBackgroundStyle) {
					returnedColor.setColor(((ColorGradientBackgroundStyle) oldStyle).getColor1());
				}
				if (oldStyle instanceof TextureBackgroundStyle) {
					returnedColor.setColor(((TextureBackgroundStyle) oldStyle).getColor1());
				}
				returned = returnedColor;
				break;
			case COLOR_GRADIENT:
				ColorGradientBackgroundStyle returnedColorGradient = colorGradientBackgroundStyle.cloneStyle();
				if (oldStyle instanceof ColorBackgroundStyle) {
					returnedColorGradient.setColor1(((ColorBackgroundStyle) oldStyle).getColor());
				}
				if (oldStyle instanceof TextureBackgroundStyle) {
					returnedColorGradient.setColor1(((TextureBackgroundStyle) oldStyle).getColor1());
					returnedColorGradient.setColor2(((TextureBackgroundStyle) oldStyle).getColor2());
				}
				returned = returnedColorGradient;
				break;
			case TEXTURE:
				TextureBackgroundStyle returnedTexture = textureBackgroundStyle.cloneStyle();
				if (oldStyle instanceof ColorBackgroundStyle) {
					returnedTexture.setColor1(((ColorBackgroundStyle) oldStyle).getColor());
				}
				if (oldStyle instanceof ColorGradientBackgroundStyle) {
					returnedTexture.setColor1(((ColorGradientBackgroundStyle) oldStyle).getColor1());
					returnedTexture.setColor2(((ColorGradientBackgroundStyle) oldStyle).getColor2());
				}
				returned = returnedTexture;
				break;
			case IMAGE:
				returned = backgroundImageBackgroundStyle.cloneStyle();
				break;
			default:
				break;
		}

		if (oldStyle != null) {
			returned.setUseTransparency(oldStyle.getUseTransparency());
			returned.setTransparencyLevel(oldStyle.getTransparencyLevel());
		}

		return returned;

	}

	protected abstract class AbstractInspectedBackgroundStyle<BS extends BackgroundStyle> extends InspectedStyle<BS>
			implements BackgroundStyle {

		protected AbstractInspectedBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, BS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public float getTransparencyLevel() {
			return getPropertyValue(BackgroundStyle.TRANSPARENCY_LEVEL);
		}

		@Override
		public void setTransparencyLevel(float aLevel) {
			setPropertyValue(BackgroundStyle.TRANSPARENCY_LEVEL, aLevel);
		}

		@Override
		public boolean getUseTransparency() {
			return getPropertyValue(BackgroundStyle.USE_TRANSPARENCY);
		}

		@Override
		public void setUseTransparency(boolean aFlag) {
			setPropertyValue(BackgroundStyle.USE_TRANSPARENCY, aFlag);
		}

		@Override
		public List<DrawingTreeNode<?, ?>> getSelection() {
			return getController().getSelectedShapesAndGeometricObjects();
		}

	}

	protected class InspectedNoneBackgroundStyle extends AbstractInspectedBackgroundStyle<NoneBackgroundStyle>
			implements NoneBackgroundStyle {

		protected InspectedNoneBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, NoneBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.NONE;
		}

		@Override
		public NoneBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof NoneBackgroundStyle) {
					return (NoneBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			if (node instanceof GeometricNode) {
				if (((GeometricNode<?>) node).getBackgroundStyle() instanceof NoneBackgroundStyle) {
					return (NoneBackgroundStyle) ((GeometricNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}
	}

	protected class InspectedColorBackgroundStyle extends AbstractInspectedBackgroundStyle<ColorBackgroundStyle>
			implements ColorBackgroundStyle {

		protected InspectedColorBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, ColorBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.COLOR;
		}

		@Override
		public Color getColor() {
			return getPropertyValue(ColorBackgroundStyle.COLOR);
		}

		@Override
		public void setColor(Color aColor) {
			setPropertyValue(ColorBackgroundStyle.COLOR, aColor);
		}

		@Override
		public ColorBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof ColorBackgroundStyle) {
					return (ColorBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			if (node instanceof GeometricNode) {
				if (((GeometricNode<?>) node).getBackgroundStyle() instanceof ColorBackgroundStyle) {
					return (ColorBackgroundStyle) ((GeometricNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		/*@Override
		public String toString() {
			return "InspectedColorBackgroundStyle.COLOR(" + getColor() + ") size=" + getSelection().size() + " default=" + getDefaultValue()
					+ " color=" + getDefaultValue().getColor();
		}*/

	}

	protected class InspectedColorGradientBackgroundStyle extends AbstractInspectedBackgroundStyle<ColorGradientBackgroundStyle>
			implements ColorGradientBackgroundStyle {

		protected InspectedColorGradientBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller,
				ColorGradientBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.COLOR_GRADIENT;
		}

		@Override
		public ColorGradientBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof ColorGradientBackgroundStyle) {
					return (ColorGradientBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			if (node instanceof GeometricNode) {
				if (((GeometricNode<?>) node).getBackgroundStyle() instanceof ColorGradientBackgroundStyle) {
					return (ColorGradientBackgroundStyle) ((GeometricNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		@Override
		public Color getColor1() {
			return getPropertyValue(ColorGradientBackgroundStyle.COLOR1);
		}

		@Override
		public void setColor1(Color aColor) {
			setPropertyValue(ColorGradientBackgroundStyle.COLOR1, aColor);
		}

		@Override
		public Color getColor2() {
			return getPropertyValue(ColorGradientBackgroundStyle.COLOR2);
		}

		@Override
		public void setColor2(Color aColor) {
			setPropertyValue(ColorGradientBackgroundStyle.COLOR2, aColor);
		}

		@Override
		public ColorGradientDirection getDirection() {
			return getPropertyValue(ColorGradientBackgroundStyle.DIRECTION);
		}

		@Override
		public void setDirection(ColorGradientDirection aDirection) {
			setPropertyValue(ColorGradientBackgroundStyle.DIRECTION, aDirection);
		}
	}

	protected class InspectedTextureBackgroundStyle extends AbstractInspectedBackgroundStyle<TextureBackgroundStyle>
			implements TextureBackgroundStyle {

		protected InspectedTextureBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller, TextureBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.TEXTURE;
		}

		@Override
		public TextureBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof TextureBackgroundStyle) {
					return (TextureBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			if (node instanceof GeometricNode) {
				if (((GeometricNode<?>) node).getBackgroundStyle() instanceof TextureBackgroundStyle) {
					return (TextureBackgroundStyle) ((GeometricNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		@Override
		public TextureType getTextureType() {
			return getPropertyValue(TextureBackgroundStyle.TEXTURE_TYPE);
		}

		@Override
		public void setTextureType(TextureType aTextureType) {
			setPropertyValue(TextureBackgroundStyle.TEXTURE_TYPE, aTextureType);
		}

		@Override
		public Color getColor1() {
			return getPropertyValue(TextureBackgroundStyle.COLOR1);
		}

		@Override
		public void setColor1(Color aColor) {
			setPropertyValue(TextureBackgroundStyle.COLOR1, aColor);
		}

		@Override
		public Color getColor2() {
			return getPropertyValue(TextureBackgroundStyle.COLOR2);
		}

		@Override
		public void setColor2(Color aColor) {
			setPropertyValue(TextureBackgroundStyle.COLOR2, aColor);
		}
	}

	protected class InspectedBackgroundImageBackgroundStyle extends AbstractInspectedBackgroundStyle<BackgroundImageBackgroundStyle>
			implements BackgroundImageBackgroundStyle {

		protected InspectedBackgroundImageBackgroundStyle(DianaInteractiveViewer<?, ?, ?> controller,
				BackgroundImageBackgroundStyle defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public BackgroundStyleType getBackgroundStyleType() {
			return BackgroundStyleType.IMAGE;
		}

		@Override
		public BackgroundImageBackgroundStyle getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ShapeNode) {
				if (((ShapeNode<?>) node).getBackgroundStyle() instanceof BackgroundImageBackgroundStyle) {
					return (BackgroundImageBackgroundStyle) ((ShapeNode<?>) node).getBackgroundStyle();
				}
			}
			if (node instanceof GeometricNode) {
				if (((GeometricNode<?>) node).getBackgroundStyle() instanceof BackgroundImageBackgroundStyle) {
					return (BackgroundImageBackgroundStyle) ((GeometricNode<?>) node).getBackgroundStyle();
				}
			}
			return null;
		}

		@Override
		public Resource getImageResource() {
			return getPropertyValue(BackgroundImageBackgroundStyle.IMAGE_RESOURCE);
		}

		@Override
		public void setImageResource(Resource anImageFile) {
			setPropertyValue(BackgroundImageBackgroundStyle.IMAGE_RESOURCE, anImageFile);
		}

		@Override
		public Color getImageBackgroundColor() {
			return getPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_COLOR);
		}

		@Override
		public void setImageBackgroundColor(Color aColor) {
			setPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_COLOR, aColor);
		}

		@Override
		public double getDeltaX() {
			return getPropertyValue(BackgroundImageBackgroundStyle.DELTA_X);
		}

		@Override
		public void setDeltaX(double aDeltaX) {
			setPropertyValue(BackgroundImageBackgroundStyle.DELTA_X, aDeltaX);
		}

		@Override
		public double getDeltaY() {
			return getPropertyValue(BackgroundImageBackgroundStyle.DELTA_Y);
		}

		@Override
		public void setDeltaY(double aDeltaY) {
			setPropertyValue(BackgroundImageBackgroundStyle.DELTA_Y, aDeltaY);
		}

		@Override
		public ImageBackgroundType getImageBackgroundType() {
			return getPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_TYPE);
		}

		@Override
		public void setImageBackgroundType(ImageBackgroundType anImageBackgroundType) {
			setPropertyValue(BackgroundImageBackgroundStyle.IMAGE_BACKGROUND_TYPE, anImageBackgroundType);
		}

		@Override
		public double getScaleX() {
			return getPropertyValue(BackgroundImageBackgroundStyle.SCALE_X);
		}

		@Override
		public void setScaleX(double aScaleX) {
			setPropertyValue(BackgroundImageBackgroundStyle.SCALE_X, aScaleX);
		}

		@Override
		public double getScaleY() {
			return getPropertyValue(BackgroundImageBackgroundStyle.SCALE_Y);
		}

		@Override
		public void setScaleY(double aScaleY) {
			setPropertyValue(BackgroundImageBackgroundStyle.SCALE_Y, aScaleY);
		}

		@Override
		public boolean getFitToShape() {
			return getPropertyValue(BackgroundImageBackgroundStyle.FIT_TO_SHAPE);
		}

		@Override
		public void setFitToShape(boolean aFlag) {
			setPropertyValue(BackgroundImageBackgroundStyle.FIT_TO_SHAPE, aFlag);
		}

		@Override
		public Image getImage() {
			return null;
		}

		@Override
		public void setImage(Image image) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setScaleXNoNotification(double aScaleX) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setScaleYNoNotification(double aScaleY) {
			// TODO Auto-generated method stub

		}

		@Override
		public File getImageFile() {
			if (getImageResource() != null && getImageResource().getLocator() != null) {
				return getImageResource().getLocator().retrieveResourceAsFile(getImageResource());
			}
			return null;
		}

		@Override
		public void setImageFile(File file) {
			setImageResource(ResourceLocator.locateResource(file.getPath()));
		}
	}

}

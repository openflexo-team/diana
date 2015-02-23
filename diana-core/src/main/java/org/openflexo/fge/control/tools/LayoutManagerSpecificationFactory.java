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
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.connie.BindingFactory;
import org.openflexo.connie.BindingModel;
import org.openflexo.connie.DataBinding;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle;
import org.openflexo.fge.ColorGradientBackgroundStyle.ColorGradientDirection;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.FGELayoutManager;
import org.openflexo.fge.FGELayoutManagerSpecification;
import org.openflexo.fge.FGELayoutManagerSpecification.LayoutManagerSpecificationType;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.TextureBackgroundStyle;
import org.openflexo.fge.TextureBackgroundStyle.TextureType;
import org.openflexo.fge.control.DianaInteractiveViewer;
import org.openflexo.fge.control.tools.BackgroundStyleFactory.AbstractInspectedBackgroundStyle;
import org.openflexo.fge.control.tools.BackgroundStyleFactory.InspectedBackgroundImageBackgroundStyle;
import org.openflexo.fge.control.tools.BackgroundStyleFactory.InspectedColorBackgroundStyle;
import org.openflexo.fge.control.tools.BackgroundStyleFactory.InspectedColorGradientBackgroundStyle;
import org.openflexo.fge.control.tools.BackgroundStyleFactory.InspectedNoneBackgroundStyle;
import org.openflexo.fge.control.tools.BackgroundStyleFactory.InspectedTextureBackgroundStyle;
import org.openflexo.fge.layout.GridLayoutManagerSpecification;

/**
 * Convenient class used to manipulate FGELayoutManagerSpecification instances over FGELayoutManagerSpecification class hierarchy
 * 
 * @author sylvain
 * 
 */
public class LayoutManagerSpecificationFactory implements StyleFactory<FGELayoutManagerSpecification<?>, LayoutManagerSpecificationType> {

	private static final Logger logger = Logger.getLogger(LayoutManagerSpecificationFactory.class.getPackage().getName());

	private static final String DELETED = "deleted";
	// private AbstractInspectedBackgroundStyle<?> backgroundStyle;

	private LayoutManagerSpecificationType backgroundStyleType = LayoutManagerSpecificationType.NONE;

	private InspectedNoneBackgroundStyle noneBackgroundStyle;
	private final InspectedColorBackgroundStyle colorBackgroundStyle;
	private final InspectedColorGradientBackgroundStyle colorGradientBackgroundStyle;
	private final InspectedTextureBackgroundStyle textureBackgroundStyle;
	private final InspectedBackgroundImageBackgroundStyle backgroundImageBackgroundStyle;

	private PropertyChangeSupport pcSupport;
	private FGEModelFactory fgeFactory;

	private final DianaInteractiveViewer<?, ?, ?> controller;

	public LayoutManagerSpecificationFactory(DianaInteractiveViewer<?, ?, ?> controller) {
		pcSupport = new PropertyChangeSupport(this);
		this.controller = controller;
		fgeFactory = controller.getFactory();
		noneBackgroundStyle = new InspectedNoneBackgroundStyle(controller, controller.getFactory().makeEmptyBackground());
		colorBackgroundStyle = new InspectedColorBackgroundStyle(controller, controller.getFactory().makeColoredBackground(
				FGEConstants.DEFAULT_BACKGROUND_COLOR));
		colorGradientBackgroundStyle = new InspectedColorGradientBackgroundStyle(controller, controller.getFactory()
				.makeColorGradientBackground(FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.WHITE,
						ColorGradientDirection.SOUTH_EAST_NORTH_WEST));
		textureBackgroundStyle = new InspectedTextureBackgroundStyle(controller, controller.getFactory().makeTexturedBackground(
				TextureType.TEXTURE1, FGEConstants.DEFAULT_BACKGROUND_COLOR, Color.WHITE));
		noneBackgroundStyle = new InspectedNoneBackgroundStyle(controller, controller.getFactory().makeEmptyBackground());
		backgroundImageBackgroundStyle = new InspectedBackgroundImageBackgroundStyle(controller, controller.getFactory()
				.makeImageBackground(FGEConstants.DEFAULT_IMAGE));
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
		} else {
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
			} else {
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

	protected abstract class AbstractInspectedLayoutManagerSpecification<LMS extends FGELayoutManagerSpecification<?>> extends
			InspectedStyle<LMS> implements FGELayoutManagerSpecification<FGELayoutManager<LMS, ?>> {

		protected AbstractInspectedLayoutManagerSpecification(DianaInteractiveViewer<?, ?, ?> controller, LMS defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public String getIdentifier() {
			return getPropertyValue(FGELayoutManagerSpecification.IDENTIFIER);
		}

		@Override
		public void setIdentifier(String identifier) {
			setPropertyValue(FGELayoutManagerSpecification.IDENTIFIER, identifier);
		}

		@Override
		public List<ContainerNode<?, ?>> getSelection() {
			return getController().getSelectedContainers();
		}

		@Override
		public BindingModel getBindingModel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BindingFactory getBindingFactory() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void notifiedBindingChanged(DataBinding<?> dataBinding) {
			// TODO Auto-generated method stub

		}

		@Override
		public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
			// TODO Auto-generated method stub

		}

		@Override
		public Boolean paintDecoration() {
			return getPropertyValue(FGELayoutManagerSpecification.PAINT_DECORATION);
		}

		@Override
		public void setPaintDecoration(Boolean paintDecoration) {
			setPropertyValue(FGELayoutManagerSpecification.PAINT_DECORATION, paintDecoration);
		}

		@Override
		public DraggingMode getDraggingMode() {
			return getPropertyValue(FGELayoutManagerSpecification.DRAGGING_MODE);
		}

		@Override
		public void setDraggingMode(DraggingMode draggingMode) {
			setPropertyValue(FGELayoutManagerSpecification.DRAGGING_MODE, draggingMode);
		}

		@Override
		public boolean animateLayout() {
			return getPropertyValue(FGELayoutManagerSpecification.ANIMATE_LAYOUT);
		}

		@Override
		public void setAnimateLayout(boolean animateLayout) {
			setPropertyValue(FGELayoutManagerSpecification.ANIMATE_LAYOUT, animateLayout);
		}

		@Override
		public int getAnimationStepsNumber() {
			return getPropertyValue(FGELayoutManagerSpecification.ANIMATION_STEPS_NUMBER);
		}

		@Override
		public void setAnimationStepsNumber(int stepsNumber) {
			setPropertyValue(FGELayoutManagerSpecification.ANIMATION_STEPS_NUMBER, stepsNumber);
		}

		@Override
		public FGELayoutManager<LMS, ?> makeLayoutManager(ContainerNode<?, ?> containerNode) {
			FGELayoutManager<LMS, ?> layoutManager = getFactory().newInstance(getLayoutManagerClass());
			((FGELayoutManager) layoutManager).setLayoutManagerSpecification(this);
			layoutManager.setContainerNode((ContainerNode) containerNode);
			getPropertyChangeSupport().addPropertyChangeListener(layoutManager);
			System.out.println("Created LayoutManager " + getIdentifier() + " : " + layoutManager);
			return layoutManager;
		}

		@Override
		public LMS getStyle(DrawingTreeNode<?, ?> node) {
			if (node instanceof ContainerNode && ((ContainerNode<?, ?>) node).getLayoutManagerSpecifications().size() > 0) {
				return (LMS) ((ContainerNode<?, ?>) node).getLayoutManagerSpecifications().get(0);
			}
			return null;
		}
	}

	protected class InspectedNoLayout extends AbstractInspectedLayoutManagerSpecification<FGELayoutManagerSpecification<?>> {

		protected InspectedNoLayout(DianaInteractiveViewer<?, ?, ?> controller, FGELayoutManagerSpecification<?> defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public LayoutManagerSpecificationType getLayoutManagerSpecificationType() {
			return LayoutManagerSpecificationType.NONE;
		}

		@Override
		public FGELayoutManagerSpecification<?> getStyle(DrawingTreeNode<?, ?> node) {
			return null;
		}

		@Override
		public Class<? extends FGELayoutManager<FGELayoutManagerSpecification<?>, ?>> getLayoutManagerClass() {
			return null;
		}

		@Override
		public boolean supportAutolayout() {
			return false;
		}

		@Override
		public boolean supportDecoration() {
			return false;
		}
	}

	protected class InspectedGridLayout extends AbstractInspectedLayoutManagerSpecification<GridLayoutManagerSpecification> implements
			GridLayoutManagerSpecification {

		protected InspectedGridLayout(DianaInteractiveViewer<?, ?, ?> controller, GridLayoutManagerSpecification defaultValue) {
			super(controller, defaultValue);
		}

		@Override
		public LayoutManagerSpecificationType getLayoutManagerSpecificationType() {
			return LayoutManagerSpecificationType.GRID;
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
			return null;
		}
	}

}

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

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.util.logging.Logger;

import org.openflexo.connie.DataBinding;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.BackgroundStyle.BackgroundStyleType;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.FGEConstants;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.control.MouseControl.MouseButton;
import org.openflexo.fge.control.PredefinedMouseClickControlActionType;
import org.openflexo.fge.control.PredefinedMouseDragControlActionType;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.notifications.ShapeChanged;
import org.openflexo.fge.notifications.ShapeNeedsToBeRedrawn;
import org.openflexo.fge.shapes.ShapeSpecification;
import org.openflexo.fge.shapes.ShapeSpecification.ShapeType;
import org.openflexo.model.factory.ProxyMethodHandler;
import org.openflexo.toolbox.ToolBox;

public abstract class ShapeGraphicalRepresentationImpl extends ContainerGraphicalRepresentationImpl
		implements ShapeGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ShapeGraphicalRepresentation.class.getPackage().getName());

	private double x = 0;
	private double y = 0;

	private LocationConstraints locationConstraints = LocationConstraints.FREELY_MOVABLE;

	private FGEArea locationConstrainedArea = null;

	private ForegroundStyle foreground;
	private BackgroundStyle background;

	private ForegroundStyle selectedForeground = null;
	private BackgroundStyle selectedBackground = null;
	private ForegroundStyle focusedForeground = null;
	private BackgroundStyle focusedBackground = null;

	private boolean hasSelectedForeground = false;
	private boolean hasSelectedBackground = false;
	private boolean hasFocusedForeground = false;
	private boolean hasFocusedBackground = false;

	// private ShapeBorder border;

	private ShapeSpecification shape = null;

	private ShadowStyle shadowStyle;

	private boolean allowToLeaveBounds = true;
	private boolean adaptBoundsToContents = false;

	private boolean isFloatingLabel = true;
	private double relativeTextX = 0.5;
	private double relativeTextY = 0.5;

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ShapeGraphicalRepresentationImpl() {
		super();
	}

	@Deprecated
	private ShapeGraphicalRepresentationImpl(Object aDrawable, Drawing<?> aDrawing) {
		this();
	}

	@Deprecated
	public ShapeGraphicalRepresentationImpl(ShapeType shapeType, Object aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);
		setShapeType(shapeType);
		layer = FGEConstants.DEFAULT_SHAPE_LAYER;
		foreground = getFactory().makeDefaultForegroundStyle();
		foreground.getPropertyChangeSupport().addPropertyChangeListener(this);
		background = getFactory().makeColoredBackground(Color.WHITE);
		background.getPropertyChangeSupport().addPropertyChangeListener(this);
		shadowStyle = getFactory().makeDefaultShadowStyle();
		shadowStyle.getPropertyChangeSupport().addPropertyChangeListener(this);

		init();
	}

	@SuppressWarnings("unused")
	@Deprecated
	private ShapeGraphicalRepresentationImpl(ShapeGraphicalRepresentation aGR, Object aDrawable, Drawing<?> aDrawing) {
		this(aDrawable, aDrawing);

		setsWith(aGR);
		init();
	}

	@Deprecated
	private void init() {

		addToMouseClickControls(
				getFactory().makeMouseClickControl("Selection", MouseButton.LEFT, 1, PredefinedMouseClickControlActionType.SELECTION));
		if (ToolBox.getPLATFORM() == ToolBox.MACOS) {
			addToMouseClickControls(getFactory().makeMouseMetaClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
		else {
			addToMouseClickControls(getFactory().makeMouseControlClickControl("Multiple selection", MouseButton.LEFT, 1,
					PredefinedMouseClickControlActionType.MULTIPLE_SELECTION));
		}
		addToMouseDragControls(getFactory().makeMouseDragControl("Move", MouseButton.LEFT, PredefinedMouseDragControlActionType.MOVE));
		addToMouseDragControls(getFactory().makeMouseDragControl("Zoom", MouseButton.RIGHT, PredefinedMouseDragControlActionType.ZOOM));
		addToMouseDragControls(getFactory().makeMouseShiftDragControl("Rectangle selection", MouseButton.LEFT,
				PredefinedMouseDragControlActionType.RECTANGLE_SELECTING));

	}

	// ***************************************************************************
	// * Deletion *
	// ***************************************************************************

	@Override
	public boolean delete(Object... context) {
		if (background != null && background.getPropertyChangeSupport() != null) {
			background.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (foreground != null && foreground.getPropertyChangeSupport() != null) {
			foreground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (selectedBackground != null && selectedBackground.getPropertyChangeSupport() != null) {
			selectedBackground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (selectedForeground != null && selectedForeground.getPropertyChangeSupport() != null) {
			selectedForeground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (focusedBackground != null && focusedBackground.getPropertyChangeSupport() != null) {
			focusedBackground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (focusedForeground != null && focusedForeground.getPropertyChangeSupport() != null) {
			focusedForeground.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		if (shadowStyle != null && shadowStyle.getPropertyChangeSupport() != null) {
			shadowStyle.getPropertyChangeSupport().removePropertyChangeListener(this);
		}
		return super.delete(context);
	}
	// *******************************************************************************
	// * Observer implementation *
	// *******************************************************************************

	// This might be very dangerous to override this
	// SGU: Developer really need to think about what he's doing while overriding this
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);

		if (evt.getPropertyName().equals(ShapeChanged.EVENT_NAME)) {
			// Reforward notification
			notifyShapeChanged();
		}

		if (evt.getPropertyName().equals(ShapeNeedsToBeRedrawn.EVENT_NAME)) {
			// Reforward notification
			notifyShapeNeedsToBeRedrawn();
		}

		if (evt.getSource() instanceof BackgroundStyle) {

			if (evt.getPropertyName().equals(ProxyMethodHandler.SERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.DESERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.MODIFIED)) {
				return;
			}

			forward(evt);
		}
		if (evt.getSource() instanceof ForegroundStyle) {

			if (evt.getPropertyName().equals(ProxyMethodHandler.SERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.DESERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.MODIFIED)) {
				return;
			}

			forward(evt);
		}
		if (evt.getSource() instanceof ShadowStyle) {

			if (evt.getPropertyName().equals(ProxyMethodHandler.SERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.DESERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.MODIFIED)) {
				return;
			}

			forward(evt);
		}
		if (evt.getSource() instanceof ShapeSpecification) {

			if (evt.getPropertyName().equals(ProxyMethodHandler.SERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.DESERIALIZING)
					|| evt.getPropertyName().equals(ProxyMethodHandler.MODIFIED)) {
				return;
			}

			forward(evt);
		}
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public void setX(double aValue) {
		FGEAttributeNotification<?> notification = requireChange(X, aValue);
		if (notification != null) {
			x = aValue;
			hasChanged(notification);
		}
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public void setY(double aValue) {
		FGEAttributeNotification<?> notification = requireChange(Y, aValue);
		if (notification != null) {
			y = aValue;
			hasChanged(notification);
		}
	}

	@Override
	public FGEPoint getLocation() {
		return new FGEPoint(getX(), getY());
	}

	@Override
	public LocationConstraints getLocationConstraints() {
		return locationConstraints;
	}

	@Override
	public void setLocationConstraints(LocationConstraints locationConstraints) {
		FGEAttributeNotification<?> notification = requireChange(LOCATION_CONSTRAINTS, locationConstraints);
		if (notification != null) {
			this.locationConstraints = locationConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public FGEArea getLocationConstrainedArea() {
		return locationConstrainedArea;
	}

	@Override
	public void setLocationConstrainedArea(FGEArea locationConstrainedArea) {
		FGEAttributeNotification<?> notification = requireChange(LOCATION_CONSTRAINED_AREA, locationConstrainedArea);
		if (notification != null) {
			this.locationConstrainedArea = locationConstrainedArea;
			hasChanged(notification);
		}
	}

	@Override
	public final boolean getAllowToLeaveBounds() {
		return allowToLeaveBounds;
	}

	@Override
	public void setAllowToLeaveBounds(boolean allowToLeaveBounds) {
		FGEAttributeNotification<?> notification = requireChange(ALLOW_TO_LEAVE_BOUNDS, allowToLeaveBounds);
		if (notification != null) {
			this.allowToLeaveBounds = allowToLeaveBounds;
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdaptBoundsToContents() {
		return adaptBoundsToContents;
	}

	@Override
	public void setAdaptBoundsToContents(boolean adaptBoundsToContents) {
		FGEAttributeNotification<?> notification = requireChange(ADAPT_BOUNDS_TO_CONTENTS, adaptBoundsToContents);
		if (notification != null) {
			this.adaptBoundsToContents = adaptBoundsToContents;
			hasChanged(notification);
			/*if (adaptBoundsToContents) {
				extendBoundsToHostContents();
			}*/
		}
	}

	// *******************************************************************************
	// * Geometry constraints *
	// *******************************************************************************

	private DataBinding<Double> xConstraints;
	private DataBinding<Double> yConstraints;
	private DataBinding<Double> widthConstraints;
	private DataBinding<Double> heightConstraints;

	@Override
	public DataBinding<Double> getXConstraints() {
		if (xConstraints == null) {
			xConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
			xConstraints.setBindingName(X_CONSTRAINTS_KEY);
		}
		return xConstraints;
	}

	@Override
	public void setXConstraints(DataBinding<Double> xConstraints) {
		FGEAttributeNotification<?> notification = requireChange(X_CONSTRAINTS, xConstraints);
		if (notification != null) {
			if (xConstraints != null) {
				xConstraints.setOwner(this);
				xConstraints.setDeclaredType(Double.class);
				xConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.xConstraints = xConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DataBinding<Double> getYConstraints() {
		if (yConstraints == null) {
			yConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
			yConstraints.setBindingName(Y_CONSTRAINTS_KEY);
		}
		return yConstraints;
	}

	@Override
	public void setYConstraints(DataBinding<Double> yConstraints) {
		FGEAttributeNotification<?> notification = requireChange(Y_CONSTRAINTS, yConstraints);
		if (notification != null) {
			if (yConstraints != null) {
				yConstraints.setOwner(this);
				yConstraints.setDeclaredType(Double.class);
				yConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.yConstraints = yConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DataBinding<Double> getWidthConstraints() {
		if (widthConstraints == null) {
			widthConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
			widthConstraints.setBindingName(WIDTH_CONSTRAINTS_KEY);
		}
		return widthConstraints;
	}

	@Override
	public void setWidthConstraints(DataBinding<Double> widthConstraints) {
		FGEAttributeNotification<?> notification = requireChange(WIDTH_CONSTRAINTS, widthConstraints);
		if (notification != null) {
			if (widthConstraints != null) {
				widthConstraints.setOwner(this);
				widthConstraints.setDeclaredType(Double.class);
				widthConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.widthConstraints = widthConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DataBinding<Double> getHeightConstraints() {
		if (heightConstraints == null) {
			heightConstraints = new DataBinding<Double>(this, Double.class, DataBinding.BindingDefinitionType.GET);
			heightConstraints.setBindingName(HEIGHT_CONSTRAINTS_KEY);
		}
		return heightConstraints;
	}

	@Override
	public void setHeightConstraints(DataBinding<Double> heightConstraints) {
		FGEAttributeNotification<?> notification = requireChange(HEIGHT_CONSTRAINTS, heightConstraints);
		if (notification != null) {
			if (heightConstraints != null) {
				heightConstraints.setOwner(this);
				heightConstraints.setDeclaredType(Double.class);
				heightConstraints.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
			}
			this.heightConstraints = heightConstraints;
			hasChanged(notification);
		}
	}

	// *******************************************************************************
	// * Accessors *
	// *******************************************************************************

	@Override
	public ForegroundStyle getForeground() {
		return foreground;
	}

	@Override
	public void setForeground(ForegroundStyle aForeground) {
		FGEAttributeNotification<ForegroundStyle> notification = requireChange(FOREGROUND, aForeground, false);
		if (notification != null) {
			if (foreground != null && foreground.getPropertyChangeSupport() != null) {
				foreground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			foreground = aForeground;
			if (aForeground != null && foreground.getPropertyChangeSupport() != null) {
				aForeground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public ForegroundStyle getSelectedForeground() {
		if (selectedForeground == null && foreground != null) {
			selectedForeground = (ForegroundStyle) foreground.clone();
		}
		return selectedForeground;
	}

	@Override
	public void setSelectedForeground(ForegroundStyle aForeground) {
		FGEAttributeNotification<ForegroundStyle> notification = requireChange(SELECTED_FOREGROUND, aForeground, false);
		if (notification != null) {
			if (selectedForeground != null && selectedForeground.getPropertyChangeSupport() != null) {
				selectedForeground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			selectedForeground = aForeground;
			if (aForeground != null && aForeground.getPropertyChangeSupport() != null) {
				aForeground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasSelectedForeground() {
		return hasSelectedForeground;
	}

	@Override
	public void setHasSelectedForeground(boolean aFlag) {
		hasSelectedForeground = aFlag;
	}

	@Override
	public ForegroundStyle getFocusedForeground() {
		if (focusedForeground == null && foreground != null) {
			focusedForeground = (ForegroundStyle) foreground.clone();
		}
		return focusedForeground;
	}

	@Override
	public void setFocusedForeground(ForegroundStyle aForeground) {
		FGEAttributeNotification<ForegroundStyle> notification = requireChange(FOCUSED_FOREGROUND, aForeground, false);
		if (notification != null) {
			if (focusedForeground != null && focusedForeground.getPropertyChangeSupport() != null) {
				focusedForeground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			focusedForeground = aForeground;
			if (aForeground != null && aForeground.getPropertyChangeSupport() != null) {
				aForeground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasFocusedForeground() {
		return hasFocusedForeground;
	}

	@Override
	public void setHasFocusedForeground(boolean aFlag) {
		hasFocusedForeground = aFlag;
	}

	@Override
	public boolean getNoStroke() {
		return foreground.getNoStroke();
	}

	@Override
	public void setNoStroke(boolean noStroke) {
		foreground.setNoStroke(noStroke);
	}

	@Override
	public BackgroundStyle getBackground() {
		return background;
	}

	@Override
	public void setBackground(BackgroundStyle aBackground) {
		FGEAttributeNotification<BackgroundStyle> notification = requireChange(BACKGROUND, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (background != null && background.getPropertyChangeSupport() != null) {
				background.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			background = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null && aBackground.getPropertyChangeSupport() != null) {
				aBackground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
			if (getPropertyChangeSupport() != null) {
				getPropertyChangeSupport().firePropertyChange(BACKGROUND_STYLE_TYPE_KEY,
						notification.oldValue() != null ? notification.oldValue().getBackgroundStyleType() : null,
						notification.newValue() != null ? notification.newValue().getBackgroundStyleType() : null);
			}

		}
	}

	@Override
	public BackgroundStyleType getBackgroundType() {
		return background.getBackgroundStyleType();
	}

	@Override
	public void setBackgroundType(BackgroundStyleType backgroundType) {
		if (backgroundType != getBackgroundType()) {
			setBackground(getFactory().makeBackground(backgroundType));
		}
	}

	@Override
	public BackgroundStyle getSelectedBackground() {
		if (selectedBackground == null && background != null) {
			selectedBackground = (BackgroundStyle) background.clone();
		}
		return selectedBackground;
	}

	@Override
	public void setSelectedBackground(BackgroundStyle aBackground) {
		FGEAttributeNotification<BackgroundStyle> notification = requireChange(SELECTED_BACKGROUND, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (selectedBackground != null && selectedBackground.getPropertyChangeSupport() != null) {
				selectedBackground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			selectedBackground = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null && aBackground.getPropertyChangeSupport() != null) {
				aBackground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasSelectedBackground() {
		return hasSelectedBackground;
	}

	@Override
	public void setHasSelectedBackground(boolean aFlag) {
		hasSelectedBackground = aFlag;
	}

	@Override
	public BackgroundStyle getFocusedBackground() {
		if (focusedBackground == null && background != null) {
			focusedBackground = (BackgroundStyle) background.clone();
		}
		return focusedBackground;
	}

	@Override
	public void setFocusedBackground(BackgroundStyle aBackground) {
		FGEAttributeNotification<BackgroundStyle> notification = requireChange(FOCUSED_BACKGROUND, aBackground, false);
		if (notification != null) {
			// background = aBackground.clone();
			if (focusedBackground != null && focusedBackground.getPropertyChangeSupport() != null) {
				focusedBackground.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			focusedBackground = aBackground;
			// background.setGraphicalRepresentation(this);
			if (aBackground != null && aBackground.getPropertyChangeSupport() != null) {
				aBackground.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public boolean getHasFocusedBackground() {
		return hasFocusedBackground;
	}

	@Override
	public void setHasFocusedBackground(boolean aFlag) {
		hasFocusedBackground = aFlag;
	}

	@Override
	public ShadowStyle getShadowStyle() {
		return shadowStyle;
	}

	@Override
	public void setShadowStyle(ShadowStyle aShadowStyle) {
		FGEAttributeNotification<ShadowStyle> notification = requireChange(SHADOW_STYLE, aShadowStyle);
		if (notification != null) {
			if (shadowStyle != null && shadowStyle.getPropertyChangeSupport() != null) {
				shadowStyle.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			this.shadowStyle = aShadowStyle;
			if (aShadowStyle != null && aShadowStyle.getPropertyChangeSupport() != null) {
				aShadowStyle.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			hasChanged(notification);
		}
	}

	@Override
	public ShapeSpecification getShapeSpecification() {
		return shape;
	}

	@Override
	public void setShapeSpecification(ShapeSpecification aShape) {
		// logger.info("================ setShapeSpecification() " + aShape);
		if (shape != aShape) {
			if (shape != null && shape.getPropertyChangeSupport() != null) {
				shape.getPropertyChangeSupport().removePropertyChangeListener(this);
			}
			if (aShape != null && aShape.getPropertyChangeSupport() != null) {
				aShape.getPropertyChangeSupport().addPropertyChangeListener(this);
			}
			FGEAttributeNotification<?> notification = requireChange(SHAPE, aShape);
			if (notification != null) {
				ShapeType oldType = shape != null ? shape.getShapeType() : null;
				this.shape = aShape;
				// shape.rebuildControlPoints();
				hasChanged(notification);
				notifyObservers(
						new FGEAttributeNotification<ShapeType>(SHAPE_TYPE, oldType, (aShape != null ? aShape.getShapeType() : null)));
				// notifyShapeChanged();
			}
		}
	}

	@Override
	public ShapeType getShapeType() {
		if (shape != null) {
			return shape.getShapeType();
		}
		return null;
	}

	@Override
	public void setShapeType(ShapeType shapeType) {
		if (getShapeType() != shapeType) {
			setShapeSpecification(getFactory().makeShape(shapeType/*, this*/));
			if (getShapeSpecification().areDimensionConstrained()) {
				double newSize = Math.max(getWidth(), getHeight());
				setWidth(newSize);
				setHeight(newSize);
			}
		}
	}

	public void notifyShapeChanged() {
		notifyObservers(new ShapeChanged());
	}

	public void notifyShapeNeedsToBeRedrawn() {
		notifyObservers(new ShapeNeedsToBeRedrawn());
	}

	@Override
	public boolean getIsFloatingLabel() {
		return isFloatingLabel;
	}

	@Override
	public void setIsFloatingLabel(boolean isFloatingLabel) {
		FGEAttributeNotification<?> notification = requireChange(IS_FLOATING_LABEL, isFloatingLabel);
		if (notification != null) {
			this.isFloatingLabel = isFloatingLabel;
			hasChanged(notification);
		}
	}

	@Override
	public double getRelativeTextX() {
		return relativeTextX;
	}

	@Override
	public void setRelativeTextX(double textX) {
		FGEAttributeNotification<?> notification = requireChange(RELATIVE_TEXT_X, textX);
		if (notification != null) {
			this.relativeTextX = textX;
			hasChanged(notification);
		}
	}

	@Override
	public double getRelativeTextY() {
		return relativeTextY;
	}

	@Override
	public void setRelativeTextY(double textY) {
		FGEAttributeNotification<?> notification = requireChange(RELATIVE_TEXT_Y, textY);
		if (notification != null) {
			this.relativeTextY = textY;
			hasChanged(notification);
		}
	}

	@Override
	public void setHorizontalTextAlignment(org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment horizontalTextAlignment) {
		super.setHorizontalTextAlignment(horizontalTextAlignment);
		// checkAndUpdateDimensionBoundsIfRequired();
	}

	@Override
	public void setVerticalTextAlignment(org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment verticalTextAlignment) {
		super.setVerticalTextAlignment(verticalTextAlignment);
		// checkAndUpdateDimensionBoundsIfRequired();
	}

	/**
	 * Override this if you want to use such a feature
	 *
	 * @return
	 */
	@Override
	public boolean isAllowedToBeDraggedOutsideParentContainer() {
		return false;
	}

	/**
	 * Override this if you want to use this feature Default implementation return always false
	 *
	 * @return
	 */
	@Override
	public boolean isAllowedToBeDraggedOutsideParentContainerInsideContainer(ContainerNode<?, ?> container) {
		return false;
	}

	/**
	 * Override this if you want to use this feature Default implementation does nothing return boolean indicating if drag was successfully
	 * performed
	 */
	@Override
	public boolean dragOutsideParentContainerInsideContainer(ContainerNode<?, ?> container, FGEPoint location) {
		return false;
	}

}

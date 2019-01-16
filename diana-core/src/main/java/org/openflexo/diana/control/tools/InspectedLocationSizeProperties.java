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

package org.openflexo.diana.control.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openflexo.connie.DataBinding;
import org.openflexo.diana.BackgroundStyle;
import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.ContainerGraphicalRepresentation;
import org.openflexo.diana.Drawing.DrawingTreeNode;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.ForegroundStyle;
import org.openflexo.diana.GRProperty;
import org.openflexo.diana.GeometricGraphicalRepresentation;
import org.openflexo.diana.GraphicalRepresentation;
import org.openflexo.diana.ShadowStyle;
import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.diana.control.DianaInteractiveViewer;

/**
 * Implementation of {@link ShadowStyle}, as a container of graphical properties synchronized with and reflecting a selection<br>
 * This is the object beeing represented in tool inspectors
 * 
 * @author sylvain
 * 
 */
public class InspectedLocationSizeProperties extends InspectedStyle<GraphicalRepresentation> {

	public InspectedLocationSizeProperties(DianaInteractiveViewer<?, ?, ?> controller) {
		super(controller, null);
	}

	@Override
	public List<DrawingTreeNode<?, ?>> getSelection() {
		return getController().getSelectedObjects();
	}

	@Override
	public GraphicalRepresentation getStyle(DrawingTreeNode<?, ?> node) {
		return node.getGraphicalRepresentation();
	}

	public boolean areLocationPropertiesApplicable() {
		return getController().getSelectedShapes().size() > 0;
	}

	public boolean areDimensionPropertiesApplicable() {
		return getController().getSelectedContainers().size() > 0;
	}

	@Override
	protected void fireChangedProperties() {
		// We replace here super code, because we have to fire changed properties for all properties
		// as the union of properties of all possible types
		List<GRProperty<?>> paramsList = new ArrayList<>();
		paramsList.addAll(GRProperty.getGRParameters(DrawingGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(GeometricGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(ShapeGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(ConnectorGraphicalRepresentation.class));
		Set<GRProperty<?>> allParams = new HashSet<>(paramsList);
		for (GRProperty<?> p : allParams) {
			fireChangedProperty(p);
		}
	}

	@Override
	public void fireSelectionUpdated() {
		super.fireSelectionUpdated();
		getPropertyChangeSupport().firePropertyChange("areLocationPropertiesApplicable", !areLocationPropertiesApplicable(),
				areLocationPropertiesApplicable());
		getPropertyChangeSupport().firePropertyChange("areDimensionPropertiesApplicable", !areDimensionPropertiesApplicable(),
				areDimensionPropertiesApplicable());
		if (getIsSelectable() != null) {
			getPropertyChangeSupport().firePropertyChange("isSelectable", !getIsSelectable(), (boolean) getIsSelectable());
		}
		if (getIsFocusable() != null) {
			getPropertyChangeSupport().firePropertyChange("isFocusable", !getIsFocusable(), (boolean) getIsFocusable());
		}
		if (getHasFocusedBackground() != null) {
			getPropertyChangeSupport().firePropertyChange("hasFocusedBackground", !getHasFocusedBackground(),
					(boolean) getHasFocusedBackground());
		}
		if (getHasSelectedBackground() != null) {
			getPropertyChangeSupport().firePropertyChange("hasSelectedBackground", !getHasSelectedBackground(),
					(boolean) getHasSelectedBackground());
		}
		if (getHasFocusedForeground() != null) {
			getPropertyChangeSupport().firePropertyChange("hasFocusedForeground", !getHasFocusedForeground(),
					(boolean) getHasFocusedForeground());
		}
		if (getHasSelectedForeground() != null) {
			getPropertyChangeSupport().firePropertyChange("hasSelectedForeground", !getHasSelectedForeground(),
					(boolean) getHasSelectedForeground());
		}
	}

	public Boolean getIsVisible() {
		return getPropertyValue(GraphicalRepresentation.IS_VISIBLE);
	}

	public void setIsVisible(Boolean value) {
		setPropertyValue(GraphicalRepresentation.IS_VISIBLE, value);
	}

	public Integer getLayer() {
		return getPropertyValue(GraphicalRepresentation.LAYER);
	}

	public void setLayer(Integer value) {
		setPropertyValue(GraphicalRepresentation.LAYER, value);
	}

	public Integer getIndexInLayer() {
		// TODO
		return 0;
	}

	public void setIndexInLayer(Integer value) {
		// TODO
	}

	public String getLayoutManagerIdentifier() {
		return getPropertyValue(ShapeGraphicalRepresentation.LAYOUT_MANAGER_IDENTIFIER);
	}

	public void setLayoutManagerIdentifier(String value) {
		setPropertyValue(ShapeGraphicalRepresentation.LAYOUT_MANAGER_IDENTIFIER, value);
	}

	public Double getX() {
		return getPropertyValue(ShapeGraphicalRepresentation.X);
	}

	public void setX(Double value) {
		setPropertyValue(ShapeGraphicalRepresentation.X, value);
	}

	public Double getY() {
		return getPropertyValue(ShapeGraphicalRepresentation.Y);
	}

	public void setY(Double value) {
		setPropertyValue(ShapeGraphicalRepresentation.Y, value);
	}

	public Double getWidth() {
		return getPropertyValue(ContainerGraphicalRepresentation.WIDTH);
	}

	public void setWidth(Double value) {
		setPropertyValue(ContainerGraphicalRepresentation.WIDTH, value);
	}

	public Double getHeight() {
		return getPropertyValue(ContainerGraphicalRepresentation.HEIGHT);
	}

	public void setHeight(Double value) {
		setPropertyValue(ContainerGraphicalRepresentation.HEIGHT, value);
	}

	public LocationConstraints getLocationConstraints() {
		return getPropertyValue(ShapeGraphicalRepresentation.LOCATION_CONSTRAINTS);
	}

	public void setLocationConstraints(LocationConstraints locationConstraints) {
		setPropertyValue(ShapeGraphicalRepresentation.LOCATION_CONSTRAINTS, locationConstraints);
	}

	public DimensionConstraints getDimensionConstraints() {
		return getPropertyValue(ShapeGraphicalRepresentation.DIMENSION_CONSTRAINTS);
	}

	public void setDimensionConstraints(DimensionConstraints dimensionConstraints) {
		setPropertyValue(ShapeGraphicalRepresentation.DIMENSION_CONSTRAINTS, dimensionConstraints);
	}

	public Boolean getIsAllowToLeaveBounds() {
		return getPropertyValue(ShapeGraphicalRepresentation.ALLOW_TO_LEAVE_BOUNDS);
	}

	public void setIsAllowToLeaveBounds(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.ALLOW_TO_LEAVE_BOUNDS, flag);
	}

	public DataBinding<Double> getXConstraints() {
		return getPropertyValue(ShapeGraphicalRepresentation.X_CONSTRAINTS);
	}

	public void setXConstraints(DataBinding<Double> xConstraints) {
		setPropertyValue(ShapeGraphicalRepresentation.X_CONSTRAINTS, xConstraints);
	}

	public DataBinding<Double> getYConstraints() {
		return getPropertyValue(ShapeGraphicalRepresentation.Y_CONSTRAINTS);
	}

	public void setYConstraints(DataBinding<Double> yConstraints) {
		setPropertyValue(ShapeGraphicalRepresentation.Y_CONSTRAINTS, yConstraints);
	}

	public DataBinding<Double> getWidthConstraints() {
		return getPropertyValue(ShapeGraphicalRepresentation.WIDTH_CONSTRAINTS);
	}

	public void setWidthConstraints(DataBinding<Double> widthConstraints) {
		setPropertyValue(ShapeGraphicalRepresentation.WIDTH_CONSTRAINTS, widthConstraints);
	}

	public DataBinding<Double> getHeightConstraints() {
		return getPropertyValue(ShapeGraphicalRepresentation.HEIGHT_CONSTRAINTS);
	}

	public void setHeightConstraints(DataBinding<Double> heightConstraints) {
		setPropertyValue(ShapeGraphicalRepresentation.HEIGHT_CONSTRAINTS, heightConstraints);
	}

	public Boolean getIsSelectable() {
		return getPropertyValue(GraphicalRepresentation.IS_SELECTABLE);
	}

	public void setIsSelectable(Boolean flag) {
		setPropertyValue(GraphicalRepresentation.IS_SELECTABLE, flag);
	}

	public Boolean getHasSelectedForeground() {
		return getPropertyValue(ShapeGraphicalRepresentation.HAS_SELECTED_FOREGROUND);
	}

	public void setHasSelectedForeground(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.HAS_SELECTED_FOREGROUND, flag);
	}

	public ForegroundStyle getSelectedForeground() {
		return getPropertyValue(ShapeGraphicalRepresentation.SELECTED_FOREGROUND);
	}

	public void setSelectedForeground(ForegroundStyle foreground) {
		setPropertyValue(ShapeGraphicalRepresentation.SELECTED_FOREGROUND, foreground);
	}

	public Boolean getHasSelectedBackground() {
		return getPropertyValue(ShapeGraphicalRepresentation.HAS_SELECTED_BACKGROUND);
	}

	public void setHasSelectedBackground(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.HAS_SELECTED_BACKGROUND, flag);
	}

	public BackgroundStyle getSelectedBackground() {
		return getPropertyValue(ShapeGraphicalRepresentation.SELECTED_BACKGROUND);
	}

	public void setSelectedBackground(BackgroundStyle background) {
		setPropertyValue(ShapeGraphicalRepresentation.SELECTED_BACKGROUND, background);
	}

	public Boolean getIsFocusable() {
		return getPropertyValue(GraphicalRepresentation.IS_FOCUSABLE);
	}

	public void setIsFocusable(Boolean flag) {
		setPropertyValue(GraphicalRepresentation.IS_FOCUSABLE, flag);
	}

	public Boolean getHasFocusedForeground() {
		return getPropertyValue(ShapeGraphicalRepresentation.HAS_FOCUSED_FOREGROUND);
	}

	public void setHasFocusedForeground(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.HAS_FOCUSED_FOREGROUND, flag);
	}

	public ForegroundStyle getFocusedForeground() {
		return getPropertyValue(ShapeGraphicalRepresentation.FOCUSED_FOREGROUND);
	}

	public void setFocusedForeground(ForegroundStyle foreground) {
		setPropertyValue(ShapeGraphicalRepresentation.FOCUSED_FOREGROUND, foreground);
	}

	public Boolean getHasFocusedBackground() {
		return getPropertyValue(ShapeGraphicalRepresentation.HAS_FOCUSED_BACKGROUND);
	}

	public void setHasFocusedBackground(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.HAS_FOCUSED_BACKGROUND, flag);
	}

	public BackgroundStyle getFocusedBackground() {
		return getPropertyValue(ShapeGraphicalRepresentation.FOCUSED_BACKGROUND);
	}

	public void setFocusedBackground(BackgroundStyle background) {
		setPropertyValue(ShapeGraphicalRepresentation.FOCUSED_BACKGROUND, background);
	}

}

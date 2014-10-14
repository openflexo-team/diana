/*
 * (c) Copyright 2010-2011 AgileBirds
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
package org.openflexo.fge.control.tools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.GraphicalRepresentation.ParagraphAlignment;
import org.openflexo.fge.GraphicalRepresentation.VerticalTextAlignment;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.ShapeBorder;
import org.openflexo.fge.control.DianaInteractiveViewer;

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

	@Override
	protected void fireChangedProperties() {
		// We replace here super code, because we have to fire changed properties for all properties
		// as the union of properties of all possible types
		List<GRProperty<?>> paramsList = new ArrayList<GRProperty<?>>();
		paramsList.addAll(GRProperty.getGRParameters(DrawingGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(GeometricGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(ShapeGraphicalRepresentation.class));
		paramsList.addAll(GRProperty.getGRParameters(ConnectorGraphicalRepresentation.class));
		Set<GRProperty<?>> allParams = new HashSet<GRProperty<?>>(paramsList);
		for (GRProperty<?> p : allParams) {
			fireChangedProperty(p);
		}
	}

	@Override
	public void fireSelectionUpdated() {
		super.fireSelectionUpdated();
		getPropertyChangeSupport().firePropertyChange("areLocationPropertiesApplicable", !areLocationPropertiesApplicable(),
				areLocationPropertiesApplicable());
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

	public HorizontalTextAlignment getHorizontalTextAlignment() {
		return getPropertyValue(GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT);
	}

	public void setHorizontalTextAlignment(HorizontalTextAlignment alignment) {
		setPropertyValue(GraphicalRepresentation.HORIZONTAL_TEXT_ALIGNEMENT, alignment);
	}

	public VerticalTextAlignment getVerticalTextAlignment() {
		return getPropertyValue(GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT);
	}

	public void setVerticalTextAlignment(VerticalTextAlignment alignment) {
		setPropertyValue(GraphicalRepresentation.VERTICAL_TEXT_ALIGNEMENT, alignment);
	}

	public ParagraphAlignment getParagraphAlignment() {
		return getPropertyValue(GraphicalRepresentation.PARAGRAPH_ALIGNEMENT);
	}

	public void setParagraphAlignment(ParagraphAlignment alignment) {
		setPropertyValue(GraphicalRepresentation.PARAGRAPH_ALIGNEMENT, alignment);
	}

	public Double getAbsoluteTextX() {
		return getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X);
	}

	public void setAbsoluteTextX(Double x) {
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_X, x);
	}

	public Double getAbsoluteTextY() {
		return getPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y);
	}

	public void setAbsoluteTextY(Double y) {
		setPropertyValue(GraphicalRepresentation.ABSOLUTE_TEXT_Y, y);
	}

	public Double getRelativeTextX() {
		return getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_X);
	}

	public void setRelativeTextX(Double x) {
		setPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_X, x);
	}

	public Double getRelativeTextY() {
		return getPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_Y);
	}

	public void setRelativeTextY(Double y) {
		setPropertyValue(ShapeGraphicalRepresentation.RELATIVE_TEXT_Y, y);
	}

	public Boolean getIsMultilineAllowed() {
		return getPropertyValue(GraphicalRepresentation.IS_MULTILINE_ALLOWED);
	}

	public void setIsMultilineAllowed(Boolean flag) {
		setPropertyValue(GraphicalRepresentation.IS_MULTILINE_ALLOWED, flag);
	}

	public Boolean getLineWrap() {
		return getPropertyValue(GraphicalRepresentation.LINE_WRAP);
	}

	public void setLineWrap(Boolean flag) {
		setPropertyValue(GraphicalRepresentation.LINE_WRAP, flag);
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

	/*@Override
	protected <T> void fireChangedProperty(GRProperty<T> p) {
		if (p.equals(ShapeGraphicalRepresentation.BORDER)) {
			T newValue = _getPropertyValue(p);
			System.out.println("Fire de border pour " + newValue);
			_doFireChangedProperty(p, null, newValue);
		}
		super.fireChangedProperty(p);
	}*/

	public ShapeBorder getBorder() {
		return getPropertyValue(ShapeGraphicalRepresentation.BORDER);
	}

	public void setBorder(ShapeBorder aBorder) {
		setPropertyValue(ShapeGraphicalRepresentation.BORDER, aBorder);
	}
}

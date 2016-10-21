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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openflexo.connie.DataBinding;
import org.openflexo.fge.ConnectorGraphicalRepresentation;
import org.openflexo.fge.ContainerGraphicalRepresentation;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.GRProperty;
import org.openflexo.fge.GeometricGraphicalRepresentation;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.fge.ShadowStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.fge.ShapeGraphicalRepresentation.LocationConstraints;
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

	public Boolean getAdaptBoundsToContents() {
		return getPropertyValue(ShapeGraphicalRepresentation.ADAPT_BOUNDS_TO_CONTENTS);
	}

	public void setAdaptBoundsToContents(Boolean flag) {
		setPropertyValue(ShapeGraphicalRepresentation.ADAPT_BOUNDS_TO_CONTENTS, flag);
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

}

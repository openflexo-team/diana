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

import java.util.logging.Logger;

import org.openflexo.diana.ContainerGraphicalRepresentation;
import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.ShapeGraphicalRepresentation.DimensionConstraints;
import org.openflexo.diana.geom.DianaDimension;
import org.openflexo.diana.geom.DianaSteppedDimensionConstraint;
import org.openflexo.diana.notifications.DianaAttributeNotification;

public abstract class ContainerGraphicalRepresentationImpl extends GraphicalRepresentationImpl implements ContainerGraphicalRepresentation {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DrawingGraphicalRepresentation.class.getPackage().getName());

	protected double width;
	protected double height;

	private double minimalWidth = 0;
	private double minimalHeight = 0;
	private double maximalWidth = Double.POSITIVE_INFINITY;
	private double maximalHeight = Double.POSITIVE_INFINITY;

	private DimensionConstraints dimensionConstraints = DimensionConstraints.FREELY_RESIZABLE;
	private DianaSteppedDimensionConstraint dimensionConstraintStep = null;
	private boolean adjustMinimalWidthToLabelWidth = true;
	private boolean adjustMinimalHeightToLabelHeight = true;
	private boolean adjustMaximalWidthToLabelWidth = false;
	private boolean adjustMaximalHeightToLabelHeight = false;

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public ContainerGraphicalRepresentationImpl() {
		super();
		width = DEFAULT_WIDTH;
		height = DEFAULT_HEIGHT;
	}

	@Override
	public double getWidth() {
		return width;
	}

	@Override
	public void setWidth(double aValue) {
		DianaAttributeNotification<?> notification = requireChange(WIDTH, aValue);
		if (notification != null) {
			// DianaDimension oldSize = getSize();
			width = aValue;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			// notifyObjectResized(oldSize);
		}
	}

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public void setHeight(double aValue) {
		DianaAttributeNotification<?> notification = requireChange(HEIGHT, aValue);
		if (notification != null) {
			// DianaDimension oldSize = getSize();
			height = aValue;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			// notifyObjectResized(oldSize);
		}
	}

	// *******************************************************************************
	// * Size management *
	// *******************************************************************************

	@Override
	public boolean getAdjustMinimalWidthToLabelWidth() {
		return adjustMinimalWidthToLabelWidth;
	}

	@Override
	public void setAdjustMinimalWidthToLabelWidth(boolean adjustMinimalWidthToLabelWidth) {
		DianaAttributeNotification<?> notification = requireChange(ADJUST_MINIMAL_WIDTH_TO_LABEL_WIDTH, adjustMinimalWidthToLabelWidth);
		if (notification != null) {
			this.adjustMinimalWidthToLabelWidth = adjustMinimalWidthToLabelWidth;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdjustMinimalHeightToLabelHeight() {
		return adjustMinimalHeightToLabelHeight;
	}

	@Override
	public void setAdjustMinimalHeightToLabelHeight(boolean adjustMinimalHeightToLabelHeight) {
		DianaAttributeNotification<?> notification = requireChange(ADJUST_MINIMAL_HEIGHT_TO_LABEL_HEIGHT, adjustMinimalHeightToLabelHeight);
		if (notification != null) {
			this.adjustMinimalHeightToLabelHeight = adjustMinimalHeightToLabelHeight;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdjustMaximalWidthToLabelWidth() {
		return adjustMaximalWidthToLabelWidth;
	}

	@Override
	public void setAdjustMaximalWidthToLabelWidth(boolean adjustMaximalWidthToLabelWidth) {
		DianaAttributeNotification<?> notification = requireChange(ADJUST_MAXIMAL_WIDTH_TO_LABEL_WIDTH, adjustMaximalWidthToLabelWidth);
		if (notification != null) {
			this.adjustMaximalWidthToLabelWidth = adjustMaximalWidthToLabelWidth;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public boolean getAdjustMaximalHeightToLabelHeight() {
		return adjustMaximalHeightToLabelHeight;
	}

	@Override
	public void setAdjustMaximalHeightToLabelHeight(boolean adjustMaximalHeightToLabelHeight) {
		DianaAttributeNotification<?> notification = requireChange(ADJUST_MAXIMAL_HEIGHT_TO_LABEL_HEIGHT, adjustMaximalHeightToLabelHeight);
		if (notification != null) {
			this.adjustMaximalHeightToLabelHeight = adjustMaximalHeightToLabelHeight;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	/*@Override
	public double getWidth() {
		return width;
	}*/

	/*@Override
	public final void setWidth(double aValue) {
		DianaNotification notification = requireChange(ShapeParameters.width, aValue);
		if (notification != null) {
			DianaDimension oldSize = getSize();
			width = aValue;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			// notifyObjectResized(oldSize);
		}
	}*/

	/*@Override
	public double getHeight() {
		return height;
	}*/

	/*@Override
	public final void setHeight(double aValue) {
		DianaNotification notification = requireChange(ShapeParameters.height, aValue);
		if (notification != null) {
			DianaDimension oldSize = getSize();
			height = aValue;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
			// notifyObjectResized(oldSize);
		}
	}*/

	/*public void setHeightNoNotification(double aValue) {
		height = aValue;
	}*/

	@Override
	public DianaDimension getSize() {
		return new DianaDimension(getWidth(), getHeight());
	}

	/*@Override
	public void setSize(DianaDimension newSize) {
		if (newSize == null) {
			return;
		}
		// Preventing size from being negative or equals to 0
		if (newSize.width <= 0) {
			newSize.width = DianaGeometricObject.EPSILON;
		}
		if (newSize.height <= 0) {
			newSize.height = DianaGeometricObject.EPSILON;
		}
		DianaDimension oldSize = getSize();
		if (!newSize.equals(oldSize)) {
			double oldWidth = getWidth();
			double oldHeight = getHeight();
			setWidthNoNotification(newSize.width);
			setHeightNoNotification(newSize.height);
			if (hasFloatingLabel()) {
				if (getAbsoluteTextX() >= 0) {
					if (getAbsoluteTextX() < getWidth()) {
						setAbsoluteTextX(getAbsoluteTextX() / oldSize.width * getWidth());
					} else {
						setAbsoluteTextX(getAbsoluteTextX() + getWidth() - oldSize.width);
					}
				}
				if (getAbsoluteTextY() >= 0) {
					if (getAbsoluteTextY() < getHeight()) {
						setAbsoluteTextY(getAbsoluteTextY() / oldSize.height * getHeight());
					} else {
						setAbsoluteTextY(getAbsoluteTextY() + getHeight() - oldSize.height);
					}
				}
			}
			checkAndUpdateDimensionBoundsIfRequired();
			if (isParentLayoutedAsContainer()) {
				((ShapeGraphicalRepresentationImpl<?>) getContainerGraphicalRepresentation()).checkAndUpdateDimensionIfRequired();
			}
			notifyObjectResized(oldSize);
			notifyChange(ShapeParameters.width, oldWidth, getWidth());
			notifyChange(ShapeParameters.height, oldHeight, getHeight());
			getShape().notifyObjectResized();
		}
	}*/

	@Override
	public double getMinimalWidth() {
		return minimalWidth;
	}

	@Override
	public final void setMinimalWidth(double minimalWidth) {
		DianaAttributeNotification<?> notification = requireChange(MINIMAL_WIDTH, minimalWidth);
		if (notification != null) {
			this.minimalWidth = minimalWidth;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getMinimalHeight() {
		return minimalHeight;
	}

	@Override
	public final void setMinimalHeight(double minimalHeight) {
		DianaAttributeNotification<?> notification = requireChange(MINIMAL_HEIGHT, minimalHeight);
		if (notification != null) {
			this.minimalHeight = minimalHeight;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getMaximalHeight() {
		return maximalHeight;
	}

	@Override
	public final void setMaximalHeight(double maximalHeight) {
		DianaAttributeNotification<?> notification = requireChange(MAXIMAL_HEIGHT, maximalHeight);
		if (notification != null) {
			this.maximalHeight = maximalHeight;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public double getMaximalWidth() {
		return maximalWidth;
	}

	@Override
	public final void setMaximalWidth(double maximalWidth) {
		DianaAttributeNotification<?> notification = requireChange(MAXIMAL_WIDTH, maximalWidth);
		if (notification != null) {
			this.maximalWidth = maximalWidth;
			// checkAndUpdateDimensionBoundsIfRequired();
			hasChanged(notification);
		}
	}

	@Override
	public DimensionConstraints getDimensionConstraints() {
		// ShapeSpecification dimension constraints may override defaults

		/*if (shape != null && shape.areDimensionConstrained()) {
			if (dimensionConstraints == DimensionConstraints.CONSTRAINED_DIMENSIONS) {
				return DimensionConstraints.CONSTRAINED_DIMENSIONS;
			}
			if (dimensionConstraints == DimensionConstraints.FREELY_RESIZABLE) {
				return DimensionConstraints.CONSTRAINED_DIMENSIONS;
			}
			return DimensionConstraints.UNRESIZABLE;
		}*/
		return dimensionConstraints;
	}

	@Override
	public void setDimensionConstraints(DimensionConstraints dimensionConstraints) {
		DianaAttributeNotification<?> notification = requireChange(DIMENSION_CONSTRAINTS, dimensionConstraints);
		if (notification != null /*&& getShape() != null*/) {
			this.dimensionConstraints = dimensionConstraints;
			hasChanged(notification);
		}
	}

	@Override
	public DianaSteppedDimensionConstraint getDimensionConstraintStep() {
		return dimensionConstraintStep;
	}

	@Override
	public void setDimensionConstraintStep(DianaSteppedDimensionConstraint dimensionConstraintStep) {
		DianaAttributeNotification<?> notification = requireChange(DIMENSION_CONSTRAINT_STEP, dimensionConstraintStep);
		if (notification != null) {
			this.dimensionConstraintStep = dimensionConstraintStep;
			hasChanged(notification);
		}
	}

}

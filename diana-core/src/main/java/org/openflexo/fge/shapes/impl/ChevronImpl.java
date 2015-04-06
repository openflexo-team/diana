package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Chevron;

/**
 * Implementation of interface Chevron.
 * 
 * @author eloubout
 * 
 */
public abstract class ChevronImpl extends ShapeSpecificationImpl implements Chevron {

	private double	arrowLength	= 0.2;

	public ChevronImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.CHEVRON;
	}

	@Override
	public double getArrowLength() {
		return this.arrowLength;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setArrowLength(final double anArrowLength) {
		final FGEAttributeNotification<Double> notification = this.requireChange(ARROW_LENGTH, anArrowLength);
		if (notification != null) {
			this.arrowLength = anArrowLength;
			this.hasChanged(notification);
		}
	}

	/**
	 * Draw a chevron by creating a filled polygon of 6 good points, ajustable with predifined arrowLenght
	 * 
	 * @return FGEPolygon
	 */
	@Override
	public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
		final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
		returned.addToPoints(new FGEPoint(0, 0));
		returned.addToPoints(new FGEPoint(this.arrowLength, 0.5));
		returned.addToPoints(new FGEPoint(0, 1));
		returned.addToPoints(new FGEPoint(1 - this.arrowLength, 1));
		returned.addToPoints(new FGEPoint(1, 0.5));
		returned.addToPoints(new FGEPoint(1 - this.arrowLength, 0));
		return returned;
	}

}

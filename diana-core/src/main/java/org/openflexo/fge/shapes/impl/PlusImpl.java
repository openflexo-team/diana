package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEShape;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Plus;

/**
 * Implementation for a Plus shape.
 * 
 * @author eloubout
 * 
 */
public abstract class PlusImpl extends ShapeSpecificationImpl implements Plus {

	/** Ratio of cut of the base rectangle. 0.5 is no short side. */
	private double	ratio	= 0.25;

	public PlusImpl() {
		super();
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.PLUS;
	}

	/**
	 * Set ratio
	 * 
	 * @param ratio
	 *            0 < ratio < 0.5, handled in serialization
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setRatio(final double aRatio) {
		final FGEAttributeNotification<Double> notification = this.requireChange(RATIO, aRatio);
		if (notification != null) {
			this.ratio = aRatio;
			this.hasChanged(notification);
		}
	}

	@Override
	public double getRatio() {
		return this.ratio;
	}

	@Override
	public boolean areDimensionConstrained() {
		return true;
	}

	/**
	 * Polygon with a big number of points well placed and that take car of ratio.
	 * 
	 * @return {@link FGEPolygon}
	 */
	@Override
	public FGEShape<?> makeFGEShape(final ShapeNode<?> node) {
		final FGEPolygon returned = new FGEPolygon(Filling.FILLED);
		returned.addToPoints(new FGEPoint(0, this.ratio));
		returned.addToPoints(new FGEPoint(0, 1 - this.ratio));
		returned.addToPoints(new FGEPoint(this.ratio, 1 - this.ratio));
		returned.addToPoints(new FGEPoint(this.ratio, 1));
		returned.addToPoints(new FGEPoint(1 - this.ratio, 1));
		returned.addToPoints(new FGEPoint(1 - this.ratio, 1 - this.ratio));
		returned.addToPoints(new FGEPoint(1, 1 - this.ratio));
		returned.addToPoints(new FGEPoint(1, this.ratio));
		returned.addToPoints(new FGEPoint(1 - this.ratio, this.ratio));
		returned.addToPoints(new FGEPoint(1 - this.ratio, 0));
		returned.addToPoints(new FGEPoint(this.ratio, 0));
		returned.addToPoints(new FGEPoint(this.ratio, this.ratio));
		return returned;
	}

}

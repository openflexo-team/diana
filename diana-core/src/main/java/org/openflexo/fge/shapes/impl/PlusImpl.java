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
	 *            if ratio > 0.5 ratio is divide by 2
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setRatio(final double aRatio) {
		double tmpRatio = aRatio;
		if (tmpRatio > 0.5) {
			tmpRatio /= 2;
		}
		FGEAttributeNotification<Double> notification = requireChange(RATIO, tmpRatio);
		if (notification != null) {
			this.ratio = tmpRatio;
			hasChanged(notification);
		}
	}

	@Override
	public double getRatio() {
		return ratio;
	}

	@Override
	public FGEShape<?> makeFGEShape(ShapeNode<?> node) {
		FGEPolygon returned = new FGEPolygon(Filling.FILLED);
		returned.addToPoints(new FGEPoint(0, ratio));
		returned.addToPoints(new FGEPoint(0, 1 - ratio));
		returned.addToPoints(new FGEPoint(ratio, 1 - ratio));
		returned.addToPoints(new FGEPoint(ratio, 1));
		returned.addToPoints(new FGEPoint(1 - ratio, 1));
		returned.addToPoints(new FGEPoint(1 - ratio, 1 - ratio));
		returned.addToPoints(new FGEPoint(1, 1 - ratio));
		returned.addToPoints(new FGEPoint(1, ratio));
		returned.addToPoints(new FGEPoint(1 - ratio, ratio));
		returned.addToPoints(new FGEPoint(1 - ratio, 0));
		returned.addToPoints(new FGEPoint(ratio, 0));
		returned.addToPoints(new FGEPoint(ratio, ratio));
		return returned;
	}

}

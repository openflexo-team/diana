package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Plus;

/**
 * Implementation for a Plus shape.
 * 
 * @author eloubout
 * 
 */
public abstract class PlusImpl extends PolygonImpl implements Plus {

	/** Ratio of cut of the base rectangle. 0.5 is no short side. */
	private double	ratio	= 0.25;

	public PlusImpl() {
		super();
		addToPoints(new FGEPoint(0, ratio));
		addToPoints(new FGEPoint(0, 1 - ratio));
		addToPoints(new FGEPoint(ratio, 1 - ratio));
		addToPoints(new FGEPoint(ratio, 1));
		addToPoints(new FGEPoint(1 - ratio, 1));
		addToPoints(new FGEPoint(1 - ratio, 1 - ratio));
		addToPoints(new FGEPoint(1, 1 - ratio));
		addToPoints(new FGEPoint(1, ratio));
		addToPoints(new FGEPoint(1 - ratio, ratio));
		addToPoints(new FGEPoint(1 - ratio, 0));
		addToPoints(new FGEPoint(ratio, 0));
		addToPoints(new FGEPoint(ratio, ratio));
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.PLUS;// ShapeType.PLUS;
	}

	/**
	 * Set ratio
	 * 
	 * @param ratio
	 *            if ratio > 0.5 ratio is divide by 2
	 */
	@SuppressWarnings("unchecked")
	public void setRation(final double aRatio) {
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

}

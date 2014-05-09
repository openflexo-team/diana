package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.notifications.FGEAttributeNotification;
import org.openflexo.fge.shapes.Chevron;

/**
 * Implementation of interface Chevron.
 * 
 * @author eloubout
 * 
 */
public abstract class ChevronImpl extends PolygonImpl implements Chevron {

	private double	arrowLength	= 0.2;

	public ChevronImpl() {
		super();
		addToPoints(new FGEPoint(0, 0));
		addToPoints(new FGEPoint(arrowLength, 0.5));
		addToPoints(new FGEPoint(0, 1));
		addToPoints(new FGEPoint(1 - arrowLength, 1));
		addToPoints(new FGEPoint(1, 0.5));
		addToPoints(new FGEPoint(1 - arrowLength, 0));
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.CHEVRON;
	}

	@Override
	public double getArrowLength() {
		return arrowLength;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setArrowLength(final double anArrowLength) {
		double tmpLgth = anArrowLength;
		if (tmpLgth > 0.5) {
			// 0 < tmpLght < 0.5
			tmpLgth = Math.abs(anArrowLength / (1 + 2 * anArrowLength));
		}
		FGEAttributeNotification<Double> notification = requireChange(ARROW_LENGTH, tmpLgth);
		if (notification != null) {
			this.arrowLength = tmpLgth;
			hasChanged(notification);
		}
	}

}

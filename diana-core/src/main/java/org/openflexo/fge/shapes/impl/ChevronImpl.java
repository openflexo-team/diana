package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.shapes.Chevron;

/**
 * Implementation of interface Chevron.
 * 
 * @author eloubout
 * 
 */
public abstract class ChevronImpl extends PolygonImpl implements Chevron {

	private final double	arrowLength	= 0.2;

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
		return ShapeType.CHEVRON;// ShapeType.PLUS;
	}

}

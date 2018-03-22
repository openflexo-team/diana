/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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

package org.openflexo.diana.geom;

import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;

@SuppressWarnings("serial")
public class DianaEllips extends DianaArc {

	private static final Logger logger = Logger.getLogger(DianaEllips.class.getPackage().getName());

	private Filling _filling;

	public DianaEllips() {
		this(Filling.NOT_FILLED);
	}

	public DianaEllips(Filling filling) {
		super(filling == Filling.FILLED ? ArcType.PIE : ArcType.OPEN);
		start = 0;
		extent = 360;
		_filling = filling;
	}

	public DianaEllips(double x, double y, double w, double h, Filling filling) {
		super(x, y, w, h, 0, 360, filling == Filling.FILLED ? ArcType.PIE : ArcType.OPEN);
		_filling = filling;
	}

	public DianaEllips(DianaPoint center, DianaDimension size, Filling filling) {
		this(center.x - size.width / 2, center.y - size.height / 2, size.width, size.height, filling);
	}

	public void setCenterAndSize(DianaPoint center, DianaDimension size) {
		setX(center.x - size.width / 2);
		setY(center.y - size.height / 2);
		setWidth(size.width);
		setHeight(size.height);
	}

	@Override
	public boolean getIsFilled() {
		return _filling == Filling.FILLED;
	}

	@Override
	public void setIsFilled(boolean filled) {
		_filling = filled ? Filling.FILLED : Filling.NOT_FILLED;
		super.setIsFilled(filled);
	}

	@Override
	public boolean includeAngle(double radianAngle) {
		return true;
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		Vector<DianaPoint> returned = new Vector<>();
		returned.add(new DianaPoint(x + width / 2.0, y));
		returned.add(new DianaPoint(x, y + height / 2.0));
		returned.add(new DianaPoint(x + width / 2.0, y + height));
		returned.add(new DianaPoint(x + width, y + height / 2.0));

		return returned;
	}

	/*public DianaPoint nearestOutlinePoint(DianaPoint aPoint) 
	{
		double atan2 = Math.PI/2-Math.atan2(aPoint.x-getCenterX(),aPoint.y-getCenterY());
		return new DianaPoint(Math.cos(atan2)*getWidth()/2+getCenterX(),Math.sin(atan2)*getHeight()/2+getCenterY());
	}*/

	@Override
	public DianaEllips transform(AffineTransform t) {
		// TODO: this implementation is not correct if AffineTransform contains rotation and if
		// width not equals to height

		DianaPoint newCenter = new DianaPoint(getX() + getWidth() / 2, getY() + getHeight() / 2).transform(t);

		DianaRectangle bounds = new DianaRectangle(getX(), getY(), getWidth(), getHeight());
		DianaArea t_bounds = bounds.transform(t);

		if (t_bounds instanceof DianaShape) {
			DianaRectangle boundingBox = ((DianaShape<?>) t_bounds).getBoundingBox();
			return new DianaEllips(newCenter, new DianaDimension(boundingBox.getWidth(), boundingBox.getHeight()), _filling);
		}
		logger.warning("Cannot compute transform for " + this + " with " + t);
		return (DianaEllips) clone();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaEllips) {
			DianaEllips p = (DianaEllips) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getX() - p.getX()) <= EPSILON && Math.abs(getY() - p.getY()) <= EPSILON
					&& Math.abs(getWidth() - p.getWidth()) <= EPSILON && Math.abs(getHeight() - p.getHeight()) <= EPSILON;
		}
		return super.equals(obj);
	}

	@Override
	public DianaRectangle getBoundingBox() {
		return new DianaRectangle(x, y, width, height, Filling.FILLED);
	}

	@Override
	public String toString() {
		return "DianaEllips: (" + x + "," + y + "," + width + "," + height + " type=" + getDianaArcType() + ")";
	}

}

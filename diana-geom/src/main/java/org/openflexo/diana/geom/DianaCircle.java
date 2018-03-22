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

import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaUnionArea;

@SuppressWarnings("serial")
public class DianaCircle extends DianaEllips {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DianaCircle.class.getPackage().getName());

	public DianaCircle() {
		this(Filling.NOT_FILLED);
	}

	public DianaCircle(Filling filling) {
		super(filling);
	}

	public DianaCircle(DianaPoint center, double radius, Filling filling) {
		super(center, new DianaDimension(radius * 2, radius * 2), filling);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaCircle) {
			DianaCircle p = (DianaCircle) obj;
			if (getIsFilled() != p.getIsFilled()) {
				return false;
			}
			return Math.abs(getRadius() - p.getRadius()) <= EPSILON && getCenter().equals(p.getCenter());
		}
		return super.equals(obj);
	}

	/**
	 * Return union of two points defining tangent to supplied circle and crossing supplied point
	 * 
	 * @param cicle
	 * @param p
	 * @return
	 */
	public static DianaUnionArea getTangentsPointsToCircle(DianaCircle circle, DianaPoint p) throws PointInsideCircleException {
		DianaLine l = new DianaLine(p, circle.getCenter());
		double asin = circle.getWidth() / 2 / DianaSegment.getLength(p, circle.getCenter());
		if (asin >= 1 || asin <= -1) {
			throw new PointInsideCircleException();
		}
		double angle = Math.toDegrees(Math.asin(asin));
		DianaLine tan1 = l.getRotatedLine(angle, p);
		DianaLine tan2 = l.getRotatedLine(-angle, p);
		DianaPoint p1 = tan1.getProjection(circle.getCenter());
		DianaPoint p2 = tan2.getProjection(circle.getCenter());
		return new DianaUnionArea(p1, p2);
	}

	public double getRadius() {
		return getWidth() / 2;
	}

	public void setRadius(double radius) {
		DianaPoint center = getCenter();
		x = center.x - radius;
		y = center.y - radius;
		width = radius * 2;
		height = radius * 2;
	}

	@Override
	public String toString() {
		return "DianaCircle: (" + x + "," + y + "," + width + "," + height + " type=" + getDianaArcType() + ")";
	}

}

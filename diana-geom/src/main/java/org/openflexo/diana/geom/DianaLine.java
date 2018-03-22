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
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaBand;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaPlane;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaLine extends DianaAbstractLine<DianaLine> {

	private static final Logger LOGGER = Logger.getLogger(DianaLine.class.getPackage().getName());

	public DianaLine(double X1, double Y1, double X2, double Y2) {
		super(X1, Y1, X2, Y2);
	}

	public DianaLine(DianaPoint p1, DianaPoint p2) {
		super(p1, p2);
	}

	public DianaLine(DianaAbstractLine<?> line) {
		super(line.getP1(), line.getP2());
	}

	public DianaLine() {
		super();
	}

	public DianaLine(double pA, double pB, double pC) {
		super(pA, pB, pC);
	}

	public DianaLine(DianaSegment s) {
		this(s.getP1(), s.getP2());
	}

	public DianaLine(DianaHalfLine hl) {
		this(hl.getP1(), hl.getP2());
	}

	/**
	 * Build vertical line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static DianaLine makeVerticalLine(DianaPoint pt, double distance) {
		return new DianaLine(pt.x, pt.y, pt.x, pt.y + distance);
	}

	/**
	 * Build vertical line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static DianaLine makeVerticalLine(DianaPoint pt) {
		return makeVerticalLine(pt, 1);
	}

	/**
	 * Build horizontal line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static DianaLine makeHorizontalLine(DianaPoint pt, double distance) {
		return new DianaLine(pt.x, pt.y, pt.x + distance, pt.y);
	}

	/**
	 * Build horizontal line containing supplied point
	 * 
	 * @param pt
	 * @return
	 */
	public static DianaLine makeHorizontalLine(DianaPoint pt) {
		return makeHorizontalLine(pt, 1);
	}

	@Override
	public boolean contains(DianaPoint p) {
		return _containsPointIgnoreBounds(p);
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint p) {
		return getProjection(p);
	}

	/**
	 * Creates a new object of the same class and with the same contents as this object.
	 * 
	 * @return a clone of this instance.
	 * @exception OutOfMemoryError
	 *                if there is not enough memory.
	 * @see java.lang.Cloneable
	 * @since 1.2
	 */
	@Override
	public DianaLine clone() {
		return (DianaLine) super.clone();
	}

	@SuppressWarnings("unused")
	private static DianaArea _compute_hl_hl_Intersection(DianaHalfLine hl1, DianaHalfLine hl2) {
		DianaHalfPlane hp1 = hl1.getHalfPlane();
		DianaHalfPlane hp2 = hl2.getHalfPlane();

		System.out.println(">>>>>>>>>>>>>> HERE");

		DianaArea intersect = hp1.intersect(hp2);

		if (intersect instanceof DianaEmptyArea) {
			return new DianaEmptyArea();
		} else if (intersect instanceof DianaBand) {
			return new DianaSegment(hl1.getLimit(), hl2.getLimit());
		}

		else if (intersect.equals(hp1)) {
			return hl1.clone();
		} else if (intersect.equals(hp2)) {
			return hl2.clone();
		} else {
			LOGGER.warning("Unexpected intersection: " + intersect);
			return null;
		}
	}

	@Override
	protected DianaArea computeLineIntersection(DianaAbstractLine<?> line) {
		LOGGER.info("computeIntersection() between " + this + "\n and " + line + " overlap=" + overlap(line));
		if (overlap(line)) {
			return line.clone();
		} else if (isParallelTo(line)) {
			return new DianaEmptyArea();
		} else {
			DianaPoint returned;
			try {
				returned = getLineIntersection(line);
				if (containsPoint(returned) && line.containsPoint(returned)) {
					return returned;
				}
			} catch (ParallelLinesException e) {
				// cannot happen
			}
			return new DianaEmptyArea();
		}
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		if (!overlap(l)) {
			return false;
		}

		if (!(containsPoint(l.getP1()) && containsPoint(l.getP2()))) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {//AB same slope 
		return (getB()==0?31:((java.lang.Double.valueOf(getA()/getB()).hashCode())));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaLine) {
			return overlap((DianaLine) obj);
		}
		return false;
	}

	@Override
	public DianaLine transform(AffineTransform t) {
		return new DianaLine(getP1().transform(t), getP2().transform(t));
	}

	@Override
	public String toString() {
		return "DianaLine: (" + getP1() + "," + getP2() + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		g.useDefaultForegroundStyle();
		DianaRectangle bounds = g.getNodeNormalizedBounds();
		bounds.intersect(this).paint(g);
		// logger.info("Paint bounds="+bounds+": "+bounds.intersect(this));
	}

	@Override
	public DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new DianaPlane();
			}
		} else if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (isVertical()) {
				return clone();
			} else {
				return new DianaPlane();
			}
		} else if (orientation == SimplifiedCardinalDirection.EAST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new DianaPlane();
			}
		} else if (orientation == SimplifiedCardinalDirection.WEST) {
			if (isHorizontal()) {
				return clone();
			} else {
				return new DianaPlane();
			}
		}
		return null;
	}

	@Override
	public DianaLine getAnchorAreaFrom(SimplifiedCardinalDirection direction) {
		return clone();
	}

	/**
	 * This area is infinite, so always return false
	 */
	@Override
	public final boolean isFinite() {
		return false;
	}

	/**
	 * This area is infinite, so always return null
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
		return null;
	}

}

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
import java.awt.geom.Arc2D;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaBand;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaExclusiveOrArea;
import org.openflexo.diana.geom.area.DianaHalfBand;
import org.openflexo.diana.geom.area.DianaHalfLine;
import org.openflexo.diana.geom.area.DianaHalfPlane;
import org.openflexo.diana.geom.area.DianaIntersectionArea;
import org.openflexo.diana.geom.area.DianaSubstractionArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

@SuppressWarnings("serial")
public class DianaArc extends Arc2D.Double implements DianaShape<DianaArc> {

	private static final Logger logger = Logger.getLogger(DianaArc.class.getPackage().getName());

	public static enum ArcType {
		/**
		 * The closure type for an open arc with no path segments connecting the two ends of the arc segment.
		 */
		OPEN,
		/**
		 * The closure type for an arc closed by drawing a straight line segment from the start of the arc segment to the end of the arc
		 * segment.
		 */
		CHORD,
		/**
		 * The closure type for an arc closed by drawing straight line segments from the start of the arc segment to the center of the full
		 * ellipse and from that point to the end of the arc segment.
		 */
		PIE
	}

	public DianaArc() {
		super(ArcType.OPEN.ordinal());
	}

	public DianaArc(ArcType type) {
		super(type.ordinal());
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param start
	 *            angle in degree
	 * @param extent
	 *            angle in degree
	 * @param type
	 */
	public DianaArc(double x, double y, double w, double h, double start, double extent, ArcType type) {
		super(x, y, w, h, start, extent, type.ordinal());
	}

	/**
	 * 
	 * @param center
	 * @param size
	 * @param start
	 *            angle in degree
	 * @param extent
	 *            angle in degree
	 * @param type
	 */
	public DianaArc(DianaPoint center, DianaDimension size, double start, double extent, ArcType type) {
		this(center.x - size.width / 2, center.y - size.height / 2, size.width, size.height, start, extent, type);
	}

	public DianaArc(double x, double y, double w, double h, double start, double extent) {
		super(x, y, w, h, start, extent, ArcType.OPEN.ordinal());
		if (extent >= 360) {
			logger.warning("Arc created instead of ellips or circle (extent >=360) ");
		}
	}

	public DianaArc(DianaPoint center, DianaDimension size, double start, double extent) {
		this(center.x - size.width / 2, center.y - size.height / 2, size.width, size.height, start, extent, ArcType.OPEN);
	}

	public DianaArc(DianaPoint center, DianaDimension size) {
		this(center, size, 0, 360, ArcType.OPEN);
	}

	@Override
	public boolean getIsFilled() {
		return getDianaArcType() == ArcType.PIE;
	}

	@Override
	public void setIsFilled(boolean filled) {
		if (!filled) {
			setDianaArcType(ArcType.OPEN);
		}
		else {
			setDianaArcType(ArcType.PIE);
		}
	}

	@Override
	public double getX() {
		return super.getX();
	}

	public void setX(double x) {
		this.x = x;
	}

	@Override
	public double getY() {
		return super.getY();
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public double getWidth() {
		return super.getWidth();
	}

	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public double getHeight() {
		return super.getHeight();
	}

	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public DianaPoint getCenter() {
		return new DianaPoint(getCenterX(), getCenterY());
	}

	public DianaRectangle getDianaBounds() {
		return new DianaRectangle(getX(), getY(), getWidth(), getHeight(), Filling.FILLED);
	}

	public ArcType getDianaArcType() {
		if (getArcType() == ArcType.OPEN.ordinal()) {
			return ArcType.OPEN;
		}
		else if (getArcType() == ArcType.CHORD.ordinal()) {
			return ArcType.CHORD;
		}
		else if (getArcType() == ArcType.PIE.ordinal()) {
			return ArcType.PIE;
		}
		return null;
	}

	public void setDianaArcType(ArcType arcType) {
		if (arcType == ArcType.OPEN) {
			setArcType(ArcType.OPEN.ordinal());
		}
		else if (arcType == ArcType.CHORD) {
			setArcType(ArcType.CHORD.ordinal());
		}
		else if (arcType == ArcType.PIE) {
			setArcType(ArcType.PIE.ordinal());
		}
	}

	public static void main(String[] args) {
		// DianaSegment s = new DianaSegment(new DianaPoint(0,5),new DianaPoint(0,2));
		// DianaHalfLine hl = new DianaHalfLine(new DianaPoint(0,0),new DianaPoint(0,1));

		DianaSegment s = new DianaSegment(new DianaPoint(0.11718749999999999, 0.7722772277227723), new DianaPoint(0.1171875, 0.5742574257425743));
		DianaHalfLine hl = new DianaHalfLine(new DianaPoint(0.11718749999999999, 0.22277227722772278),
				new DianaPoint(0.11718749999999999, 1.2227722772277227));

		// DianaSegment s = new DianaSegment(new DianaPoint(0.1171875,0.7722772277227723),new DianaPoint(0.1171875,0.5742574257425743));
		// DianaHalfLine hl = new DianaHalfLine(new DianaPoint(0.1171875,0.22277227722772278),new DianaPoint(0.1171875,1.2227722772277227));

		System.out.println("intersect: " + s.intersect(hl));
		// System.out.println("intersect: "+hl.intersect(s));

		// DianaArc arc = new DianaArc(new DianaPoint(0, 0), new DianaDimension(100, 100));

		/*Area area1 = new Area(arc);
		Area area2 = new Area(line);
		
		System.out.println("area1="+area1);
		System.out.println("area2="+area2);
		
		Area intersect = new Area(area1);
		intersect.intersect(area2);
		
		System.out.println("intersect="+intersect);
		
		PathIterator i = intersect.getPathIterator(null);
		System.out.println("PathIterator="+i);
		
		while (!i.isDone()) {
			double[] coords = null;
			i.next();
			i.currentSegment(coords);
			System.out.println("coords="+coords);
		}*/

		/*DianaLine line = new DianaLine(new DianaPoint(-100,-100),new DianaPoint(100,100));
		System.out.println("1: "+arc.computeIntersection(line));
		
		DianaLine line2 = new DianaLine(new DianaPoint(50,0),new DianaPoint(50,100));
		System.out.println("2: "+arc.computeIntersection(line2));
		
		DianaLine line3 = new DianaLine(new DianaPoint(0,0),new DianaPoint(0,100));
		System.out.println("3: "+arc.computeIntersection(line3));
		
		DianaLine line4 = new DianaLine(new DianaPoint(-100,50),new DianaPoint(100,50));
		System.out.println("4: "+arc.computeIntersection(line4));*/

	}

	/**
	 * Returns the angular extent of the arc, expressed in radians, in the range -2.PI/2.PI
	 * 
	 * @return A double value that represents the angular extent of the arc in radians.
	 */

	public double getRadianAngleExtent() {
		double angdeg = getAngleExtent();

		// while (angdeg < -360) angdeg += 360;
		// while (angdeg > 360) angdeg -= 360;
		return Math.toRadians(angdeg);
	}

	/**
	 * Returns the starting angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the starting angle of the arc in degrees.
	 */
	public double getRadianAngleStart() {
		return normalizeDegAngle(getAngleStart());
	}

	/**
	 * Returns the ending angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the ending angle of the arc in degrees.
	 */
	public double getRadianAngleEnd() {
		return normalizeDegAngle(getAngleStart() + getAngleExtent());
	}

	/**
	 * Returns the supplied angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the normalized angle in radians
	 */
	public static double normalizeRadianAngle(double radianAngle) {
		while (radianAngle <= -Math.PI) {
			radianAngle += 2 * Math.PI;
		}
		while (radianAngle > Math.PI) {
			radianAngle -= 2 * Math.PI;
		}
		return radianAngle;
	}

	/**
	 * Returns the supplied angle of the arc expressed in radians, in the range -PI / PI
	 * 
	 * @return a double value that represents the normalized angle in radians
	 */
	public static double normalizeDegAngle(double degAngle) {
		return normalizeRadianAngle(Math.toRadians(degAngle));
	}

	/**
	 * Returns the starting point of the arc. This point is the intersection of the ray from the center defined by the starting angle and
	 * the elliptical boundary of the arc.
	 * 
	 * @return A <CODE>DianaPoint</CODE> object representing the x,y coordinates of the starting point of the arc.
	 */
	@Override
	public DianaPoint getStartPoint() {
		double angle = Math.toRadians(-getAngleStart());
		double x = getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth();
		double y = getY() + (Math.sin(angle) * 0.5 + 0.5) * getHeight();
		return new DianaPoint(x, y);
	}

	/**
	 * Returns the ending point of the arc. This point is the intersection of the ray from the center defined by the starting angle plus the
	 * angular extent of the arc and the elliptical boundary of the arc.
	 * 
	 * @return A <CODE>DianaPoint</CODE> object representing the x,y coordinates of the ending point of the arc.
	 */
	@Override
	public DianaPoint getEndPoint() {
		double angle = Math.toRadians(-getAngleStart() - getAngleExtent());
		double x = getX() + (Math.cos(angle) * 0.5 + 0.5) * getWidth();
		double y = getY() + (Math.sin(angle) * 0.5 + 0.5) * getHeight();
		return new DianaPoint(x, y);
	}

	/**
	 * Returns boolean indicating if supplied angle (expressed in radians) in contained in this arc
	 * 
	 * @param radianAngle
	 * @return
	 */
	public boolean includeAngle(double radianAngle) {
		while (radianAngle < -Math.PI - EPSILON) {
			radianAngle += 2 * Math.PI;
		}
		while (radianAngle > Math.PI + EPSILON) {
			radianAngle -= 2 * Math.PI;
		}

		double angle_start = getRadianAngleStart();
		double angle_extent = getRadianAngleExtent();

		if (angle_extent > 0) {
			return radianAngle >= angle_start - EPSILON && radianAngle <= angle_start + angle_extent + EPSILON
					|| radianAngle + 2 * Math.PI >= angle_start - EPSILON
							&& radianAngle + 2 * Math.PI <= angle_start + angle_extent + EPSILON
					|| radianAngle - 2 * Math.PI >= angle_start - EPSILON
							&& radianAngle - 2 * Math.PI <= angle_start + angle_extent + EPSILON;
		}
		else if (angle_extent < 0) {
			return radianAngle <= angle_start + EPSILON && radianAngle >= angle_start + angle_extent - EPSILON
					|| radianAngle + 2 * Math.PI <= angle_start + EPSILON
							&& radianAngle + 2 * Math.PI >= angle_start + angle_extent - EPSILON
					|| radianAngle - 2 * Math.PI <= angle_start + EPSILON
							&& radianAngle - 2 * Math.PI >= angle_start + angle_extent - EPSILON;
		}
		return false;

	}

	/*private DianaArea computeIntersection(DianaAbstractLine line)
	{
	
		double a,b,c;
	
		a = line.getA()*x+line.getB()*y+line.getB()*height/2.0+line.getC();
		b = line.getB()*height;
		c = line.getA()*x+line.getB()*y+line.getA()*width+line.getB()*height/2.0+line.getC();
	
	
		double delta = b*b-4*a*c;
	
		if (Math.abs(a) > EPSILON && Math.abs(delta) < EPSILON) {
			double t = -b/(2*a);
			double alpha = Math.atan(t)*2.0;
			// One solution, with angle alpha
			return new DianaPoint(width/2.0*Math.cos(alpha)+x+width/2.0,height/2.0*Math.sin(alpha)+y+height/2.0);
		}
	
		double alpha1; // in the range -PI / PI
		double alpha2; // in the range -PI / PI
	
		if (Math.abs(a) < EPSILON) {
			double t = -c/b;
			// Two solutions, with angle -alpha and PI-alpha
			alpha1 = -Math.atan(t)*2.0;
			alpha2 = Math.PI-Math.atan(t)*2.0;
		}
	
		else if (delta > 0) {
			double t1,t2;
			t1 = -(b+Math.sqrt(delta))/(2.0*a);
			t2 = -(b-Math.sqrt(delta))/(2.0*a);
			alpha1 = -Math.atan(t1)*2.0; // in the range -PI / PI
			alpha2 = -Math.atan(t2)*2.0; // in the range -PI / PI
			// Two solutions, with angle alpha1 and alpha2
		}
		else {
			// No solution
			return new DianaEmptyArea();
		}
	
		DianaPoint p1 = new DianaPoint(width/2.0*Math.cos(-alpha1)+x+width/2.0,height/2.0*Math.sin(-alpha1)+y+height/2.0);
		DianaPoint p2 = new DianaPoint(width/2.0*Math.cos(-alpha2)+x+width/2.0,height/2.0*Math.sin(-alpha2)+y+height/2.0);
		boolean includeP1 = includeAngle(alpha1);
		boolean includeP2 = includeAngle(alpha2);
		//logger.info("Found angle "+Math.toDegrees(alpha1)+" "+(includeP1?"INCLUDED":"- not included - "));
		//logger.info("Found angle "+Math.toDegrees(alpha2)+" "+(includeP2?"INCLUDED":"- not included - "));
		if (includeP1 && includeP2) {
			if (getDianaArcType() == ArcType.OPEN) {
				return DianaUnionArea.makeUnion(p1,p2);
			}
			else {
				return new DianaSegment(p1,p2);
			}				
		}
		DianaArea pp1 = (new DianaSegment(getCenter(),getStartPoint())).intersect(line);
		DianaArea pp2 = (new DianaSegment(getCenter(),getEndPoint())).intersect(line);
		if (includeP1) {
			if (getDianaArcType() == ArcType.OPEN) {
				return p1;
			}
			else {
				if (pp1 instanceof DianaPoint) 
					return new DianaSegment((DianaPoint)pp1,p1);
				if (pp2 instanceof DianaPoint) 
					return new DianaSegment((DianaPoint)pp2,p1);
				logger.warning("Unexpected situation encoutered here: arc="+this+" line="+line+" pp1="+pp1+" pp2="+pp2);
			}				
		}
		else if (includeP2) {
			if (getDianaArcType() == ArcType.OPEN) {
				return p2;
			}
			else {
				if (pp1 instanceof DianaPoint) 
					return new DianaSegment((DianaPoint)pp1,p2);
				if (pp2 instanceof DianaPoint) 
					return new DianaSegment((DianaPoint)pp2,p2);
				logger.warning("Unexpected situation encoutered here: arc="+this+" line="+line+" pp1="+pp1+" pp2="+pp2);
			}				
		}
		else {
			if (getDianaArcType() == ArcType.OPEN
					|| getDianaArcType() == ArcType.CHORD) return new DianaEmptyArea();
			// ArcType is ArcType.PIE
			if (pp1 instanceof DianaPoint && pp2 instanceof DianaPoint) {
				if (pp1.equals(pp2)) {
					return pp1;
				}
				else {
					// NOTE:
					// if is filled (not implemented yet)
					// return segment : return new DianaSegment((DianaPoint)pp1,(DianaPoint)pp2);
					return DianaUnionArea.makeUnion(pp1,pp2);
				}
			}
		}
	
		return new DianaEmptyArea();
	}*/

	private static class LineIntersectionResult {
		private static enum SolutionType {
			NO_SOLUTION, ONE_SOLUTION, TWO_SOLUTIONS
		}

		SolutionType solutionType;
		double alpha1; // in the range -PI / PI
		double alpha2; // in the range -PI / PI

		@Override
		public String toString() {
			return "LineIntersectionResult solutionType=" + solutionType + " alpha1=" + alpha1 + " alpha2=" + alpha2;
		}
	}

	private LineIntersectionResult _computeIntersection(DianaAbstractLine<?> line) {
		LineIntersectionResult result = new LineIntersectionResult();
		double a, b, c;

		a = line.getA() * x + line.getB() * y + line.getB() * height / 2.0 + line.getC();
		b = line.getB() * height;
		c = line.getA() * x + line.getB() * y + line.getA() * width + line.getB() * height / 2.0 + line.getC();

		double delta = b * b - 4 * a * c;

		if (Math.abs(a) > EPSILON && Math.abs(delta) < EPSILON) {
			double t = -b / (2 * a);
			double alpha = Math.atan(t) * 2.0;
			// One solution, with angle alpha
			result.solutionType = org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.ONE_SOLUTION;
			result.alpha1 = alpha;
			result.alpha2 = alpha;
			return result;
		}

		if (Math.abs(a) < EPSILON) {
			double t = -c / b;
			// Two solutions, with angle -alpha and PI-alpha
			result.solutionType = org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS;
			result.alpha1 = -Math.atan(t) * 2.0;
			result.alpha2 = Math.PI - Math.atan(t) * 2.0;
			return result;
		}

		else if (delta > 0) {
			double t1, t2;
			t1 = -(b + Math.sqrt(delta)) / (2.0 * a);
			t2 = -(b - Math.sqrt(delta)) / (2.0 * a);
			// Two solutions, with angle alpha1 and alpha2
			result.solutionType = org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS;
			result.alpha1 = -Math.atan(t1) * 2.0; // in the range -PI / PI
			result.alpha2 = -Math.atan(t2) * 2.0; // in the range -PI / PI
			return result;
		}
		else {
			// No solution
			result.solutionType = org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.NO_SOLUTION;
			return result;
		}
	}

	private DianaArea computeIntersection(DianaAbstractLine<?> line) {
		DianaArea a = computeLineIntersection(new DianaLine(line.getP1(), line.getP2()));
		// logger.info("computeLineIntersection(): "+a+" line="+line+" intersect="+(a.intersect(line)));
		return a.intersect(line);
	}

	private DianaArea computeLineIntersection(DianaLine line) {
		LineIntersectionResult result = _computeIntersection(line);

		if (result.solutionType == org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.NO_SOLUTION) {
			return new DianaEmptyArea();
		}

		else if (result.solutionType == org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.ONE_SOLUTION) {
			// One solution, with angle alpha
			return new DianaPoint(width / 2.0 * Math.cos(result.alpha1) + x + width / 2.0,
					height / 2.0 * Math.sin(result.alpha1) + y + height / 2.0);
		}

		else if (result.solutionType == org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS) {
			DianaPoint p1 = new DianaPoint(width / 2.0 * Math.cos(-result.alpha1) + x + width / 2.0,
					height / 2.0 * Math.sin(-result.alpha1) + y + height / 2.0);
			DianaPoint p2 = new DianaPoint(width / 2.0 * Math.cos(-result.alpha2) + x + width / 2.0,
					height / 2.0 * Math.sin(-result.alpha2) + y + height / 2.0);
			boolean includeP1 = includeAngle(result.alpha1);
			boolean includeP2 = includeAngle(result.alpha2);
			// logger.info("Found angle "+Math.toDegrees(alpha1)+" "+(includeP1?"INCLUDED":"- not included - "));
			// logger.info("Found angle "+Math.toDegrees(alpha2)+" "+(includeP2?"INCLUDED":"- not included - "));
			if (includeP1 && includeP2) {
				if (getDianaArcType() == ArcType.OPEN) {
					return DianaUnionArea.makeUnion(p1, p2);
				}
				else {
					return new DianaSegment(p1, p2);
				}
			}
			DianaArea pp1 = new DianaSegment(getCenter(), getStartPoint()).intersect(line);
			DianaArea pp2 = new DianaSegment(getCenter(), getEndPoint()).intersect(line);
			if (pp1 instanceof DianaEmptyArea && pp2 instanceof DianaEmptyArea) {
				// Nothing
				return new DianaEmptyArea();
			}
			if (includeP1) {
				if (getDianaArcType() == ArcType.OPEN) {
					return p1;
				}
				else {
					if (pp1 instanceof DianaSegment && pp2 instanceof DianaSegment) {
						// They are aligned
						return DianaUnionArea.makeUnion(pp1, pp2);
					}
					else if (pp1 instanceof DianaSegment) {
						return pp1;
					}
					else if (pp2 instanceof DianaSegment) {
						return pp2;
					}
					else if (pp1 instanceof DianaPoint) {
						return new DianaSegment((DianaPoint) pp1, p1);
					}
					else if (pp2 instanceof DianaPoint) {
						return new DianaSegment((DianaPoint) pp2, p1);
					}
					logger.warning("Unexpected situation encoutered here: arc=" + this + " line=" + line + " pp1=" + pp1 + " pp2=" + pp2
							+ " p1=" + p1 + " p2=" + p2);
				}
			}
			else if (includeP2) {
				if (getDianaArcType() == ArcType.OPEN) {
					return p2;
				}
				else {
					if (pp1 instanceof DianaSegment && pp2 instanceof DianaSegment) {
						// They are aligned
						return DianaUnionArea.makeUnion(pp1, pp2);
					}
					else if (pp1 instanceof DianaSegment) {
						return pp1;
					}
					else if (pp2 instanceof DianaSegment) {
						return pp2;
					}
					else if (pp1 instanceof DianaPoint) {
						return new DianaSegment((DianaPoint) pp1, p2);
					}
					if (pp2 instanceof DianaPoint) {
						return new DianaSegment((DianaPoint) pp2, p2);
					}
					logger.warning("Unexpected situation encoutered here: arc=" + this + " line=" + line + " pp1=" + pp1 + " pp2=" + pp2);
				}
			}
			else {
				if (getDianaArcType() == ArcType.OPEN || getDianaArcType() == ArcType.CHORD) {
					return new DianaEmptyArea();
				}
				// ArcType is ArcType.PIE
				if (pp1 instanceof DianaPoint && pp2 instanceof DianaPoint) {
					if (pp1.equals(pp2)) {
						return pp1;
					}
					else {
						// NOTE:
						// if is filled (not implemented yet)
						// return segment : return new DianaSegment((DianaPoint)pp1,(DianaPoint)pp2);
						return DianaUnionArea.makeUnion(pp1, pp2);
					}
				}
			}

			logger.warning("Unexpected situation here " + result);
			logger.warning("p1=" + p1);
			logger.warning("p2=" + p2);
			logger.warning("includeP1=" + includeP1);
			logger.warning("includeP2=" + includeP2);
			logger.warning("pp1=" + pp1);
			logger.warning("pp2=" + pp2);

		}

		logger.warning("Unexpected situation here " + result);
		return new DianaEmptyArea();
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
	public DianaArc clone() {
		return (DianaArc) super.clone();
	}

	@Override
	public DianaPoint getNearestPoint(DianaPoint aPoint) {
		if (getIsFilled() && containsPoint(aPoint)) {
			return aPoint.clone();
		}

		return nearestOutlinePoint(aPoint);
	}

	@Override
	public DianaPoint nearestOutlinePoint(DianaPoint aPoint) {

		double angle = angleForPoint(aPoint);
		if (!includeAngle(angle)) {
			// System.out.println("Hop, "+aPoint+" est en dehors de "+this+", je choisis entre: ");
			// System.out.println("angle: "+Math.toDegrees(getRadianAngleStart())+" is: "+getPointAtRadianAngle(getRadianAngleStart()));
			// System.out.println("angle2: "+Math.toDegrees(getRadianAngleEnd())+" is: "+getPointAtRadianAngle(getRadianAngleEnd()));
			DianaPoint returned = DianaPoint.getNearestPoint(aPoint, getPointAtRadianAngle(getRadianAngleStart()),
					getPointAtRadianAngle(getRadianAngleEnd()));
			if (!containsPoint(returned)) {
				logger.warning("Returning point not beeing in area " + this);
			}
			return returned;

			// System.out.println("C'est: "+returned);
			// return returned;
		}

		DianaArea intersection = computeIntersection(new DianaLine(aPoint, getCenter()));

		// System.out.println("aPoint="+aPoint);
		// System.out.println("Intersection="+intersection);
		// System.out.println("angle: "+Math.toDegrees(angle)+" is: "+getPointAtRadianAngle(angle));

		Vector<DianaPoint> pts = new Vector<>();
		if (intersection instanceof DianaPoint) {
			pts.add((DianaPoint) intersection);
		}
		else if (intersection instanceof DianaSegment) {
			pts.add(((DianaSegment) intersection).getP1());
			pts.add(((DianaSegment) intersection).getP2());
		}
		else if (intersection instanceof DianaUnionArea) {
			for (DianaArea a : ((DianaUnionArea) intersection).getObjects()) {
				if (a instanceof DianaPoint) {
					pts.add((DianaPoint) a);
				}
			}
		}
		if (pts.size() > 0) {
			DianaPoint returned = null;
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			for (DianaPoint p : pts) {
				double currentDistanceSq = DianaPoint.distanceSq(p, aPoint);
				if (currentDistanceSq < minimalDistanceSq) {
					returned = p.clone();
					minimalDistanceSq = currentDistanceSq;
				}
			}
			return returned;
		}

		if (getDianaArcType() == ArcType.OPEN || getDianaArcType() == ArcType.CHORD) {
			if (DianaPoint.distanceSq(aPoint, getStartPoint()) < DianaPoint.distanceSq(aPoint, getEndPoint())) {
				return getStartPoint();
			}
			else {
				return getEndPoint();
			}
		}

		// NOTE: CHORD not well handled here: also handle segment [getStartPoint();getEndPoint()]

		else { // PIE
			DianaSegment s1 = new DianaSegment(getCenter(), getStartPoint());
			DianaSegment s2 = new DianaSegment(getCenter(), getEndPoint());
			DianaPoint pp1 = s1.getNearestPointOnSegment(aPoint);
			DianaPoint pp2 = s2.getNearestPointOnSegment(aPoint);
			if (DianaPoint.distanceSq(aPoint, pp1) < DianaPoint.distanceSq(aPoint, pp2)) {
				return pp1;
			}
			else {
				return pp2;
			}
		}
	}

	/**
	 * Return nearest point from point "from" following supplied orientation
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param from
	 *            point from which we are coming to area
	 * @param orientation
	 *            orientation we are coming from
	 * @return
	 */
	@Override
	public DianaPoint nearestPointFrom(DianaPoint from, SimplifiedCardinalDirection orientation) {
		// logger.info("Request for nearestPointFrom: "+from+" orientation="+orientation+" for arc "+this);

		DianaHalfLine hl = DianaHalfLine.makeHalfLine(from, orientation);

		DianaArea intersect = intersect(hl);

		// logger.info("hl="+hl);
		// logger.info("intersect="+intersect);

		if (intersect instanceof DianaEmptyArea) {
			return null;
		}

		if (intersect instanceof DianaPoint) {
			return (DianaPoint) intersect;
		}

		if (intersect instanceof DianaSegment) {
			DianaPoint p1, p2;
			p1 = ((DianaSegment) intersect).getP1();
			p2 = ((DianaSegment) intersect).getP2();
			if (DianaPoint.distanceSq(from, p1) < DianaPoint.distanceSq(from, p2)) {
				return p1;
			}
			else {
				return p2;
			}
		}

		if (intersect instanceof DianaUnionArea) {
			DianaPoint returned = null;
			double minimalDistanceSq = java.lang.Double.POSITIVE_INFINITY;
			for (DianaArea o : ((DianaUnionArea) intersect).getObjects()) {
				if (o instanceof DianaPoint) {
					double distSq = DianaPoint.distanceSq(from, (DianaPoint) o);
					if (distSq < minimalDistanceSq) {
						returned = (DianaPoint) o;
						minimalDistanceSq = distSq;
					}
				}
			}
			return returned;
		}

		return null;
	}

	@Override
	public DianaArea exclusiveOr(DianaArea area) {
		return new DianaExclusiveOrArea(this, area);
	}

	@Override
	public DianaArea intersect(DianaArea area) {
		if (area.containsArea(this)) {
			return this.clone();
		}
		if (containsArea(area)) {
			return area.clone();
		}
		if (area instanceof DianaAbstractLine) {
			return computeIntersection((DianaAbstractLine<?>) area);
		}
		if (area instanceof DianaArc && ((DianaArc) area).overlap(this)) {
			return computeArcIntersection((DianaArc) area);
		}
		if (area instanceof DianaBand || area instanceof DianaHalfBand) {
			// TODO please really implement this
			DianaRectangle boundingBox = getBoundingBox();
			boundingBox.setIsFilled(true);
			DianaArea boundsIntersect = boundingBox.intersect(area);
			if (boundsIntersect instanceof DianaEmptyArea) {
				return new DianaEmptyArea();
			}
			DianaArea returned = intersect(boundsIntersect);
			if (returned instanceof DianaIntersectionArea) {
				// Cannot do better, sorry
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Unable to compute intersection between " + this + " and " + area);
				}
			}
			return returned;
		}
		if (area instanceof DianaHalfPlane) {
			DianaHalfPlane hp = (DianaHalfPlane) area;
			DianaLine line = hp.line;
			DianaArea intersect = intersect(line);
			DianaPoint p1 = null;
			DianaPoint p2 = null;
			if (intersect instanceof DianaUnionArea && ((DianaUnionArea) intersect).isUnionOfPoints()
					&& ((DianaUnionArea) intersect).getObjects().size() > 1) {
				p1 = (DianaPoint) ((DianaUnionArea) intersect).getObjects().get(0);
				p2 = (DianaPoint) ((DianaUnionArea) intersect).getObjects().get(1);
			}
			else if (intersect instanceof DianaSegment) {
				p1 = ((DianaSegment) intersect).getP1();
				p2 = ((DianaSegment) intersect).getP2();
			}
			else if (intersect instanceof DianaPoint) {
				return intersect;
			}
			if (p1 != null && p2 != null) {
				ArcType type = getDianaArcType();
				if (type == ArcType.PIE) {
					type = line.contains(getCenter()) ? ArcType.PIE : ArcType.CHORD;
				}
				double startAngle = Math.toDegrees(angleForPoint(p1));
				double endAngle = Math.toDegrees(angleForPoint(p2));
				DianaArc arc1 = new DianaArc(x, y, width, height, startAngle, endAngle - startAngle + (endAngle >= startAngle ? 0 : 360), type);
				DianaArc arc2 = new DianaArc(x, y, width, height, endAngle, startAngle - endAngle + (startAngle >= endAngle ? 0 : 360), type);
				if (hp.containsPoint(arc1.getMiddle())) {
					return arc1;
				}
				else {
					return arc2;
				}
			}
		}

		DianaIntersectionArea returned = new DianaIntersectionArea(this, area);
		if (returned.isDevelopable()) {
			return returned.makeDevelopped();
		}
		else {
			return returned;
		}
	}

	@Override
	public DianaArea substract(DianaArea area, boolean isStrict) {
		return new DianaSubstractionArea(this, area, isStrict);
	}

	@Override
	public DianaArea union(DianaArea area) {
		if (containsArea(area)) {
			return clone();
		}
		if (area.containsArea(this)) {
			return area.clone();
		}

		if (area instanceof DianaArc && ((DianaArc) area).overlap(this)) {
			return computeArcUnion((DianaArc) area);
		}
		return new DianaUnionArea(this, area);
	}

	@Override
	public boolean containsPoint(DianaPoint p) {
		if (!getDianaBounds().containsPoint(p)) {
			return false;
		}

		double radianAngle = angleForPoint(p);

		if (!includeAngle(radianAngle)) {
			return false;
		}

		DianaPoint outlinePoint = getPointAtRadianAngle(radianAngle);

		if (getDianaArcType() == ArcType.OPEN) {
			return outlinePoint.equals(p);
		}

		else if (new DianaSegment(getCenter(), outlinePoint).containsPoint(p)) {
			if (getDianaArcType() == ArcType.PIE) {
				return true;
			}
			else if (getDianaArcType() == ArcType.CHORD) {
				return !new DianaPolygon(Filling.FILLED, getCenter(), getStartPoint(), getEndPoint()).contains(p);
			}
		}

		return false;

		// DianaArea intersection = computeIntersection(new DianaLine(getCenter(),p));
		// System.out.println("p= "+p+" intersection="+intersection+" intersection.containsPoint(p)="+intersection.containsPoint(p));
		// return (intersection.containsPoint(p));
	}

	@Override
	public boolean containsLine(DianaAbstractLine<?> l) {
		return containsPoint(l.getP1()) && containsPoint(l.getP2()) && l instanceof DianaSegment;
	}

	@Override
	public boolean containsArea(DianaArea a) {
		if (a instanceof DianaArc && ((DianaArc) a).overlap(this)) {
			DianaArea arc1 = _equivalentForArcIn1D(this, false);
			DianaArea arc2 = _equivalentForArcIn1D((DianaArc) a, false);
			if (arc1.containsArea(arc2)) {
				return true;
			}
			arc1 = _equivalentForArcIn1D(this, true);
			arc2 = _equivalentForArcIn1D((DianaArc) a, true);
			if (arc1.containsArea(arc2)) {
				return true;
			}
			return false;
		}
		if (a instanceof DianaPoint) {
			return containsPoint((DianaPoint) a);
		}
		if (a instanceof DianaSegment) {
			return containsPoint(((DianaSegment) a).getP1()) && containsPoint(((DianaSegment) a).getP2());
		}
		if (a instanceof DianaShape) {
			return AreaComputation.isShapeContainedInArea((DianaShape<?>) a, this);
		}
		return false;
	}

	@Override
	public DianaArc transform(AffineTransform t) {
		// logger.info("Not well implemented, do it later");

		// TODO: this implementation is not correct if AffineTransform contains rotation

		DianaPoint p1 = new DianaPoint(getX(), getY()).transform(t);
		DianaPoint p2 = new DianaPoint(getX() + getWidth(), getY() + getHeight()).transform(t);

		// TODO: if transformation contains a rotation, change angle start, with and heigth are not correct either
		return new DianaArc(Math.min(p1.x, p2.x), Math.min(p1.y, p2.y), Math.abs(p1.x - p2.x), Math.abs(p1.y - p2.y), getAngleStart(),
				getAngleExtent(), getDianaArcType());
	}

	@Override
	public List<DianaPoint> getControlPoints() {
		Vector<DianaPoint> returned = new Vector<>();
		returned.add(getCenter());
		returned.add(getStartPoint());
		returned.add(getEndPoint());
		/*returned.add(new DianaPoint(x+width/2.0,y));
		returned.add(new DianaPoint(x,y+height/2.0));
		returned.add(new DianaPoint(x+width/2.0,y+height));
		returned.add(new DianaPoint(x+width,y+height/2.0));*/

		return returned;
	}

	@Override
	public void paint(AbstractDianaGraphics g) {
		if (getDianaArcType() == ArcType.CHORD || getDianaArcType() == ArcType.PIE) {
			g.useDefaultBackgroundStyle();
			g.fillArc(getX(), getY(), getWidth(), getHeight(), getAngleStart(), getAngleExtent(), getDianaArcType() == ArcType.CHORD);
		}
		g.useDefaultForegroundStyle();
		/*Stroke defaultForegroundStroke = null;
		if (g.getGraphicalRepresentation().getSpecificStroke() != null) {
			defaultForegroundStroke = g.getGraphics().getStroke();
			g.getGraphics().setStroke(g.getGraphicalRepresentation().getSpecificStroke());
		}*/
		g.drawArc(getX(), getY(), getWidth(), getHeight(), getAngleStart(), getAngleExtent());
		/*if (defaultForegroundStroke != null) {
			g.getGraphics().setStroke(defaultForegroundStroke);
		}*/
	}

	@Override
	public String toString() {
		return "DianaArc: (" + x + "," + y + "," + width + "," + height + "," + getAngleStart() + "," + getAngleExtent() + " type="
				+ getDianaArcType() + ")";
	}

	@Override
	public String getStringRepresentation() {
		return toString();
	}

	/*public DianaRectangle getBoundingBox()
	{
		return new DianaRectangle(x,y,width,height,Filling.FILLED);
	}*/

	@Override
	public DianaRectangle getBoundingBox() {
		double minX = java.lang.Double.POSITIVE_INFINITY;
		double minY = java.lang.Double.POSITIVE_INFINITY;
		double maxX = java.lang.Double.NEGATIVE_INFINITY;
		double maxY = java.lang.Double.NEGATIVE_INFINITY;

		Vector<java.lang.Double> angles = new Vector<>();
		if (includeAngle(0)) {
			angles.add(0.0);
		}
		if (includeAngle(Math.PI / 2)) {
			angles.add(Math.PI / 2);
		}
		if (includeAngle(Math.PI)) {
			angles.add(Math.PI);
		}
		if (includeAngle(3 * Math.PI / 2)) {
			angles.add(3 * Math.PI / 2);
		}
		angles.add(getRadianAngleStart());
		angles.add(getRadianAngleStart() + getRadianAngleExtent());
		for (java.lang.Double angle : angles) {
			DianaPoint p = getPointAtRadianAngle(angle);
			if (p.x < minX) {
				minX = p.x;
			}
			if (p.x > maxX) {
				maxX = p.x;
			}
			if (p.y < minY) {
				minY = p.y;
			}
			if (p.y > maxY) {
				maxY = p.y;
			}
		}

		return new DianaRectangle(new DianaPoint(minX, minY), new DianaPoint(maxX, maxY), Filling.FILLED);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DianaArc) {
			DianaArc p = (DianaArc) obj;
			if (getDianaArcType() != p.getDianaArcType()) {
				return false;
			}
			return Math.abs(getX() - p.getX()) <= EPSILON && Math.abs(getY() - p.getY()) <= EPSILON
					&& Math.abs(getWidth() - p.getWidth()) <= EPSILON && Math.abs(getHeight() - p.getHeight()) <= EPSILON
					&& Math.abs(normalizeDegAngle(getAngleStart()) - normalizeDegAngle(p.getAngleStart())) <= EPSILON
					&& Math.abs(getAngleExtent() - p.getAngleExtent()) <= EPSILON;
		}
		return super.equals(obj);
	}

	public boolean overlap(DianaArc arc) {
		if (getDianaArcType() != arc.getDianaArcType()) {
			return false;
		}
		return Math.abs(getX() - arc.getX()) <= EPSILON && Math.abs(getY() - arc.getY()) <= EPSILON
				&& Math.abs(getWidth() - arc.getWidth()) <= EPSILON && Math.abs(getHeight() - arc.getHeight()) <= EPSILON;
	}

	@Override
	public final DianaArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation) {
		if (orientation == null) {
			return new DianaEmptyArea();
		}
		DianaRectangle boundingBox;
		DianaArea anchorArea = getAnchorAreaFrom(orientation);
		if (anchorArea instanceof DianaArc) {
			boundingBox = ((DianaArc) anchorArea).getBoundingBox();
		}
		else {
			boundingBox = getBoundingBox();
		}

		// System.out.println("Orientation: "+orientation+" Bounding box for "+anchorArea+" is "+boundingBox);

		DianaArea hb = null;
		switch (orientation) {
			case EAST:
				hb = boundingBox.getWest().getOrthogonalPerspectiveArea(orientation);
				break;
			case WEST:
				hb = boundingBox.getEast().getOrthogonalPerspectiveArea(orientation);
				break;
			case NORTH:
				hb = boundingBox.getSouth().getOrthogonalPerspectiveArea(orientation);
				break;
			case SOUTH:
				hb = boundingBox.getNorth().getOrthogonalPerspectiveArea(orientation);
				break;
			default:
				return new DianaEmptyArea();
		}
		DianaArc filledArc = clone();
		filledArc.setIsFilled(true);
		return new DianaSubstractionArea(hb, filledArc, false);
	}

	@Override
	public final DianaArea getAnchorAreaFrom(SimplifiedCardinalDirection orientation) {
		DianaArc returned;
		switch (orientation) {
			case EAST:
				returned = new DianaArc(x, y, width, height, -90, 180, ArcType.OPEN);
				break;
			case WEST:
				returned = new DianaArc(x, y, width, height, 90, 180, ArcType.OPEN);
				break;
			case NORTH:
				returned = new DianaArc(x, y, width, height, 0, 180, ArcType.OPEN);
				break;
			case SOUTH:
				returned = new DianaArc(x, y, width, height, -180, 180, ArcType.OPEN);
				break;
			default:
				return null;
		}

		return computeArcIntersection(returned);

	}

	/**
	 * Compute angle extent given start and end angle
	 * 
	 * @param startAngle
	 *            , expressed in radians
	 * @param endAngle
	 *            , expressed in radians
	 * @return extent in radians
	 */
	public static double angleExtent(double startAngle, double endAngle) {
		double start = normalizeRadianAngle(startAngle);
		double end = normalizeRadianAngle(endAngle);
		if (start <= end) {
			return end - start;
		}
		else {
			return end + 2 * Math.PI - start;
		}
	}

	/**
	 * Compute an equivalent for an arc in the 1-D model Angles are normalized and a segment (or a union of 2 segments mapping 2.PI-modulo
	 * is retrieved )
	 * 
	 * @param anArc
	 * @return
	 */
	private static DianaArea _equivalentForArcIn1D(DianaArc anArc, boolean forIntersection) {
		double startAngle;
		double endAngle;

		if (anArc instanceof DianaEllips || anArc.getRadianAngleExtent() >= 2 * Math.PI || anArc.getRadianAngleExtent() <= -2 * Math.PI) {
			return new DianaUnionArea(new DianaSegment(new DianaPoint(-Math.PI, 0), new DianaPoint(Math.PI, 0)),
					new DianaSegment(new DianaPoint(Math.PI, 0), new DianaPoint(3 * Math.PI, 0)));
		}

		if (anArc.getAngleExtent() >= 0) {
			startAngle = anArc.getRadianAngleStart();
			endAngle = normalizeRadianAngle(anArc.getRadianAngleStart() + anArc.getRadianAngleExtent());
		}
		else {
			endAngle = anArc.getRadianAngleStart();
			startAngle = normalizeRadianAngle(anArc.getRadianAngleStart() + anArc.getRadianAngleExtent());
		}

		if (endAngle < startAngle) {
			endAngle += 2 * Math.PI;
		}

		DianaSegment segment = new DianaSegment(new DianaPoint(startAngle, 0), new DianaPoint(endAngle, 0));

		if (anArc.includeAngle(Math.PI)) {
			if (forIntersection) {
				return new DianaUnionArea(segment,
						new DianaSegment(new DianaPoint(startAngle + 2 * Math.PI, 0), new DianaPoint(endAngle + 2 * Math.PI, 0)));
			}
		}

		return segment;
	}

	/**
	 * Compute an equivalent for an area in the 1-D model to arc model
	 * 
	 * @param anArc
	 * @return
	 */
	private DianaArea _equivalentToArcIn1D(DianaArea anArea) {
		if (anArea instanceof DianaEmptyArea) {
			return new DianaEmptyArea();
		}
		else if (anArea instanceof DianaSegment) {
			DianaSegment s = (DianaSegment) anArea;
			if (s.getP2().x >= s.getP1().x + 2 * Math.PI || s.getP2().x <= s.getP1().x - 2 * Math.PI) {
				return new DianaEllips(x, y, width, height, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
			}
			return new DianaArc(x, y, width, height, Math.toDegrees(normalizeRadianAngle(s.getP1().x)),
					Math.toDegrees(s.getP2().x - s.getP1().x), getDianaArcType());
		}
		else if (anArea instanceof DianaPoint) {
			DianaPoint p = (DianaPoint) anArea;
			if (getIsFilled()) {
				return new DianaSegment(getCenter(), getPointAtRadianAngle(p.x));
			}
			else {
				return getPointAtRadianAngle(p.x);
			}
		}
		else if (anArea instanceof DianaUnionArea) {
			DianaUnionArea u = (DianaUnionArea) anArea;
			// if (u.isUnionOfSegments() || u.isUnionOfPoints()) {
			Vector<DianaArea> returned = new Vector<>();
			for (DianaArea a : u.getObjects()) {
				DianaArea newArea = _equivalentToArcIn1D(a);
				if (!returned.contains(newArea)) {
					returned.add(newArea);
				}
			}
			if (returned.size() == 1) {
				return returned.firstElement();
			}
			if (returned.size() == 2 && returned.firstElement() instanceof DianaArc && returned.elementAt(1) instanceof DianaArc) {
				// There is still a case where there can be 2 adjascent arcs
				// At point corresponding to -180 degree
				DianaArc arc1 = (DianaArc) returned.firstElement();
				DianaArc arc2 = (DianaArc) returned.elementAt(1);
				boolean arc1StartFromPI = GeomUtils.doubleEquals(arc1.getRadianAngleStart(), Math.PI)
						|| GeomUtils.doubleEquals(arc1.getRadianAngleStart(), -Math.PI);
				boolean arc1EndToPI = GeomUtils.doubleEquals(arc1.getRadianAngleEnd(), Math.PI)
						|| GeomUtils.doubleEquals(arc1.getRadianAngleEnd(), -Math.PI);
				boolean arc2StartFromPI = GeomUtils.doubleEquals(arc2.getRadianAngleStart(), Math.PI)
						|| GeomUtils.doubleEquals(arc2.getRadianAngleStart(), -Math.PI);
				boolean arc2EndToPI = GeomUtils.doubleEquals(arc2.getRadianAngleEnd(), Math.PI)
						|| GeomUtils.doubleEquals(arc2.getRadianAngleEnd(), -Math.PI);
				if (arc1EndToPI && arc2StartFromPI) {
					return _equivalentToArcIn1D(new DianaSegment(new DianaPoint(arc1.getRadianAngleStart(), 0),
							new DianaPoint(arc2.getRadianAngleEnd() + 2 * Math.PI, 0)));
				}
				if (arc2EndToPI && arc1StartFromPI) {
					return _equivalentToArcIn1D(new DianaSegment(new DianaPoint(arc2.getRadianAngleStart(), 0),
							new DianaPoint(arc1.getRadianAngleEnd() + 2 * Math.PI, 0)));
				}
			}
			DianaUnionArea union = new DianaUnionArea(returned);

			if (returned.size() == 2 && returned.get(0) instanceof DianaArc && returned.get(1) instanceof DianaPoint) {
				new Exception("------------ Ca vient de la ce truc bizarre !!!").printStackTrace();
				logger.info("Area=" + anArea);
			}

			if (union.getObjects().size() == 1) {
				return union.getObjects().firstElement();
			}
			return union;
			// }
		}
		logger.warning("Unexpected result: " + anArea);
		return new DianaEmptyArea();

	}

	/**
	 * Compute arc union, asserting that this arc and supplied arc differs only because of their start angles and/or angle extent
	 * 
	 * @param anArc
	 * @return
	 */
	protected DianaArea computeArcUnion(DianaArc anArc) {
		if (anArc instanceof DianaEllips || anArc.getRadianAngleExtent() >= 2 * Math.PI || anArc.getRadianAngleExtent() <= -2 * Math.PI
				|| this instanceof DianaEllips || getRadianAngleExtent() >= 2 * Math.PI || getRadianAngleExtent() <= -2 * Math.PI) {
			// Trivial case
			return new DianaEllips(x, y, width, height, getIsFilled() ? Filling.FILLED : Filling.NOT_FILLED);
		}

		DianaArea equ1 = _equivalentForArcIn1D(this, false);
		DianaArea equ2 = _equivalentForArcIn1D(anArc, false);

		DianaArea union = equ1.union(equ2);

		/*System.out.println("arc1="+this);
		System.out.println("arc2="+anArc);
		System.out.println("equ1="+equ1);
		System.out.println("equ2="+equ2);
		System.out.println("union="+union);*/

		return _equivalentToArcIn1D(union);
	}

	/**
	 * Compute arc intersection, asserting that this arc and supplied arc differs only because of their start angles and/or angle extent
	 * 
	 * @param anArc
	 * @return
	 */
	protected DianaArea computeArcIntersection(DianaArc anArc) {
		if (anArc instanceof DianaEllips || anArc.getRadianAngleExtent() >= 2 * Math.PI || anArc.getRadianAngleExtent() <= -2 * Math.PI) {
			// Trivial case
			return clone();
		}
		if (this instanceof DianaEllips || getRadianAngleExtent() >= 2 * Math.PI || getRadianAngleExtent() <= -2 * Math.PI) {
			// Trivial case
			return anArc.clone();
		}

		DianaArea equ1 = _equivalentForArcIn1D(this, true);
		DianaArea equ2 = _equivalentForArcIn1D(anArc, true);

		DianaArea intersection = equ1.intersect(equ2);

		/*System.out.println("arc1="+this);
		System.out.println("arc2="+anArc);
		System.out.println("equ1="+equ1);
		System.out.println("arc2="+equ2);
		System.out.println("intersection="+intersection);*/

		return _equivalentToArcIn1D(intersection);
	}

	// Old (buggy implementation)
	/*protected DianaArea computeArcIntersection2(DianaArc anArc)
	{
		double startAngle1 = getRadianAngleStart();
		double endAngle1 = normalizeRadianAngle(getRadianAngleStart()+getRadianAngleExtent());
		double startAngle2 = anArc.getRadianAngleStart();
		double endAngle2 = normalizeRadianAngle(anArc.getRadianAngleStart()+anArc.getRadianAngleExtent());
		
		double startAngle;
		double endAngle;
		
		if (anArc.includeAngle(startAngle1)) {
			if (anArc.includeAngle(endAngle1)) {
				System.out.println("anArc="+anArc+" include angle "+Math.toDegrees(endAngle1));
				double midAngle1 = (startAngle1+endAngle1)/2;
				//if (anArc.includeAngle(midAngle1)) {
				if (getRadianAngleExtent() < anArc.getRadianAngleExtent()) {
					System.out.println("Hop1");
					System.out.println("anArc="+anArc);
					System.out.println("this="+this);
					System.out.println("getRadianAngleExtent()="+getRadianAngleExtent());
					System.out.println("anArc.getRadianAngleExtent()="+anArc.getRadianAngleExtent());
					return this.clone();
				}
				else {
					double a1 = Math.toDegrees(startAngle1);
					double e1 = Math.toDegrees(angleExtent(startAngle1,endAngle2));
					double a2 = Math.toDegrees(startAngle2);
					double e2 = Math.toDegrees(angleExtent(startAngle2,endAngle1));
					DianaArc arc1 = new DianaArc(x,y,width,height,a1,e1,getDianaArcType());
					DianaArc arc2 = new DianaArc(x,y,width,height,a2,e2,getDianaArcType());
					
					System.out.println("Hop2");
					return new DianaUnionArea(arc1,arc2);
				}
			}
			else {
				startAngle = startAngle1;
				endAngle = endAngle2;
				System.out.println("Hop3");
			}
		}
		else {
			if (anArc.includeAngle(endAngle1)) {
				startAngle = startAngle2;
				endAngle = endAngle1;
				System.out.println("Hop4");
			}
			else {
				double midAngle2 = (startAngle2+endAngle2)/2;
				if (includeAngle(midAngle2)) {
					System.out.println("Hop5");
					return anArc.clone();
				}
				else {
					System.out.println("Hop6");
					return new DianaEmptyArea();
				}
			}
		}
		
		double angleExtent = Math.toDegrees(angleExtent(startAngle,endAngle));
		startAngle = Math.toDegrees(startAngle);
		
		return new DianaArc(x,y,width,height,startAngle,angleExtent,getDianaArcType());
	
	}*/

	public DianaPoint getPointAtAngle(double degAngle) {
		return getPointAtRadianAngle(Math.toRadians(degAngle));
	}

	public DianaPoint getPointAtRadianAngle(double radianAngle) {
		return new DianaPoint(getCenter().x + getWidth() / 2 * Math.cos(radianAngle),
				getCenter().y - getHeight() / 2 * Math.sin(radianAngle));
	}

	/**
	 * 
	 * @param p
	 * @return angle in radians
	 */
	public double angleForPoint(DianaPoint p) {
		DianaHalfLine hl = new DianaHalfLine(getCenter(), p);
		LineIntersectionResult result = _computeIntersection(hl);

		if (result.solutionType == org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.NO_SOLUTION) {
			logger.warning("Unexpected situation here");
			return java.lang.Double.NaN;
		}

		else if (result.solutionType == org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.ONE_SOLUTION) {
			return result.alpha1;
		}

		else if (result.solutionType == org.openflexo.diana.geom.DianaArc.LineIntersectionResult.SolutionType.TWO_SOLUTIONS) {
			DianaPoint p1 = new DianaPoint(width / 2.0 * Math.cos(-result.alpha1) + x + width / 2.0,
					height / 2.0 * Math.sin(-result.alpha1) + y + height / 2.0);
			@SuppressWarnings("unused")
			DianaPoint p2 = new DianaPoint(width / 2.0 * Math.cos(-result.alpha2) + x + width / 2.0,
					height / 2.0 * Math.sin(-result.alpha2) + y + height / 2.0);
			if (hl.containsPoint(p1)) {
				return result.alpha1;
			}
			else {
				/*if (hl.containsPoint(p2))*/return result.alpha2;
			}
		}

		logger.warning("Unexpected situation while retrieving angle for point " + p + " on " + this);
		logger.warning("result=" + result);
		return java.lang.Double.NaN;
	}

	public boolean isClockWise() {
		return extent < 0;
	}

	/**
	 * This area is finite, so always return true
	 */
	@Override
	public final boolean isFinite() {
		return true;
	}

	/**
	 * This area is finite, so always return null
	 */
	@Override
	public final DianaRectangle getEmbeddingBounds() {
		// TODO: sub-optimal implementation, you can do better job
		return new DianaRectangle(x, y, width, height, Filling.FILLED);
	}

	public DianaPoint getMiddle() {
		if (this instanceof DianaEllips) {
			logger.warning("getMiddle() invoked for an ellips");
			return getCenter();
		}
		else {
			double angle = getAngleStart() + getAngleExtent() / 2;
			return getPointAtAngle(angle);
		}
	}

}

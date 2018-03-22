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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;

public interface DianaShape<O extends DianaGeometricObject<O>> extends DianaGeometricObject<O>, Shape {

	static final Logger logger = Logger.getLogger(DianaShape.class.getPackage().getName());

	public boolean getIsFilled();

	public void setIsFilled(boolean aFlag);

	public DianaPoint nearestOutlinePoint(DianaPoint aPoint);

	public DianaPoint getCenter();

	public DianaRectangle getBoundingBox();

	public static class AreaComputation {

		public static <O extends DianaGeometricObject<O>> boolean isShapeContainedInArea(DianaShape<O> shape, DianaArea area) {
			if (shape.getControlPoints().size() == 0) {
				return false;
			}
			for (DianaPoint p : shape.getControlPoints()) {
				if (!area.containsPoint(p)) {
					return false;
				}
			}
			return true;
		}

		public static DianaArea computeShapeIntersection(DianaShape<?> shape1, DianaShape<?> shape2) {
			// System.out.println("computeShapeIntersection() with "+shape1+" and "+shape2);
			Area area1 = new Area(shape1);
			// System.out.println(">>> First shape: ");
			// debugPathIterator(area1.getPathIterator(new AffineTransform()));
			Area area2 = new Area(shape2);
			// System.out.println(">>> Second shape: ");
			// debugPathIterator(area2.getPathIterator(new AffineTransform()));
			area1.intersect(area2);
			// System.out.println(">>> Third shape: ");
			// debugPathIterator(area1.getPathIterator(new AffineTransform()));
			if (isPolygonalPathIterator(area1.getPathIterator(new AffineTransform()))) {
				DianaArea returned = makePolygonalShapeFromPathIterator(area1.getPathIterator(new AffineTransform()));
				if (returned instanceof DianaEmptyArea) {
					// In some cases, path iterator computation can miss something
					// From here, assert that both shapes have null intersection with path iterator method
					if (shape1 instanceof DianaPolygon) {
						if (shape2 instanceof DianaPolygon) {
							return ((DianaPolygon) shape1).getOutline().intersect(((DianaPolygon) shape2).getOutline());
						}
						else if (shape2 instanceof DianaRectangle) {
							return ((DianaPolygon) shape1).getOutline().intersect(((DianaRectangle) shape2).getOutline());
						}
					}
					else if (shape1 instanceof DianaRectangle) {
						if (shape2 instanceof DianaPolygon) {
							return ((DianaRectangle) shape1).getOutline().intersect(((DianaPolygon) shape2).getOutline());
						}
						else if (shape2 instanceof DianaRectangle) {
							return ((DianaRectangle) shape1).getOutline().intersect(((DianaRectangle) shape2).getOutline());
						}
					}
				}
				return returned;
			}
			else {
				logger.warning("Non-polygonal shapes not supported yet...");
				return new DianaEmptyArea();
			}
		}

		protected static DianaArea makePolygonalShapeFromPathIterator(PathIterator pathIterator) {
			Vector<DianaPolygon> polygons = new Vector<>();
			Vector<DianaPoint> currentPolygon = new Vector<>();

			while (!pathIterator.isDone()) {
				double[] coords = new double[6];
				int i = pathIterator.currentSegment(coords);
				@SuppressWarnings("unused")
				String pathType = "";
				switch (i) {
					case PathIterator.SEG_LINETO:
						pathType = "SEG_LINETO";
						currentPolygon.add(new DianaPoint(coords[0], coords[1]));
						break;
					case PathIterator.SEG_MOVETO:
						pathType = "SEG_MOVETO";
						currentPolygon = new Vector<>();
						currentPolygon.add(new DianaPoint(coords[0], coords[1]));
						break;
					case PathIterator.SEG_CLOSE:
						pathType = "SEG_CLOSE";
						polygons.add(new DianaPolygon(Filling.FILLED, currentPolygon));
						break;
					default:
						logger.warning("Unexpected PathIterator item found: " + i);
						return new DianaEmptyArea();
				}
				// logger.info(pathType+" "+coords[0]+" "+coords[1]+" "+coords[2]+" "+coords[3]+" "+coords[4]+" "+coords[5]);
				pathIterator.next();
			}

			if (polygons.size() == 0) {
				return new DianaEmptyArea();
			}
			if (polygons.size() == 1) {
				return polygons.firstElement();
			}
			return DianaUnionArea.makeUnion(polygons);

		}

		protected static boolean isPolygonalPathIterator(PathIterator pathIterator) {
			double[] coords = new double[6];

			while (!pathIterator.isDone()) {
				int i = pathIterator.currentSegment(coords);
				switch (i) {
					case PathIterator.SEG_CUBICTO:
						return false;
					case PathIterator.SEG_QUADTO:
						return false;
					default:
						break;
				}
				pathIterator.next();
			}

			// Only PathIterator.SEG_LINETO, PathIterator.SEG_MOVETO, PathIterator.SEG_CLOSE found
			return true;

		}

		@SuppressWarnings("unused")
		private static void debugPathIterator(PathIterator pi) {
			while (!pi.isDone()) {
				double[] coords = new double[6];
				int i = pi.currentSegment(coords);
				String pathType = "";
				switch (i) {
					case PathIterator.SEG_LINETO:
						pathType = "SEG_LINETO";
						break;
					case PathIterator.SEG_MOVETO:
						pathType = "SEG_MOVETO";
						break;
					case PathIterator.SEG_CUBICTO:
						pathType = "SEG_CUBICTO";
						break;
					case PathIterator.SEG_QUADTO:
						pathType = "SEG_QUADTO";
						break;
					case PathIterator.SEG_CLOSE:
						pathType = "SEG_CLOSE";
						break;
					default:
						break;
				}
				System.out.println(pathType + " " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " "
						+ coords[5]);
				pi.next();
			}
		}

	}

}

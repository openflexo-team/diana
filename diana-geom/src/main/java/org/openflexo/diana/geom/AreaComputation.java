package org.openflexo.diana.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.geom.DianaGeneralShape.Closure;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;

/**
 * Utility class used to perform area computation of {@link DianaShape} over diana-geom library
 * 
 * @author sylvain
 *
 */
public class AreaComputation {

	private static final Logger logger = Logger.getLogger(AreaComputation.class.getPackage().getName());

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

		if (isPolygonalArea(area1)) {
			DianaArea returned = makePolygonalShapeFromArea(area1);
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
			// debugPathIterator(area1.getPathIterator(new AffineTransform()));
			return makeGeneralShapeFromArea(area1);
		}
	}

	public static DianaArea computeShapeUnion(DianaShape<?> shape1, DianaShape<?> shape2) {
		// System.out.println("computeShapeUnion() with "+shape1+" and "+shape2);
		Area area1 = new Area(shape1);
		// System.out.println(">>> First shape: ");
		// debugPathIterator(area1.getPathIterator(new AffineTransform()));
		Area area2 = new Area(shape2);
		// System.out.println(">>> Second shape: ");
		// debugPathIterator(area2.getPathIterator(new AffineTransform()));
		area1.add(area2);
		// System.out.println(">>> Third shape: ");
		// debugPathIterator(area1.getPathIterator(new AffineTransform()));

		if (isPolygonalArea(area1)) {
			return makePolygonalShapeFromArea(area1);
		}
		else {
			return makeGeneralShapeFromArea(area1);
		}
	}

	public static DianaArea computeShapeSubstraction(DianaShape<?> shape1, DianaShape<?> shape2) {
		//System.out.println("computeShapeSubstraction() with " + shape1 + " and " + shape2);
		Area area1 = new Area(shape1);
		//System.out.println(">>> First shape: ");
		//debugPathIterator(area1.getPathIterator(new AffineTransform()));
		Area area2 = new Area(shape2);
		//System.out.println(">>> Second shape: ");
		//debugPathIterator(area2.getPathIterator(new AffineTransform()));
		area1.subtract(area2);
		//System.out.println(">>> Third shape: ");
		//debugPathIterator(area1.getPathIterator(new AffineTransform()));

		if (isPolygonalArea(area1)) {
			return makePolygonalShapeFromArea(area1);
		}
		else {
			return makeGeneralShapeFromArea(area1);
		}
	}

	protected static DianaArea makePolygonalShapeFromArea(Area area) {

		PathIterator pathIterator = area.getPathIterator(new AffineTransform());

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
					DianaShape.logger.warning("Unexpected PathIterator item found: " + i);
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

	protected static boolean isPolygonalArea(Area area) {

		PathIterator pathIterator = area.getPathIterator(new AffineTransform());

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
			System.out.println(
					pathType + " " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " " + coords[5]);
			pi.next();
		}
	}

	protected static DianaArea makeGeneralShapeFromArea(Area area) {

		PathIterator pathIterator = area.getPathIterator(new AffineTransform());

		Vector<DianaGeneralShape<?>> generalShapes = new Vector<>();
		DianaGeneralShape<?> currentShape = null;

		// logger.info("makeGeneralShapeFromPathIterator");

		while (!pathIterator.isDone()) {
			double[] coords = new double[6];
			int i = pathIterator.currentSegment(coords);
			String pathType = "";
			switch (i) {
				case PathIterator.SEG_MOVETO:
					pathType = "SEG_MOVETO";
					currentShape = new DianaGeneralShape<>(Closure.CLOSED_FILLED);
					currentShape.beginAtPoint(new DianaPoint(coords[0], coords[1]));
					break;
				case PathIterator.SEG_LINETO:
					pathType = "SEG_LINETO";
					currentShape.addSegment(new DianaPoint(coords[0], coords[1]));
					break;
				case PathIterator.SEG_CUBICTO:
					pathType = "SEG_CUBICTO";
					currentShape.addCubicCurve(new DianaPoint(coords[0], coords[1]), new DianaPoint(coords[2], coords[3]),
							new DianaPoint(coords[4], coords[5]));
					break;
				case PathIterator.SEG_QUADTO:
					pathType = "SEG_QUADTO";
					currentShape.addQuadCurve(new DianaPoint(coords[0], coords[1]), new DianaPoint(coords[2], coords[3]));
					break;
				case PathIterator.SEG_CLOSE:
					pathType = "SEG_CLOSE";
					generalShapes.add(currentShape);
					break;
				default:
					DianaShape.logger.warning("Unexpected PathIterator item found: " + i);
					return new DianaEmptyArea();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(pathType + " " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " "
						+ coords[5]);
			}
			pathIterator.next();
		}

		if (generalShapes.size() == 0) {
			return new DianaEmptyArea();
		}
		if (generalShapes.size() == 1) {
			return generalShapes.firstElement();
		}
		return DianaUnionArea.makeUnion(generalShapes);

	}

}

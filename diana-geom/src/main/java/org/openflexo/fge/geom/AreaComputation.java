package org.openflexo.fge.geom;

import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fge.geom.FGEGeneralShape.Closure;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.area.FGEArea;
import org.openflexo.fge.geom.area.FGEEmptyArea;
import org.openflexo.fge.geom.area.FGEUnionArea;

/**
 * Utility class used to perform area computation of {@link FGEShape} over diana-geom library
 * 
 * @author sylvain
 *
 */
public class AreaComputation {

	private static final Logger logger = Logger.getLogger(AreaComputation.class.getPackage().getName());

	public static <O extends FGEGeometricObject<O>> boolean isShapeContainedInArea(FGEShape<O> shape, FGEArea area) {
		if (shape.getControlPoints().size() == 0) {
			return false;
		}
		for (FGEPoint p : shape.getControlPoints()) {
			if (!area.containsPoint(p)) {
				return false;
			}
		}
		return true;
	}

	public static FGEArea computeShapeIntersection(FGEShape<?> shape1, FGEShape<?> shape2) {
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
			FGEArea returned = makePolygonalShapeFromArea(area1);
			if (returned instanceof FGEEmptyArea) {
				// In some cases, path iterator computation can miss something
				// From here, assert that both shapes have null intersection with path iterator method
				if (shape1 instanceof FGEPolygon) {
					if (shape2 instanceof FGEPolygon) {
						return ((FGEPolygon) shape1).getOutline().intersect(((FGEPolygon) shape2).getOutline());
					}
					else if (shape2 instanceof FGERectangle) {
						return ((FGEPolygon) shape1).getOutline().intersect(((FGERectangle) shape2).getOutline());
					}
				}
				else if (shape1 instanceof FGERectangle) {
					if (shape2 instanceof FGEPolygon) {
						return ((FGERectangle) shape1).getOutline().intersect(((FGEPolygon) shape2).getOutline());
					}
					else if (shape2 instanceof FGERectangle) {
						return ((FGERectangle) shape1).getOutline().intersect(((FGERectangle) shape2).getOutline());
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

	public static FGEArea computeShapeUnion(FGEShape<?> shape1, FGEShape<?> shape2) {
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

	public static FGEArea computeShapeSubstraction(FGEShape<?> shape1, FGEShape<?> shape2) {
		System.out.println("computeShapeSubstraction() with " + shape1 + " and " + shape2);
		Area area1 = new Area(shape1);
		System.out.println(">>> First shape: ");
		debugPathIterator(area1.getPathIterator(new AffineTransform()));
		Area area2 = new Area(shape2);
		System.out.println(">>> Second shape: ");
		debugPathIterator(area2.getPathIterator(new AffineTransform()));
		area1.subtract(area2);
		System.out.println(">>> Third shape: ");
		debugPathIterator(area1.getPathIterator(new AffineTransform()));

		if (isPolygonalArea(area1)) {
			return makePolygonalShapeFromArea(area1);
		}
		else {
			return makeGeneralShapeFromArea(area1);
		}
	}

	protected static FGEArea makePolygonalShapeFromArea(Area area) {

		PathIterator pathIterator = area.getPathIterator(new AffineTransform());

		Vector<FGEPolygon> polygons = new Vector<>();
		Vector<FGEPoint> currentPolygon = new Vector<>();

		while (!pathIterator.isDone()) {
			double[] coords = new double[6];
			int i = pathIterator.currentSegment(coords);
			@SuppressWarnings("unused")
			String pathType = "";
			switch (i) {
				case PathIterator.SEG_LINETO:
					pathType = "SEG_LINETO";
					currentPolygon.add(new FGEPoint(coords[0], coords[1]));
					break;
				case PathIterator.SEG_MOVETO:
					pathType = "SEG_MOVETO";
					currentPolygon = new Vector<>();
					currentPolygon.add(new FGEPoint(coords[0], coords[1]));
					break;
				case PathIterator.SEG_CLOSE:
					pathType = "SEG_CLOSE";
					polygons.add(new FGEPolygon(Filling.FILLED, currentPolygon));
					break;
				default:
					FGEShape.logger.warning("Unexpected PathIterator item found: " + i);
					return new FGEEmptyArea();
			}
			// logger.info(pathType+" "+coords[0]+" "+coords[1]+" "+coords[2]+" "+coords[3]+" "+coords[4]+" "+coords[5]);
			pathIterator.next();
		}

		if (polygons.size() == 0) {
			return new FGEEmptyArea();
		}
		if (polygons.size() == 1) {
			return polygons.firstElement();
		}
		return FGEUnionArea.makeUnion(polygons);

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

	protected static FGEArea makeGeneralShapeFromArea(Area area) {

		PathIterator pathIterator = area.getPathIterator(new AffineTransform());

		Vector<FGEGeneralShape<?>> generalShapes = new Vector<>();
		FGEGeneralShape<?> currentShape = null;

		// logger.info("makeGeneralShapeFromPathIterator");

		while (!pathIterator.isDone()) {
			double[] coords = new double[6];
			int i = pathIterator.currentSegment(coords);
			String pathType = "";
			switch (i) {
				case PathIterator.SEG_MOVETO:
					pathType = "SEG_MOVETO";
					currentShape = new FGEGeneralShape<>(Closure.CLOSED_FILLED);
					currentShape.beginAtPoint(new FGEPoint(coords[0], coords[1]));
					break;
				case PathIterator.SEG_LINETO:
					pathType = "SEG_LINETO";
					currentShape.addSegment(new FGEPoint(coords[0], coords[1]));
					break;
				case PathIterator.SEG_CUBICTO:
					pathType = "SEG_CUBICTO";
					currentShape.addCubicCurve(new FGEPoint(coords[0], coords[1]), new FGEPoint(coords[2], coords[3]),
							new FGEPoint(coords[4], coords[5]));
					break;
				case PathIterator.SEG_QUADTO:
					pathType = "SEG_QUADTO";
					currentShape.addQuadCurve(new FGEPoint(coords[0], coords[1]), new FGEPoint(coords[2], coords[3]));
					break;
				case PathIterator.SEG_CLOSE:
					pathType = "SEG_CLOSE";
					generalShapes.add(currentShape);
					break;
				default:
					FGEShape.logger.warning("Unexpected PathIterator item found: " + i);
					return new FGEEmptyArea();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine(pathType + " " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " "
						+ coords[5]);
			}
			pathIterator.next();
		}

		if (generalShapes.size() == 0) {
			return new FGEEmptyArea();
		}
		if (generalShapes.size() == 1) {
			return generalShapes.firstElement();
		}
		return FGEUnionArea.makeUnion(generalShapes);

	}

}

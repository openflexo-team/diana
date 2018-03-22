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

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.diana.geom.area.DefaultAreaProvider;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.geom.area.DianaAreaProvider;
import org.openflexo.diana.geom.area.DianaEmptyArea;
import org.openflexo.diana.geom.area.DianaUnionArea;
import org.openflexo.diana.graphics.AbstractDianaGraphics;

public class DianaRectPolylin extends DianaPolylin {

	static final Logger logger = Logger.getLogger(DianaRectPolylin.class.getPackage().getName());

	private DianaArea startArea;
	private SimplifiedCardinalDirection startOrientation;
	private DianaArea endArea;
	private SimplifiedCardinalDirection endOrientation;

	private double overlapX = 10;
	private double overlapY = 10;

	private boolean straightWhenPossible = false;

	private DianaArea resultingStartArea;
	private DianaArea resultingEndArea;

	private boolean respectAllConstraints = true;

	// TODO: debug only remove this
	// private static final DianaModelFactory DEBUG_FACTORY = GeomUtils.TOOLS_FACTORY;
	// private static final ForegroundStyle DEBUG_FOCUSED_FG = DEBUG_FACTORY.makeForegroundStyle(Color.RED, 0.5f, DashStyle.MEDIUM_DASHES);
	// private static final BackgroundStyle DEBUG_FOCUSED_BG = DEBUG_FACTORY.makeTexturedBackground(TextureType.TEXTURE1, Color.RED,
	// Color.WHITE);

	// TODO: debug only remove this
	// static {
	// DEBUG_FOCUSED_BG.setUseTransparency(true);
	// DEBUG_FOCUSED_BG.setTransparencyLevel(0.1f);
	// }

	public DianaRectPolylin() {
		super();
	}

	public DianaRectPolylin(List<DianaPoint> points) {
		super(points);
	}

	public DianaRectPolylin(DianaPoint... points) {
		super(makeList(points));
	}

	private static List<DianaPoint> makeList(DianaPoint... points) {
		Vector<DianaPoint> returned = new Vector<>();
		for (DianaPoint pt : points) {
			returned.add(pt);
		}
		return returned;
	}

	public DianaRectPolylin(List<DianaPoint> points, boolean straightWhenPossible, double overlapX, double overlapY) {
		super(points);

		this.straightWhenPossible = straightWhenPossible;

		if (overlapX < 0) {
			logger.warning("Called DianaRectPolylin with negative overlapX: " + overlapX);
			overlapX = 0;
		}
		this.overlapX = overlapX;

		if (overlapY < 0) {
			logger.warning("Called DianaRectPolylin with negative overlapY: " + overlapY);
			overlapY = 0;
		}
		this.overlapY = overlapY;
	}

	/**
	 * Build and return a DianaRectPolylin given supplied start and end orientation, and a bunch of parameters
	 * 
	 * @param startArea
	 * @param startOrientation
	 * @param endArea
	 * @param endOrientation
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public DianaRectPolylin(DianaArea aStartArea, SimplifiedCardinalDirection startOrientation, DianaArea anEndArea,
			SimplifiedCardinalDirection endOrientation, boolean straightWhenPossible, double overlapX, double overlapY) {
		super();
		this.startArea = aStartArea.clone();
		if (startArea instanceof DianaShape) {
			((DianaShape<?>) startArea).setIsFilled(false);
		}
		this.startOrientation = startOrientation;
		this.endArea = anEndArea.clone();
		if (endArea instanceof DianaShape) {
			((DianaShape<?>) endArea).setIsFilled(false);
		}
		this.endOrientation = endOrientation;
		this.straightWhenPossible = straightWhenPossible;

		// logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DianaRectPolylin BEGIN");

		if (overlapX < 0) {
			logger.warning("Called DianaRectPolylin with negative overlapX: " + overlapX);
			overlapX = 0;
		}
		this.overlapX = overlapX;

		if (overlapY < 0) {
			logger.warning("Called DianaRectPolylin with negative overlapY: " + overlapY);
			overlapY = 0;
		}
		this.overlapY = overlapY;

		computeResultingOrthogonalPerspectiveAreas();
		restoreDefaultLayout();

		respectAllConstraints = true;

		if (getOrientationOfSegment(0) != startOrientation && overlapX > 0 && overlapY > 0 && getFirstSegment() != null
				&& getFirstSegment().getLength() > 0) {
			if (getOrientationOfSegment(0) != null) {
				logger.info("Inconsistant start orientation invariant... " + getOrientationOfSegment(0) + " != " + startOrientation);
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.info("startArea=" + startArea);
				logger.info("startOrientation=" + startOrientation);
				logger.info("endArea=" + endArea);
				logger.info("endOrientation=" + endOrientation);
				logger.info("polylin=" + this);
			}
			respectAllConstraints = false;
		}
		if (endOrientation != null && getOrientationOfSegment(getSegmentNb() - 1) != endOrientation.getOpposite() && overlapX > 0
				&& overlapY > 0 && getLastSegment() != null && getLastSegment().getLength() > 0) {
			if (getOrientationOfSegment(getSegmentNb() - 1) != null) {
				logger.info("Inconsistant end orientation invariant... " + getOrientationOfSegment(getSegmentNb() - 1) + " != "
						+ endOrientation.getOpposite());
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.info("startArea=" + startArea);
				logger.info("startOrientation=" + startOrientation);
				logger.info("endArea=" + endArea);
				logger.info("endOrientation=" + endOrientation);
				logger.info("polylin=" + this);
			}
			respectAllConstraints = false;
		}
		if (getPointsNb() == 0) {
			logger.info("Inconsistant invariant getPointsNb() == 0");
			if (logger.isLoggable(Level.FINE)) {
				logger.info("startArea=" + startArea);
				logger.info("startOrientation=" + startOrientation);
				logger.info("endArea=" + endArea);
				logger.info("endOrientation=" + endOrientation);
				logger.info("resultingStartArea=" + resultingStartArea);
				logger.info("resultingEndArea=" + resultingEndArea);
			}
			respectAllConstraints = false;
		}
		// logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< DianaRectPolylin END");
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions are examined, and best solution is returned: -
	 * 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaArea startArea, DianaArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY) {
		return makeShortestRectPolylin(startArea, endArea, straightWhenPossible, overlapX, overlapY,
				(Vector<SimplifiedCardinalDirection>) null, (Vector<SimplifiedCardinalDirection>) null);
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned: - 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaArea startArea, DianaArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY, SimplifiedCardinalDirection startOrientation, SimplifiedCardinalDirection endOrientation) {
		return makeShortestRectPolylin(startArea, endArea, straightWhenPossible, overlapX, overlapY,
				SimplifiedCardinalDirection.allDirectionsExcept(startOrientation),
				SimplifiedCardinalDirection.allDirectionsExcept(endOrientation));
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned: - 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaArea startArea, DianaArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		DianaRectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection startOrientation : SimplifiedCardinalDirection.values()) {
			if (excludedStartOrientations == null || !excludedStartOrientations.contains(startOrientation)) {
				for (SimplifiedCardinalDirection endOrientation : SimplifiedCardinalDirection.values()) {
					if (excludedEndOrientations == null || !excludedEndOrientations.contains(endOrientation)) {
						DianaRectPolylin tried = new DianaRectPolylin(startArea, startOrientation, endArea, endOrientation,
								straightWhenPossible, overlapX, overlapY);
						if (tried.doesRespectAllConstraints() && tried.getPointsNb() < bestNbOfPoints) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
						}
						else if (tried.doesRespectAllConstraints() && tried.getPointsNb() == bestNbOfPoints) {
							if (tried.getLength() < bestLength) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
							}
						}
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned regarding distance between returned polylin and supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaArea startArea, DianaArea endArea, boolean straightWhenPossible, double overlapX,
			double overlapY, DianaPoint minimizeDistanceToThisPoint, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		DianaRectPolylin returned = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection startOrientation : SimplifiedCardinalDirection.values()) {
			if (excludedStartOrientations == null || !excludedStartOrientations.contains(startOrientation)) {
				for (SimplifiedCardinalDirection endOrientation : SimplifiedCardinalDirection.values()) {
					if (excludedEndOrientations == null || !excludedEndOrientations.contains(endOrientation)) {
						DianaRectPolylin tried = new DianaRectPolylin(startArea, startOrientation, endArea, endOrientation,
								straightWhenPossible, overlapX, overlapY);
						double distance = DianaPoint.distance(tried.getNearestPoint(minimizeDistanceToThisPoint),
								minimizeDistanceToThisPoint);
						if (tried.doesRespectAllConstraints() && distance < bestDistance) {
							returned = tried;
							bestDistance = distance;
						}
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions are
	 * examined, and best solution is returned regarding distance between returned polylin and supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeRectPolylinCrossingPoint(DianaArea startArea, DianaArea endArea, DianaPoint crossedPoint,
			boolean straightWhenPossible, double overlapX, double overlapY) {
		return makeRectPolylinCrossingPoint(startArea, endArea, crossedPoint, straightWhenPossible, overlapX, overlapY, null, null);
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point, using supplied start and end
	 * orientation
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeRectPolylinCrossingPoint(DianaArea startArea, DianaArea endArea, DianaPoint crossedPoint,
			SimplifiedCardinalDirection startOrientation, SimplifiedCardinalDirection endOrientation, boolean straightWhenPossible,
			double overlapX, double overlapY) {
		return makeRectPolylinCrossingPoint(startArea, endArea, crossedPoint, straightWhenPossible, overlapX, overlapY,
				SimplifiedCardinalDirection.allDirectionsExcept(startOrientation),
				SimplifiedCardinalDirection.allDirectionsExcept(endOrientation));
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions are examined, and best solution is returned: -
	 * 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startAreaProvider
	 * @param endAreaProvider
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, boolean straightWhenPossible, double overlapX, double overlapY) {
		return makeShortestRectPolylin(startAreaProvider, endAreaProvider, straightWhenPossible, overlapX, overlapY,
				(Vector<SimplifiedCardinalDirection>) null, (Vector<SimplifiedCardinalDirection>) null);
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned: - 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, boolean straightWhenPossible, double overlapX, double overlapY,
			SimplifiedCardinalDirection startOrientation, SimplifiedCardinalDirection endOrientation) {
		return makeShortestRectPolylin(startAreaProvider, endAreaProvider, straightWhenPossible, overlapX, overlapY,
				SimplifiedCardinalDirection.allDirectionsExcept(startOrientation),
				SimplifiedCardinalDirection.allDirectionsExcept(endOrientation));
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned: - 1st: try to minimize number of points - 2nd: try to minimize total length of path
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, boolean straightWhenPossible, double overlapX, double overlapY,
			Vector<SimplifiedCardinalDirection> excludedStartOrientations, Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		DianaRectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection startOrientation : SimplifiedCardinalDirection.values()) {
			if (excludedStartOrientations == null || !excludedStartOrientations.contains(startOrientation)) {
				DianaArea startArea = startAreaProvider.getArea(startOrientation);
				if (startArea instanceof DianaEmptyArea) {
					continue;
				}
				for (SimplifiedCardinalDirection endOrientation : SimplifiedCardinalDirection.values()) {
					if (excludedEndOrientations == null || !excludedEndOrientations.contains(endOrientation)) {
						DianaArea endArea = endAreaProvider.getArea(endOrientation);
						if (endArea instanceof DianaEmptyArea) {
							continue;
						}
						DianaRectPolylin tried = new DianaRectPolylin(startArea, startOrientation, endArea, endOrientation,
								straightWhenPossible, overlapX, overlapY);
						if (tried.doesRespectAllConstraints() && tried.getPointsNb() < bestNbOfPoints) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
						}
						else if (tried.doesRespectAllConstraints() && tried.getPointsNb() == bestNbOfPoints) {
							if (tried.getLength() < bestLength) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
							}
						}
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Return and build a DianaRectPolylin, given some parameters. All orientation solutions (except those supplied as to be excluded) are
	 * examined, and best solution is returned regarding distance between returned polylin and supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeShortestRectPolylin(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, boolean straightWhenPossible, double overlapX, double overlapY,
			DianaPoint minimizeDistanceToThisPoint, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		DianaRectPolylin returned = null;
		double bestDistance = Double.POSITIVE_INFINITY;

		for (SimplifiedCardinalDirection startOrientation : SimplifiedCardinalDirection.values()) {
			if (excludedStartOrientations == null || !excludedStartOrientations.contains(startOrientation)) {
				DianaArea startArea = startAreaProvider.getArea(startOrientation);
				for (SimplifiedCardinalDirection endOrientation : SimplifiedCardinalDirection.values()) {
					if (excludedEndOrientations == null || !excludedEndOrientations.contains(endOrientation)) {
						DianaArea endArea = endAreaProvider.getArea(endOrientation);
						DianaRectPolylin tried = new DianaRectPolylin(startArea, startOrientation, endArea, endOrientation,
								straightWhenPossible, overlapX, overlapY);
						double distance = DianaPoint.distance(tried.getNearestPoint(minimizeDistanceToThisPoint),
								minimizeDistanceToThisPoint);
						if (tried.doesRespectAllConstraints() && distance < bestDistance) {
							returned = tried;
							bestDistance = distance;
						}
					}
				}
			}
		}
		return returned;
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions are
	 * examined, and best solution is returned regarding distance between returned polylin and supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeRectPolylinCrossingPoint(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, DianaPoint crossedPoint, boolean straightWhenPossible,
			double overlapX, double overlapY) {
		return makeRectPolylinCrossingPoint(startAreaProvider, endAreaProvider, crossedPoint, straightWhenPossible, overlapX, overlapY,
				null, null);
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point, using supplied start and end
	 * orientation
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	public static DianaRectPolylin makeRectPolylinCrossingPoint(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, DianaPoint crossedPoint,
			SimplifiedCardinalDirection startOrientation, SimplifiedCardinalDirection endOrientation, boolean straightWhenPossible,
			double overlapX, double overlapY) {
		return makeRectPolylinCrossingPoint(startAreaProvider, endAreaProvider, crossedPoint, straightWhenPossible, overlapX, overlapY,
				SimplifiedCardinalDirection.allDirectionsExcept(startOrientation),
				SimplifiedCardinalDirection.allDirectionsExcept(endOrientation));
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	/*public static DianaRectPolylin makeRectPolylinCrossingPoint (
	         DianaArea startArea,
	         DianaArea endArea,
	         DianaPoint crossedPoint,
	         boolean straightWhenPossible,
	         double overlap,
	         Vector<SimplifiedCardinalDirection> excludedStartOrientations,
	         Vector<SimplifiedCardinalDirection> excludedEndOrientations)
	 {
	     System.out.println("excludedStartOrientations="+excludedStartOrientations);
	     System.out.println("excludedEndOrientations="+excludedEndOrientations);
	
	     DianaRectPolylin polylin = makeShortestRectPolylin(
	             startArea,
	             endArea,
	             straightWhenPossible,
	             overlap,
	             crossedPoint,
	             excludedStartOrientations,
	             excludedEndOrientations);
	     DianaSegment projectionSegment = polylin.getProjectionSegment(crossedPoint);
	     if (projectionSegment == null) projectionSegment = polylin.getNearestSegment(crossedPoint);
	     SimplifiedCardinalDirection orientation = projectionSegment.getApproximatedOrientation();
	     Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations
	     = SimplifiedCardinalDirection.allDirectionsExcept(orientation.getOpposite());
	     Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations
	     = SimplifiedCardinalDirection.allDirectionsExcept(orientation);
	
	     DianaRectPolylin polylin1 = makeShortestRectPolylin(
	             startArea,
	             crossedPoint,
	             straightWhenPossible,
	             0,
	             excludedStartOrientations,
	             polylin1ExcludedEndOrientations);
	     DianaRectPolylin polylin2 = makeShortestRectPolylin(
	             crossedPoint,
	             endArea,
	             straightWhenPossible,
	             0,
	             polylin2ExcludedStartOrientations,
	             excludedEndOrientations);
	     DianaRectPolylin returned = mergePolylins(polylin1, 0, polylin1.getPointsNb()-2, polylin2, 1, polylin2.getPointsNb()-1);
	     if (returned.hasExtraPoints()) returned.removeExtraPoints();
	     return returned;
	 }*/

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	@SuppressWarnings("unused")
	public static DianaRectPolylin makeRectPolylinCrossingPoint(DianaArea startArea, DianaArea endArea, DianaPoint crossedPoint,
			boolean straightWhenPossible, double overlapX, double overlapY, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		DianaRectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;
		boolean isCurrentlyChoosenPolylinWithCrossedPointAsCorner = false;

		SimplifiedCardinalDirection bestStartOrientation = null;
		SimplifiedCardinalDirection bestMiddleOrientation = null;
		SimplifiedCardinalDirection bestEndOrientation = null;

		Hashtable<SimplifiedCardinalDirection, DianaRectPolylin> polylins1 = new Hashtable<SimplifiedCardinalDirection, DianaRectPolylin>();
		Hashtable<SimplifiedCardinalDirection, DianaRectPolylin> polylins2 = new Hashtable<SimplifiedCardinalDirection, DianaRectPolylin>();

		// Following regards performances optimization
		// Replace further commented code

		for (SimplifiedCardinalDirection orientation1 : SimplifiedCardinalDirection.values()) {
			DianaRectPolylin polylin = makeShortestRectPolylin(startArea, crossedPoint, true, overlapX, overlapY, excludedStartOrientations,
					SimplifiedCardinalDirection.allDirectionsExcept(orientation1));
			if (polylin != null) {
				polylins1.put(orientation1, polylin);
			}
		}

		for (SimplifiedCardinalDirection orientation2 : SimplifiedCardinalDirection.values()) {
			DianaRectPolylin polylin = makeShortestRectPolylin(crossedPoint, endArea, true, overlapX, overlapY,
					SimplifiedCardinalDirection.allDirectionsExcept(orientation2), excludedEndOrientations);
			if (polylin != null) {
				polylins2.put(orientation2, polylin);
			}
		}

		for (SimplifiedCardinalDirection orientation1 : SimplifiedCardinalDirection.values()) {
			for (SimplifiedCardinalDirection orientation2 : SimplifiedCardinalDirection.values()) {
				if (orientation1 != orientation2) {

					/*Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations
					     = SimplifiedCardinalDirection.allDirectionsExcept(orientation1);
					     Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations
					     = SimplifiedCardinalDirection.allDirectionsExcept(orientation2);
					
					     DianaRectPolylin polylin1 = makeShortestRectPolylin(
					             startArea,
					             crossedPoint,
					             true,
					             overlapX,
					             overlapY,
					             excludedStartOrientations,
					             polylin1ExcludedEndOrientations);
					     DianaRectPolylin polylin2 = makeShortestRectPolylin(
					             crossedPoint,
					             endArea,
					             true,
					             overlapX,
					             overlapY,
					             polylin2ExcludedStartOrientations,
					             excludedEndOrientations);*/

					// Performances, see above
					DianaRectPolylin polylin1 = polylins1.get(orientation1);
					DianaRectPolylin polylin2 = polylins2.get(orientation2);

					if (polylin1 != null && polylin1.doesRespectAllConstraints() && polylin2 != null
							&& polylin2.doesRespectAllConstraints()) {
						DianaRectPolylin tried;
						boolean cornerChoosen;
						if (orientation1 == orientation2.getOpposite()) {
							// In this case, crossedPoint is belonging to a segment
							cornerChoosen = false;
							tried = mergePolylins(polylin1, 0, polylin1.getPointsNb() - 2, polylin2, 1, polylin2.getPointsNb() - 1);
						}
						else {
							// In this case, crossedPoint is a corner, take all points of polylin1 and concatenate it
							// with all points of polylin2 except the first one (which is also crossedPoint)
							cornerChoosen = true;
							tried = mergePolylins(polylin1, 0, polylin1.getPointsNb() - 1, polylin2, 1, polylin2.getPointsNb() - 1);
						}
						if (tried.hasExtraPoints()) {
							tried.removeExtraPoints();
						}

						// First of all, a polylin not crossing itself is better than any other
						if (returned != null && returned.crossedItSelf() && !tried.crossedItSelf()) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
							isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
							bestStartOrientation = polylin1.getStartOrientation();
							bestMiddleOrientation = polylin1.getEndOrientation();
							bestEndOrientation = polylin2.getEndOrientation();
						}

						// Then, try to minimize number of points
						else if (tried.getPointsNb() < bestNbOfPoints) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
							isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
							bestStartOrientation = polylin1.getStartOrientation();
							bestMiddleOrientation = polylin1.getEndOrientation();
							bestEndOrientation = polylin2.getEndOrientation();
						}

						// Then, minimise total length
						else if (tried.getPointsNb() == bestNbOfPoints) {
							if (tried.getLength() < bestLength - EPSILON) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
								isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
								bestStartOrientation = polylin1.getStartOrientation();
								bestMiddleOrientation = polylin1.getEndOrientation();
								bestEndOrientation = polylin2.getEndOrientation();
							}
							// In case of same length, try to choose layout where crossed point is a corner
							else if (cornerChoosen && !isCurrentlyChoosenPolylinWithCrossedPointAsCorner) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
								isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
								bestStartOrientation = polylin1.getStartOrientation();
								bestMiddleOrientation = polylin1.getEndOrientation();
								bestEndOrientation = polylin2.getEndOrientation();
							}
						}
					}
				}
			}
		}

		// logger.info(" Choosen polylin "+bestStartOrientation+","+bestMiddleOrientation+","+bestEndOrientation);

		return returned;
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	@SuppressWarnings("unused")
	public static DianaRectPolylin makeRectPolylinCrossingPoint(DianaAreaProvider<SimplifiedCardinalDirection> startAreaProvider,
			DianaAreaProvider<SimplifiedCardinalDirection> endAreaProvider, DianaPoint crossedPoint, boolean straightWhenPossible,
			double overlapX, double overlapY, Vector<SimplifiedCardinalDirection> excludedStartOrientations,
			Vector<SimplifiedCardinalDirection> excludedEndOrientations) {
		DianaRectPolylin returned = null;
		int bestNbOfPoints = Integer.MAX_VALUE;
		double bestLength = Double.POSITIVE_INFINITY;
		boolean isCurrentlyChoosenPolylinWithCrossedPointAsCorner = false;

		SimplifiedCardinalDirection bestStartOrientation = null;
		SimplifiedCardinalDirection bestMiddleOrientation = null;
		SimplifiedCardinalDirection bestEndOrientation = null;

		Hashtable<SimplifiedCardinalDirection, DianaRectPolylin> polylins1 = new Hashtable<SimplifiedCardinalDirection, DianaRectPolylin>();
		Hashtable<SimplifiedCardinalDirection, DianaRectPolylin> polylins2 = new Hashtable<SimplifiedCardinalDirection, DianaRectPolylin>();

		// Following regards performances optimization
		// Replace further commented code

		DefaultAreaProvider<SimplifiedCardinalDirection> crossedPointAreaProvider = new DefaultAreaProvider<SimplifiedCardinalDirection>(
				crossedPoint);
		for (SimplifiedCardinalDirection orientation1 : SimplifiedCardinalDirection.values()) {
			DianaRectPolylin polylin = makeShortestRectPolylin(startAreaProvider, crossedPointAreaProvider, true, overlapX, overlapY,
					excludedStartOrientations, SimplifiedCardinalDirection.allDirectionsExcept(orientation1));
			if (polylin != null) {
				polylins1.put(orientation1, polylin);
			}
		}

		for (SimplifiedCardinalDirection orientation2 : SimplifiedCardinalDirection.values()) {
			DianaRectPolylin polylin = makeShortestRectPolylin(crossedPointAreaProvider, endAreaProvider, true, overlapX, overlapY,
					SimplifiedCardinalDirection.allDirectionsExcept(orientation2), excludedEndOrientations);
			if (polylin != null) {
				polylins2.put(orientation2, polylin);
			}
		}

		for (SimplifiedCardinalDirection orientation1 : SimplifiedCardinalDirection.values()) {
			for (SimplifiedCardinalDirection orientation2 : SimplifiedCardinalDirection.values()) {
				if (orientation1 != orientation2) {

					/*Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations
					     = SimplifiedCardinalDirection.allDirectionsExcept(orientation1);
					     Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations
					     = SimplifiedCardinalDirection.allDirectionsExcept(orientation2);
					
					     DianaRectPolylin polylin1 = makeShortestRectPolylin(
					             startArea,
					             crossedPoint,
					             true,
					             overlapX,
					             overlapY,
					             excludedStartOrientations,
					             polylin1ExcludedEndOrientations);
					     DianaRectPolylin polylin2 = makeShortestRectPolylin(
					             crossedPoint,
					             endArea,
					             true,
					             overlapX,
					             overlapY,
					             polylin2ExcludedStartOrientations,
					             excludedEndOrientations);*/

					// Performances, see above
					DianaRectPolylin polylin1 = polylins1.get(orientation1);
					DianaRectPolylin polylin2 = polylins2.get(orientation2);

					if (polylin1 != null && polylin1.doesRespectAllConstraints() && polylin2 != null
							&& polylin2.doesRespectAllConstraints()) {
						DianaRectPolylin tried;
						boolean cornerChoosen;
						if (orientation1 == orientation2.getOpposite()) {
							// In this case, crossedPoint is belonging to a segment
							cornerChoosen = false;
							tried = mergePolylins(polylin1, 0, polylin1.getPointsNb() - 2, polylin2, 1, polylin2.getPointsNb() - 1);
						}
						else {
							// In this case, crossedPoint is a corner, take all points of polylin1 and concatenate it
							// with all points of polylin2 except the first one (which is also crossedPoint)
							cornerChoosen = true;
							tried = mergePolylins(polylin1, 0, polylin1.getPointsNb() - 1, polylin2, 1, polylin2.getPointsNb() - 1);
						}
						if (tried.hasExtraPoints()) {
							tried.removeExtraPoints();
						}

						// First of all, a polylin not crossing itself is better than any other
						if (returned != null && returned.crossedItSelf() && !tried.crossedItSelf()) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
							isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
							bestStartOrientation = polylin1.getStartOrientation();
							bestMiddleOrientation = polylin1.getEndOrientation();
							bestEndOrientation = polylin2.getEndOrientation();
						}

						// Then, try to minimize number of points
						else if (tried.getPointsNb() < bestNbOfPoints) {
							returned = tried;
							bestNbOfPoints = tried.getPointsNb();
							bestLength = tried.getLength();
							isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
							bestStartOrientation = polylin1.getStartOrientation();
							bestMiddleOrientation = polylin1.getEndOrientation();
							bestEndOrientation = polylin2.getEndOrientation();
						}

						// Then, minimise total length
						else if (tried.getPointsNb() == bestNbOfPoints) {
							if (tried.getLength() < bestLength - EPSILON) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
								isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
								bestStartOrientation = polylin1.getStartOrientation();
								bestMiddleOrientation = polylin1.getEndOrientation();
								bestEndOrientation = polylin2.getEndOrientation();
							}
							// In case of same length, try to choose layout where crossed point is a corner
							else if (cornerChoosen && !isCurrentlyChoosenPolylinWithCrossedPointAsCorner) {
								returned = tried;
								bestNbOfPoints = tried.getPointsNb();
								bestLength = tried.getLength();
								isCurrentlyChoosenPolylinWithCrossedPointAsCorner = cornerChoosen;
								bestStartOrientation = polylin1.getStartOrientation();
								bestMiddleOrientation = polylin1.getEndOrientation();
								bestEndOrientation = polylin2.getEndOrientation();
							}
						}
					}
				}
			}
		}

		// logger.info(" Choosen polylin "+bestStartOrientation+","+bestMiddleOrientation+","+bestEndOrientation);

		return returned;
	}

	/**
	 * Return and build a DianaRectPolylin linking a start and an end area, and crossing supplied point. All orientation solutions (except
	 * those supplied as to be excluded) are examined, and best solution is returned regarding distance between returned polylin and
	 * supplied point
	 * 
	 * @param startArea
	 * @param endArea
	 * @param straightWhenPossible
	 * @param overlap
	 */
	/*public static DianaRectPolylin makeRectPolylinCrossingPoint (
	         DianaArea startArea,
	         DianaArea endArea,
	         DianaPoint crossedPoint,
	         boolean straightWhenPossible,
	         double overlapX,
	         double overlapY,
	         Vector<SimplifiedCardinalDirection> excludedStartOrientations,
	         Vector<SimplifiedCardinalDirection> excludedEndOrientations)
	 {
	     DianaRectPolylin returned = null;
	     int bestNbOfPoints = Integer.MAX_VALUE;
	     double bestLength = Double.POSITIVE_INFINITY;
	
	     for (SimplifiedCardinalDirection orientation : SimplifiedCardinalDirection.values()) {
	         Vector<SimplifiedCardinalDirection> polylin1ExcludedEndOrientations
	         = SimplifiedCardinalDirection.allDirectionsExcept(orientation.getOpposite());
	         Vector<SimplifiedCardinalDirection> polylin2ExcludedStartOrientations
	         = SimplifiedCardinalDirection.allDirectionsExcept(orientation);
	         DianaRectPolylin polylin1 = makeShortestRectPolylin(
	                 startArea,
	                 crossedPoint,
	                 true,
	                 overlapX,
	                 overlapY,
	                 excludedStartOrientations,
	                 polylin1ExcludedEndOrientations);
	         DianaRectPolylin polylin2 = makeShortestRectPolylin(
	                 crossedPoint,
	                 endArea,
	                 true,
	                 overlapX,
	                 overlapY,
	                 polylin2ExcludedStartOrientations,
	                 excludedEndOrientations);
	         DianaRectPolylin tried = mergePolylins(polylin1, 0, polylin1.getPointsNb()-2, polylin2, 1, polylin2.getPointsNb()-1);
	         if (tried.hasExtraPoints()) tried.removeExtraPoints();
	         if (returned != null && returned.crossedItSelf() && !tried.crossedItSelf()) {
	             returned = tried;
	             bestNbOfPoints = tried.getPointsNb();
	             bestLength = tried.getLength();
	         }
	         else if (tried.getPointsNb() < bestNbOfPoints) {
	             returned = tried;
	             bestNbOfPoints = tried.getPointsNb();
	             bestLength = tried.getLength();
	         }
	         else if (tried.getPointsNb() == bestNbOfPoints) {
	             if (tried.getLength() < bestLength-EPSILON) {
	                 returned = tried;
	                 bestNbOfPoints = tried.getPointsNb();
	                 bestLength = tried.getLength();
	             }
	         }
	     }
	     return returned;
	 }
	  */
	public boolean crossedItSelf() {
		for (DianaSegment s1 : getSegments()) {
			for (DianaSegment s2 : getSegments()) {
				if (s1 != s2 && (s1.overlap(s2) || s1.intersectsInsideSegment(s2) && !s1.getLineIntersection(s2).equals(s1.getP1())
						&& !s1.getLineIntersection(s2).equals(s1.getP2()) && !s1.getLineIntersection(s2).equals(s2.getP1())
						&& !s1.getLineIntersection(s2).equals(s2.getP2()))) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Merge polylin given supplied polylin and indexes. Ignore extra points...
	 * 
	 * @param p1
	 * @param startIndex1
	 * @param endIndex1
	 * @param p2
	 * @param startIndex2
	 * @param endIndex2
	 * @return
	 */
	public static DianaRectPolylin mergePolylins(DianaRectPolylin p1, int startIndex1, int endIndex1, DianaRectPolylin p2, int startIndex2,
			int endIndex2) {
		DianaPoint previous = null;
		DianaRectPolylin returned = new DianaRectPolylin();
		returned.setOverlapX(p1.getOverlapX());
		returned.setOverlapY(p1.getOverlapY());
		for (int i = startIndex1; i <= endIndex1; i++) {
			if (previous != null && previous.equals(p1.getPointAt(i))) {
				// System.out.println("ignore point: "+previous);
			}
			else {
				returned.addToPoints(p1.getPointAt(i));
			}
			previous = p1.getPointAt(i);
		}
		for (int i = startIndex2; i <= endIndex2; i++) {
			if (previous != null && previous.equals(p2.getPointAt(i))) {
				// System.out.println("ignore point: "+previous);
			}
			else {
				returned.addToPoints(p2.getPointAt(i));
			}
			previous = p2.getPointAt(i);
		}
		return returned;
	}

	private void computeResultingOrthogonalPerspectiveAreas() {
		if (startOrientation != null) {
			resultingStartArea = startArea.getOrthogonalPerspectiveArea(startOrientation);
		}
		else {
			resultingStartArea = new DianaEmptyArea();
		}

		if (endOrientation != null) {
			resultingEndArea = endArea.getOrthogonalPerspectiveArea(endOrientation);
			// DianaHalfPlane north2 = new DianaHalfPlane(DianaLine.makeHorizontalLine(new DianaPoint(0,getMinYFor(endArea))),new
			// DianaPoint(0,getMinYFor(endArea)-1));
			/*System.out.println("resultingEndArea="+resultingEndArea);
			   System.out.println("north2="+north2);
			   System.out.println("result="+resultingEndArea.intersect(north2));*/
			// resultingEndArea = resultingEndArea.intersect(north2);
		}
		else {
			resultingEndArea = new DianaEmptyArea();
		}

		/*if (startArea instanceof DianaPoint) {
		      resultingStartArea = ((DianaPoint)startArea).getOrthogonalPerspectiveArea(startOrientation);
		  }
		  else if (startArea instanceof DianaSegment) {
		      resultingStartArea = ((DianaSegment)startArea).getOrthogonalPerspectiveArea(startOrientation);
		  }
		  else if (startArea instanceof DianaRectangle) {
		      resultingStartArea = ((DianaRectangle)startArea).getOrthogonalPerspectiveArea(startOrientation);
		  }
		  else {
		      logger.warning("What to do with a "+startArea+" ?");
		  }
		
		  if (endArea instanceof DianaPoint) {
		      resultingEndArea = ((DianaPoint)endArea).getOrthogonalPerspectiveArea(endOrientation);
		  }
		  else if (endArea instanceof DianaSegment) {
		      resultingEndArea = ((DianaSegment)endArea).getOrthogonalPerspectiveArea(endOrientation);
		  }
		  else if (endArea instanceof DianaRectangle) {
		      resultingEndArea = ((DianaRectangle)endArea).getOrthogonalPerspectiveArea(endOrientation);
		  }
		  else {
		      logger.warning("What to do with a "+endArea+" ?");
		  }*/
	}

	/*public void debugPaint(DianaGraphics g) {
		ForegroundStyle fg = g.getDefaultForeground();
		BackgroundStyle bg = g.getDefaultBackground();
		g.setDefaultForeground(DEBUG_FOCUSED_FG);
		g.setDefaultBackground(DEBUG_FOCUSED_BG);
		if (resultingStartArea != null) {
			resultingStartArea.paint(g);
		}
		if (resultingEndArea != null) {
			resultingEndArea.paint(g);
		}
		g.setDefaultForeground(fg);
		g.setDefaultBackground(bg);
		super.paint(g);
		getMiddle().paint(g);
	}*/

	@SuppressWarnings("unused")
	public void paintWithRounds(AbstractDianaGraphics g, int arcSize) {
		g.useDefaultForegroundStyle();

		DianaPoint arcP1 = g.convertViewCoordinatesToNormalizedPoint(new Point(0, 0), 1.0);
		DianaPoint arcP2 = g.convertViewCoordinatesToNormalizedPoint(new Point(arcSize, arcSize), 1.0);
		double requestedArcWidth = arcP2.x - arcP1.x;
		double requestedArcHeight = arcP2.y - arcP1.y;

		DianaPoint current = null;

		for (int i = 0; i < _segments.size(); i++) {
			DianaSegment s = _segments.get(i);
			DianaSegment next = i < _segments.size() - 1 ? _segments.get(i + 1) : null;
			if (next == null) {
				if (current == null) {
					s.paint(g);
				}
				else {
					new DianaSegment(current, s.getP2()).paint(g);
				}
			}
			else {
				DianaPoint p = s.getP2();
				SimplifiedCardinalDirection currentOrientation;
				SimplifiedCardinalDirection nextOrientation;
				double angleStart = 0;
				boolean clockWise = false;
				DianaPoint circleCenter = null;
				boolean displayArc;

				double arcWidth = requestedArcWidth;
				double arcHeight = requestedArcHeight;

				// Prevent rounded radius exceed half of segment length
				double arcRatio = requestedArcWidth / requestedArcHeight;
				if (s.isVertical() && next.isHorizontal()) {
					if (s.getLength() < requestedArcHeight * 2) {
						arcHeight = s.getLength() / 2;
						arcWidth = arcHeight * arcRatio;
					}
					if (next.getLength() < arcWidth * 2) {
						arcWidth = next.getLength() / 2;
						arcHeight = arcWidth / arcRatio;
					}
				}
				else if (s.isHorizontal() && next.isVertical()) {
					if (s.getLength() < requestedArcWidth * 2) {
						arcWidth = s.getLength() / 2;
						arcHeight = arcWidth / arcRatio;
					}
					if (next.getLength() < arcHeight * 2) {
						arcHeight = next.getLength() / 2;
						arcWidth = arcHeight * arcRatio;
					}
				}

				if (s.isVertical() && next.isHorizontal()) {
					displayArc = true;
					if (next.getP1().x < next.getP2().x) {
						if (s.getP1().y < s.getP2().y) {
							currentOrientation = SimplifiedCardinalDirection.SOUTH;
							nextOrientation = SimplifiedCardinalDirection.EAST;
							circleCenter = new DianaPoint(p.x + arcWidth, p.y - arcHeight);
							angleStart = -180;
							clockWise = false;
						}
						else {
							currentOrientation = SimplifiedCardinalDirection.NORTH;
							nextOrientation = SimplifiedCardinalDirection.EAST;
							circleCenter = new DianaPoint(p.x + arcWidth, p.y + arcHeight);
							angleStart = 90;
							clockWise = true;
						}
					}
					else {
						if (s.getP1().y < s.getP2().y) {
							currentOrientation = SimplifiedCardinalDirection.SOUTH;
							nextOrientation = SimplifiedCardinalDirection.WEST;
							circleCenter = new DianaPoint(p.x - arcWidth, p.y - arcHeight);
							angleStart = -90;
							clockWise = true;
						}
						else {
							currentOrientation = SimplifiedCardinalDirection.NORTH;
							nextOrientation = SimplifiedCardinalDirection.WEST;
							circleCenter = new DianaPoint(p.x - arcWidth, p.y + arcHeight);
							angleStart = 0;
							clockWise = false;
						}
					}
				}
				else if (s.isHorizontal() && next.isVertical()) {
					displayArc = true;
					if (next.getP1().y < next.getP2().y) {
						if (s.getP1().x < s.getP2().x) {
							currentOrientation = SimplifiedCardinalDirection.EAST;
							nextOrientation = SimplifiedCardinalDirection.SOUTH;
							circleCenter = new DianaPoint(p.x - arcWidth, p.y + arcHeight);
							angleStart = 0;
							clockWise = true;
						}
						else {
							currentOrientation = SimplifiedCardinalDirection.WEST;
							nextOrientation = SimplifiedCardinalDirection.SOUTH;
							circleCenter = new DianaPoint(p.x + arcWidth, p.y + arcHeight);
							angleStart = 90;
							clockWise = false;
						}
					}
					else {
						if (s.getP1().x < s.getP2().x) {
							currentOrientation = SimplifiedCardinalDirection.EAST;
							nextOrientation = SimplifiedCardinalDirection.NORTH;
							circleCenter = new DianaPoint(p.x - arcWidth, p.y - arcHeight);
							angleStart = -90;
							clockWise = false;
						}
						else {
							currentOrientation = SimplifiedCardinalDirection.WEST;
							nextOrientation = SimplifiedCardinalDirection.NORTH;
							circleCenter = new DianaPoint(p.x + arcWidth, p.y - arcHeight);
							angleStart = -180;
							clockWise = true;
						}
					}
				}
				else {
					logger.warning("Unexpected situation while drawing rounded RectPolylin connectors");
					displayArc = false;
					// return;
				}

				if (displayArc) {

					DianaArc arc = new DianaArc(circleCenter, new DianaDimension(arcWidth * 2, arcHeight * 2), angleStart, 90);
					DianaPoint startRound = arc.getPointAtAngle(clockWise ? angleStart + 90 : angleStart);
					DianaPoint endRound = arc.getPointAtAngle(clockWise ? angleStart : angleStart + 90);

					// DEBUG
					/*g.setDefaultForeground(ForegroundStyle.makeStyle(Color.CYAN));
					     (new DianaEllips(circleCenter,new DianaDimension(arcWidth*2,arcHeight*2),Filling.NOT_FILLED)).paint(g);
					     startRound.paint(g);
					     endRound.paint(g);*/

					g.useDefaultForegroundStyle();

					if (current == null) {
						new DianaSegment(s.getP1(), startRound).paint(g);
					}
					else {
						new DianaSegment(current, startRound).paint(g);
					}
					arc.paint(g);
					current = endRound;
				}
				else {
					// For some reasons (for example 2 continuous colinear segments)
					// cannot display round

					g.useDefaultForegroundStyle();
					s.paint(g);
					current = s.getP2();
				}
			}
		}

		/*for (DianaSegment s : _segments) {
		      s.paint(g);
		  }*/
	}

	private void restoreDefaultLayout() {
		clearPoints();

		/*DianaHalfPlane north1 = new DianaHalfPlane(DianaLine.makeHorizontalLine(new DianaPoint(0,getMinYFor(startArea))),new DianaPoint(0,
		getMinYFor(startArea)-1));
		  //resultingStartArea = resultingStartArea.intersect(north1);
		
		  DianaHalfPlane north2 = new DianaHalfPlane(DianaLine.makeHorizontalLine(new DianaPoint(0,getMinYFor(endArea))),new DianaPoint(0,
		  getMinYFor(endArea)-1));
		  System.out.println("resultingEndArea="+resultingEndArea);
		  System.out.println("north2="+north2);
		  System.out.println("result="+resultingEndArea.intersect(north2));
		  resultingEndArea = resultingEndArea.intersect(north2);*/

		DianaArea intersect = resultingStartArea.intersect(resultingEndArea);

		/*logger.info("startOrientation="+startOrientation+" endOrientation="+endOrientation);
		      logger.info("resultingStartArea="+resultingStartArea);
		      logger.info("resultingEndArea="+resultingEndArea);
		      logger.info("Intersect="+intersect);*/

		if (intersect instanceof DianaPoint) {
			DianaPoint p = (DianaPoint) intersect;

			// DianaPoint p_start = startArea.getNearestPoint(p);
			// DianaPoint p_end = endArea.getNearestPoint(p);

			DianaPoint p_start = startOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, startArea)
					: nearestPointOnVerticalLine(p, startArea);

			DianaPoint p_end = endOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, endArea)
					: nearestPointOnVerticalLine(p, endArea);

			addToPoints(p_start);
			addToPoints(p);
			addToPoints(p_end);
			return;
		}
		else if (intersect instanceof DianaSegment) {
			DianaPoint p = ((DianaSegment) intersect).getMiddle();

			// DianaPoint p_start = startArea.getNearestPoint(p);
			// DianaPoint p_end = endArea.getNearestPoint(p);

			DianaPoint p_start = startOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, startArea)
					: nearestPointOnVerticalLine(p, startArea);

			DianaPoint p_end = endOrientation.isHorizontal() ? nearestPointOnHorizontalLine(p, endArea)
					: nearestPointOnVerticalLine(p, endArea);

			if (DianaPoint.areAligned(p_start, p, p_end) && straightWhenPossible) {
				addToPoints(p_start);
				addToPoints(p_end);
				return;
			}
			else {
				addToPoints(p_start);
				addToPoints(p);
				addToPoints(p_end);
				return;
			}
		}
		else if (intersect instanceof DianaShape || intersect.isFinite() && intersect.getEmbeddingBounds() != null) {

			DianaPoint center;

			/*logger.info("startOrientation="+startOrientation);
			   logger.info("endOrientation="+endOrientation);
			   logger.info("startArea="+startArea);
			   logger.info("endArea="+endArea);
			   logger.info("resultingStartArea="+resultingStartArea);
			   logger.info("resultingEndArea="+resultingEndArea);
			   logger.info("ShapeSpecification is "+intersect);*/

			if (intersect instanceof DianaShape) {
				center = ((DianaShape<?>) intersect).getCenter();
			}
			else { // intersect is finite with non-null bounds
				center = intersect.getEmbeddingBounds().getCenter();
			}

			DianaPoint p_start = startOrientation.isHorizontal() ? nearestPointOnHorizontalLine(center, startArea)
					: nearestPointOnVerticalLine(center, startArea);

			DianaPoint p_end = endOrientation.isHorizontal() ? nearestPointOnHorizontalLine(center, endArea)
					: nearestPointOnVerticalLine(center, endArea);

			// DianaPoint p_start = DianaPoint.getNearestPoint(center,startArea.nearestPointFrom(center,
			// startOrientation.getOpposite()),startArea.nearestPointFrom(center, startOrientation));
			// DianaPoint p_end = DianaPoint.getNearestPoint(center,endArea.nearestPointFrom(center,
			// endOrientation.getOpposite()),endArea.nearestPointFrom(center, endOrientation));

			/*logger.info("p_start="+p_start);
			   logger.info("p_end="+p_end);
			   logger.info("startArea="+startArea);
			   logger.info("endArea="+endArea);
			   logger.info("center="+center);*/

			/*if (p_start == null) {
			       p_start = startArea.getNearestPoint(center);
			       logger.warning("Cound not find nearest point on start area along axis, selecting nearest.");
			   }
			
			   if (p_end == null) {
			       p_end = endArea.getNearestPoint(center);
			       logger.warning("Cound not find nearest point on end area along axis, selecting nearest.");
			   }*/

			// This test is added to handle cases where intersection is disjointed.
			// In this case, center can be outside resulting areas, and causes
			// orientations not to be correct
			if (resultingStartArea.containsPoint(center) && resultingEndArea.containsPoint(center)) {

				if (DianaPoint.areAligned(p_start, center, p_end) && straightWhenPossible) {
					addToPoints(p_start);
					addToPoints(p_end);
					return;
				}
				else {
					addToPoints(p_start);
					addToPoints(center);
					addToPoints(p_end);
					return;
				}
			}

		}

		// logger.info("*********** For "+startOrientation+"/"+endOrientation+" CONTINUE");

		if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.EAST) {
			restoreDefaultLayoutForEastEast();
		}
		else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.WEST) {
			restoreDefaultLayoutForWestWest();
		}
		else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForNorthNorth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			restoreDefaultLayoutForSouthSouth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.WEST) {
			restoreDefaultLayoutForEastWest();
		}
		else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForSouthNorth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForEastNorth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.EAST && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			restoreDefaultLayoutForEastSouth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.NORTH) {
			restoreDefaultLayoutForWestNorth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			restoreDefaultLayoutForWestSouth();
		}
		else if (startOrientation == SimplifiedCardinalDirection.WEST && endOrientation == SimplifiedCardinalDirection.EAST) {
			computeAs(new DianaRectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		}
		else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.SOUTH) {
			computeAs(new DianaRectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		}
		else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.EAST) {
			computeAs(new DianaRectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		}
		else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.EAST) {
			computeAs(new DianaRectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		}
		else if (startOrientation == SimplifiedCardinalDirection.NORTH && endOrientation == SimplifiedCardinalDirection.WEST) {
			computeAs(new DianaRectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		}
		else if (startOrientation == SimplifiedCardinalDirection.SOUTH && endOrientation == SimplifiedCardinalDirection.WEST) {
			computeAs(new DianaRectPolylin(endArea, endOrientation, startArea, startOrientation, straightWhenPossible, overlapX, overlapY));
		}
		else {
			logger.warning("Unexpected case: startOrientation=" + startOrientation + " endOrientation=" + endOrientation);
			new Exception("???").printStackTrace();
		}

	}

	private void computeAs(DianaRectPolylin poly) {
		for (int i = poly.getPointsNb() - 1; i >= 0; i--) {
			addToPoints(poly.getPointAt(i));
		}
	}

	private static DianaPoint nearestPointOnHorizontalLine(DianaPoint p, DianaArea area) {
		DianaPoint returned = DianaPoint.getNearestPoint(p, area.nearestPointFrom(p, SimplifiedCardinalDirection.EAST),
				area.nearestPointFrom(p, SimplifiedCardinalDirection.WEST));
		if (returned == null) {
			returned = area.getNearestPoint(p);
			logger.warning("Cound not find nearest point on area along horizontal axis, selecting nearest, area=" + area);
		}
		return returned;

	}

	private static DianaPoint nearestPointOnVerticalLine(DianaPoint p, DianaArea area) {
		DianaPoint returned = DianaPoint.getNearestPoint(p, area.nearestPointFrom(p, SimplifiedCardinalDirection.NORTH),
				area.nearestPointFrom(p, SimplifiedCardinalDirection.SOUTH));
		if (returned == null) {
			returned = area.getNearestPoint(p);
			logger.warning("Cound not find nearest point on area along horizontal axis, selecting nearest, area=" + area);
		}
		return returned;

	}

	private void restoreDefaultLayoutForEastEast() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForEastEast()");
		}

		DianaLine line = DianaLine.makeVerticalLine(new DianaPoint(Math.max(getMaxXFor(startArea), getMaxXFor(endArea)) + overlapX, 0));

		DianaPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.EAST);
		DianaPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.EAST);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p2);
		DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnHorizontalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForWestWest() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForWestWest()");
		}

		DianaLine line = DianaLine.makeVerticalLine(new DianaPoint(Math.min(getMinXFor(startArea), getMinXFor(endArea)) - overlapX, 0));

		DianaPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.WEST);
		DianaPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.WEST);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p2);
		DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnHorizontalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForNorthNorth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForNorthNorth()");
		}

		DianaLine line = DianaLine.makeHorizontalLine(new DianaPoint(0, Math.min(getMinYFor(startArea), getMinYFor(endArea)) - overlapY));

		DianaPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.NORTH);
		DianaPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.NORTH);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p2);
		DianaPoint p_start = nearestPointOnVerticalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnVerticalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForSouthSouth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForSouthSouth()");
		}

		DianaLine line = DianaLine.makeHorizontalLine(new DianaPoint(0, Math.max(getMaxYFor(startArea), getMaxYFor(endArea)) + overlapY));

		DianaPoint p1 = getSignificativeAnchorAreaLocationFor(resultingStartArea.intersect(line), SimplifiedCardinalDirection.SOUTH);
		DianaPoint p2 = getSignificativeAnchorAreaLocationFor(resultingEndArea.intersect(line), SimplifiedCardinalDirection.SOUTH);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p2);
		DianaPoint p_start = nearestPointOnVerticalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnVerticalLine(p2, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForEastWest() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForEastWest()");
		}

		DianaPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.EAST);
		DianaPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.WEST);

		DianaArea startAnchorArea = startArea.getAnchorAreaFrom(SimplifiedCardinalDirection.EAST);
		DianaArea endAnchorArea = endArea.getAnchorAreaFrom(SimplifiedCardinalDirection.WEST);

		if (getMaxXFor(startAnchorArea) > getMinXFor(endAnchorArea)) {
			// if (getMinXFor(startAnchorArea) > getMaxXFor(endAnchorArea)) { /* XXX */

			double middleY = significativeStartLocation.y <= significativeEndLocation.y ? (getMaxYFor(startArea) + getMinYFor(endArea)) / 2
					: (getMinYFor(startArea) + getMaxYFor(endArea)) / 2;
			DianaLine line = DianaLine.makeHorizontalLine(new DianaPoint(0, middleY));
			// DianaLine line1 = DianaLine.makeVerticalLine(new DianaPoint(getMaxXFor(startAnchorArea)+overlapX,0));
			// DianaLine line2 = DianaLine.makeVerticalLine(new DianaPoint(getMinXFor(endAnchorArea)-overlapX,0));
			DianaLine line1 = DianaLine.makeVerticalLine(new DianaPoint(getMaxXFor(startAnchorArea) + overlapX, 0));
			DianaLine line2 = DianaLine.makeVerticalLine(new DianaPoint(getMinXFor(endAnchorArea) - overlapX, 0));

			DianaPoint p1 = getLocationFor(resultingStartArea.intersect(line1));
			DianaPoint p2 = line1.getLineIntersection(line);
			DianaPoint p3 = line2.getLineIntersection(line);
			DianaPoint p4 = getLocationFor(resultingEndArea.intersect(line2));

			// DianaPoint p_start = startArea.getNearestPoint(p1);
			// DianaPoint p_end = endArea.getNearestPoint(p4);
			DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
			DianaPoint p_end = nearestPointOnHorizontalLine(p4, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p3);
			addToPoints(p4);
			addToPoints(p_end);
		}
		else {
			DianaLine line = DianaLine.makeVerticalLine(new DianaPoint(significativeStartLocation.x <= significativeEndLocation.x
					? (getMaxXFor(startArea) + getMinXFor(endArea)) / 2 : (getMinXFor(startArea) + getMaxXFor(endArea)) / 2, 0));

			DianaPoint p1 = getLocationFor(resultingStartArea.intersect(line));
			DianaPoint p2 = getLocationFor(resultingEndArea.intersect(line));

			// DianaPoint p_start = startArea.getNearestPoint(p1);
			// DianaPoint p_end = endArea.getNearestPoint(p2);
			DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
			DianaPoint p_end = nearestPointOnHorizontalLine(p2, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p_end);

		}
	}

	private void restoreDefaultLayoutForSouthNorth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForSouthNorth()");
		}

		DianaPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.SOUTH);
		DianaPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.NORTH);

		DianaArea startAnchorArea = startArea.getAnchorAreaFrom(SimplifiedCardinalDirection.SOUTH);
		DianaArea endAnchorArea = endArea.getAnchorAreaFrom(SimplifiedCardinalDirection.NORTH);

		if (getMaxYFor(startAnchorArea) > getMinYFor(endAnchorArea)) {
			// if (getMinYFor(startAnchorArea) > getMaxYFor(endAnchorArea)) { /* XXX */

			double middleX = significativeStartLocation.x <= significativeEndLocation.x ? (getMaxXFor(startArea) + getMinXFor(endArea)) / 2
					: (getMinXFor(startArea) + getMaxXFor(endArea)) / 2;
			DianaLine line = DianaLine.makeVerticalLine(new DianaPoint(middleX, 0));
			// DianaLine line1 = DianaLine.makeHorizontalLine(new DianaPoint(0,getMinYFor(startAnchorArea)+overlapY)); /* XXX */
			// DianaLine line2 = DianaLine.makeHorizontalLine(new DianaPoint(0,getMaxYFor(endAnchorArea)-overlapY)); /* XXX */
			DianaLine line1 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMaxYFor(startAnchorArea) + overlapY));
			DianaLine line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMinYFor(endAnchorArea) - overlapY));

			DianaPoint p1 = getLocationFor(resultingStartArea.intersect(line1));
			DianaPoint p2 = line1.getLineIntersection(line);
			DianaPoint p3 = line2.getLineIntersection(line);
			DianaPoint p4 = getLocationFor(resultingEndArea.intersect(line2));

			// DianaPoint p_start = startArea.getNearestPoint(p1);
			// DianaPoint p_end = endArea.getNearestPoint(p4);
			DianaPoint p_start = nearestPointOnVerticalLine(p1, startArea);
			DianaPoint p_end = nearestPointOnVerticalLine(p4, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p3);
			addToPoints(p4);
			addToPoints(p_end);
		}
		else {

			DianaLine line = DianaLine.makeHorizontalLine(new DianaPoint(0, significativeStartLocation.y <= significativeEndLocation.y
					? (getMaxYFor(startArea) + getMinYFor(endArea)) / 2 : (getMinYFor(startArea) + getMaxYFor(endArea)) / 2));
			DianaLine alternativeLine = DianaLine
					.makeHorizontalLine(new DianaPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));

			DianaArea i1 = resultingStartArea.intersect(line);
			DianaArea i2 = resultingEndArea.intersect(line);

			if (i1 instanceof DianaEmptyArea) {
				i1 = resultingStartArea.intersect(alternativeLine);
			}
			if (i2 instanceof DianaEmptyArea) {
				i2 = resultingEndArea.intersect(alternativeLine);
			}

			if (i1 instanceof DianaEmptyArea) {
				logger.warning("Unexpected empty area for intersection: " + resultingStartArea + " and " + alternativeLine);
			}
			if (i2 instanceof DianaEmptyArea) {
				logger.warning("Unexpected empty area for intersection: " + resultingEndArea + " and " + alternativeLine);
			}

			DianaPoint p1 = getLocationFor(i1);
			DianaPoint p2 = getLocationFor(i2);

			// DianaPoint p_start = startArea.getNearestPoint(p1);
			// DianaPoint p_end = endArea.getNearestPoint(p2);
			DianaPoint p_start = nearestPointOnVerticalLine(p1, startArea);
			DianaPoint p_end = nearestPointOnVerticalLine(p2, endArea);
			addToPoints(p_start);
			addToPoints(p1);
			addToPoints(p2);
			addToPoints(p_end);

		}
	}

	private void restoreDefaultLayoutForEastNorth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForEastNorth()");
		}

		DianaPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.EAST);
		DianaPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.NORTH);

		/*System.out.println("startArea="+startArea);
		  System.out.println("endArea"+endArea);
		  System.out.println("significativeStartLocation="+significativeStartLocation);
		  System.out.println("significativeEndLocation"+significativeEndLocation);*/

		CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);
		DianaLine line1 = null;
		DianaLine line2 = null;
		DianaLine alternativeLine1 = null;
		DianaLine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			logger.warning("Unexpected call to restoreDefaultForEastNorth() while quadrant is SOUTH_EAST");
			return;
		}
		else if (quadrant == CardinalQuadrant.NORTH_EAST) {
			useAlternativeLine1 = getMaxXFor(startArea) > getMinXFor(endArea);
			line1 = DianaLine.makeVerticalLine(new DianaPoint((getMaxXFor(startArea) + getMinXFor(endArea)) / 2, 0));
			alternativeLine1 = DianaLine.makeVerticalLine(new DianaPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMinYFor(endArea) - overlapY));
		}
		else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMaxXFor(startArea) + overlapX, 0));
			useAlternativeLine2 = getMaxYFor(startArea) > getMinYFor(endArea);
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (getMaxYFor(startArea) + getMinYFor(endArea)) / 2));
			alternativeLine2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}
		else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMaxXFor(startArea) + overlapX, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMinYFor(endArea) - overlapY));
		}

		if (useAlternativeLine1) {
			line1 = alternativeLine1;
		}
		if (useAlternativeLine2) {
			line2 = alternativeLine2;
		}

		DianaArea i1 = resultingStartArea.intersect(line1);
		DianaArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof DianaEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof DianaEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		DianaPoint p1 = getLocationFor(i1);
		DianaPoint p2 = line1.getLineIntersection(line2);
		DianaPoint p3 = getLocationFor(i2);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p3);
		DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForEastSouth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForEastSouth()");
		}

		DianaPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.EAST);
		DianaPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.SOUTH);

		CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);

		DianaLine line1 = null;
		DianaLine line2 = null;
		DianaLine alternativeLine1 = null;
		DianaLine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.NORTH_EAST) {
			logger.warning("Unexpected call to restoreDefaultForEastSouth() while quadrant is NORTH_EAST");
			return;
		}
		else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			useAlternativeLine1 = getMaxXFor(startArea) > getMinXFor(endArea);
			line1 = DianaLine.makeVerticalLine(new DianaPoint((getMaxXFor(startArea) + getMinXFor(endArea)) / 2, 0));
			alternativeLine1 = DianaLine.makeVerticalLine(new DianaPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMaxYFor(endArea) + overlapY));
		}
		else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMaxXFor(startArea) + overlapX, 0));
			useAlternativeLine2 = getMinYFor(startArea) < getMaxYFor(endArea);
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (getMinYFor(startArea) + getMaxYFor(endArea)) / 2));
			alternativeLine2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}
		else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMaxXFor(startArea) + overlapX, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMaxYFor(endArea) + overlapY));
		}

		if (useAlternativeLine1) {
			line1 = alternativeLine1;
		}
		if (useAlternativeLine2) {
			line2 = alternativeLine2;
		}

		DianaArea i1 = resultingStartArea.intersect(line1);
		DianaArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof DianaEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof DianaEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		DianaPoint p1 = getLocationFor(i1);
		DianaPoint p2 = line1.getLineIntersection(line2);
		DianaPoint p3 = getLocationFor(i2);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p3);
		DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);
	}

	private void restoreDefaultLayoutForWestNorth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForWestNorth()");
		}

		DianaPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.WEST);
		DianaPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.NORTH);

		CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);
		DianaLine line1 = null;
		DianaLine line2 = null;
		DianaLine alternativeLine1 = null;
		DianaLine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			logger.warning("Unexpected call to restoreDefaultForWestNorth() while quadrant is SOUTH_WEST");
			return;
		}
		else if (quadrant == CardinalQuadrant.NORTH_WEST) {
			useAlternativeLine1 = getMinXFor(startArea) < getMaxXFor(endArea);
			line1 = DianaLine.makeVerticalLine(new DianaPoint((getMinXFor(startArea) + getMaxXFor(endArea)) / 2, 0));
			alternativeLine1 = DianaLine.makeVerticalLine(new DianaPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMinYFor(endArea) - overlapY));
		}
		else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMinXFor(startArea) - overlapX, 0));
			useAlternativeLine2 = getMaxYFor(startArea) > getMinYFor(endArea);
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (getMaxYFor(startArea) + getMinYFor(endArea)) / 2));
			alternativeLine2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}
		else if (quadrant == CardinalQuadrant.NORTH_EAST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMinXFor(startArea) - overlapX, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMinYFor(endArea) - overlapY));
		}

		if (useAlternativeLine1) {
			line1 = alternativeLine1;
		}
		if (useAlternativeLine2) {
			line2 = alternativeLine2;
		}

		DianaArea i1 = resultingStartArea.intersect(line1);
		DianaArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof DianaEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof DianaEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		DianaPoint p1 = getLocationFor(i1);
		DianaPoint p2 = line1.getLineIntersection(line2);
		DianaPoint p3 = getLocationFor(i2);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p3);
		DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);

	}

	private void restoreDefaultLayoutForWestSouth() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("restoreDefaultForWestSouth()");
		}

		DianaPoint significativeStartLocation = getSignificativeAnchorAreaLocationFor(startArea, SimplifiedCardinalDirection.WEST);
		DianaPoint significativeEndLocation = getSignificativeAnchorAreaLocationFor(endArea, SimplifiedCardinalDirection.SOUTH);

		CardinalQuadrant quadrant = DianaPoint.getCardinalQuadrant(significativeStartLocation, significativeEndLocation);

		DianaLine line1 = null;
		DianaLine line2 = null;
		DianaLine alternativeLine1 = null;
		DianaLine alternativeLine2 = null;

		boolean useAlternativeLine1 = false;
		boolean useAlternativeLine2 = false;

		if (quadrant == CardinalQuadrant.NORTH_WEST) {
			logger.warning("Unexpected call to restoreDefaultForWestSouth() while quadrant is NORTH_WEST");
			return;
		}
		else if (quadrant == CardinalQuadrant.SOUTH_WEST) {
			useAlternativeLine1 = getMinXFor(startArea) < getMaxXFor(endArea);
			line1 = DianaLine.makeVerticalLine(new DianaPoint((getMinXFor(startArea) + getMaxXFor(endArea)) / 2, 0));
			alternativeLine1 = DianaLine.makeVerticalLine(new DianaPoint((significativeStartLocation.x + significativeEndLocation.x) / 2, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMaxYFor(endArea) + overlapY));
		}
		else if (quadrant == CardinalQuadrant.NORTH_EAST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMinXFor(startArea) - overlapX, 0));
			useAlternativeLine2 = getMinYFor(startArea) < getMaxYFor(endArea);
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (getMinYFor(startArea) + getMaxYFor(endArea)) / 2));
			alternativeLine2 = DianaLine.makeHorizontalLine(new DianaPoint(0, (significativeStartLocation.y + significativeEndLocation.y) / 2));
		}
		else if (quadrant == CardinalQuadrant.SOUTH_EAST) {
			line1 = DianaLine.makeVerticalLine(new DianaPoint(getMinXFor(startArea) - overlapX, 0));
			line2 = DianaLine.makeHorizontalLine(new DianaPoint(0, getMaxYFor(endArea) + overlapY));
		}

		if (useAlternativeLine1) {
			line1 = alternativeLine1;
		}
		if (useAlternativeLine2) {
			line2 = alternativeLine2;
		}

		DianaArea i1 = resultingStartArea.intersect(line1);
		DianaArea i2 = resultingEndArea.intersect(line2);

		if (i1 instanceof DianaEmptyArea && alternativeLine1 != null) {
			// Special case, use alternative line 1
			line1 = alternativeLine1;
			i1 = resultingStartArea.intersect(line1);
		}
		if (i2 instanceof DianaEmptyArea && alternativeLine2 != null) {
			// Special case, use alternative line 1
			line2 = alternativeLine2;
			i2 = resultingEndArea.intersect(line2);
		}

		DianaPoint p1 = getLocationFor(i1);
		DianaPoint p2 = line1.getLineIntersection(line2);
		DianaPoint p3 = getLocationFor(i2);

		// DianaPoint p_start = startArea.getNearestPoint(p1);
		// DianaPoint p_end = endArea.getNearestPoint(p3);
		DianaPoint p_start = nearestPointOnHorizontalLine(p1, startArea);
		DianaPoint p_end = nearestPointOnVerticalLine(p3, endArea);
		addToPoints(p_start);
		addToPoints(p1);
		addToPoints(p2);
		addToPoints(p3);
		addToPoints(p_end);
	}

	private DianaPoint getLocationFor(DianaArea a) {
		if (a instanceof DianaEmptyArea) {
			logger.warning("Unexpected empty area while computing getLocationFor(DianaArea)");
		}

		if (a instanceof DianaPoint) {
			return ((DianaPoint) a).clone();
		}
		if (a instanceof DianaSegment) {
			return ((DianaSegment) a).getMiddle();
		}
		if (a instanceof DianaPolylin) {
			return ((DianaPolylin) a).getMiddle();
		}
		if (a instanceof DianaArc) {
			return ((DianaArc) a).getMiddle();
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints()) {
			return (DianaPoint) ((DianaUnionArea) a).getObjects().firstElement();
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getNearestPoint(a.getEmbeddingBounds().getCenter());
		}
		if (a.isFinite() && !(a instanceof DianaEmptyArea)) {
			DianaRectangle r = a.getEmbeddingBounds();
			if (r != null) {
				return r.getCenter();
			}
		}

		logger.warning("Unexpected " + a);
		if (logger.isLoggable(Level.INFO)) {
			logger.info("Infos:" + "\nstartArea=" + startArea + "\nstartOrientation=" + startOrientation + "\nendArea=" + endArea
					+ "\nendOrientation=" + endOrientation + "\noverlapX=" + overlapX + "\noverlapY=" + overlapY);
		}

		return new DianaPoint(0, 0);
	}

	private DianaPoint getSignificativeAnchorAreaLocationFor(DianaArea a, SimplifiedCardinalDirection direction) {
		DianaArea anchorArea = a.getAnchorAreaFrom(direction);

		/*if (anchorArea instanceof DianaUnionArea && ((DianaUnionArea)anchorArea).isUnionOfSegments()) {
		      anchorArea = ((DianaUnionArea)anchorArea).getObjects().firstElement();
		  }
		
		  logger.info("Anchor area for "+a+" is "+anchorArea);*/

		if (anchorArea instanceof DianaPoint) {
			return ((DianaPoint) anchorArea).clone();
		}
		if (anchorArea instanceof DianaSegment) {
			return ((DianaSegment) anchorArea).getMiddle();
		}
		if (anchorArea instanceof DianaPolylin) {
			return ((DianaPolylin) anchorArea).getMiddle();
		}
		if (anchorArea instanceof DianaArc) {
			return ((DianaArc) anchorArea).getMiddle();
		}
		if (anchorArea instanceof DianaShape) {
			return getSignificativeAnchorAreaLocationFor(((DianaShape<?>) anchorArea).getBoundingBox(), direction);
		}
		if (anchorArea instanceof DianaUnionArea && ((DianaUnionArea) anchorArea).isUnionOfPoints()) {
			return getSignificativeAnchorAreaLocationFor(((DianaUnionArea) anchorArea).getObjects().firstElement(), direction);
		}
		if (anchorArea instanceof DianaUnionArea && ((DianaUnionArea) anchorArea).isUnionOfFiniteGeometricObjects()) {
			return anchorArea.getNearestPoint(anchorArea.getEmbeddingBounds().getCenter());
		}
		logger.warning("Unexpected " + anchorArea + " for " + a);
		return new DianaPoint(0, 0);
	}

	private static double getMaxXFor(DianaArea a) {
		if (a instanceof DianaPoint) {
			return ((DianaPoint) a).x;
		}
		if (a instanceof DianaSegment) {
			return Math.max(((DianaSegment) a).getP1().x, ((DianaSegment) a).getP2().x);
		}
		if (a instanceof DianaShape) {
			return ((DianaShape<?>) a).getBoundingBox().x + ((DianaShape<?>) a).getBoundingBox().width;
		}
		if (a instanceof DianaPolylin) {
			return ((DianaPolylin) a).getBoundingBox().x + ((DianaPolylin) a).getBoundingBox().width;
		}
		if (a instanceof DianaArc) {
			return ((DianaArc) a).getBoundingBox().x + ((DianaArc) a).getBoundingBox().width;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints()) {
			double returned = Double.NEGATIVE_INFINITY;
			for (DianaArea p : ((DianaUnionArea) a).getObjects()) {
				DianaPoint pt = (DianaPoint) p;
				if (pt.x > returned) {
					returned = pt.x;
				}
			}
			return returned;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMaxX();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	private static double getMinXFor(DianaArea a) {
		if (a instanceof DianaPoint) {
			return ((DianaPoint) a).x;
		}
		if (a instanceof DianaSegment) {
			return Math.min(((DianaSegment) a).getP1().x, ((DianaSegment) a).getP2().x);
		}
		if (a instanceof DianaShape) {
			return ((DianaShape<?>) a).getBoundingBox().x;
		}
		if (a instanceof DianaPolylin) {
			return ((DianaPolylin) a).getBoundingBox().x;
		}
		if (a instanceof DianaArc) {
			return ((DianaArc) a).getBoundingBox().x;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints()) {
			double returned = Double.POSITIVE_INFINITY;
			for (DianaArea p : ((DianaUnionArea) a).getObjects()) {
				DianaPoint pt = (DianaPoint) p;
				if (pt.x < returned) {
					returned = pt.x;
				}
			}
			return returned;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMinX();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	private static double getMaxYFor(DianaArea a) {
		if (a instanceof DianaPoint) {
			return ((DianaPoint) a).y;
		}
		if (a instanceof DianaSegment) {
			return Math.max(((DianaSegment) a).getP1().y, ((DianaSegment) a).getP2().y);
		}
		if (a instanceof DianaShape) {
			return ((DianaShape<?>) a).getBoundingBox().y + ((DianaShape<?>) a).getBoundingBox().height;
		}
		if (a instanceof DianaPolylin) {
			return ((DianaPolylin) a).getBoundingBox().y + ((DianaPolylin) a).getBoundingBox().height;
		}
		if (a instanceof DianaArc) {
			return ((DianaArc) a).getBoundingBox().y + ((DianaArc) a).getBoundingBox().height;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints()) {
			double returned = Double.NEGATIVE_INFINITY;
			for (DianaArea p : ((DianaUnionArea) a).getObjects()) {
				DianaPoint pt = (DianaPoint) p;
				if (pt.y > returned) {
					returned = pt.y;
				}
			}
			return returned;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMaxY();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	private static double getMinYFor(DianaArea a) {
		if (a instanceof DianaPoint) {
			return ((DianaPoint) a).y;
		}
		if (a instanceof DianaSegment) {
			return Math.min(((DianaSegment) a).getP1().y, ((DianaSegment) a).getP2().y);
		}
		if (a instanceof DianaShape) {
			return ((DianaShape<?>) a).getBoundingBox().y;
		}
		if (a instanceof DianaPolylin) {
			return ((DianaPolylin) a).getBoundingBox().y;
		}
		if (a instanceof DianaArc) {
			return ((DianaArc) a).getBoundingBox().y + ((DianaArc) a).getBoundingBox().height;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfPoints()) {
			double returned = Double.POSITIVE_INFINITY;
			for (DianaArea p : ((DianaUnionArea) a).getObjects()) {
				DianaPoint pt = (DianaPoint) p;
				if (pt.y < returned) {
					returned = pt.y;
				}
			}
			return returned;
		}
		if (a instanceof DianaUnionArea && ((DianaUnionArea) a).isUnionOfFiniteGeometricObjects()) {
			return a.getEmbeddingBounds().getMinY();
		}
		logger.warning("Unexpected " + a);
		return 0;
	}

	public SimplifiedCardinalDirection getEndOrientation() {
		if (endOrientation == null && getSegmentNb() > 0) {
			endOrientation = getApproximatedOrientationOfSegment(getSegments().size() - 1);
		}
		return endOrientation;
	}

	public SimplifiedCardinalDirection getStartOrientation() {
		if (startOrientation == null && getSegmentNb() > 0) {
			startOrientation = getApproximatedOrientationOfSegment(0);
		}
		return startOrientation;
	}

	/**
	 * Return the segment on which middle is located
	 * 
	 * @return
	 */
	public DianaSegment getMiddleSegment() {
		double middleDistancePath = getLength() / 2;
		double distance = 0;
		for (DianaSegment s : getSegments()) {
			if (distance <= middleDistancePath && distance + s.getLength() >= middleDistancePath) {
				return s;
			}
			distance += s.getLength();
		}
		logger.warning("Unexpected situation while computing middle segment of rect polylin");
		return null;
	}

	public DianaPoint getNearestPointLocatedOnRoundedRepresentation(DianaPoint p, double arcWidth, double arcHeight) {
		DianaArc arc = getArcForNearestPointLocatedOnRoundedRepresentation(p, arcWidth, arcHeight);
		if (arc == null) {
			return getNearestPoint(p); // Point located outside arc
		}
		else {
			return arc.getNearestPoint(p);
		}
	}

	public DianaArc getArcForNearestPointLocatedOnRoundedRepresentation(DianaPoint p, double arcWidth, double arcHeight) {
		DianaPoint point = getNearestPoint(p);
		DianaSegment segment = getProjectionSegment(point);
		// DianaPoint point = segment.getNearestPointOnSegment(p);
		int segmentId = getSegmentIndex(segment);
		SimplifiedCardinalDirection orientation = getApproximatedOrientationOfSegment(segmentId);
		DianaArc arc = null;
		DianaArc startArc = null;
		DianaArc endArc = null;
		if (segmentId > 0) {
			DianaSegment previousSegment = getSegmentAt(segmentId - 1);
			startArc = getRoundedArc(previousSegment, segment, arcWidth, arcHeight);
		}
		if (segmentId < getSegmentNb() - 1) {
			DianaSegment nextSegment = getSegmentAt(segmentId + 1);
			endArc = getRoundedArc(segment, nextSegment, arcWidth, arcHeight);
		}
		if (orientation == SimplifiedCardinalDirection.NORTH) {
			if (segment.y1 - point.y < arcHeight) {
				arc = startArc;
			}
			if (point.y - segment.y2 < arcHeight) {
				arc = endArc;
			}
		}
		if (orientation == SimplifiedCardinalDirection.SOUTH) {
			if (point.y - segment.y1 < arcHeight) {
				arc = startArc;
			}
			if (segment.y2 - point.y < arcHeight) {
				arc = endArc;
			}
		}
		if (orientation == SimplifiedCardinalDirection.WEST) {
			if (segment.x1 - point.x < arcWidth) {
				arc = startArc;
			}
			if (point.x - segment.x2 < arcWidth) {
				arc = endArc;
			}
		}
		if (orientation == SimplifiedCardinalDirection.EAST) {
			if (point.x - segment.x1 < arcWidth) {
				arc = startArc;
			}
			if (segment.x2 - point.x < arcWidth) {
				arc = endArc;
			}
		}
		return arc;
	}

	protected DianaArc getRoundedArc(DianaSegment s1, DianaSegment s2, double arcWidth, double arcHeight) {
		if (s1 == null || s2 == null) {
			return null;
		}

		SimplifiedCardinalDirection orientation1 = s1.getApproximatedOrientation();
		SimplifiedCardinalDirection orientation2 = s2.getApproximatedOrientation();

		// Prevent rounded radius exceed half of segment length
		double arcRatio = arcWidth / arcHeight;
		if (s1.isVertical() && s2.isHorizontal()) {
			if (s1.getLength() < arcHeight * 2) {
				arcHeight = s1.getLength() / 2;
				arcWidth = arcHeight * arcRatio;
			}
			if (s2.getLength() < arcWidth * 2) {
				arcWidth = s2.getLength() / 2;
				arcHeight = arcWidth / arcRatio;
			}
		}
		else if (s1.isHorizontal() && s2.isVertical()) {
			if (s1.getLength() < arcWidth * 2) {
				arcWidth = s1.getLength() / 2;
				arcHeight = arcWidth / arcRatio;
			}
			if (s2.getLength() < arcHeight * 2) {
				arcHeight = s2.getLength() / 2;
				arcWidth = arcHeight * arcRatio;
			}
		}

		if (orientation1 == SimplifiedCardinalDirection.NORTH) {
			if (orientation2 == SimplifiedCardinalDirection.EAST) {
				return new DianaArc(new DianaPoint(s1.x2 + arcWidth, s1.y2 + arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), 180,
						-90);
			}
			if (orientation2 == SimplifiedCardinalDirection.WEST) {
				return new DianaArc(new DianaPoint(s1.x2 - arcWidth, s1.y2 + arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), 0, 90);
			}
			return null;
		}
		if (orientation1 == SimplifiedCardinalDirection.SOUTH) {
			if (orientation2 == SimplifiedCardinalDirection.EAST) {
				return new DianaArc(new DianaPoint(s1.x2 + arcWidth, s1.y2 - arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), -180,
						90);
			}
			if (orientation2 == SimplifiedCardinalDirection.WEST) {
				return new DianaArc(new DianaPoint(s1.x2 - arcWidth, s1.y2 - arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), 0, -90);
			}
			return null;
		}
		if (orientation1 == SimplifiedCardinalDirection.EAST) {
			if (orientation2 == SimplifiedCardinalDirection.NORTH) {
				return new DianaArc(new DianaPoint(s1.x2 - arcWidth, s1.y2 - arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), -90,
						90);
			}
			if (orientation2 == SimplifiedCardinalDirection.SOUTH) {
				return new DianaArc(new DianaPoint(s1.x2 - arcWidth, s1.y2 + arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), 90,
						-90);
			}
			return null;
		}
		if (orientation1 == SimplifiedCardinalDirection.WEST) {
			if (orientation2 == SimplifiedCardinalDirection.NORTH) {
				return new DianaArc(new DianaPoint(s1.x2 + arcWidth, s1.y2 - arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), -90,
						-90);
			}
			if (orientation2 == SimplifiedCardinalDirection.SOUTH) {
				return new DianaArc(new DianaPoint(s1.x2 + arcWidth, s1.y2 + arcHeight), new DianaDimension(arcWidth * 2, arcHeight * 2), 90, 90);
			}
			return null;
		}
		return null;
	}

	/**
	 * Return the nearest segment (if any) on which supplied point p might be projected
	 * 
	 * @return the related segment, or null if no such segment has been found
	 */
	public DianaSegment getProjectionSegment(DianaPoint p) {
		double shortestDistance = Double.POSITIVE_INFINITY;
		DianaSegment returned = null;
		for (DianaSegment s : getSegments()) {
			if (s.projectionIntersectsInsideSegment(p)) {
				double distance = DianaPoint.distance(p, s.getProjection(p));
				if (distance <= shortestDistance) {
					returned = s;
					shortestDistance = distance;
				}
			}
		}
		return returned;
	}

	public SimplifiedCardinalDirection getOrientationOfSegment(int index) {
		DianaSegment segment = getSegmentAt(index);
		if (segment != null) {
			return segment.getOrientation();
		}
		return null;

		/*double angle = segment.getAngle();
		
		  if (Math.abs(angle) < EPSILON) return SimplifiedCardinalDirection.WEST;
		  else if ((Math.abs(angle-Math.PI) < EPSILON)
		          || (Math.abs(angle+Math.PI) < EPSILON)) return SimplifiedCardinalDirection.EAST;
		  else if (Math.abs(angle-Math.PI/2) < EPSILON) return SimplifiedCardinalDirection.SOUTH;
		  else if (Math.abs(angle-3*Math.PI/2) < EPSILON) return SimplifiedCardinalDirection.NORTH;
		  else return null;*/
	}

	public SimplifiedCardinalDirection getApproximatedOrientationOfSegment(int index) {
		DianaSegment segment = getSegmentAt(index);
		if (segment != null) {
			return segment.getApproximatedOrientation();
		}
		return null;

		/*SimplifiedCardinalDirection returned = getOrientationOfSegment(index);
		  if (returned != null) return returned;
		
		  DianaSegment segment = getSegmentAt(index);
		  return DianaPoint.getSimplifiedOrientation(segment.getP1(), segment.getP2());*/
	}

	@Override
	public DianaRectPolylin transform(AffineTransform t) {
		Vector<DianaPoint> points = new Vector<>();
		for (DianaPoint p : _points) {
			points.add(p.transform(t));
		}
		DianaRectPolylin returned = new DianaRectPolylin(points);
		return returned;
	}

	@Override
	public DianaRectPolylin clone() {
		// return (DianaRectPolylin)super.clone();
		DianaRectPolylin returned = new DianaRectPolylin();
		returned.overlapX = overlapX;
		returned.overlapY = overlapY;
		returned.straightWhenPossible = straightWhenPossible;
		for (DianaPoint p : getPoints()) {
			returned.addToPoints(p.clone());
		}
		return returned;
	}

	/**
	 * Return a flag indicating wether this polylin is normalized or not A rect polylin is normalized when: - all segments are rect (their
	 * orientations matches cardinal orientation NORTH, EAST, SOUTH, WEST) - no extra points are found (no point located inside a segment,
	 * which means that no colinear adjascent segment were found)
	 * 
	 * @return true if this polylin is normalized, false otherwise
	 */
	public boolean isNormalized() {
		return !hasNonRectSegments() && !hasExtraPoints();
	}

	/**
	 * Return a flag indicating wether this polylin has non-rect segment (their orientations doesn't matches cardinal orientation NORTH,
	 * EAST, SOUTH, WEST)
	 * 
	 * @return
	 */
	public boolean hasNonRectSegments() {
		for (int i = 0; i < getSegmentNb(); i++) {
			if (getOrientationOfSegment(i) == null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return a flag indicating wether this polylin has extra point (no point located inside a segment, which means that no colinear
	 * adjascent segment were found)
	 * 
	 * @return true if this polylin has extra points, false otherwise
	 */
	public boolean hasExtraPoints() {
		return hasEqualsAdjascentPoints() || hasColinearAdjascentSegments();
	}

	// Check for 2 equal adjascent point
	private boolean hasEqualsAdjascentPoints() {
		DianaPoint previous = null;
		for (DianaPoint p : getPoints()) {
			if (previous != null && previous.equals(p)) {
				return true;
			}
			previous = p;
		}
		return false;
	}

	// Remove point, if any
	private void removeFirstEqualsAdjascentPoints() {
		DianaPoint previous = null;
		for (int i = 0; i < getPointsNb(); i++) {
			if (previous != null && previous.equals(getPointAt(i))) {
				removePointAtIndex(i);
				return;
			}
			previous = getPointAt(i);
		}
	}

	// Check for 2 colinear adjascent segments
	private boolean hasColinearAdjascentSegments() {
		DianaSegment previous_s = null;
		for (DianaSegment s : getSegments()) {
			if (previous_s != null && previous_s.getApproximatedOrientation().equals(s.getApproximatedOrientation())) {
				return true;
			}
			previous_s = s;
		}
		return false;
	}

	// Remove point, if any
	private void removeFirstColinearAdjascentSegments() {
		DianaSegment previous_s = null;
		for (int i = 0; i < getSegmentNb(); i++) {
			DianaSegment s = getSegmentAt(i);
			if (previous_s != null && previous_s.getApproximatedOrientation().equals(s.getApproximatedOrientation())) {
				removePointAtIndex(i);
				return;
			}
			previous_s = s;
		}
	}

	public void removeExtraPoints() {
		/*logger.info("removeExtraPoints()");
		  for (DianaPoint p : getPoints()) {
		      System.out.println("WAS:"+p);
		  }
		  for (DianaSegment s : getSegments()) {
		      System.out.println("WAS:"+s);
		  }*/
		while (hasEqualsAdjascentPoints()) {
			removeFirstEqualsAdjascentPoints();
		}
		while (hasColinearAdjascentSegments()) {
			removeFirstColinearAdjascentSegments();
		}
		/*for (DianaPoint p : getPoints()) {
		      System.out.println("NOW:"+p);
		  }
		  for (DianaSegment s : getSegments()) {
		      System.out.println("NOW:"+s);
		  }*/
	}

	public boolean isNormalizable() {
		if (isNormalized()) {
			return true;
		}
		DianaRectPolylin normalizedRect = makeNormalizedRectPolylin();
		return normalizedRect.getPointsNb() == getPointsNb();
	}

	public void normalize() {
		if (isNormalized()) {
			return;
		}
		DianaRectPolylin normalizedRect = makeNormalizedRectPolylin();
		if (normalizedRect.getPointsNb() != getPointsNb()) {
			throw new IllegalArgumentException("Cannot normalize a non-normalizable rect");
		}
		for (int i = 0; i < getPointsNb(); i++) {
			updatePointAt(i, normalizedRect.getPointAt(i));
		}
	}

	/**
	 * Build and return a normalized rect polylin given this polylin
	 * 
	 * @return
	 */
	public DianaRectPolylin makeNormalizedRectPolylin() {

		DianaRectPolylin updatedPolylin = clone();

		// Now iterate both sides
		/*DianaPoint*/
		currentPointStartingSide = updatedPolylin.getPointAt(0);
		/*DianaPoint*/
		currentPointEndingSide = updatedPolylin.getPointAt(updatedPolylin.getPointsNb() - 1);
		int currentStartIndex = 0;
		int currentEndIndex = updatedPolylin.getPointsNb() - 1;

		for (int i = 1; i < getPointsNb() / 2; i++) {
			// Starting side
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Checking point at " + i);
			}
			currentStartIndex = i;

			DianaPoint previousPointStartingSide = updatedPolylin.getPointAt(i - 1);
			currentPointStartingSide = updatedPolylin.getPointAt(i);
			SimplifiedCardinalDirection orientation = updatedPolylin.getApproximatedOrientationOfSegment(i - 1);
			if (orientation.isVertical()) {
				if (currentPointStartingSide.x != previousPointStartingSide.x) {
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Updating point at " + i + " for vertical orientation");
					}
					currentPointStartingSide.x = previousPointStartingSide.x;
					updatedPolylin.updatePointAt(i, currentPointStartingSide);
				}
			}
			else if (orientation.isHorizontal()) {
				if (currentPointStartingSide.y != previousPointStartingSide.y) {
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Updating point at " + i + " for horizontal orientation");
					}
					currentPointStartingSide.y = previousPointStartingSide.y;
					updatedPolylin.updatePointAt(i, currentPointStartingSide);
				}
			}

			// Ending side
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Checking point at " + (updatedPolylin.getPointsNb() - i - 1));
			}
			currentEndIndex = updatedPolylin.getPointsNb() - i - 1;

			DianaPoint previousPointEndingSide = updatedPolylin.getPointAt(updatedPolylin.getPointsNb() - i);
			currentPointEndingSide = updatedPolylin.getPointAt(updatedPolylin.getPointsNb() - i - 1);
			SimplifiedCardinalDirection orientation2 = updatedPolylin
					.getApproximatedOrientationOfSegment(updatedPolylin.getPointsNb() - i - 1);
			if (orientation2.isVertical()) {
				if (currentPointEndingSide.x != previousPointEndingSide.x) {
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Updating point at " + (updatedPolylin.getPointsNb() - i - 1) + " for vertical orientation");
					}
					currentPointEndingSide.x = previousPointEndingSide.x;
					updatedPolylin.updatePointAt(updatedPolylin.getPointsNb() - i - 1, currentPointEndingSide);
				}
			}
			else if (orientation2.isHorizontal()) {
				if (currentPointEndingSide.y != previousPointEndingSide.y) {
					if (logger.isLoggable(Level.FINEST)) {
						logger.finest("Updating point at " + (updatedPolylin.getPointsNb() - i - 1) + " for horizontal orientation");
					}
					currentPointEndingSide.y = previousPointEndingSide.y;
					updatedPolylin.updatePointAt(updatedPolylin.getPointsNb() - i - 1, currentPointEndingSide);
				}
			}
		}
		// Finish updating
		if (logger.isLoggable(Level.FINEST)) {
			logger.finest("Still have to finish updating, currentStartIndex=" + currentStartIndex + " currentEndIndex=" + currentEndIndex);
		}

		if (currentPointEndingSide.equals(currentPointStartingSide)
				|| Math.abs(currentPointEndingSide.x - currentPointStartingSide.x) < EPSILON
				|| Math.abs(currentPointEndingSide.y - currentPointStartingSide.y) < EPSILON) {
			// No need to append extra path
		}
		else {
			Vector<SimplifiedCardinalDirection> excludedStartOrientations = new Vector<>();
			Vector<SimplifiedCardinalDirection> excludedEndOrientations = new Vector<>();
			if (currentStartIndex - 1 >= 0 && currentStartIndex - 1 < updatedPolylin.getSegmentNb()) {
				excludedStartOrientations.add(updatedPolylin.getApproximatedOrientationOfSegment(currentStartIndex - 1).getOpposite());
				// System.out.println("excludedStartOrientations="+excludedStartOrientations);
			}
			if (currentEndIndex >= 0 && currentEndIndex < updatedPolylin.getSegmentNb()) {
				excludedEndOrientations.add(updatedPolylin.getApproximatedOrientationOfSegment(currentEndIndex));
				// System.out.println("excludedEndOrientations="+excludedEndOrientations);
			}
			// System.out.println("overlap="+overlap);
			if (currentEndIndex - currentStartIndex == 1) {
				/*DianaRectPolylin*/
				missingPath = makeShortestRectPolylin(currentPointStartingSide, currentPointEndingSide, true, overlapX, overlapY,
						excludedStartOrientations, excludedEndOrientations);
				for (int i = 1; i < missingPath.getPointsNb() - 1; i++) {
					updatedPolylin.insertPointAtIndex(missingPath.getPointAt(i), currentStartIndex + i);
				}
			}
			else if (currentEndIndex - currentStartIndex == 2) {
				/*DianaRectPolylin*/// missingPath = makeShortestRectPolylin(currentPointStartingSide, currentPointEndingSide, true, overlap,
				// updatedPolylin.getPointAt(currentStartIndex+1), excludedStartOrientations, excludedEndOrientations);
				missingPath = makeRectPolylinCrossingPoint(currentPointStartingSide, currentPointEndingSide,
						updatedPolylin.getPointAt(currentStartIndex + 1), true, overlapX, overlapY, excludedStartOrientations,
						excludedEndOrientations);
				updatedPolylin.removePointAtIndex(currentStartIndex + 1);
				for (int i = 1; i < missingPath.getPointsNb() - 1; i++) {
					updatedPolylin.insertPointAtIndex(missingPath.getPointAt(i), currentStartIndex + i);
				}
			}
		}

		return updatedPolylin;
	}

	// TODO debug: remove this
	public DianaRectPolylin missingPath;
	public DianaPoint currentPointStartingSide;
	public DianaPoint currentPointEndingSide;

	public double getOverlapX() {
		return overlapX;
	}

	public void setOverlapX(double overlap) {
		this.overlapX = overlap;
	}

	public double getOverlapY() {
		return overlapY;
	}

	public void setOverlapY(double overlap) {
		this.overlapY = overlap;
	}

	public DianaArea getEndArea() {
		// if no start area defined, return start point
		if (endArea == null && getPointsNb() > 0) {
			return getPointAt(getPointsNb() - 1);
		}
		return endArea;
	}

	public DianaArea getStartArea() {
		// if no start area defined, return start point
		if (startArea == null && getPointsNb() > 0) {
			return getPointAt(0);
		}
		return startArea;
	}

	public boolean doesRespectAllConstraints() {
		return respectAllConstraints && getPointsNb() > 0;
	}
}

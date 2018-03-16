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

package org.openflexo.diana.geom.area;

import java.awt.geom.AffineTransform;

import org.openflexo.diana.geom.FGEAbstractLine;
import org.openflexo.diana.geom.FGELine;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.FGERectangle;
import org.openflexo.diana.geom.FGEShape;
import org.openflexo.diana.geom.FGEGeometricObject.SimplifiedCardinalDirection;
import org.openflexo.diana.graphics.AbstractFGEGraphics;

/**
 * This interface describe the general concept of area, which is a formal set of points in the 2D-plane.
 * 
 * Notion of area is here purely formal and concerns finite (see {@link FGEShape} but also infinite object such as lines (see
 * {@link FGELine} or {@link FGEHalfLine}), half-plane (see {@link FGEHalfPlane}), quarter-plane (see {@link FGEQuarterPlane}), bands (see
 * {@link FGEBand}) or half-bands (see {@link FGEHalfBand})
 * 
 * This geometry-library is built above java.awt.geom which codes only finite geometry, and extends this to formal notions.
 * 
 * @author sylvain
 * 
 */
public interface FGEArea extends Cloneable {

	/**
	 * Test if this area contains supplied (whole) area (all points contained in a are also contained in this area)
	 * 
	 * @param a
	 * @return
	 */
	public boolean containsArea(FGEArea a);

	/**
	 * Test if this area contains supplied (whole) line (all points contained in a are also contained in this area)
	 * 
	 * @param l
	 * @return
	 */
	public boolean containsLine(FGEAbstractLine<?> l);

	/**
	 * Test if this area contains supplied point
	 * 
	 * @param p
	 * @return
	 */
	public boolean containsPoint(FGEPoint p);

	/**
	 * Return intersection of this area this supplied area Try to compute and return the most relevant object descriving resulting area,
	 * otherwise return an {@link FGEIntersectionArea}
	 * 
	 * @param area
	 * @return
	 */
	public FGEArea intersect(FGEArea area);

	/**
	 * Return union of this area this supplied area Try to compute and return the most relevant object descriving resulting area, otherwise
	 * return an {@link FGEUnionArea}
	 * 
	 * @param area
	 * @return
	 */
	public FGEArea union(FGEArea area);

	/**
	 * Return substraction of this area this supplied area Try to compute and return the most relevant object descriving resulting area,
	 * otherwise return an {@link FGESubstractionArea}
	 * 
	 * @param area
	 *            area to substract to this area
	 * @param isStrict
	 *            boolean indicating if a point located in the border of substracted area should be consider inside resulting area or not If
	 * 
	 *            <pre>
	 * isStrict
	 * </pre>
	 * 
	 *            is true, point should NOT be considered.
	 * @return
	 */
	public FGEArea substract(FGEArea area, boolean isStrict);

	/**
	 * Return xor of this area this supplied area Try to compute and return the most relevant object descriving resulting area, otherwise
	 * return an {@link FGEExclusiveOrArea}
	 * 
	 * @param area
	 * @return
	 */
	public FGEArea exclusiveOr(FGEArea area);

	/**
	 * Clone this area
	 * 
	 * @return
	 */
	public FGEArea clone();

	/**
	 * Compute and return a new {@link FGEArea} as a result of transformation of current area with supplied {@link AffineTransform}
	 * 
	 * @param t
	 * @return
	 */
	public FGEArea transform(AffineTransform t);

	/**
	 * Paint this area to supplied {@link FGEGraphics}
	 * 
	 * @param g
	 */
	public void paint(AbstractFGEGraphics g);

	/**
	 * Compute and return nearest point of supplied point belonging to this area (returned point must be contained into this area)
	 * 
	 * @param aPoint
	 * @return
	 */
	public FGEPoint getNearestPoint(FGEPoint aPoint);

	/**
	 * Compute and return an area defined as the one which is straight-accessible from a given direction
	 * 
	 * @param direction
	 * @return
	 */
	public FGEArea getAnchorAreaFrom(SimplifiedCardinalDirection direction);

	/**
	 * Compute and return an area defined as the one containing all points that are straight-accessible from a given direction (a segment of
	 * supplied orientation can join any point of returned area to this area)
	 * 
	 * @param orientation
	 * @return
	 */
	public FGEArea getOrthogonalPerspectiveArea(SimplifiedCardinalDirection orientation);

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
	public FGEPoint nearestPointFrom(FGEPoint from, SimplifiedCardinalDirection orientation);

	/**
	 * Return a flag indicating if this area is finite or not
	 * 
	 * @return
	 */
	public boolean isFinite();

	/**
	 * If this area is finite, return embedding bounds as a FGERectangle (this is not guaranteed to be optimal in some cases). For
	 * non-finite areas (if this area is not finite), return null
	 * 
	 * @return
	 */
	public FGERectangle getEmbeddingBounds();

}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.diana.shapes;

import java.util.List;

import org.openflexo.diana.cp.ControlPoint;
import org.openflexo.diana.geom.DianaLine;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.area.DianaArea;
import org.openflexo.diana.graphics.DianaShapeGraphics;
import org.openflexo.diana.shapes.ShapeSpecification.ShapeType;

public interface Shape<SS extends ShapeSpecification> {

	public abstract SS getShapeSpecification();

	/**
	 * Retrieve all control area used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<ControlPoint> getControlAreas();

	/**
	 * Retrieve all control points used to manage this connector
	 * 
	 * @return
	 */
	public abstract List<ControlPoint> getControlPoints();

	public abstract DianaShape<?> getShape();

	public abstract DianaShape<?> getOutline();

	public abstract ShapeType getShapeType();

	/**
	 * Return nearest point located on outline, asserting aPoint is related to shape coordinates, and normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	public abstract DianaPoint nearestOutlinePoint(DianaPoint aPoint);

	/**
	 * Return flag indicating if position represented is located inside shape, asserting aPoint is related to shape coordinates, and
	 * normalized to shape
	 * 
	 * @param aPoint
	 * @return
	 */
	public abstract boolean isPointInsideShape(DianaPoint aPoint);

	/**
	 * Compute point where supplied line intersects with shape outline trying to minimize distance from "from" point
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	public abstract DianaPoint outlineIntersect(DianaLine line, DianaPoint from);

	/**
	 * Compute point where a line formed by current shape's center and "from" point intersects with shape outline trying to minimize
	 * distance from "from" point This implementation provide simplified computation with outer bounds (relative coordinates (0,0)-(1,1))
	 * and must be overriden when required
	 * 
	 * Returns null if no intersection was found
	 * 
	 * @param aLine
	 * @param from
	 * @return
	 */
	public abstract DianaPoint outlineIntersect(DianaPoint from);

	public abstract DianaArea getAllowedHorizontalConnectorLocationFromEast();

	public abstract DianaArea getAllowedHorizontalConnectorLocationFromWest();

	public abstract DianaArea getAllowedVerticalConnectorLocationFromNorth();

	public abstract DianaArea getAllowedVerticalConnectorLocationFromSouth();

	// public abstract void paintShadow(DianaShapeGraphics g);

	public abstract void paintShape(DianaShapeGraphics g);

}

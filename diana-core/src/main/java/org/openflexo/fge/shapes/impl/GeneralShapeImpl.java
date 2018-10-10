/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

package org.openflexo.fge.shapes.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.impl.FGEObjectImpl;
import org.openflexo.fge.shapes.GeneralShape;

public abstract class GeneralShapeImpl extends ShapeSpecificationImpl implements GeneralShape {

	@Override
	public ShapeType getShapeType() {
		return ShapeType.GENERALSHAPE;
	}

	@Override
	public FGEGeneralShape<?> makeNormalizedFGEShape(ShapeNode<?> node) {
		FGEGeneralShape<?> returned = new FGEGeneralShape<>(getClosure());
		returned.beginAtPoint(getStartPoint());
		for (GeneralShapePathElement pathElement : getPathElements()) {
			if (pathElement instanceof SegmentPathElement) {
				returned.addSegment(((SegmentPathElement) pathElement).getPoint());
			}
			else if (pathElement instanceof QuadCurvePathElement) {
				returned.addQuadCurve(((QuadCurvePathElement) pathElement).getControlPoint(),
						((QuadCurvePathElement) pathElement).getPoint());
			}
			else if (pathElement instanceof CubicCurvePathElement) {
				returned.addCubicCurve(((CubicCurvePathElement) pathElement).getControlPoint1(),
						((CubicCurvePathElement) pathElement).getControlPoint2(), ((CubicCurvePathElement) pathElement).getPoint());
			}
		}
		return returned;
	}

	public static abstract class GeneralShapePathElementImpl extends FGEObjectImpl implements GeneralShapePathElement {

	}

	public static abstract class SegmentPathElementImpl extends GeneralShapePathElementImpl implements SegmentPathElement {

	}

	public static abstract class QuadCurvePathElementImpl extends GeneralShapePathElementImpl implements QuadCurvePathElement {

	}

	public static abstract class CubicCurvePathElementImpl extends GeneralShapePathElementImpl implements CubicCurvePathElement {

	}

}

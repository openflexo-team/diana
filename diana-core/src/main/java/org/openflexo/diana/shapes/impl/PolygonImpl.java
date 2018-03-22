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

package org.openflexo.diana.shapes.impl;

import java.util.List;
import java.util.logging.Logger;

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.DianaGeometricObject.Filling;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.geom.DianaPolygon;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.shapes.Polygon;

public abstract class PolygonImpl extends ShapeSpecificationImpl implements Polygon {

	private static final Logger logger = Logger.getLogger(PolygonImpl.class.getPackage().getName());

	// *******************************************************************************
	// * Constructor *
	// *******************************************************************************

	/**
	 * This constructor should not be used, as it is invoked by PAMELA framework to create objects, as well as during deserialization
	 */
	public PolygonImpl() {
		super();
	}

	public PolygonImpl(List<DianaPoint> points) {
		this();
		for (DianaPoint pt : points) {
			addToPoints(pt);
		}
	}

	public PolygonImpl(DianaPolygon polygon) {
		this();
		for (DianaPoint pt : polygon.getPoints()) {
			addToPoints(pt);
		}
	}

	@Override
	public ShapeType getShapeType() {
		return ShapeType.CUSTOM_POLYGON;
	}

	@Override
	public DianaShape<?> makeDianaShape(ShapeNode<?> node) {
		return new DianaPolygon(Filling.FILLED, getPoints());
	}

}

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

import java.util.ArrayList;
import java.util.List;

import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.DianaShape;
import org.openflexo.diana.geom.DianaShapeUnion;
import org.openflexo.diana.shapes.ShapeSpecification;
import org.openflexo.diana.shapes.ShapeUnion;

public abstract class ShapeUnionImpl extends ShapeSpecificationImpl implements ShapeUnion {

	@Override
	public ShapeType getShapeType() {
		return ShapeType.UNION;
	}

	@Override
	public DianaShapeUnion makeNormalizedDianaShape(ShapeNode<?> node) {
		List<DianaShape<?>> shapes = new ArrayList<>();
		for (ShapeSpecification shapeSpecification : getShapes()) {
			shapes.add(shapeSpecification.makeDianaShape(node));
		}
		return new DianaShapeUnion(shapes);
	}

	/*@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("ShapeUnionImpl: nObjects=" + getShapes().size() + "\n");
		for (int i = 0; i < getShapes().size(); i++) {
			sb.append(" " + (i + 1) + " > " + getShapes().get(i) + "\n");
		}
		return sb.toString();
	}*/
}

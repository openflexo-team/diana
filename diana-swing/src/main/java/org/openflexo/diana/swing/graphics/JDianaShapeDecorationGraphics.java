/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-swing, a component of the software infrastructure 
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

package org.openflexo.diana.swing.graphics;

import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.diana.ShapeGraphicalRepresentation;
import org.openflexo.diana.Drawing.ShapeNode;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.graphics.DianaShapeDecorationGraphics;
import org.openflexo.diana.swing.view.JShapeView;

public class JDianaShapeDecorationGraphics extends JDianaGraphics implements DianaShapeDecorationGraphics {

	private static final Logger LOGGER = Logger.getLogger(JDianaShapeDecorationGraphics.class.getPackage().getName());

	public <O> JDianaShapeDecorationGraphics(ShapeNode<O> node, JShapeView<O> view) {
		super(node, view);
	}

	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}

//	public double getWidth() {//infinite loop
//		return getWidth();
//	}
//
//	public double getHeight() {//infinite loop
//		return getHeight();
//	}

	// Decoration graphics doesn't use normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) (x * getScale()), (int) (y * getScale()));
	}

	// Decoration graphics doesn't use normalized coordinates system
	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new DianaPoint(x / getScale(), y / getScale());
	}

}

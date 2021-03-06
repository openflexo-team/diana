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

import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.Drawing.RootNode;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.graphics.DianaDrawingDecorationGraphics;
import org.openflexo.diana.swing.view.JDrawingView;

public class JDianaDrawingDecorationGraphics extends JDianaGraphics implements DianaDrawingDecorationGraphics {

	private static final Logger logger = Logger.getLogger(JDianaDrawingDecorationGraphics.class.getPackage().getName());

	public <O> JDianaDrawingDecorationGraphics(RootNode<O> rootNode, JDrawingView<O> view) {
		super(rootNode, view);
	}

	@Override
	public RootNode<?> getNode() {
		return (RootNode<?>) super.getNode();
	}

	@Override
	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return (DrawingGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	public double getWidth() {
		return getNode().getWidth();
	}

	public double getHeight() {
		return getNode().getHeight();
	}

	// Decoration graphics doesn't use normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) Math.round(x * getScale()), (int) Math.round(y * getScale()));
	}

	// Decoration graphics doesn't use normalized coordinates system
	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new DianaPoint(x / getScale(), y / getScale());
	}

}

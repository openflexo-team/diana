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

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.logging.Logger;

import org.openflexo.diana.DrawingGraphicalRepresentation;
import org.openflexo.diana.Drawing.RootNode;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.diana.graphics.FGEDrawingGraphics;
import org.openflexo.diana.swing.view.JDrawingView;

public class JFGEDrawingGraphics extends JFGEGraphics implements FGEDrawingGraphics {

	private static final Logger logger = Logger.getLogger(JFGEDrawingGraphics.class.getPackage().getName());

	private JFGEDrawingDecorationGraphics drawingDecorationGraphics;

	public <O> JFGEDrawingGraphics(RootNode<O> rootNode, JDrawingView<O> view) {
		super(rootNode, view);
		drawingDecorationGraphics = new JFGEDrawingDecorationGraphics(rootNode, view);
	}

	@Override
	public DrawingGraphicalRepresentation getGraphicalRepresentation() {
		return (DrawingGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	@Override
	public FGEDrawingDecorationGraphics getDrawingDecorationGraphics() {
		return drawingDecorationGraphics;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	public void createGraphics(Graphics2D graphics2D) {
		super.createGraphics(graphics2D);
		drawingDecorationGraphics.createGraphics(graphics2D);
	}

	public void releaseGraphics() {
		super.releaseGraphics();
		drawingDecorationGraphics.releaseGraphics();
	}

	// Drawing graphics doesn't use normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) (x * getScale()), (int) (y * getScale()));
	}

	// Drawing graphics doesn't use normalized coordinates system
	@Override
	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new FGEPoint(x / getScale(), y / getScale());
	}

}

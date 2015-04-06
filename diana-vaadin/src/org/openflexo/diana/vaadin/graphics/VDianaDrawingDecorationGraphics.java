/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.diana.vaadin.graphics;

import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.DrawingGraphicalRepresentation;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.GraphicalRepresentation.HorizontalTextAlignment;
import org.openflexo.fge.TextStyle;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaEditor;
import org.openflexo.fge.geom.FGECubicCurve;
import org.openflexo.fge.geom.FGEDimension;
import org.openflexo.fge.geom.FGEGeneralShape;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.geom.FGEPolygon;
import org.openflexo.fge.geom.FGEQuadCurve;
import org.openflexo.fge.geom.FGERectangle;
import org.openflexo.fge.graphics.FGEDrawingDecorationGraphics;
import org.openflexo.diana.vaadin.view.VDrawingView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;

public class VDianaDrawingDecorationGraphics extends VDianaGraphics implements FGEDrawingDecorationGraphics {

	private static final Logger logger = Logger.getLogger(VDianaDrawingDecorationGraphics.class.getPackage().getName());

	public <O> VDianaDrawingDecorationGraphics(RootNode<O> rootNode, VDrawingView<O> view) {
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
	public FGEPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new FGEPoint(x / getScale(), y / getScale());
	}

}
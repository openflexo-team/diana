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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.util.logging.Logger;

import org.openflexo.diana.vaadin.view.VShapeView;
import org.openflexo.diana.vaadin.view.widgetset.client.vdrawingview.VDrawingViewClientRpc;
import org.openflexo.fge.BackgroundImageBackgroundStyle;
import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.ForegroundStyle;
import org.openflexo.fge.ShapeGraphicalRepresentation;
import org.openflexo.fge.graphics.FGEShapeDecorationGraphics;
import org.openflexo.fge.graphics.FGEShapeGraphics;
import org.openflexo.fge.impl.FGECachedModelFactory;
import org.openflexo.model.exceptions.ModelDefinitionException;

public class VDianaShapeGraphics extends VDianaGraphics implements FGEShapeGraphics {

	private static final Logger logger = Logger.getLogger(VDianaShapeGraphics.class.getPackage().getName());

	private final VDianaShapeDecorationGraphics shapeDecorationGraphics;

	public <O> VDianaShapeGraphics(ShapeNode<O> node, VShapeView<O> view) {
		super(node, view);
		shapeDecorationGraphics = new VDianaShapeDecorationGraphics(node, view);
		//paintShadow();
		//System.out.print("SSSSSSSSSSSSSSSSVDianaShapeGraphics is " + rpc + "/n");
	}

	@Override
	public ShapeGraphicalRepresentation getGraphicalRepresentation() {
		return (ShapeGraphicalRepresentation) super.getGraphicalRepresentation();
	}


	@Override
	public ShapeNode<?> getNode() {
		return (ShapeNode<?>) super.getNode();
	}

	private static FGECachedModelFactory SHADOW_FACTORY = null;

	static {
		try {
			SHADOW_FACTORY = new FGECachedModelFactory();
		} catch (ModelDefinitionException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
	}

	public void paintShadow(){
		Delegate.rpc.fillRect(20.0, 20.0, 40.0, 40.0);
		//System.out.print("\n the rpc in the VDianaShapeGraphics is " + Delegate.rpc);
	}
	
	@Override
	public FGEShapeDecorationGraphics getShapeDecorationGraphics() {
		// TODO Auto-generated method stub
		return null;
	}

}

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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.logging.Logger;

import org.openflexo.diana.ConnectorGraphicalRepresentation;
import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.connectors.ConnectorSymbol;
import org.openflexo.diana.geom.FGEPoint;
import org.openflexo.diana.geom.area.FGEArea;
import org.openflexo.diana.graphics.FGEConnectorGraphics;
import org.openflexo.diana.swing.view.JConnectorView;

public class JFGEConnectorGraphics extends JFGEGraphics implements FGEConnectorGraphics {

	private static final Logger logger = Logger.getLogger(JFGEConnectorGraphics.class.getPackage().getName());

	private final JFGESymbolGraphics symbolGraphics;

	public <O> JFGEConnectorGraphics(ConnectorNode<O> node, JConnectorView<O> view) {
		super(node, view);
		symbolGraphics = new JFGESymbolGraphics(node, view);
	}

	@Override
	public ConnectorGraphicalRepresentation getGraphicalRepresentation() {
		return (ConnectorGraphicalRepresentation) super.getGraphicalRepresentation();
	}

	@Override
	public JFGESymbolGraphics getSymbolGraphics() {
		return symbolGraphics;
	}

	/**
	 * 
	 * @param graphics2D
	 * @param controller
	 */
	@Override
	public void createGraphics(Graphics2D graphics2D) {
		super.createGraphics(graphics2D);
		symbolGraphics.createGraphics(graphics2D);
	}

	@Override
	public void releaseGraphics() {
		super.releaseGraphics();
		symbolGraphics.releaseGraphics();
	}

	/**
	 * 
	 * @param point
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	@Override
	public void drawSymbol(FGEPoint point, ConnectorSymbol symbol, double size, double angle) {
		drawSymbol(point.x, point.y, symbol, size, angle);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param symbol
	 * @param size
	 * @param angle
	 *            in radians
	 */
	@Override
	public void drawSymbol(double x, double y, ConnectorSymbol symbol, double size, double angle) {
		Point p = convertNormalizedPointToViewCoordinates(x, y);

		if (symbol == null) {
			return;
		}

		if (getGraphicalRepresentation().getApplyForegroundToSymbols()) {
			symbolGraphics.setDefaultForeground(symbol.getForegroundStyle(getGraphicalRepresentation().getForeground(), getFactory()));
		}

		Color fgColor = getGraphicalRepresentation().getForeground().getColor();
		Color bgColor = Color.WHITE;
		symbolGraphics.setDefaultBackground(symbol.getBackgroundStyle(fgColor, bgColor, getFactory()));

		FGEArea symbolShape = symbol.getSymbol();

		// Debug: to see bounds
		// symbolShape = new FGEUnionArea(symbolShape,new FGERectangle(0,0,1,1,Filling.NOT_FILLED));

		symbolShape = symbolShape.transform(AffineTransform.getTranslateInstance(-0.5, -0.5));
		symbolShape = symbolShape.transform(AffineTransform.getRotateInstance(-angle));
		symbolShape = symbolShape.transform(AffineTransform.getScaleInstance(size * getScale(), size * getScale()));
		symbolShape = symbolShape.transform(AffineTransform.getTranslateInstance(p.x - size / 2 * Math.cos(-angle) * getScale(), p.y - size
				/ 2 * Math.sin(-angle) * getScale()));

		// System.out.println("Ce que je dessine: "+symbolShape);

		symbolShape.paint(symbolGraphics);

	}

}

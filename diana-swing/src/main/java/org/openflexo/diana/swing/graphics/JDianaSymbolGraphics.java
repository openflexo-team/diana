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

import org.openflexo.diana.Drawing.ConnectorNode;
import org.openflexo.diana.geom.DianaPoint;
import org.openflexo.diana.graphics.DianaSymbolGraphics;
import org.openflexo.diana.swing.view.JConnectorView;

//import org.openflexo.diana.ShapeGraphicalRepresentation;

public class JDianaSymbolGraphics extends JDianaGraphics implements DianaSymbolGraphics {

	private static final Logger logger = Logger.getLogger(JDianaSymbolGraphics.class.getPackage().getName());

	public <O> JDianaSymbolGraphics(ConnectorNode<O> node, JConnectorView<O> view) {
		super(node, view);
	}

	// sometimes this method throw a class cast exception with a TokenEdgeGR instead of a
	// ShapeGraphicalRepresentation.
	// don't know the source cause, but commenting this code seems to be a workaround.

	// The real solution could be to use Generic Typing to find the root cause of this issue
	// ... but it's a lot of complex work.

	// public ShapeGraphicalRepresentation getGraphicalRepresentation()
	// {
	// return (ShapeGraphicalRepresentation)super.getGraphicalRepresentation();
	// }

	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public Point convertNormalizedPointToViewCoordinates(double x, double y) {
		return new Point((int) x, (int) y);
	}

	// Symbol graphics doesn't used normalized coordinates system
	@Override
	public DianaPoint convertViewCoordinatesToNormalizedPoint(int x, int y) {
		return new DianaPoint(x, y);
	}

}

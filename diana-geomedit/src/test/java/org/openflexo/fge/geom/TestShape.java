/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geomedit, a component of the software infrastructure 
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

package org.openflexo.fge.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;

import org.openflexo.fge.geom.FGEEllips;
import org.openflexo.fge.geom.FGEGeometricObject.Filling;
import org.openflexo.fge.geom.FGERectangle;

public class TestShape {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FGERectangle rectangle = new FGERectangle(0, 0, 10, 10);
		System.out.println("Le rectangle: ");
		debugShape(rectangle);
		Area rotatedRectangle = new Area(rectangle);
		rotatedRectangle.transform(AffineTransform.getRotateInstance(Math.PI / 4));
		System.out.println("Le rectangle qu'a tourne: ");
		debugShape(rotatedRectangle);
		FGEEllips circle = new FGEEllips(0, 0, 10, 10, Filling.NOT_FILLED);
		System.out.println("Le cercle: ");
		debugShape(circle);
	}

	private static void debugShape(Shape shape) {
		PathIterator pi = shape.getPathIterator(new AffineTransform());
		while (!pi.isDone()) {
			double[] coords = new double[6];
			int i = pi.currentSegment(coords);
			String pathType = "";
			switch (i) {
			case PathIterator.SEG_LINETO:
				pathType = "SEG_LINETO";
				break;
			case PathIterator.SEG_MOVETO:
				pathType = "SEG_MOVETO";
				break;
			case PathIterator.SEG_CUBICTO:
				pathType = "SEG_CUBICTO";
				break;
			case PathIterator.SEG_QUADTO:
				pathType = "SEG_QUADTO";
				break;
			case PathIterator.SEG_CLOSE:
				pathType = "SEG_CLOSE";
				break;
			default:
				break;
			}
			System.out.println(pathType + " " + coords[0] + " " + coords[1] + " " + coords[2] + " " + coords[3] + " " + coords[4] + " "
					+ coords[5]);
			pi.next();
		}
	}

}

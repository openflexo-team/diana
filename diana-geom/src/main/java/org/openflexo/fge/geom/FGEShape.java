/**
 * 
 * Copyright (c) 2013-2014, Openflexo
 * Copyright (c) 2011-2012, AgileBirds
 * 
 * This file is part of Diana-geom, a component of the software infrastructure 
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
import java.util.logging.Logger;

import org.openflexo.fge.graphics.BGStyle;
import org.openflexo.fge.graphics.FGStyle;

/**
 * Implementation of a finite area
 * 
 * @author sylvain
 *
 * @param <O>
 *            type of represented {@link FGEShape}
 */
public interface FGEShape<O extends FGEGeometricObject<O>> extends FGEGeometricObject<O>, Shape {

	static final Logger logger = Logger.getLogger(FGEShape.class.getPackage().getName());

	public boolean getIsFilled();

	public void setIsFilled(boolean aFlag);

	public FGEPoint nearestOutlinePoint(FGEPoint aPoint);

	public FGEPoint getCenter();

	public FGERectangle getBoundingBox();

	/**
	 * Return background eventually overriding default background (usefull in ShapeUnion)<br>
	 * Default value is null
	 * 
	 * @return
	 */
	public BGStyle getBackground();

	/**
	 * Sets background eventually overriding default background (usefull in ShapeUnion)<br>
	 * 
	 * @param aBackground
	 */
	public void setBackground(BGStyle aBackground);

	/**
	 * Return foreground eventually overriding default foreground (usefull in ShapeUnion)<br>
	 * Default value is null
	 * 
	 * @return
	 */
	public FGStyle getForeground();

	/**
	 * Sets foreground eventually overriding default foreground (usefull in ShapeUnion)<br>
	 * 
	 * @param aForeground
	 */
	public void setForeground(FGStyle aForeground);

}

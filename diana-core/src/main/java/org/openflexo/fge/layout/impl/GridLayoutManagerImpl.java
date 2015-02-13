/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-api, a component of the software infrastructure 
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

package org.openflexo.fge.layout.impl;

import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.geom.FGEPoint;
import org.openflexo.fge.impl.FGELayoutManagerImpl;
import org.openflexo.fge.layout.GridLayoutManager;
import org.openflexo.fge.layout.GridLayoutManagerSpecification;

/**
 * Default implementation for {@link GridLayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class GridLayoutManagerImpl<O> extends FGELayoutManagerImpl<GridLayoutManagerSpecification, O> implements
		GridLayoutManager<O> {

	@Override
	public void layout(ShapeNode<?> node) {
		super.layout(node);
		System.out.println("x=" + node.getX());
		System.out.println("y=" + node.getY());

		double newX = ((int) (node.getX() / 30)) * 30;
		double newY = ((int) (node.getY() / 30)) * 30;

		System.out.println("newX=" + newX);
		System.out.println("newY=" + newY);

		node.setLocation(new FGEPoint(newX, newY));
	}
}

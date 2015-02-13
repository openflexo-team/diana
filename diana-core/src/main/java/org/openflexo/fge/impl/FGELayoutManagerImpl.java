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

package org.openflexo.fge.impl;

import java.util.ArrayList;
import java.util.List;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.FGELayoutManager;
import org.openflexo.fge.FGELayoutManagerSpecification;

/**
 * Default implementation for {@link FGELayoutManager}
 * 
 * @author sylvain
 * 
 */
public abstract class FGELayoutManagerImpl<LMS extends FGELayoutManagerSpecification<?>, O> extends FGEObjectImpl implements
		FGELayoutManager<LMS, O> {

	private boolean invalidated = true;

	private final List<ShapeNode<?>> nodes;

	public FGELayoutManagerImpl() {
		nodes = new ArrayList<ShapeNode<?>>();
	}

	/**
	 * Called to invalidate the whole layout<br>
	 * All contained {@link ShapeNode} will be invalidated
	 */
	@Override
	public void invalidate() {
		invalidated = true;
	}

	/**
	 * Called to invalidate a {@link ShapeNode}
	 * 
	 * @param node
	 */
	@Override
	public void invalidate(ShapeNode<?> node) {
		node.invalidateLayout();
	}

	/**
	 * Perform layout for all invalidated {@link ShapeNode} contained in this layout
	 */
	@Override
	public void doLayout() {
		boolean wasInvalidated = invalidated;
		if (wasInvalidated) {
			preComputeLayout();
			invalidated = false;
		}
		for (ShapeNode<?> node : nodes) {
			if (node.isValid() && !node.isLayoutValidated()) {
				layout(node);
			}
		}
	}

	/**
	 * Perform layout for supplied {@link ShapeNode}, if this node is invalidated<br>
	 * If node was not invalidated, simply return
	 * 
	 * @param node
	 */
	@Override
	public void layout(ShapeNode<?> node) {
		System.out.println("OK, je suis sense faire le layout de " + node);
	}

	/**
	 * Called at the beginning of layout computation for the whole container
	 */
	@Override
	public void preComputeLayout() {
		nodes.clear();
		for (DrawingTreeNode<?, ?> dtn : getContainerNode().getChildNodes()) {
			if (dtn instanceof ShapeNode) {
				if (((ShapeNode<O>) dtn).getLayoutManager() == this) {
					nodes.add((ShapeNode<O>) dtn);
				}
			}
		}
		System.out.println(toString() + ": je prends en compte les noeuds suivant pour mon layout: " + nodes);
	}
}

/**
 * 
 * Copyright (c) 2014, Openflexo
 * 
 * This file is part of Diana-core, a component of the software infrastructure 
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

import java.util.logging.Logger;

import org.openflexo.fge.Drawing.GraphNode;
import org.openflexo.fge.GRBinding.GraphGRBinding;
import org.openflexo.fge.graph.FGEGraph;
import org.openflexo.fge.graphics.FGEShapeGraphics;

public class GraphNodeImpl<G extends FGEGraph> extends ShapeNodeImpl<G>implements GraphNode<G> {

	private static final Logger logger = Logger.getLogger(GraphNodeImpl.class.getPackage().getName());

	public static final String GRAPH_NEEDS_TO_BE_REDRAWN = "GraphNeedsToBeRedrawn";

	// TODO: change to protected
	public GraphNodeImpl(DrawingImpl<?> drawingImpl, G graph, GraphGRBinding<G> grBinding, ContainerNodeImpl<?, ?> parentNode) {
		super(drawingImpl, graph, grBinding, parentNode);
	}

	@Override
	public boolean delete() {
		return super.delete();
	}

	@Override
	public void paint(FGEShapeGraphics g) {
		// First draw outline (fg and bg)
		super.paint(g);

		// Paint the graph
		getDrawable().paint(g);

	}

	@Override
	public void notifyGraphNeedsToBeRedrawn() {
		getPropertyChangeSupport().firePropertyChange(GRAPH_NEEDS_TO_BE_REDRAWN, false, true);
	}

	@Override
	public int getBorderTop() {
		if (getDrawable() != null) {
			return getDrawable().getBorderTop();
		}
		return 10;
	}

	@Override
	public int getBorderBottom() {
		if (getDrawable() != null) {
			return getDrawable().getBorderBottom();
		}
		return 10;
	}

	@Override
	public int getBorderLeft() {
		if (getDrawable() != null) {
			return getDrawable().getBorderLeft();
		}
		return 10;
	}

	@Override
	public int getBorderRight() {
		if (getDrawable() != null) {
			return getDrawable().getBorderRight();
		}
		return 10;
	}

	@Override
	public String toString() {
		return "GraphNodeImpl[" + Integer.toHexString(hashCode()) + "]";
	}
}

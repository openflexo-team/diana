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

import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ConnectorNode;
import org.openflexo.fge.Drawing.DrawingTreeNodeIdentifier;
import org.openflexo.fge.Drawing.PendingConnector;

public class PendingConnectorImpl<O> implements PendingConnector<O> {
	private final ConnectorNodeImpl<O> connectorNode;
	private final DrawingTreeNodeIdentifier<?> parentNodeIdentifier;
	private final DrawingTreeNodeIdentifier<?> startNodeIdentifier;
	private final DrawingTreeNodeIdentifier<?> endNodeIdentifier;

	public PendingConnectorImpl(ConnectorNode<O> connectorNode, DrawingTreeNodeIdentifier<?> parentNodeIdentifier,
			DrawingTreeNodeIdentifier<?> startNodeIdentifier, DrawingTreeNodeIdentifier<?> endNodeIdentifier) {
		super();
		this.connectorNode = (ConnectorNodeImpl<O>) connectorNode;
		this.parentNodeIdentifier = parentNodeIdentifier;
		this.startNodeIdentifier = startNodeIdentifier;
		this.endNodeIdentifier = endNodeIdentifier;
	}

	@Override
	public ConnectorNode<O> getConnectorNode() {
		return connectorNode;
	}

	@Override
	public DrawingTreeNodeIdentifier<?> getParentNodeIdentifier() {
		return parentNodeIdentifier;
	}

	@Override
	public DrawingTreeNodeIdentifier<?> getStartNodeIdentifier() {
		return startNodeIdentifier;
	}

	@Override
	public DrawingTreeNodeIdentifier<?> getEndNodeIdentifier() {
		return endNodeIdentifier;
	}

	@Override
	public boolean tryToResolve(Drawing<?> drawing) {
		ContainerNodeImpl<?, ?> parentNode = (ContainerNodeImpl<?, ?>) drawing.getDrawingTreeNode(parentNodeIdentifier);
		ShapeNodeImpl<?> startNode = (ShapeNodeImpl<?>) drawing.getDrawingTreeNode(startNodeIdentifier);
		ShapeNodeImpl<?> endNode = (ShapeNodeImpl<?>) drawing.getDrawingTreeNode(endNodeIdentifier);
		if (parentNode != null && startNode != null && endNode != null) {
			parentNode.addChild(connectorNode);
			connectorNode.setStartNode(startNode);
			connectorNode.setEndNode(endNode);
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "PendingConnectorImpl[" + connectorNode.getDrawable() + "]";
	}

}

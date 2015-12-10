package org.openflexo.server;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.json.JsonVisitor;

public class CreateChange extends Change {
	private final DrawingTreeNode<?, ?> parent;
	private final DrawingTreeNode<?, ?> createdNode;

	public CreateChange(DrawingTreeNode<?, ?> createdNode, DrawingTreeNode<?, ?> parent, JsonVisitor visitor) {
		this.createdNode = createdNode;
		this.parent = parent;
	}

	public DrawingTreeNode<?, ?> getCreatedNode() {
		return createdNode;
	}

	@Override
	public void apply(ChangeManager manager) {
		System.out.println("created change applied");
	}
}

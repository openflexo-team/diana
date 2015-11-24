package org.openflexo.server;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.json.JsonVisitor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CreateChange {
	private final DrawingTreeNode<?, ?> parent;
	private final DrawingTreeNode<?, ?> createdNode;

	private final JsonVisitor jsonVisitor;
	private final JsonNode jsonNode;

	public CreateChange(DrawingTreeNode<?, ?> createdNode, DrawingTreeNode<?, ?> parent, JsonVisitor visitor) {
		this.createdNode = createdNode;
		this.parent = parent;
		this.jsonVisitor = visitor;
		jsonNode = jsonVisitor.getJsonNode(createdNode);
	}

	public JsonNode toJson() {
		ObjectNode node = jsonVisitor.getMapper().createObjectNode();
		node.put("type", "CREATE");
		node.set("created", jsonNode);
		return node;
	}
}

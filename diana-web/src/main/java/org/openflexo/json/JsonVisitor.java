package org.openflexo.json;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.openflexo.fge.BackgroundStyle;
import org.openflexo.fge.ColorBackgroundStyle;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.Drawing.ContainerNode;
import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.fge.Drawing.DrawingTreeNodeIdentifier;
import org.openflexo.fge.Drawing.RootNode;
import org.openflexo.fge.Drawing.ShapeNode;
import org.openflexo.fge.DrawingVisitor;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonVisitor implements DrawingVisitor, PropertyChangeListener {
	private final Map<DrawingTreeNode<?, ?>, ObjectNode> nodeMap;

	// TODO: use a shared object Mapper
	private final ObjectMapper mapper;
	private JsonNode rootNode;

	public JsonVisitor() {
		nodeMap = new HashMap<>();
		mapper = new ObjectMapper();
	}

	@Override
	public void visit(Drawing drawing) {
		System.out.println("Drawing");
	}

	@Override
	public void visit(DrawingTreeNode<?, ?> dtn) {
		ObjectNode node = createNode(dtn);
	}

	@Override
	public void visit(ContainerNode<?, ?> dtn) {
		ObjectNode node = createNode(dtn);
	}

	@Override
	public void visit(ShapeNode<?> dtn) {
		ObjectNode node = createNode(dtn);
		dtn.getPropertyChangeSupport().addPropertyChangeListener(this);
	}

	@Override
	public void visit(RootNode<?> dtn) {
		ObjectNode node = createNode(dtn);
		this.rootNode = node;
	}

	/*@Override
	public void visit(ShapeGraphicalRepresentation GR) {
		nodeStack.add(createNode(dtn));
	}*/

	/*@Override
	public void public void visit(GRProperty<?> property) {
		((ObjectNode) currentNode()).put(property.getName(), property.get)
	};*/

	public ObjectNode createNode(DrawingTreeNode<?, ?> dtn) {
		ObjectNode node = mapper.createObjectNode();
		DrawingTreeNodeIdentifier id = new DrawingTreeNodeIdentifier(dtn.getDrawable(), dtn.getGRBinding());
		/*node.putObject("properties");
		((ObjectNode) node.get("properties")).put("id", id.hashCode());*/
		node.put("id", id.hashCode());

		if (dtn.getParentNode() != null) {
			ObjectNode parentNode = nodeMap.get(dtn.getParentNode());
			addChild(parentNode, node);
		}
		nodeMap.put(dtn, node);
		return node;
	}

	public ObjectNode createNode(ContainerNode<?, ?> dtn) {
		ObjectNode node = createNode((DrawingTreeNode<?, ?>) dtn);
		return node;
	}

	public ObjectNode createNode(ShapeNode<?> dtn) {
		ObjectNode node = createNode((ContainerNode) dtn);
		node.put("x", dtn.getX());
		node.put("y", dtn.getY());
		node.put("type", "shape");
		node.put("width", dtn.getWidth());
		node.put("height", dtn.getHeight());
		node.put("shape", dtn.getShape().getShapeType().toString().toLowerCase());
		return node;
	}

	public ObjectNode createNode(RootNode<?> dtn) {
		ObjectNode node = createNode((ContainerNode) dtn);
		node.put("type", "root");
		return node;
	}

	private void addChild(JsonNode parent, JsonNode child) {
		if (parent.get("children") == null)
			((ObjectNode) parent).putArray("children");

		((ArrayNode) parent.get("children")).add(child);
	}

	@Override
	public void visit(BackgroundStyle bg, DrawingTreeNode<?, ?> dtn) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(ColorBackgroundStyle bg, DrawingTreeNode<?, ?> dtn) {
		ObjectNode dtnNode = nodeMap.get(dtn);
		ObjectNode bgNode = dtnNode.putObject("background");
		bgNode.put("red", bg.getColor().getRed());
		bgNode.put("green", bg.getColor().getGreen());
		bgNode.put("blue", bg.getColor().getBlue());
	}

	public void printJson(PrintStream out) {
		try {
			mapper.writeValue(out, rootNode);
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public JsonNode getRootNode() {
		return rootNode;
	}

	public JsonNode getJsonNode(DrawingTreeNode<?, ?> dtn) {
		return nodeMap.get(dtn);
	}

	public String JsonAsString() {
		try {
			return mapper.writeValueAsString(rootNode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		// ((DrawingTreeNode<?, ?>) evt.getSource());
		printJson(System.out);
	}
}

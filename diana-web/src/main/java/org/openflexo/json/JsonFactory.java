package org.openflexo.json;

import java.io.IOException;

import org.openflexo.fge.Drawing.DrawingTreeNode;
import org.openflexo.server.Change;
import org.openflexo.server.CreateChange;
import org.openflexo.server.UpdateChange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class JsonFactory {
	public static JsonFactory INSTANCE = new JsonFactory();

	private final ObjectMapper mapper;

	public JsonFactory() {
		mapper = new ObjectMapper();
	}

	public JsonVisitor makeJsonVisitor() {
		JsonVisitor returned = new JsonVisitor(this.mapper);
		return returned;
	}

	public JsonNode toJson(DrawingTreeNode<?, ?> dtn) {
		JsonVisitor visitor = makeJsonVisitor();
		dtn.accept(visitor);
		return visitor.getRootNode();
	}

	public JsonNode toJson(Change change) {
		if (change instanceof CreateChange) {
			return toJson((CreateChange) change);
		}
		else if (change instanceof UpdateChange<?>) {
			return toJson((UpdateChange) change);
		}
		return null;
	}

	public JsonNode toJson(CreateChange change) {
		ObjectNode node = mapper.createObjectNode();
		node.put("type", "CREATE");
		node.set("created", toJson(change.getCreatedNode()));
		return node;
	}

	public JsonNode toJson(UpdateChange<?> change) {
		ObjectNode node = mapper.createObjectNode();
		node.put("type", "UPDATE");
		node.put("updated", change.getUpdatedNodeId());
		node.put("changedProperty", change.getChangedProperty());
		if (change.getNewValue() instanceof Integer)
			node.put("newValue", (int) change.getNewValue());
		else if (change.getNewValue() instanceof Double)
			node.put("newValue", (double) change.getNewValue());
		else
			node.put("newValue", change.getNewValue().toString());
		return node;
	}

	public String toJsonString(JsonNode json) {
		try {
			return this.mapper.writeValueAsString(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public UpdateChange<?> deserialize(String string) {
		try {
			JsonNode json = mapper.readTree(string);
			return new UpdateChange<Double>(json.get("updated").asInt(), json.get("changedProperty").asText(),
					json.get("newValue").asDouble());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

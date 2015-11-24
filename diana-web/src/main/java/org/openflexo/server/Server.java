package org.openflexo.server;

import org.openflexo.diagram.Diagram;
import org.openflexo.diagram.DiagramDrawing;
import org.openflexo.diagram.Shape;
import org.openflexo.diagram.Shape.ShapeType;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.json.JsonVisitor;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.json.impl.Json;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class Server extends Verticle {
	private EventBus eb;
	private HttpServer server;

	Drawing<Diagram> drawing;
	JsonVisitor jsonVisitor;

	public Server() {
		super();

		// For test purpose
		Diagram diagram = new Diagram();
		diagram.add(new Shape(ShapeType.RECTANGLE, 20, 40, 120, 80));
		diagram.add(new Shape(ShapeType.TRIANGLE, 80, 20, 150, 100));
		diagram.add(new Shape(ShapeType.OVAL, 50, 30, 90, 90));

		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}

		drawing = new DiagramDrawing(diagram, factory);
		jsonVisitor = new JsonVisitor();
		drawing.accept(jsonVisitor);
		jsonVisitor.printJson(System.out);
	}

	@Override
	public void start() {
		eb = vertx.eventBus();
		server = vertx.createHttpServer();

		// AuditVerticle displays every message published on the bus he listens to
		container.deployVerticle("org.openflexo.server.AuditVerticle");

		// HTTPRequesthandler
		server.requestHandler(new Handler<HttpServerRequest>() {

			@Override
			public void handle(HttpServerRequest req) {
				Logger logger = container.logger();

				String file = req.path().equals("/") ? "index.html" : req.path();
				req.response().sendFile("../diana-webclient/" + file);

				// eb.publish("testing.vertest.Server.announcements", "We received a request for resource: " + req.path());
			}
		});

		Handler<Message<Json>> clientHandler = new Handler<Message<Json>>() {
			@Override
			public void handle(Message<Json> message) {
				container.logger().info("Received on client.query : " + message.body());
				ObjectNode messageNode = jsonVisitor.getMapper().createObjectNode();
				CreateChange change = new CreateChange(drawing.getRoot(), null, jsonVisitor);
				JsonNode changeNode = change.toJson();
				try {
					JsonObject json = new JsonObject(jsonVisitor.getMapper().writeValueAsString(changeNode));
					message.reply(json);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		};
		eb.registerHandler("client.query", clientHandler);

		// Utilisation d'un server SockJS, creation d'un bridge
		JsonObject config = new JsonObject().putString("prefix", "/eventbus");

		JsonArray noPermitted = new JsonArray();
		noPermitted.add(new JsonObject());

		vertx.createSockJSServer(server).bridge(config, noPermitted, noPermitted);

		// gotta listen
		server.listen(8080);
	}

}

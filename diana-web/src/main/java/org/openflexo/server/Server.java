package org.openflexo.server;

import javax.swing.JDialog;

import org.openflexo.diagram.Diagram;
import org.openflexo.diagram.DiagramDrawing;
import org.openflexo.diagram.Shape;
import org.openflexo.diagram.Shape.ShapeType;
import org.openflexo.fge.Drawing;
import org.openflexo.fge.FGEModelFactory;
import org.openflexo.fge.FGEModelFactoryImpl;
import org.openflexo.fge.swing.JDianaInteractiveViewer;
import org.openflexo.fge.swing.control.SwingToolFactory;
import org.openflexo.json.JsonFactory;
import org.openflexo.json.JsonVisitor;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public class Server extends Verticle {
	private static Logger logger;
	private EventBus eb;
	private HttpServer server;

	private final JsonFactory jsonFactory;
	private final Drawing<Diagram> drawing;
	private final ChangeManager changeManager;
	private final JsonVisitor jsonVisitor;

	public Server() {
		super();

		Diagram diagram = new Diagram();
		Shape rectangle = new Shape(ShapeType.RECTANGLE, 20, 40, 200, 200);
		rectangle.addChild(new Shape(ShapeType.OVAL, 100, 100, 40, 30));
		diagram.add(rectangle);

		diagram.add(new Shape(ShapeType.TRIANGLE, 80, 20, 150, 100));
		diagram.add(new Shape(ShapeType.TRIANGLE, 400, 320, 90, 200));
		// diagram.add(new Shape(ShapeType.OVAL, 50, 30, 90, 90));

		FGEModelFactory factory = null;
		try {
			factory = new FGEModelFactoryImpl();
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
		}

		drawing = new DiagramDrawing(diagram, factory);

		jsonFactory = JsonFactory.INSTANCE;
		jsonVisitor = jsonFactory.makeJsonVisitor();
		drawing.accept(jsonVisitor);

		changeManager = new ChangeManager(this);
		drawing.accept(changeManager);

		JDianaInteractiveViewer<Diagram> viewer = new JDianaInteractiveViewer<>(drawing, drawing.getFactory(), SwingToolFactory.DEFAULT);

		JDialog dialog = new JDialog();
		dialog.setContentPane(viewer.getDrawingView());
		dialog.setSize(800, 600);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		dialog.setVisible(true);
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

		Handler<Message<JsonObject>> clientHandler = new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				container.logger().info("Received on client.query : " + message.body());
				CreateChange change = new CreateChange(drawing.getRoot(), null, jsonVisitor);
				JsonNode changeNode = jsonFactory.toJson(change);
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

		Handler<Message<JsonObject>> changeHandler = new Handler<Message<JsonObject>>() {
			@Override
			public void handle(Message<JsonObject> message) {
				container.logger().info("Received on client.changes : " + message.body());

				UpdateChange<?> change = jsonFactory.deserialize(message.body().toString());
				changeManager.apply(change);
				// UpdateChange<?> bChange = new UpdateChange<?>(updatedNodeId, changedProperty, newValue)
				// eb.publish("server.changes", change);
				// publishChange("server.changes", change);
			}
		};
		eb.registerHandler("client.changes", changeHandler);

		// Utilisation d'un server SockJS, creation d'un bridge
		JsonObject config = new JsonObject().putString("prefix", "/eventbus");

		JsonArray noPermitted = new JsonArray();
		noPermitted.add(new JsonObject());

		vertx.createSockJSServer(server).bridge(config, noPermitted, noPermitted);

		// gotta listen
		server.listen(8080);
	}

	public void publishChange(String address, Change change) {
		String jsonString = jsonFactory.toJsonString(jsonFactory.toJson(change));
		JsonObject json = new JsonObject(jsonString);
		eb.publish(address, json);
		// container.logger().info("sent : " + json.toString());
	}
}

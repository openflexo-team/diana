package org.openflexo.server;

import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.EventBus;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.logging.Logger;
import org.vertx.java.platform.Verticle;

public class AuditVerticle extends Verticle {

	@Override
	public void start() {
		EventBus eb = vertx.eventBus();
		Handler<Message> auditHandler = new Handler<Message>() {
			@Override
			public void handle(Message message) {
				Logger logger = container.logger();
				logger.info("AuditVerticle here, message published : " + message.body());
			}
		};

		eb.registerHandler("org.openflexo.server.Server.announcements", auditHandler);
	}
}

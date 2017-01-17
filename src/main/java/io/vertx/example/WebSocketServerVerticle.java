package io.vertx.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;

public class WebSocketServerVerticle extends AbstractVerticle {

    private Map<String, ServerWebSocket> _socketList;

    // コンストラクタ
    public WebSocketServerVerticle()
    {
    }
    
    @Override
    public void init(Vertx vertx, Context context) {
        super.init(vertx, context);

        _socketList = new HashMap<String, ServerWebSocket>();

        HttpServer server = vertx.createHttpServer();
        server.websocketHandler(websocket -> {
            String handlerId = RandomStringUtils.random(20, "0123456789abcdefghijklmnopqrstuvwxyz");
            _socketList.put(handlerId, websocket);
            System.out.println("handlerId: " + handlerId + " connect.");

            websocket.handler(new Handler<Buffer>() {
                @Override
                public void handle(final Buffer data)
                {
                    vertx.eventBus().send("test.local", "messageTest");
                }
            });

            websocket.closeHandler(new Handler<Void>() {
                @Override
                public void handle(Void event)
                {
                    _socketList.remove(handlerId);
                    System.out.println("handlerId: " + handlerId + " close.");
                }
            });

        }).listen(8080);
        System.out.println("WebSocketServerVerticle init completed.");
    }
}

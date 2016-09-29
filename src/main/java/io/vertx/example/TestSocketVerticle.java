package io.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class TestSocketVerticle extends AbstractVerticle {

    @Override
    public void start() {
    	HttpServer server = vertx.createHttpServer();
    	server.websocketHandler(websocket -> {
    		System.out.println("Connected!");
    		websocket.handler(new Handler<Buffer>() {
    			@Override
    			public void handle(final Buffer data) {
    				System.out.println("Receive Data From Client: " + data.getString(0, data.length()));
    				Buffer buffer = Buffer.buffer().appendString("connected");
    				websocket.write(buffer);
    			}
    		});
    	}).listen(8080);
    }
}

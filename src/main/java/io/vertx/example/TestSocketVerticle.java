package io.vertx.example;

import java.util.HashMap;
import java.util.Map;

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
        System.out.println("start");
        HttpServer server = vertx.createHttpServer();
        server.websocketHandler(websocket -> {
            System.out.println("Connected!");
            websocket.handler(new Handler<Buffer>() {
                @Override
                public void handle(final Buffer data) {
                    ReceivePacket rp = new ReceivePacket(data.getBytes());
                    Map<String, String> receive = rp.getData();
                    System.out.println("receive: " + receive.get("id") + " : " + receive.get("flag") + " : " + receive.get("日本語のキー"));

                    Map<String, String> send = getData();
                    System.out.println("send: " + send.get("id") + " : " + send.get("flag") + " : " + send.get("日本語のキー"));

                    SendPacket sp = new SendPacket();
                    sp.Add(send);
                    Buffer buffer = Buffer.buffer().appendBytes(sp.toByteArray());
                    websocket.write(buffer);
                }
            });
        }).listen(8080);
    }
    
    private Map<String, String> getData ()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        map.put("flag", "false");
        map.put("日本語のキー", "日本語の値：サーバ");
        return map;
    }
}

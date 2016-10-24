package io.vertx.example;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class TestSocketVerticle extends AbstractVerticle
{
    public static final double FPS = 30.0f;
    private Map<String, ServerWebSocket> _socketList;

    long _coolFrame = 0;

    @Override
    public void start()
    {
        System.out.println("start");
        _socketList = new HashMap<String, ServerWebSocket>();
        
        FPS _fps = new FPS(vertx);
        _fps.start(this::onTick, FPS);

        HttpServer server = vertx.createHttpServer();
        server.websocketHandler(websocket -> {

            String handlerId = RandomStringUtils.random(20, "0123456789abcdefghijklmnopqrstuvwxyz");
            _socketList.put(handlerId, websocket);
            System.out.println("handlerId: " + handlerId + " connect.");

            websocket.handler(new Handler<Buffer>() {
                @Override
                public void handle(final Buffer data)
                {
                    ReceivePacket rp = new ReceivePacket(data.getBytes());
                    Map<String, String> receive = rp.getData();
                    System.out.println("handlerId: " + handlerId + " receive: " + receive.get("id") + " : " + receive.get("flag") + " : " + receive.get("日本語のキー"));

                    Map<String, String> send = getData();
                    System.out.println("handlerId: " + handlerId + " send: " + send.get("id") + " : " + send.get("flag") + " : " + send.get("日本語のキー"));

                    SendPacket sp = new SendPacket();
                    sp.Add(send);
                    Buffer buffer = Buffer.buffer().appendBytes(sp.toByteArray());
                    websocket.write(buffer);
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
    }

    protected void onTick(Long microDelay)
    {
        if (++_coolFrame >= 30)
        {
            packetPublish(getHeartBeatPacket(), null);
            _coolFrame = 0;
        }
//        System.out.println("onTick microDelay: " + microDelay);
    }
    
    private void packetPublish(Map<String, String> packet, String ignoreHandlerId)
    {
        for (Map.Entry<String, ServerWebSocket> e : _socketList.entrySet()) {
            if (ignoreHandlerId == e.getKey()) continue; 
            SendPacket sp = new SendPacket();
            sp.Add(getHeartBeatPacket());
            Buffer buffer = Buffer.buffer().appendBytes(sp.toByteArray());
            e.getValue().write(buffer);
        }
    }
    
    private Map<String, String> getData ()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "2");
        map.put("flag", "false");
        map.put("日本語のキー", "日本語の値：サーバ");
        return map;
    }
    
    private Map<String, String> getHeartBeatPacket ()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        map.put("connectCount", String.valueOf(_socketList.size()));
        return map;
    }
}

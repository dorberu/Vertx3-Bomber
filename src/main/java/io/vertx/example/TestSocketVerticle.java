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
//                    Log("Receive: ", receive);

                    Map<String, String> send = receive;
                    send.put("handlerId", handlerId);
                    packetPublish(send, handlerId);
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
            sp.Add(packet);
            Buffer buffer = Buffer.buffer().appendBytes(sp.toByteArray());
            e.getValue().write(buffer);
        }
    }
    
    private Map<String, String> getHeartBeatPacket ()
    {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", "1");
        map.put("connectCount", String.valueOf(_socketList.size()));
        return map;
    }
    
    private void Log (String preStr, Map<String, String> data)
    {
        String strLog = preStr;
        for (Map.Entry<String, String> e : data.entrySet()) {
            strLog += (e.getKey() + "=" + e.getValue() + "; ");
        }
        System.out.println(strLog);
    }
}

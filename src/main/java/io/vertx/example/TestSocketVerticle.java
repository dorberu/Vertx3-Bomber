package io.vertx.example;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

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
                    Map<String, String> receive = deserialize(data.getBytes());
                    System.out.println("receive: " + receive.get("id") + " : " + receive.get("flag") + " : " + receive.get("日本語のキー"));

                    Map<String, String> send = getData();
                    System.out.println("send: " + send.get("id") + " : " + send.get("flag") + " : " + send.get("日本語のキー"));

                    Buffer buffer = Buffer.buffer().appendBytes(serialize(send));
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
    
    public byte[] serialize (Map<String, String> data)
    {
        byte[] ret = null;
        MessageBufferPacker packer = MessagePack.newDefaultBufferPacker();
        try {
            packer.packMapHeader(data.size());
            for (Map.Entry<String, String> map : data.entrySet())
            {
                packer.packString(map.getKey());
                packer.packString(map.getValue());
            }
            packer.close();
            ret = packer.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
    
    public Map<String, String> deserialize (byte[] data)
    {
        Map<String, String> ret = new HashMap<String, String> ();
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data);
        try {
            int mapSize = unpacker.unpackMapHeader();
            for (int i = 0; i < mapSize; i++)
            {
                String key = unpacker.unpackString();
                String value = unpacker.unpackString();
                ret.put(key, value);
            }
            unpacker.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return ret;
    }
}

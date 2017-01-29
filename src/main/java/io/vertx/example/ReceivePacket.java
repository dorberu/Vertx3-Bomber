package io.vertx.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.core.MessagePack.UnpackerConfig;
import org.msgpack.core.MessageUnpacker;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

public class ReceivePacket
{
    private Map<String, String> _data = new HashMap<String, String> ();
    
    public ReceivePacket (Buffer data)
    {
        MessageUnpacker unpacker = new UnpackerConfig().newUnpacker(data.getBytes());
        try {
            String text = unpacker.unpackString();
            System.out.println(getClass().getName() + ": ReceivePacket: " + text);

            JsonObject jsonObject = new JsonObject(text);
            int id = jsonObject.getInteger("id");
            JsonArray jsonArray = jsonObject.getJsonArray("position");
            float x = jsonArray.getFloat(0);
            float y = jsonArray.getFloat(1);
            float z = jsonArray.getFloat(2);
            System.out.println(getClass().getName() + ", id:" + id + ", x:" + x + ", y:" + y + ", z:" + z);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Map<String, String> getData() {
        return _data;
    }
}

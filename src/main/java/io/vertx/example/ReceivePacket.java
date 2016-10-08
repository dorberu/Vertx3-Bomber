package io.vertx.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;

public class ReceivePacket
{
    private Map<String, String> _data = new HashMap<String, String> ();
    
    public ReceivePacket (byte[] data)
    {
        MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(data);
        try {
            int mapSize = unpacker.unpackMapHeader();
            for (int i = 0; i < mapSize; i++) {
                String key = unpacker.unpackString();
                String value = unpacker.unpackString();
                _data.put(key, value);
            }
            unpacker.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Map<String, String> getData() {
        return _data;
    }
}

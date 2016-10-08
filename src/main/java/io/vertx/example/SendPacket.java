package io.vertx.example;

import java.io.IOException;
import java.util.Map;

import org.msgpack.core.MessageBufferPacker;
import org.msgpack.core.MessagePack;

public class SendPacket
{
    private MessageBufferPacker _packer;

    public SendPacket ()
    {
        _packer = MessagePack.newDefaultBufferPacker();
    }
    
    public void Add (Map<String, String> values)
    {
        try {
            _packer.packMapHeader(values.size());
            for (Map.Entry<String, String> map : values.entrySet()) {
                _packer.packString(map.getKey());
                _packer.packString(map.getValue());
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public byte[] toByteArray()
    {
        try {
            _packer.close();
            return _packer.toByteArray();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}

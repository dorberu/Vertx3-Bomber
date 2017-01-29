package io.vertx.example;

import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;

import java.util.Map;

public class EventBusMessageHandler
{
    /**
     * EventBus通信 イベントインターフェース
     */
    public interface MessageReceiver
    {
        public abstract boolean onMessagePacketReceive(String handlerId,
                                                       Packet p);

        public abstract boolean onMessagePacketBytesReceive(String handlerId,
                                                            String packetData);

        public abstract void onMessageClose();
    }

    protected String _globalAddr;
    protected String _localAddr;
    protected MessageReceiver _receiver;
    private MessageConsumer<Buffer> _consumer;

    /**
     * コンストラクタ
     */
    public EventBusMessageHandler(Vertx vertx,
                                  MessageReceiver receiver,
                                  String localAddress,
                                  String globalAddress)
    {
        _receiver = receiver;
        if (globalAddress != null)
        {
            _globalAddr = globalAddress;
            _consumer = vertx.eventBus().consumer(globalAddress);
        }
        if (localAddress != null && (globalAddress == null || !localAddress.equals(globalAddress)))
        {
            _localAddr = localAddress;
            _consumer = vertx.eventBus().consumer(localAddress);
        }
        
        if (_consumer != null) {
            _consumer.handler(message -> {
                try {
                    ReceivePacket packet = new ReceivePacket(message.body());
                    Map<String,String> data = packet.getData();
                    _receiver.onMessagePacketBytesReceive(data.get("id"), data.get("Packet"));
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            });
        }
    }
}

package io.vertx.example;

import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class EventBusMessageHandler implements Handler<Message<JsonObject>>
{
    /**
     * EventBus通信 イベントインターフェース
     */
    public interface MessageReceiver
    {
        public abstract boolean onMessagePacketReceive(String handlerId,
                                                       Packet p);

        public abstract boolean onMessagePacketBytesReceive(String handlerId,
                                                            byte[] packetData);

        public abstract void onMessageClose();
    }

    protected String _globalAddr;
    protected String _localAddr;
    protected MessageReceiver _receiver;
    private MessageConsumer<String> _consumer;

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
                System.out.println("I have received a message: " + message.body());
            });
        }
    }

    @Override
    public void handle(Message<JsonObject> event)
    {
        byte[] packetData;
        String handlerId;
                
        JsonObject body = event.body();
        handlerId = body.getString("id");
        packetData = body.getBinary("Packet");
        _receiver.onMessagePacketBytesReceive(handlerId, packetData);
    }
}

package io.vertx.example;

import io.vertx.core.Vertx;

public class TestServer implements EventBusMessageHandler.MessageReceiver
{
    protected static final TestServer _this = new TestServer();

    protected Vertx _vertx;
    protected EventBusMessageHandler _eventBusMessageHandler;

    long _coolFrame = 0;
    String _handlerId = "";

    /**
     * クラスの唯一のインスタンス取得
     */
    public static/* synchronized */TestServer getInstance()
    {
        return _this;
    }

    public boolean onInit(Vertx vertx)
    {
        _vertx = vertx;
        _eventBusMessageHandler = new EventBusMessageHandler(_vertx, this, "test.local", "test.global");
        return true;
    }

    protected void onTick(Long microDelay)
    {
        if (++_coolFrame >= 30)
        {
            _coolFrame = 0;
        }
    }

    @Override
    public boolean onMessagePacketReceive(String handlerId, Packet p) {
        return false;
    }

    @Override
    public boolean onMessagePacketBytesReceive(String handlerId, byte[] packetData) {
        System.out.println("TestServer onMessagePacketBytesReceive");
        _handlerId = handlerId;
        return false;
    }

    @Override
    public void onMessageClose() {
    }
}

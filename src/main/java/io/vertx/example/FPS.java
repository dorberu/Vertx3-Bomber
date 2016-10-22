package io.vertx.example;

import java.util.function.Consumer;

import io.vertx.core.Handler;
import io.vertx.core.Vertx;

public class FPS implements Handler<Long>
{
    public static final long NANOTIME_GIVUPCORRECTION = 300000000000L;

    private Consumer<Long> _callback;
    private double _fps;
    private long _nanoInterval;

    private long _nanoStartTime;
    private long _nanoEndTime;
    private long _delta;

    private Vertx _vertx;

    public FPS(Vertx vertx)
    {
        _vertx = vertx;
    }

    public void start(Consumer<Long> callback, double fps)
    {
        _callback = callback;

        updateFps(fps);

        _delta = 0;
        _nanoEndTime = System.nanoTime();
        _nanoStartTime = _nanoEndTime - 1000000;
        _vertx.setTimer((_nanoInterval - 1000000) / 1000000, this);
    }

    public void updateFps(double fps)
    {
        _fps = fps;
        _nanoInterval = (long) (1000000000.0 / _fps);
    }

    @Override
    public void handle(Long event)
    {
        _callback.accept(startWatch() / 1000);
        _vertx.setTimer(endWatch() / 1000000, this);
    }

    private long startWatch()
    {
        long currentTime = System.nanoTime();
        long delay = currentTime - _nanoStartTime;
        _delta += _nanoInterval - delay;
        if (_delta >= NANOTIME_GIVUPCORRECTION || _delta <= -NANOTIME_GIVUPCORRECTION)
        {
            _delta = 0;
        }
        _nanoStartTime = currentTime;
        return delay;
    }

    private long endWatch()
    {
        _nanoEndTime = System.nanoTime();
        long sleep = _nanoInterval + _delta - (_nanoEndTime - _nanoStartTime);
        if (sleep < 0)
        {
            sleep = 1000000;
        }
        else if (sleep < 1000000)
        {
            sleep = 1000000;
        }
        return sleep;
    }
}

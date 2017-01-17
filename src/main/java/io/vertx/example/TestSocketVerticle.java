package io.vertx.example;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;

/**
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class TestSocketVerticle extends AbstractVerticle
{
    public static final double FPS = 30.0f;

    protected static final TestServer _server = TestServer.getInstance();

    @Override
    public void start()
    {
        _server.onInit(vertx);

        FPS _fps = new FPS(vertx);
        _fps.start(this::onTick, FPS);

        DeploymentOptions options = new DeploymentOptions().setWorker(true);
        vertx.deployVerticle("io.vertx.example.WebSocketServerVerticle", options);
    }

    protected void onTick(Long microDelay)
    {
        _server.onTick(microDelay);
    }
}

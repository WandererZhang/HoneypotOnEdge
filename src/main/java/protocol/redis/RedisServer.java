package protocol.redis;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redis服务器
 *
 * @author 78445
 */
public class RedisServer implements Runnable {
    private final int port;
    private static Channel redisChannel;
    private static final Logger logger = LoggerFactory.getLogger(RedisServer.class);

    public RedisServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new RedisServerInitializer());
            ChannelFuture f = b.bind(port).sync();
            logger.info("Netty protocol.redis server listening on port " + port);
            redisChannel = f.channel();
            redisChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void closeServer() {
        if (redisChannel != null) {
            logger.info("close RedisServer");
            redisChannel.close();
            redisChannel = null;
        }
    }
}

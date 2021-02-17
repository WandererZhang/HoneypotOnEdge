package com.honeypot.protocol.mysql;

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
 * Mysql服务器
 *
 * @author 78445
 */
public class MysqlServer implements Runnable {
    private final int port;
    private static Channel mysqlChannel;
    private static final Logger logger = LoggerFactory.getLogger(MysqlServer.class);

    public MysqlServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 128);
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MysqlServerInitializer());
            ChannelFuture f = b.bind(port).sync();
            logger.info("Netty com.honeypot.protocol.mysql server listening on port " + port);
            mysqlChannel = f.channel();
            mysqlChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void closeServer() {
        if (mysqlChannel != null) {
            logger.info("close MysqlServer");
            mysqlChannel.close();
            mysqlChannel = null;
        }
    }
}

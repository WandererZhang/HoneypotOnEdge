package com.honeypot.protocol.telnet;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * telnet服务器
 *
 * @author 78445
 */
public class TelnetServer implements Runnable {
    private final int port;
    private static Channel telnetChannel;
    private static final Logger logger = LoggerFactory.getLogger(TelnetServer.class);

    public TelnetServer(int port) {
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
                    .childHandler(new TelnetServerInitializer());
            ChannelFuture f = b.bind(port).sync();
            logger.info("Netty com.honeypot.protocol.telnet server listening on port " + port);
            telnetChannel = f.channel();
            telnetChannel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public void closeServer() {
        if (telnetChannel != null) {
            logger.info("close TelnetServer");
            telnetChannel.close();
            telnetChannel = null;
        }
    }
}

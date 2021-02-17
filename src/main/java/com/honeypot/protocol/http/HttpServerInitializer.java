package com.honeypot.protocol.http;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http协议初始化
 *
 * @author 78445
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerInitializer.class);
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
        pipeline.addLast(new HttpServerExpectContinueHandler());
        pipeline.addLast(new HttpServerHandler());
        logger.info("HttpServer Initialized");
    }
}

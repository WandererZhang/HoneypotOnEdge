package protocol.redis;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Redis协议初始化
 *
 * @author 78445
 */
public class RedisServerInitializer extends ChannelInitializer<SocketChannel> {
    private static final Logger logger = LoggerFactory.getLogger(RedisServerInitializer.class);
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new RedisDecoder());
        pipeline.addLast(new RedisEncoder());
        pipeline.addLast(new RedisBulkStringAggregator());
        pipeline.addLast(new RedisArrayAggregator());
        pipeline.addLast(new RedisServerHandler());
        logger.info("RedisServer Initialized");
    }
}

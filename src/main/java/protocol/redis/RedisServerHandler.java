package protocol.redis;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Message;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redis协议Handler
 *
 * @author 78445
 */
public class RedisServerHandler extends ChannelDuplexHandler {
    private static final Logger logger = LoggerFactory.getLogger(RedisServerHandler.class);
    private static Map<String, String> redisMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String incoming = ctx.channel().remoteAddress().toString();
        logger.info("Redis : " + incoming + " request: " + msg);
        List<RedisMessage> redisMessage = ((ArrayRedisMessage) msg).children();
        String command = ((FullBulkStringRedisMessage) redisMessage.get(0)).content().toString(CharsetUtil.UTF_8).toLowerCase();
        switch (command) {
            case "set":
                if (redisMessage.size() > 3) {
                    ctx.writeAndFlush(new ErrorRedisMessage("syntax error"));
                } else if (redisMessage.size() < 3) {
                    ctx.writeAndFlush(new ErrorRedisMessage("wrong number of arguments for 'set' command"));
                } else {
                    String setKey = ((FullBulkStringRedisMessage) redisMessage.get(1)).content().toString(CharsetUtil.UTF_8);
                    String value = ((FullBulkStringRedisMessage) redisMessage.get(2)).content().toString(CharsetUtil.UTF_8);
                    redisMap.put(setKey, value);
                    ctx.writeAndFlush(ByteBufUtil.writeUtf8(ctx.alloc(), "+OK\r\n"));
                }
                break;
            case "get":
                String getKey = ((FullBulkStringRedisMessage) redisMessage.get(1)).content().toString(CharsetUtil.UTF_8);
                String s = redisMap.get(getKey);
                if (redisMessage.size() != 2) {
                    ctx.writeAndFlush(new ErrorRedisMessage("wrong number of arguments for 'get' command"));
                } else if (s == null) {
                    ctx.writeAndFlush(ByteBufUtil.writeUtf8(ctx.alloc(), "$-1\r\n"));
                } else {
                    ctx.writeAndFlush(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), s)));
                }
                break;
            case "del":
                if (redisMessage.size() < 2) {
                    ctx.writeAndFlush(new ErrorRedisMessage("wrong number of arguments for 'del' command"));
                } else {
                    int removeCount = 0;
                    for (int i = 1; i < redisMessage.size(); i++) {
                        String removeKey = ((FullBulkStringRedisMessage) redisMessage.get(i)).content().toString(CharsetUtil.UTF_8);
                        if (redisMap.remove(removeKey) != null) {
                            removeCount++;
                        }
                    }
                    ctx.writeAndFlush(ByteBufUtil.writeUtf8(ctx.alloc(), ":" + removeCount + "\r\n"));
                }
                break;
            case "quit":
                ctx.close();
                break;
            default:
                ctx.writeAndFlush(new ErrorRedisMessage("unknown command '" + command + "'"));
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String incoming = ctx.channel().remoteAddress().toString();
        logger.info("protocol.redis ip: " + incoming);
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setAddress(incoming);
        msg.setMethod("Redis");
        //TODO 推送消息到mqtt
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

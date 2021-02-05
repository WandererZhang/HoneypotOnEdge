package protocol.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.codec.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.Message;
import serialize.Serializer;
import serialize.SerializerImpl;

import java.net.SocketAddress;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http协议Handler
 *
 * @author 78445
 */
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {
    private HttpHeaders headers;
    private HttpRequest request;
    private FullHttpRequest fullHttpRequest;

    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private static final String FAVICON_ICO = "/favicon.ico";
    private static final String CONTENT_TYPE_1 = "application/json";
    private static final String CONTENT_TYPE_2 = "application/x-www-form-urlencoded";
    private static final AsciiString CONTENT_TYPE = AsciiString.cached("Content-Type");
    private static final AsciiString CONTENT_LENGTH = AsciiString.cached("Content-Length");
    private static final AsciiString CONNECTION = AsciiString.cached("Connection");
    private static final AsciiString KEEP_ALIVE = AsciiString.cached("keep-alive");

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, HttpObject httpObject) throws Exception {
        Channel incoming = channelHandlerContext.channel();
        SocketAddress address = incoming.remoteAddress();
        Message msg = new Message();
        msg.setDate(new Date());
        msg.setAddress(address.toString());
        if (httpObject instanceof HttpRequest) {
            request = (HttpRequest) httpObject;
            headers = request.headers();
            String uri = request.uri();
            logger.info("protocol.http uri: " + uri);
            if (uri.equals(FAVICON_ICO)) {
                return;
            }
            HttpMethod method = request.method();
            if (method.equals(HttpMethod.GET)) {
                QueryStringDecoder queryStringDecoder = new QueryStringDecoder(uri, Charsets.toCharset(CharEncoding.UTF_8));
                Map<String, List<String>> listMap = queryStringDecoder.parameters();
                for (Map.Entry<String, List<String>> attr : listMap.entrySet()) {
                    for (String attrVal : attr.getValue()) {
                        logger.info(attr.getKey() + "=" + attrVal);
                    }
                }
                msg.setMethod("Http/Get");
            } else if (method.equals(HttpMethod.POST)) {
                fullHttpRequest = (FullHttpRequest) httpObject;
                dealWithContentType();
                msg.setMethod("Http/Post");
            }
            Serializer serializer = new SerializerImpl();
            byte[] content = serializer.serialize(address.toString());
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(content));
            response.headers().set(CONTENT_TYPE, "text/plain");
            response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (!keepAlive) {
                channelHandlerContext.write(response).addListener(ChannelFutureListener.CLOSE);
            } else {
                response.headers().set(CONNECTION, KEEP_ALIVE);
                channelHandlerContext.write(response);
            }
            //TODO 推送消息到mqtt
            logger.info(msg.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void dealWithContentType() {
        String str = headers.get("Content-Type");
        if (str == null) {
            return;
        }
        String[] list = str.split(";");
        String contentType = list[0];
        if (CONTENT_TYPE_1.equals(contentType)) {
            String jsonStr = fullHttpRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
            JSONObject obj = JSON.parseObject(jsonStr);
            for (Map.Entry<String, Object> item : obj.entrySet()) {
                logger.info(item.getKey() + "=" + item.getValue().toString());
            }

        } else if (CONTENT_TYPE_2.equals(contentType)) {
            String jsonStr = fullHttpRequest.content().toString(Charsets.toCharset(CharEncoding.UTF_8));
            QueryStringDecoder queryDecoder = new QueryStringDecoder(jsonStr, false);
            Map<String, List<String>> uriAttributes = queryDecoder.parameters();
            for (Map.Entry<String, List<String>> attr : uriAttributes.entrySet()) {
                for (String attrVal : attr.getValue()) {
                    logger.info(attr.getKey() + "=" + attrVal);
                }
            }
        }
    }

}

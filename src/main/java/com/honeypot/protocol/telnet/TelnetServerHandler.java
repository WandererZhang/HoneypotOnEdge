package com.honeypot.protocol.telnet;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import com.honeypot.mqtt.AnalysisYaml;
import com.honeypot.mqtt.KubeedgeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.honeypot.pojo.Message;

import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Telnet协议Handler
 *
 * @author 78445
 */
public class TelnetServerHandler extends SimpleChannelInboundHandler<String> {
    private static final String CLIENT_STATUS_1 = "login";
    private static final String CLIENT_STATUS_2 = "password";
    private static final String EXIT = "exit";
    private static final Logger logger = LoggerFactory.getLogger(TelnetServerHandler.class);

    private static final Map<SocketAddress, String> clientStatus = new ConcurrentHashMap<>();
    private static final Map<SocketAddress, String> clientUser = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Message msg = new Message();
        msg.setAddress(ctx.channel().remoteAddress().toString());
        msg.setDate(new Date());
        msg.setMethod("Telnet");
        KubeedgeClient.getClientInstance().putData(AnalysisYaml.toJsonObject(msg).toString());
        logger.info(msg.toString());
        ctx.write("Ubuntu 16.04.7 LTS\r\n");
        ctx.write("ubuntu login: ");
        ctx.flush();
        clientStatus.put(ctx.channel().remoteAddress(), "login");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress address = ctx.channel().remoteAddress();
        clientStatus.remove(address);
        clientUser.remove(address);
        logger.info(ctx.channel().remoteAddress().toString() + " EXIT");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        SocketAddress incoming = ctx.channel().remoteAddress();
        String status = clientStatus.get(incoming);
        String response = "";
        if (status.equals(CLIENT_STATUS_1)) {
            logger.info("Telnet" + incoming + " login: " + msg);
            clientUser.put(incoming, msg);
            response = "Password: ";
            clientStatus.put(incoming, "password");
        } else if (status.equals(CLIENT_STATUS_2)) {
            logger.info("Telnet" + incoming + " password: " + msg);
            response = "Welcome to Ubuntu 16.04.7 LTS (GNU/Linux 4.15.0-112-generic x86_64)\r\n\r\n* Documentation:  https://help.ubuntu.com\r\n* Management:     https://landscape.canonical.com\r\n* Support:        https://ubuntu.com/advantage\r\n\r\n" + clientUser.get(incoming) + "@ubuntu:~$ ";
            clientStatus.put(ctx.channel().remoteAddress(), "else");
        } else if (!msg.toLowerCase().equals(EXIT)) {
            logger.info("Telnet" + incoming + " input: " + msg);
            response = clientUser.get(incoming) + "@ubuntu:~$ ";
        }
        ChannelFuture future = ctx.write(response);
        ctx.flush();
        if (msg.toLowerCase().equals(EXIT)) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}

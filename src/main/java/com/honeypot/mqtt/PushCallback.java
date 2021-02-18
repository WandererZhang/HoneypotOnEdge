package com.honeypot.mqtt;

import com.alibaba.fastjson.JSONObject;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.honeypot.threadpool.ServerThreadPool;

/**
 * 发布消息的回调类
 *
 * @author 78445
 */
public class PushCallback implements MqttCallback {
    private static final Logger logger = LoggerFactory.getLogger(PushCallback.class);
    private static final String ON = "ON";
    private static final String OFF = "OFF";

    @Override
    public void connectionLost(Throwable throwable) {
        throwable.printStackTrace();
        logger.info("com.honeypot.mqtt connect is closed, reconnecting");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        logger.info("com.honeypot.mqtt-topic: " + s);
        logger.info("com.honeypot.mqtt-Qos: " + mqttMessage.getQos());
        logger.info("com.honeypot.mqtt-msg: " + new String(mqttMessage.getPayload()));
        JSONObject msg = JSONObject.parseObject(new String(mqttMessage.getPayload()));
        JSONObject twin = (JSONObject) msg.get("twin");
        String httpStatus = (String) twin.getJSONObject("httpStatus").getJSONObject("current").getJSONObject("actual").get("value");
        String redisStatus = (String) twin.getJSONObject("redisStatus").getJSONObject("current").getJSONObject("actual").get("value");
        String telnetStatus = (String) twin.getJSONObject("telnetStatus").getJSONObject("current").getJSONObject("actual").get("value");
        String mysqlStatus = (String) twin.getJSONObject("mysqlStatus").getJSONObject("current").getJSONObject("actual").get("value");
        ServerThreadPool serverThreadPool = ServerThreadPool.getInstance();
        if (ON.equals(httpStatus)) {
            serverThreadPool.httpServerOn();
        } else if (OFF.equals(httpStatus)) {
            serverThreadPool.httpServerOff();
        }
        if (ON.equals(telnetStatus)) {
            serverThreadPool.telnetServerOn();
        } else if (OFF.equals(telnetStatus)) {
            serverThreadPool.telnetServerOff();
        }
        if (ON.equals(redisStatus)) {
            serverThreadPool.redisServerOn();
        } else if (OFF.equals(redisStatus)) {
            serverThreadPool.redisServerOff();
        }
        if (ON.equals(mysqlStatus)) {
            serverThreadPool.mysqlServerOn();
        } else if (OFF.equals(mysqlStatus)) {
            serverThreadPool.mysqlServerOff();
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        logger.info("deliveryStatus :" + iMqttDeliveryToken.isComplete());
    }
}

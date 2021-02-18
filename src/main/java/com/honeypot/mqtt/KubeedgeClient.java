package com.honeypot.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * MQTT连接类，用于推送/订阅消息
 *
 * @author 78445
 */
public class KubeedgeClient {
    private MqttMessage message;
    private MqttClient client;
    private MqttConnectOptions options;
    private MqttTopic clientTopic;
    private MqttTopic serverTopic;
    private static final String URL = "tcp://121.36.219.169:1883";
    private static final String CLIENT_TOPIC_STR = "$hw/events/device/honeypot/twin/update/document";
    private static final String SERVER_TOPIC_STR = "$hw/events/device/honeypot/twin/update";
    private static final Logger logger = LoggerFactory.getLogger(KubeedgeClient.class);

    private static KubeedgeClient clientInstance = new KubeedgeClient();

    private KubeedgeClient() {
        initializer();
        listerData();
    }

    public static KubeedgeClient getClientInstance() {
        return clientInstance;
    }

    private void initializer() {
        try {
            client = new MqttClient(URL, "KubeEdgeClient", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(new PushCallback());
            clientTopic = client.getTopic(CLIENT_TOPIC_STR);
            serverTopic = client.getTopic(SERVER_TOPIC_STR);
            client.connect(options);
            logger.info("com.honeypot.mqtt Initialized");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listerData() {
        int[] qos = {1};
        String[] topic = {CLIENT_TOPIC_STR};
        try {
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void publish(MqttTopic topic, MqttMessage msg) throws MqttException {
        MqttDeliveryToken token = topic.publish(msg);
        token.waitForCompletion();
        logger.info("mqttMessage is published completely! " + token.isComplete());
    }

    public void putData(String info) {
        message = new MqttMessage();
        message.setQos(1);
        message.setRetained(true);
        message.setPayload(info.getBytes());
        try {
            publish(serverTopic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

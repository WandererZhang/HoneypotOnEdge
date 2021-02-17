package com.honeypot;

import com.honeypot.mqtt.KubeedgeClient;
import lombok.extern.slf4j.Slf4j;
import com.honeypot.threadpool.ServerThreadPool;

/**
 * @author 78445
 */
@Slf4j
public class Main {
    public static void main(String[] args) {
        ServerThreadPool.getInstance();
        KubeedgeClient.getClientInstance();
    }
}

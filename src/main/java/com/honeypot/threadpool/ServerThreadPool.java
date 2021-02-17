package com.honeypot.threadpool;

import lombok.extern.slf4j.Slf4j;
import com.honeypot.pojo.PortStatus;
import com.honeypot.protocol.http.HttpServer;
import com.honeypot.protocol.mysql.MysqlServer;
import com.honeypot.protocol.redis.RedisServer;
import com.honeypot.protocol.telnet.TelnetServer;

import java.util.concurrent.*;

/**
 * 服务器线程池
 *
 * @author 78445
 */
@Slf4j
public class ServerThreadPool {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_NUM;
    private static final int MAXIMUM_POOL_SIZE = CPU_NUM * 2;
    private static final long KEEP_ALIVE_TIME = 10;
    private static final int HTTP_PORT = 8080;
    private static final int TELNET_PORT = 23;
    private static final int REDIS_PORT = 6379;
    private static final int MYSQL_PORT = 3306;
    private static final BlockingQueue<Runnable> WORK_QUEUE = new ArrayBlockingQueue<>(CPU_NUM);
    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, WORK_QUEUE, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
    private final HttpServer httpServer = new HttpServer(HTTP_PORT);
    private final TelnetServer telnetServer = new TelnetServer(TELNET_PORT);
    private final RedisServer redisServer = new RedisServer(REDIS_PORT);
    private final MysqlServer mysqlServer = new MysqlServer(MYSQL_PORT);
    private final PortStatus portStatus = PortStatus.getInstance();

    private static final ServerThreadPool serverThreadPool = new ServerThreadPool();

    private ServerThreadPool() {
        initializer();
    }

    public static ServerThreadPool getInstance() {
        return serverThreadPool;
    }

    private void initializer() {
        THREAD_POOL.prestartAllCoreThreads();
        log.info("serverThreadPool is running");
    }

    public void httpServerOn() {
        if (!portStatus.portIsAlive(HTTP_PORT)) {
            THREAD_POOL.execute(httpServer);
            portStatus.startPort(HTTP_PORT);
        }
    }

    public void httpServerOff() {
        if (portStatus.portIsAlive(HTTP_PORT)) {
            httpServer.closeServer();
            portStatus.endPort(HTTP_PORT);
        }
    }

    public void redisServerOn() {
        if (!portStatus.portIsAlive(REDIS_PORT)) {
            THREAD_POOL.execute(redisServer);
            portStatus.startPort(REDIS_PORT);
        }
    }

    public void redisServerOff() {
        if (portStatus.portIsAlive(REDIS_PORT)) {
            redisServer.closeServer();
            portStatus.startPort(REDIS_PORT);
        }
    }

    public void mysqlServerOn() {
        if (!portStatus.portIsAlive(MYSQL_PORT)) {
            THREAD_POOL.execute(mysqlServer);
            portStatus.startPort(MYSQL_PORT);
        }
    }

    public void mysqlServerOff() {
        if (portStatus.portIsAlive(MYSQL_PORT)) {
            mysqlServer.closeServer();
            portStatus.endPort(MYSQL_PORT);
        }
    }

    public void telnetServerOn() {
        if (!portStatus.portIsAlive(TELNET_PORT)) {
            THREAD_POOL.execute(telnetServer);
            portStatus.startPort(TELNET_PORT);
        }
    }

    public void telnetServerOff() {
        if (portStatus.portIsAlive(TELNET_PORT)) {
            telnetServer.closeServer();
            portStatus.endPort(TELNET_PORT);
        }
    }
}

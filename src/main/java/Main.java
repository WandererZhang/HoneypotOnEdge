
import protocol.http.*;
import lombok.extern.slf4j.Slf4j;
import protocol.mysql.MysqlServer;
import protocol.redis.RedisServer;
import protocol.telnet.TelnetServer;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author 78445
 */
@Slf4j
public class Main {
    private static final int CPU_NUM = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_NUM;
    private static final int MAXIMUM_POOL_SIZE = CPU_NUM * 2;
    private static final long KEEP_ALIVE_TIME = 10;
    private static final int HTTP_PORT = 8080;
    private static final int TELNET_PORT = 23;
    private static final int REDIS_PORT = 6379;
    private static final int MYSQL_PORT = 3306;

    public static void main(String[] args) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(CPU_NUM);
        ThreadPoolExecutor serverThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        serverThreadPool.prestartAllCoreThreads();
        log.info("serverThreadPool is running");
        HttpServer httpServer = new HttpServer(HTTP_PORT);
        TelnetServer telnetServer = new TelnetServer(TELNET_PORT);
        RedisServer redisServer = new RedisServer(REDIS_PORT);
        MysqlServer mysqlServer = new MysqlServer(MYSQL_PORT);
        while (true) {
            //TODO 通过MQTT推送消息控制蜜罐启动
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            if (str.equals("1")) {
                serverThreadPool.execute(httpServer);
            }
            if (str.equals("2")) {
                serverThreadPool.execute(telnetServer);
            }
            if (str.equals("3")) {
                httpServer.closeServer();
            }
            if (str.equals("4")) {
                telnetServer.closeServer();
            }
            if (str.equals("5")){
                serverThreadPool.execute(redisServer);
            }
            if (str.equals("6")){
                redisServer.closeServer();
            }
            if (str.equals("7")){
                serverThreadPool.execute(mysqlServer);
            }
            if (str.equals("8")){
                mysqlServer.closeServer();
            }
        }
    }
}

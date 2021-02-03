
import protocol.http.*;
import lombok.extern.slf4j.Slf4j;

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

    public static void main(String[] args) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(CPU_NUM);
        ThreadPoolExecutor serverThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        serverThreadPool.prestartAllCoreThreads();
        log.info("serverThreadPool is running");
        HttpServer httpServer = new HttpServer(HTTP_PORT);
        while (true) {
            //TODO 通过MQTT推送消息控制蜜罐启动
            Scanner scanner = new Scanner(System.in);
            String str = scanner.nextLine();
            if (str.equals("1")) {
                serverThreadPool.execute(httpServer);
                log.info("HttpServer is running");
            }
        }
    }
}

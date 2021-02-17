package pojo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 记录端口状态
 *
 * @author 78445
 */
public class PortStatus {
    private static final int HTTP_PORT = 8080;
    private static final int TELNET_PORT = 23;
    private static final int REDIS_PORT = 6379;
    private static final int MYSQL_PORT = 3306;
    private static Map<Integer, Boolean> portStatusMap = new ConcurrentHashMap<>();

    private static PortStatus portStatus = new PortStatus();

    private PortStatus() {
        portStatusMap.put(HTTP_PORT, false);
        portStatusMap.put(TELNET_PORT, false);
        portStatusMap.put(REDIS_PORT, false);
        portStatusMap.put(MYSQL_PORT, false);
    }

    public static PortStatus getInstance() {
        return portStatus;
    }

    public void startPort(int port) {
        portStatusMap.put(port, true);
    }

    public void endPort(int port) {
        portStatusMap.put(port, false);
    }

    public boolean portIsAlive(int port) {
        return portStatusMap.get(port);
    }

    public String portValue(int port) {
        if (portIsAlive(port)) {
            return "ON";
        } else {
            return "OFF";
        }
    }
}

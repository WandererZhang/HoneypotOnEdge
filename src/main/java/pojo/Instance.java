package pojo;

import java.net.SocketAddress;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 全局单例消息Map
 *
 * @author 78445
 */
public enum Instance {
    //消息Map
    MESSAGE_MAP;
    private static final Map<SocketAddress, Message> messageMap = new ConcurrentHashMap<>();

    public boolean isExist(SocketAddress address){
        return messageMap.containsKey(address);
    }

    public Message removeMessage(SocketAddress address) {
        Message msg = messageMap.get(address);
        messageMap.remove(address);
        return msg;
    }

    public void setMessage(SocketAddress address) {
        Message msg = new Message();
        msg.setAddress(address.toString());
        messageMap.put(address, msg);
    }

    public void setDate(SocketAddress address, Date date) {
        Message msg = messageMap.get(address);
        msg.setDate(date);
        messageMap.put(address, msg);
    }

    public void setMethod(SocketAddress address, String method) {
        Message msg = messageMap.get(address);
        msg.setMethod(method);
        messageMap.put(address, msg);
    }
}

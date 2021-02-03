package pojo;

import java.util.Date;

/**
 * 用于攻击者消息记录
 *
 * @author 78445
 */
public class Message {
    private String address;
    private String method;
    private Date date;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "address='" + address + '\'' +
                ", method='" + method + '\'' +
                ", date=" + date +
                '}';
    }
}

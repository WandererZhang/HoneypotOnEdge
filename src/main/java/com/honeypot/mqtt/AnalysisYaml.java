package com.honeypot.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import com.honeypot.pojo.Message;
import com.honeypot.pojo.PortStatus;

/**
 * 解析json
 *
 * @author 78445
 */
public class AnalysisYaml {
    private static final int HTTP_PORT = 8080;
    private static final int TELNET_PORT = 23;
    private static final int REDIS_PORT = 6379;
    private static final int MYSQL_PORT = 3306;

    @Data
    static class Struct {
        @JSONField(name = "actual")
        private JSONObject actual = new JSONObject();
        @JSONField(name = "metadata")
        private JSONObject metadata = new JSONObject();

        public Struct(String value, String type) {
            this.actual.put("value", value);
            this.metadata.put("type", type);
        }
    }

    public static JSONObject toJsonObject(Message msg) {
        PortStatus portStatus = PortStatus.getInstance();
        JSONObject jsonObject = new JSONObject(true);
        JSONObject twin = new JSONObject(true);
        jsonObject.put("event_id", "");
        jsonObject.put("timestamp", 0);
        twin.put("address", new Struct(msg.getAddress(), "Updated"));
        twin.put("method", new Struct(msg.getMethod(), "Updated"));
        twin.put("date", new Struct(msg.getDate().toString(), "Updated"));
        twin.put("httpStatus", new Struct(portStatus.portValue(HTTP_PORT), "Updated"));
        twin.put("telnetStatus", new Struct(portStatus.portValue(TELNET_PORT), "Updated"));
        twin.put("redisStatus", new Struct(portStatus.portValue(REDIS_PORT), "Updated"));
        twin.put("mysqlStatus", new Struct(portStatus.portValue(MYSQL_PORT), "Updated"));
        jsonObject.put("twin", twin);
        return jsonObject;
    }
}

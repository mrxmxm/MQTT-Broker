package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MqttDataReceiver {
    private static final String BROKER_URL = "tcp://localhost:8083";
    private static final String TOPIC = "test/topic";

    // 默认用户名和密码
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "public";

    private static final Map<Long, String> messageStore = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        // 启动 MQTT 订阅
        subscribeToMqtt();

        // 启动 RESTful API
        Spark.port(4567);
        Spark.get("/stats", MqttDataReceiver::getStats);
    }

    private static void subscribeToMqtt() {
        try {
            MqttClient client = new MqttClient(BROKER_URL, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            // 设置用户名和密码
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            client.connect(options);

            client.subscribe(TOPIC, (topic, message) -> {
                String payload = new String(message.getPayload());
                Message msg = parseMessage(payload);
                if (msg != null) {
                    messageStore.put(msg.getTimestamp(), msg.getType());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Message parseMessage(String payload) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(payload, Message.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String getStats(Request request, Response response) {
        long startTime = Long.parseLong(request.queryParams("start")) * 60;
        long endTime = Long.parseLong(request.queryParams("end")) * 60;

        Map<String, Integer> stats = new HashMap<>();
        stats.put("A", 0);
        stats.put("B", 0);
        stats.put("C", 0);
        stats.put("D", 0);

        for (Map.Entry<Long, String> entry : messageStore.entrySet()) {
            long timestamp = entry.getKey();
            String type = entry.getValue();
            if (timestamp >= startTime && timestamp <= endTime) {
                stats.put(type, stats.get(type) + 1);
            }
        }

        response.type("application/json");
        return new Gson().toJson(stats);
    }

    static class Message {
        private long timestamp;
        private String type;

        public long getTimestamp() {
            return timestamp;
        }

        public String getType() {
            return type;
        }
    }
}
package org.example;

import org.eclipse.paho.client.mqttv3.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MqttDataSender {
    private static final String BROKER_URL = "tcp://localhost:8083";
    private static final String TOPIC = "test/topic";
    private static final int MESSAGE_COUNT = 100;

    // 默认用户名和密码
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "public";

    public static void main(String[] args) {
        try {
            MqttClient client = new MqttClient(BROKER_URL, MqttClient.generateClientId());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);

            // 设置用户名和密码
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            client.connect(options);

            ObjectMapper objectMapper = new ObjectMapper();
            Random random = new Random();

            for (int i = 0; i < MESSAGE_COUNT; i++) {
                // 构造 JSON 消息
                long currentTime = System.currentTimeMillis() / 1000;
                long timestamp = currentTime + random.nextInt(61) - 30; // 当前时间前后 30 秒
                String type = getRandomType(random);

                Message message = new Message(timestamp, type);
                String payload = objectMapper.writeValueAsString(message);

                // 发布消息
                MqttMessage mqttMessage = new MqttMessage(payload.getBytes());
                mqttMessage.setQos(1);
                client.publish(TOPIC, mqttMessage);

                TimeUnit.MILLISECONDS.sleep(100); // 模拟间隔发送
            }

            client.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandomType(Random random) {
        String[] types = {"A", "B", "C", "D"};
        return types[random.nextInt(types.length)];
    }

    static class Message {
        private long timestamp;
        private String type;

        public Message(long timestamp, String type) {
            this.timestamp = timestamp;
            this.type = type;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public String getType() {
            return type;
        }
    }
}
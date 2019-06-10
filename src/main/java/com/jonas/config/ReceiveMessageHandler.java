package com.jonas.config;

import com.jonas.service.MqttService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * 接收消息处理器
 *
 * @author shenjy 2019/06/10
 */
public class ReceiveMessageHandler implements MessageHandler {

    @Autowired
    private MqttService mqttService;

    @Override
    public void handleMessage(Message<?> message) throws MessagingException {
        String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
        if ("hello".equalsIgnoreCase(topic)) {
            System.out.println("hello, " + message.getPayload().toString());
        } else {
            System.out.println("hi, " + message.getPayload().toString());
        }
    }

}

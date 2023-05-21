package com.jonas.controller;

import com.jonas.mqtt.gateway.MqttGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 【 enter the class description 】
 *
 * @author shenjy 2019/06/10
 */
@RestController
@RequestMapping("/mqtt")
@RequiredArgsConstructor
public class MqttController {
    private final MqttGateway mqttGateway;

    @PostMapping("/send")
    public String sendMsg(String data) {
        mqttGateway.sendToMqtt(data);
        return "success";
    }

    /**
     * 发送mqtt消息
     *
     * @param data  负载
     * @param topic 话题
     * @return
     */
    @PostMapping("/topic/send")
    public String sendMsg(String data, String topic) {
        mqttGateway.sendToMqtt(data, topic);
        return "success";
    }
}

package com.jonas.controller;

import com.jonas.mqtt.MqttGateway;
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
public class MqttController {

    @Autowired
    private MqttGateway mqttGateway;

    /**
     * 发送mqtt消息
     *
     * @param data  负载
     * @param topic 话题
     * @return
     */
    @PostMapping("/sendMsg")
    public String sendMsg(String data, String topic) {
        mqttGateway.sendToMqtt(data, topic);
        return "success";
    }
}

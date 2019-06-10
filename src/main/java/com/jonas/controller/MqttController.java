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

    @PostMapping("/sendMsg")
    public String sendMsg(String data) {
        mqttGateway.sendToMqtt(data, "hello");
        return "success";
    }
}

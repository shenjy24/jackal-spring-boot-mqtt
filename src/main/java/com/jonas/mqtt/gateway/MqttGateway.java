package com.jonas.mqtt.gateway;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;

/**
 * 发送消息接口
 *
 * @author shenjy 2019/06/10
 */
@MessagingGateway(defaultRequestChannel = "mqttOutputChannel")
public interface MqttGateway {

    /**
     * 向默认topic发送消息
     *
     * @param data 数据
     */
    void sendToMqtt(String data);

    /**
     * 指定 topic 进行消息发送
     *
     * @param data  数据
     * @param topic 主题
     */
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);

    /**
     * 指定 topic 和 qos 进行消息发送
     *
     * @param data  数据
     * @param topic 主题
     * @param qos   服务质量
     */
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos);
}

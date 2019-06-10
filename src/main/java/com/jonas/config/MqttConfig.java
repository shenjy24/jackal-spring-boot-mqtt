package com.jonas.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * 【 enter the class description 】
 *
 * @author shenjy 2019/06/10
 */
@Configuration
@IntegrationComponentScan
public class MqttConfig {

    @Autowired
    private MqttProperties mqttProp;

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(mqttProp.getUsername());
        options.setPassword(mqttProp.getPassword().toCharArray());
        options.setServerURIs(mqttProp.getHostUrl());
        return options;
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    /** 发送通道 */
    @Bean
    public MessageChannel mqttOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutputChannel")
    public MessageHandler mqttOutputHandler() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(mqttProp.getClientId(), mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultTopic(mqttProp.getDefaultTopic());
        return handler;
    }


    /** 接收通道 */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /** 配置client监听的topic */
    @Bean
    public MessageProducer messageProducer() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(mqttProp.getClientId() + "_input", mqttClientFactory(), mqttProp.getSubscriptionTopic());
        adapter.setCompletionTimeout(mqttProp.getConnectionTimeout());
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttInputHandler() {
        return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
                if ("hello".equalsIgnoreCase(topic)) {
                    System.out.println("hello, " + message.getPayload().toString());
                } else {
                    System.out.println("hi, " + message.getPayload().toString());
                }
            }
        };
    }
}

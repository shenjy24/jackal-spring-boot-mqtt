package com.jonas.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 【 enter the class description 】
 *
 * @author shenjy 2019/06/10
 */
@Getter
@Setter
@Component
@ConfigurationProperties("mqtt")
public class MqttProperties {
    private String username;
    private String password;
    private String[] hostUrl;
    private String clientId;
    private String defaultTopic;
    private Long connectionTimeout;
    private String[] subscriptionTopic;
}

## 简介
MQTT介绍：https://rensanning.iteye.com/blog/2406490

## Broker选型
Broker选用EMQ, 如下是安装说明。

下载地址：https://www.emqx.io/downloads#broker

程序包下载后，可直接解压启动运行，例如 Mac 平台:
```
unzip emqx-macosx-v3.1.0.zip && cd emqx

# 启动emqx
./bin/emqx start

# 检查运行状态
./bin/emqx_ctl status

# 停止emqx
./bin/emqx stop
```

控制台地址: http://127.0.0.1:18083，默认用户名: admin，密码：public

## Java客户端选型
选择spring boot集成的spring-integration-mqtt
所需依赖如下：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-integration</artifactId>
    <scope>compile</scope>
</dependency>

<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-stream</artifactId>
    <scope>compile</scope>
</dependency>

<dependency>
    <groupId>org.springframework.integration</groupId>
    <artifactId>spring-integration-mqtt</artifactId>
    <scope>compile</scope>
</dependency>
```

默认配置如下：
```yml
#### MQTT配置
## username: EMQX的登录用户名
## password: EMQX的登录密码
## host-url: MQTT-服务器连接地址，如果有多个，用逗号隔开，如：tcp://127.0.0.1:61613，tcp://192.168.2.133:61613
## client-id: MQTT-连接服务器默认客户端ID
## default-topic: MQTT-默认的消息推送主题，实际可在调用接口时指定
## connection-timeout: 连接超时时间
## subscription-topic: 客户端订阅的主题，此处配置的是共享订阅 https://www.emqx.io/docs/zh/v5.0/mqtt/mqtt-shared-subscription.html#%E6%9C%BA%E5%88%B6%E4%BB%8B%E7%BB%8D
mqtt:
  username: admin
  password: public
  host-url:
    - tcp://localhost:1883
  client-id: mqttId2
  default-topic: topic
  connection-timeout: 3000
  subscription-topic:
    - $share/group1/hello
    - $share/group1/hello1
```

subscription-topic 配置规则：




## Java Config
#### 通用配置
```java
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
```

```java
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
```

#### 发送通道配置
```java
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
```

#### 接收通道配置
```java
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
    return new ReceiveMessageHandler();
}
```

## 发送消息接口
```java
/**
 * 发送消息接口
 *
 * @author shenjy 2019/06/10
 */
@MessagingGateway(defaultRequestChannel = "mqttOutputChannel")
public interface MqttGateway {
    void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
}
```

## 接收消息处理器
```java
/**
 * 接收消息处理器
 *
 * @author shenjy 2019/06/10
 */
public class ReceiveMessageHandler implements MessageHandler {

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
```

## 完整例子
https://github.com/shenjy24/jackal-spring-boot-mqtt


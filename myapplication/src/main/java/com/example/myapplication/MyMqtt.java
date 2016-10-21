package com.example.myapplication;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Administrator on 2016/10/21 0021.
 */
public class MyMqtt {
    private String host = "tcp://10.1.8.243:61613";
    private String userName = "admin";
    private String passWord = "password";
    private MqttClient client;
    private MqttTopic topic;
    private String myTopic = "Topics/htjs/serverToPhone";
    private MqttMessage mqttMessage;
    public MessageCallback messageCallback;

    public MyMqtt(MessageCallback messageCallback) {
        this.messageCallback = messageCallback;
        try {
            client = new MqttClient(host, "Server", new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(mqttCallback);
            client.connect(options);


        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    MqttCallback mqttCallback = new MqttCallback() {
        //链接断开
        @Override
        public void connectionLost(Throwable cause) {

        }

        //接收消息
        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            messageCallback.msgCall(message.toString());
        }
        //成功响应
        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }
    };

    public void sedMessage(String msg) {

        try {
            mqttMessage = new MqttMessage();
            mqttMessage.setQos(1);
            mqttMessage.setPayload(msg.getBytes());
            topic = client.getTopic(myTopic);
            MqttDeliveryToken token = topic.publish(mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}

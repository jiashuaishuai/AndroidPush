package com.example.administrator.myapplicationmqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2016/10/21 0021.
 */
public class MyMqttService extends Service {
    private static String TAG = "PushService";
    private String host = "tcp://10.1.8.243:61613";
    private String userName = "admin";
    private String passWord = "password";
    private MqttClient client;
    private MqttConnectOptions options;
    private String[] myTopics = {"Topics/htjs/phoneToServer", "Topics/htjs/serverToPhone"};
    private int[] myQos = {2, 2};
    private ScheduledExecutorService scheduler;
    private NotificationManager notificationManager;


    @Override
    public void onCreate() {
        super.onCreate();
        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startReconnect();
        return super.onStartCommand(intent, flags, startId);

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private void init() {
        try {
            client = new MqttClient(host, "test", new MemoryPersistence());
            options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setUserName(userName);
            options.setPassword(passWord.toCharArray());
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);
            client.setCallback(mqttCallback);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    MqttCallback mqttCallback = new MqttCallback() {
        @Override
        public void connectionLost(Throwable cause) {
            // 连接丢失后，一般在这里面进行重连
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            // subscribe后得到的消息会执行到这里面

            Log.e(TAG, topic + "---" + message.toString());
//            Message msg = new Message();
//            msg.what  = 1;
//            msg.obj = topic+"----"+message.toString();
//            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new Notification();
//            notification.flags |= Notification.FLAG_SHOW_LIGHTS;
//            notification.flags |= Notification.FLAG_AUTO_CANCEL;
//            notification.defaults = Notification.DEFAULT_ALL;
//            notification.icon = R.mipmap.ic_launcher;
//            notification.when = System.currentTimeMillis();
//            Intent notificationIntent = new Intent(MyMqttService.this, MainActivity.class);
//            PendingIntent contentIntent = PendingIntent.getActivity(MyMqttService.this, 0,notificationIntent,0);
//            notification.setLatestEventInfo(getApplicationContext(), "My notification", message.toString(), contentIntent);
//            notificationManager.notify(1, notification);

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
// publish后会执行到这里
        }
    };

    private void startReconnect() {
        Log.i(TAG, "---->startReconnect");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "---->startReconnect" + "-------->" + client.isConnected());
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void connect() {
        Log.i(TAG, "---->connect");
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);// 建立连接
                    client.subscribe(myTopics, myQos);// 订阅主题
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        }).start();

    }
}

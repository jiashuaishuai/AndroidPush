package com.example.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class MainActivityMqtt extends AppCompatActivity implements MessageCallback {
    private EditText edit;
    private TextView tv;
    private Button btn;
    private MyMqtt myMqtt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_mqtt);
        edit = (EditText) findViewById(R.id.edit);
        tv = (TextView) findViewById(R.id.tv);
        btn = (Button) findViewById(R.id.btn);
        myMqtt = new MyMqtt(this);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myMqtt.sedMessage(edit.getText().toString());
            }
        });
    }

    @Override
    public void msgCall(String msg) {
        tv.setText(msg);
    }
}

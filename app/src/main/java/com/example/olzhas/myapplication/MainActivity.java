package com.example.olzhas.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    SensorManager SM;
    WifiManager wifiManager;
    private LinkedList<float[]> data = new LinkedList<>();
    int samplingPeriodUs = 50000;
    double time = 3;
    double threshold = 0.1;
    String result = "";
    TextView tv;

    private SensorEventListener SEL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    float[] temp = {event.values[0], event.values[1]};
                    data.add(temp);
                    if (data.size() > time * 1000000 / samplingPeriodUs) {
                        data.remove();
                    }

            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private BroadcastReceiver rssiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.startScan();
            int newRssi = wifiManager.getConnectionInfo().getRssi();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // accelerometer
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        SM.registerListener(SEL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), samplingPeriodUs);

        tv = (TextView)findViewById(R.id.textView);
        Button bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                double magnitude = 0;
                for (int i = 0; i < data.size(); i++) {
                    float[] temp = data.get(i);
                    magnitude = Math.sqrt(temp[0]*temp[0] + temp[1]*temp[1] + temp[2]*temp[2]) - 9.8;
                }
                magnitude = magnitude/data.size();
                if (magnitude > threshold) {
                    result = "Move\n";
                } else {
                    result = "Not move\n";
                }
                tv.setText(result);
            }
        });

        // wifi
        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(rssiReceiver);
    }
}

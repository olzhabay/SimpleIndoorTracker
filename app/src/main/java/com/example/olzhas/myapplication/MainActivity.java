package com.example.olzhas.myapplication;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qozix.tileview.TileView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager SM;
    private MapApplication mapApplication;
    TextView textStatus;
    Button buttonTracking;
    Button buttonShowMap;
    private LinkedList<float[]> data = new LinkedList<>();
    int samplingPeriodUs = 50000;
    double time = 3;
    double threshold = 0.1;
    String result = "";

    private SensorEventListener SEL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    float[] temp = {event.values[0], event.values[1], event.values[2]};
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // accelerometer
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        SM.registerListener(SEL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), samplingPeriodUs);

        // application
        mapApplication = (MapApplication) getApplication();

        // gui views
        textStatus = (TextView)findViewById(R.id.textView);
        buttonTracking = (Button)findViewById(R.id.buttonTracking);
        buttonShowMap = (Button)findViewById(R.id.buttonShowMap);

        // buttons
        initButtons();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initButtons() {

        buttonTracking.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                double magnitude = 0;
                for (int i = 0; i < data.size(); i++) {
                    float[] temp = data.get(i);
                    magnitude = Math.sqrt(temp[0]*temp[0] + temp[1]*temp[1] + temp[2]*temp[2]) - 9.8;
                }
                magnitude = magnitude/data.size();
                if (magnitude > threshold) {
                    result = "Move " + magnitude + "\n";
                } else {
                    result = "Not move\n";
                }
                textStatus.setText(result);
            }
        });

        buttonShowMap.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
                startActivity(intent);
            }
        });
    }

}

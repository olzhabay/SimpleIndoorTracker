package com.example.olzhas.myapplication;

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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SensorManager SM;
    WifiManager wifiManager;
    private LinkedList<float[]> data = new LinkedList<>();
    ArrayList<HashMap<String, String>> wifiList = new ArrayList<>();
    int samplingPeriodUs = 50000;
    double time = 3;
    double threshold = 0.1;
    String result = "";
    TextView textStatus;
    ListView listView;
    Button buttonRecognize;
    Button buttonScan;
    int scanSize = 0;
    SimpleAdapter adapter;

    String ITEM_KEY = "key";
    List<ScanResult> scanResults;

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
            scanResults = wifiManager.getScanResults();
            scanSize = scanResults.size();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // accelerometer
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        SM.registerListener(SEL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), samplingPeriodUs);

        textStatus = (TextView)findViewById(R.id.textView);
        listView = (ListView)findViewById(R.id.listView);
        buttonRecognize = (Button)findViewById(R.id.buttonRecognize);
        buttonScan = (Button)findViewById(R.id.buttonScan);
        buttonRecognize.setOnClickListener(new Button.OnClickListener(){
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
                textStatus.setText(result);
            }
        });
        buttonScan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiList.clear();
                wifiManager.startScan();
                Toast.makeText(getApplicationContext(), "Scanning..." + scanSize, Toast.LENGTH_SHORT).show();
                try {
                    scanSize = scanSize - 1;
                    while (scanSize >= 0) {
                        HashMap<String, String> item = new HashMap<>();
                        item.put(ITEM_KEY, scanResults.get(scanSize).SSID + " " + scanResults.get(scanSize).capabilities);
                        wifiList.add(item);
                        scanSize--;
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) { }
            }
        });

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled... makinng it enable", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        adapter = new SimpleAdapter(
                MainActivity.this,
                wifiList,
                R.layout.row,
                new String[] { ITEM_KEY },
                new int[] { R.id.list_value});
        registerReceiver(rssiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
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

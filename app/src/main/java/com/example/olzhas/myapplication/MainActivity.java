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
    WifiManager wifiManager;
    private BroadcastReceiver wifiReceiver;
    private MapApplication mapApplication;
    MapView mapView;
    TextView textStatus;
    Button buttonTracking;
    Button buttonScan;
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

        // gui views
        textStatus = (TextView)findViewById(R.id.textView);
        buttonTracking = (Button)findViewById(R.id.buttonTracking);
        buttonScan = (Button)findViewById(R.id.buttonScan);
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.loadMap(getResources(), R.drawable.floormap);

        // wifi
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                registerScanResults(wifiManager.getScanResults());
            }
        };
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        // Wifi set on
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(), "wifi is disabled... Enabling", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        // Permission ask
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        initButtons();
        scanWifiNetworks();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    private void scanWifiNetworks() {
        Log.d("MainActivity", "(scanWifiNetworks) start");
        wifiManager.startScan();
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
    }

    private void initButtons() {

        buttonScan.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanWifiNetworks();
            }
        });

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
                    result = "Move\n";
                } else {
                    result = "Not move\n";
                }
                textStatus.setText(result);
            }
        });
    }

    public void registerScanResults(List<ScanResult> results) {
        Log.d("MainActivity", "(registerScanResults) Scan result size=" + results.size());
        try {
            Fingerprint fingerprint = new Fingerprint();
            for (ScanResult scanResult : results) {
                AccessPoint ap;
                if (mapApplication.hasAccessPoint(scanResult.BSSID)) {
                    ap = mapApplication.getAccessPoint(scanResult.BSSID);
                } else {
                    ap = new AccessPoint(
                            scanResult.BSSID,
                            scanResult.SSID,
                            scanResult.capabilities,
                            scanResult.level,
                            scanResult.frequency);
                    mapApplication.addAccessPoint(ap);
                }
                int signalLevel = WifiManager.calculateSignalLevel(scanResult.level, 5);
                double distance = calculateDistance(scanResult.level, scanResult.frequency);
                fingerprint.add(ap, distance, signalLevel);
            }
            mapApplication.addFingerprint(fingerprint);
        } catch (Exception e) {
            Log.w("MainActivity", "(registerScanResults) Exception: " + e);
        }
    }

    public static double calculateDistance(double dbLevel, double mhzFrequency) {
        return Math.pow(10, (27.55 - (20 * Math.log10(mhzFrequency)) + Math.abs(dbLevel)) / 20);
    }
}

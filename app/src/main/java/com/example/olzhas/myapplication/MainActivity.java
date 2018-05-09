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

    private MapApplication mapApplication;
    TextView textStatus;
    Button buttonShowMap;
    Button buttonDeleteDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // application
        mapApplication = (MapApplication) getApplication();

        // gui views
        textStatus = (TextView)findViewById(R.id.textView);
        buttonShowMap = (Button)findViewById(R.id.buttonShowMap);
        buttonDeleteDB = (Button)findViewById(R.id.buttonDeleteDB);

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

        buttonShowMap.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MapViewActivity.class);
            startActivity(intent);
        });

        buttonDeleteDB.setOnClickListener(v -> {
            mapApplication.purge();
            Toast.makeText(MainActivity.this, "Fingerprints Purged", Toast.LENGTH_LONG).show();
        });
    }

}

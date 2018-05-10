package com.example.olzhas.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapViewActivity extends TileViewActivity {
    private static int LEFT = 0;
    private static int TOP = 2783;
    private static int RIGHT = 5464;
    private static int BOTTOM = 0;

    private final static double PX_PER_METER = 1314.69/50.1;
    private final static int KNN_VALUE = 6;

    MapApplication mapApplication;
    WifiManager wifiManager;
    private BroadcastReceiver wifiReceiver;
    List<ScanResult> scanResults;
    Pair<Double, Double> currentPosition;
    ImageView positionMarker;
    SensorManager SM;
    boolean isTracking = false;
    private LinkedList<float[]> data = new LinkedList<>();

    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float[] R_ = new float[9];
    private float[] I_ = new float[9];
    private float azimuth;

    private SensorEventListener SEL = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            final float alpha = 0.97f;
            synchronized (this) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
                        mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
                        mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
                        break;
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
                        mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
                        mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
                        break;
                }
                boolean success = SensorManager.getRotationMatrix(R_, I_, mGravity, mGeomagnetic);
                if (success) {
                    float orientation[] = new float[3];
                    SensorManager.getOrientation(R_, orientation);
                    azimuth = (float) Math.toDegrees(orientation[0]);
                    azimuth = azimuth  % 360;
                    Log.d("Compass", "Azimuth = " + azimuth);
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    Handler handler = new Handler();
    final Runnable locationUpdate = new Runnable() {
        @Override
        public void run() {
            if (isTracking) {
                wifiManager.startScan();
                currentPosition = calculateKNNCoordinate(calculateMeasurements(scanResults), KNN_VALUE);
                getTileView().moveMarker(positionMarker, currentPosition.first, currentPosition.second);
                Log.d("trackingThread", " Coordinate " + currentPosition.toString());
            }
            //This line will continuously call this Runnable with 3000 milliseconds gap
            handler.postDelayed(locationUpdate, 3000);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Application
        mapApplication = (MapApplication) getApplication();

        // accelerometer
        SM = (SensorManager)getSystemService(SENSOR_SERVICE);
        SM.registerListener(SEL, SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
        SM.registerListener(SEL, SM.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);


        // wifi
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                scanResults = wifiManager.getScanResults();
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

        // TileView
        final TileView tileView = getTileView();
        tileView.setSize(RIGHT, TOP);
        tileView.addDetailLevel(1.0f, "tiles/map-%d_%d.png");
        tileView.defineBounds(LEFT, TOP, RIGHT, BOTTOM);
        tileView.setScale(0.5f);
        tileView.setViewportPadding(256);
        tileView.setShouldRenderWhilePanning(true);
        HotSpot hotSpot = new HotSpot();
        hotSpot.setTag(this);
        hotSpot.set(new Rect(LEFT, BOTTOM, RIGHT, TOP));
        hotSpot.setHotSpotTapListener((hotspot, x, y) -> {
            Activity activity = (Activity) hotspot.getTag();
            Log.d("MapViewActivity", "(onHotSpotTap) coordinates " + x + " " + y);
            View view = new View(activity.getApplicationContext());
            activity.addContentView(view, new ViewGroup.LayoutParams(100, 100));
            PopupMenu popup = new PopupMenu (activity.getApplicationContext(), view);
            popup.setOnMenuItemClickListener (item -> {
                int id = item.getItemId();
                switch (id)
                {
                    case R.id.menu_fingerprint:
                        Log.d ("onMenuClick", "menu_fingerprint");
                        fingerprintPosition(x, y);
                        break;
                    case R.id.menu_track:
                        Log.d ("onMenuClick", "menu_track");
                        startTrackMode();
                        break;
                }
                return true;
            });
            popup.getMenuInflater().inflate(R.menu.menu_layout, popup.getMenu());
            popup.show();
        });
        tileView.addHotSpot(hotSpot);

        // position init
        currentPosition = new Pair<>(0.0, 0.0);
        positionMarker = new ImageView(this);
        positionMarker.setTag(currentPosition);
        positionMarker.setImageResource(R.drawable.map_marker_normal);
        tileView.addMarker(positionMarker, currentPosition.first, currentPosition.second, null, null);

        registerForContextMenu(getTileView());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

    private void startTrackMode() {
        if (isTracking) {
            Toast.makeText(getApplicationContext(), "Tracking is OFF", Toast.LENGTH_SHORT).show();
            isTracking = false;
            try {
                locationUpdate.wait();
            } catch (Exception e) {
                //
                Log.d("startTrackMode", "Exception" + e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Tracking is ON", Toast.LENGTH_SHORT).show();
            isTracking = true;
            locationUpdate.run();
        }
    }

    private void fingerprintPosition(double x, double y) {
        Log.d("MapViewActivity", "(fingerprintPosition) start");
        wifiManager.startScan();
        registerFingerprint(scanResults, x, y);
        Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
    }

    public void registerFingerprint(List<ScanResult> results, double x, double y) {
        try {
            Log.d("MapViewActivity", "(registerFingerprint) Scan result size=" + results.size());
            Fingerprint fingerprint = new Fingerprint();
            fingerprint.setCoordinates(x, y);
            fingerprint.setMeasurements(calculateMeasurements(results));
            mapApplication.addFingerprint(fingerprint);
        } catch (Exception e) {
            Log.w("MapViewActivity", "(registerFingerprint) Exception: " + e);
        }
    }

    public static double calculateDistance(double dbLevel, double mhzFrequency) {
        return Math.pow(10, (27.55 - (20 * Math.log10(mhzFrequency)) + Math.abs(dbLevel)) / 20);
    }

    private HashMap<String, Measurement> calculateMeasurements(List<ScanResult> results) {
        List<AccessPoint> accessPoints = getCurrentAccessPoints(results);
        HashMap<String, Measurement> measurements = new HashMap<>();
        for (AccessPoint accessPoint : accessPoints) {
            int signalLevel = WifiManager.calculateSignalLevel(accessPoint.getLevel(), 5);
            double distance = calculateDistance(accessPoint.getLevel(), accessPoint.getFrequency());
            Measurement measurement = new Measurement(accessPoint, distance, signalLevel);
            measurements.put(accessPoint.getBSSID(), measurement);
        }
        return measurements;
    }

    private Pair<Double, Double> calculateKNNCoordinate(HashMap<String, Measurement> currentMeasurements, int k) {
        ArrayList<Fingerprint> fingerprints = mapApplication.getFingerprints();
        ArrayList<Pair<Double, Fingerprint>> pairs = new ArrayList<>();
        for (Fingerprint fingerprint : fingerprints) {
            double score = 0.0;
            for (Map.Entry<String, Measurement> entry : currentMeasurements.entrySet()) {
                double diff = 0.0;
                Measurement measurement = entry.getValue();
                try {
                    Measurement jMeasurement = fingerprint.getMeasurement(entry.getKey());
                    diff += measurement.getDistance() - jMeasurement.getDistance();
                } catch (Exception e) {
                    // skip
                }
                score += Math.pow(diff, 2);
            }
            score = Math.sqrt(score) / fingerprints.size();
            Pair<Double, Fingerprint> pair = new Pair<>(score, fingerprint);
            pairs.add(pair);
        }
        pairs.sort((p1, p2) -> p1.first.compareTo(p2.first));
        k = Math.min(k, pairs.size());
        double x = 0.0;
        double y = 0.0;
        if (k > 0) {
            for (int i = 0; i < k; i++) {
                x += pairs.get(i).second.getX();
                y += pairs.get(i).second.getY();
            }
            x = x / k;
            y = y / k;
        }
        return new Pair<>(x, y);
    }


    private List<AccessPoint> getCurrentAccessPoints(List<ScanResult> scanResults) {
        List<AccessPoint> accessPoints = new ArrayList<>();
        try {
            for (ScanResult scanResult : scanResults) {
                AccessPoint accessPoint;
                if (mapApplication.hasAccessPoint(scanResult.BSSID)) {
                    accessPoint = mapApplication.getAccessPoint(scanResult.BSSID);
                } else {
                    accessPoint = new AccessPoint(
                            scanResult.BSSID,
                            scanResult.SSID,
                            scanResult.capabilities,
                            scanResult.level,
                            scanResult.frequency);
                    mapApplication.addAccessPoint(accessPoint);
                }
                accessPoints.add(accessPoint);
            }
        } catch (Exception e) {
            // skip
        }
        return accessPoints;
    }
}

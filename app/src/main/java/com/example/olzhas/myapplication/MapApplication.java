package com.example.olzhas.myapplication;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapApplication extends Application {

    private ArrayList<Fingerprint> fingerprints;
    private HashMap<String, AccessPoint> accessPoints;
    private MapDBHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new MapDBHelper(this);
        accessPoints = dbHelper.getAccessPoints();
        fingerprints = dbHelper.getFingerprints(accessPoints);
    }

    public ArrayList<Fingerprint> getFingerprints() {
        return fingerprints;
    }

    public HashMap<String, AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public void addFingerprint(Fingerprint fingerprint) {
        Log.d("MapApplication", "(addFingerprint) new fingerprint added " + fingerprint.getX() + ":" + fingerprint.getY());
        fingerprints.add(fingerprint);
        dbHelper.addFingerprint(fingerprint);
    }

    public void addAccessPoint(AccessPoint accessPoint) {
        Log.d("MapApplication", "(addAccessPoint) new ap added " + accessPoint.getBSSID());
        accessPoints.put(accessPoint.getBSSID(), accessPoint);
        dbHelper.addAccessPoint(accessPoint);
    }

    public AccessPoint getAccessPoint(String BSSID) {
        return accessPoints.get(BSSID);
    }

    public boolean hasAccessPoint(String BSSID) {
        return accessPoints.containsKey(BSSID);
    }

    public void purge() {
        dbHelper.purge();
    }
}

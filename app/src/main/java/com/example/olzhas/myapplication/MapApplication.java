package com.example.olzhas.myapplication;

import android.app.Application;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapApplication extends Application {

    private ArrayList<Fingerprint> fingerprints;
    private HashMap<Integer, AccessPoint> accessPoints;
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

    public HashMap<Integer, AccessPoint> getAccessPoints() {
        return accessPoints;
    }

    public void addFingerprint(Fingerprint fingerprint) {
        Log.d("MapApplication", "(addFingerprint) new fingerprint added " + fingerprint.getX() + ":" + fingerprint.getY());
        fingerprints.add(fingerprint);
        dbHelper.addFingerprint(fingerprint);
    }

    public void addAccessPoint(AccessPoint accessPoint) {
        Log.d("MapApplication", "(addAccessPoint) new ap added " + accessPoint.getBSSID());
        accessPoints.put(accessPoint.getId(), accessPoint);
        dbHelper.addAccessPoint(accessPoint);
    }

    public AccessPoint getAccessPoint(String BSSID) {
        for (Map.Entry<Integer, AccessPoint> accessPoint : accessPoints.entrySet()) {
            if (accessPoint.getValue().getBSSID().equals(BSSID)) {
                return accessPoint.getValue();
            }
        }
        return null;
    }

    public boolean hasAccessPoint(String BSSID) {
        for (Map.Entry<Integer, AccessPoint> accessPoint : accessPoints.entrySet()) {
            if (accessPoint.getValue().getBSSID().equals(BSSID)) {
                return true;
            }
        }
        return false;
    }

    public void purge() {
        dbHelper.purge();
    }
}

package com.example.olzhas.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class WifiReceiver extends BroadcastReceiver {
    private WifiManager wifiManager;
    private HashMap<String, AccessPoint> accessPoints;

    public WifiReceiver(WifiManager wifiManager) {
        this.wifiManager = wifiManager;
        accessPoints = new HashMap<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("WifiReceiver", "onReceive");
        List<ScanResult> scanResults = wifiManager.getScanResults();
        Log.d("WifiReceiver", "Scan result size=" + scanResults.size());
        try {
            Fingerprint fingerprint = new Fingerprint();
            for (ScanResult scanResult : scanResults) {
                AccessPoint ap;
                if (!accessPoints.containsKey(scanResult.BSSID)) {
                    ap = new AccessPoint(
                            scanResult.BSSID,
                            scanResult.SSID,
                            scanResult.capabilities,
                            scanResult.level,
                            scanResult.frequency);
                    accessPoints.put(scanResult.BSSID, ap);
                } else {
                    ap = accessPoints.get(scanResult.BSSID);
                }
                int signalLevel = WifiManager.calculateSignalLevel(scanResult.level, 5);
                double distance = calculateDistance(scanResult.level, scanResult.frequency);
                fingerprint.add(ap, distance, signalLevel);
            }
        } catch (Exception e) {
            Log.w("WifiScanner", "Exception: " + e);
        }
    }

    public static double calculateDistance(double dbLevel, double mhzFrequency) {
        return Math.pow(10, (27.55 - (20 * Math.log10(mhzFrequency)) + Math.abs(dbLevel)) / 20);
    }
}

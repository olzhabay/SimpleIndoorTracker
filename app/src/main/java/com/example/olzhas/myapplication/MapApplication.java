package com.example.olzhas.myapplication;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class MapApplication extends Application {

    private ArrayList<Fingerprint> fingerprints;
    private HashMap<Integer, AccessPoint> accessPoints;

    @Override
    public void onCreate() {
        super.onCreate();
    }

}

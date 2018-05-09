package com.example.olzhas.myapplication;

import java.util.ArrayList;
import java.util.HashMap;

public class Fingerprint {
    public static String TABLE_NAME = "fingerprints";
    public static String COLUMN_ID = "id";
    public static String COLUMN_X = "x";
    public static String COLUMN_Y = "y";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_X + " FLOAT, "
                    + COLUMN_Y + " FLOAT"
                    + ")";

    private static int AUTOINCREMENT_ID = 0;

    private int id;
    private HashMap<String, Measurement> measurements;
    private double x;
    private double y;

    public Fingerprint() {
        AUTOINCREMENT_ID++;
        this.id = AUTOINCREMENT_ID;
        this.measurements = new HashMap<>();
    }

    public Fingerprint(double x, double y, HashMap<String, Measurement> measurements) {
        AUTOINCREMENT_ID++;
        this.id = AUTOINCREMENT_ID;
        this.x = x;
        this.y = y;
        this.measurements = measurements;
    }

    public Fingerprint(int id, double x, double y, HashMap<String, Measurement> measurements) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.measurements = measurements;
        if (AUTOINCREMENT_ID < id) AUTOINCREMENT_ID = id;
    }

    public void add(AccessPoint accessPoint, double distance, int level) {
        Measurement measurement = new Measurement(accessPoint, distance, level);
        measurements.put(accessPoint.getBSSID(), measurement);
    }


    public int getId() {
        return id;
    }

    public Measurement getMeasurement(String BSSID) {
        return measurements.get(BSSID);
    }

    public HashMap<String, Measurement> getMeasurements() {
        return measurements;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setMeasurements(HashMap<String, Measurement> measurements) {
        this.measurements = measurements;
    }
}

package com.example.olzhas.myapplication;


import java.util.ArrayList;

class Measurement {
    public static String TABLE_NAME = "ap_measurements";
    public static String COLUMN_ID = "id";
    public static String COLUMN_FINGERPRINT = "fingerprint_id";
    public static String COLUMN_AP_BSSID = "access_point_bssid";
    public static String COLUMN_DISTANCE = "distance";
    public static String COLUMN_LEVEL = "level";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID +  " INTEGER PRIMARY KEY, "
                    + COLUMN_FINGERPRINT + " INTEGER, "
                    + COLUMN_AP_BSSID + " TEXT, "
                    + COLUMN_DISTANCE + " FLOAT, "
                    + COLUMN_LEVEL + " INTEGER"
                    + ")";

    private static int AUTOINCREMENT_ID = 0;

    private int id;
    private AccessPoint accessPoint;
    private double distance;
    private int level;

    public Measurement(int id, AccessPoint accessPoint, double distance, int level) {
        this.id = id;
        this.accessPoint = accessPoint;
        this.distance = distance;
        this.level = level;
        if (AUTOINCREMENT_ID < id) AUTOINCREMENT_ID = id;
    }

    public Measurement(AccessPoint accessPoint, double distance, int level) {
        AUTOINCREMENT_ID++;
        this.id = AUTOINCREMENT_ID;
        this.accessPoint = accessPoint;
        this.distance = distance;
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public AccessPoint getAccessPoint() {
        return accessPoint;
    }

    public double getDistance() {
        return distance;
    }

    public int getLevel() {
        return level;
    }
}

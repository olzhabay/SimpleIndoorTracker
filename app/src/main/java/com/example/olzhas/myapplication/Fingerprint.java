package com.example.olzhas.myapplication;

import java.util.ArrayList;

public class Fingerprint {
    public static String TABLE_NAME = "fingerprints";
    public static String COLUMN_ID = "id";
    public static String COLUMN_X = "x";
    public static String COLUMN_Y = "y";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY"
                    + COLUMN_X + " FLOAT, "
                    + COLUMN_Y + " FLOAT"
                    + ")";

    private static int AUTOINCREMENT_ID = 0;

    private int id;
    private ArrayList<Measurement> measurements;
    private double x;
    private double y;

    public Fingerprint(double x, double y, ArrayList<Measurement> measurements) {
        AUTOINCREMENT_ID++;
        this.id = AUTOINCREMENT_ID;
        this.x = x;
        this.y = y;
        this.measurements = measurements;
    }

    public Fingerprint(int id, double x, double y, ArrayList<Measurement> measurements) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.measurements = measurements;
        if (AUTOINCREMENT_ID < id) AUTOINCREMENT_ID = id;
    }

    public void add(AccessPoint accessPoint, double distance, int level) {
        Measurement measurement = new Measurement(accessPoint, distance, level);
        measurements.add(measurement);
    }


    public int getId() {
        return id;
    }

    public ArrayList<Measurement> getMeasurements() {
        return measurements;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

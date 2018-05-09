package com.example.olzhas.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// ref: https://www.androidhive.info/2011/11/android-sqlite-database-tutorial/

public class MapDBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "fingerprint_db";


    public MapDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AccessPoint.CREATE_TABLE);
        db.execSQL(Fingerprint.CREATE_TABLE);
        db.execSQL(Measurement.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AccessPoint.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Fingerprint.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Measurement.TABLE_NAME);
        onCreate(db);
    }

    public void addAccessPoint(AccessPoint accessPoint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AccessPoint.COLUMN_ID, accessPoint.getId());
        values.put(AccessPoint.COLUMN_BSSID, accessPoint.getBSSID());
        values.put(AccessPoint.COLUMN_SSID, accessPoint.getSSID());
        values.put(AccessPoint.COLUMN_CAPABILITIES, accessPoint.getCapabilities());
        values.put(AccessPoint.COLUMN_LEVEL, accessPoint.getLevel());
        values.put(AccessPoint.COLUMN_FREQUENCY, accessPoint.getFrequency());

        db.insert(AccessPoint.TABLE_NAME, null, values);
        db.close();
    }

    public void addFingerprint(Fingerprint fingerprint) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues fingerprintValues = new ContentValues();
        fingerprintValues.put(Fingerprint.COLUMN_ID, fingerprint.getId());
        fingerprintValues.put(Fingerprint.COLUMN_X, fingerprint.getX());
        fingerprintValues.put(Fingerprint.COLUMN_Y, fingerprint.getY());
        db.insert(Fingerprint.TABLE_NAME, null, fingerprintValues);
        for (Map.Entry<String, Measurement> pair : fingerprint.getMeasurements().entrySet()) {
            Measurement measurement = pair.getValue();
            ContentValues measurementValues = new ContentValues();
            measurementValues.put(Measurement.COLUMN_ID, measurement.getId());
            measurementValues.put(Measurement.COLUMN_FINGERPRINT, fingerprint.getId());
            measurementValues.put(Measurement.COLUMN_AP_BSSID, measurement.getAccessPoint().getBSSID());
            measurementValues.put(Measurement.COLUMN_DISTANCE, measurement.getDistance());
            measurementValues.put(Measurement.COLUMN_LEVEL, measurement.getLevel());
            db.insert(Measurement.TABLE_NAME, null, measurementValues);
        }
        db.close();
    }

    public HashMap<String, AccessPoint> getAccessPoints() {
        HashMap<String, AccessPoint> accessPoints = new HashMap<>();
        String ACCESSPOINTS_SELECT = "SELECT * FROM " + AccessPoint.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor accesspointCursor = db.rawQuery(ACCESSPOINTS_SELECT, null);
        if (accesspointCursor.moveToFirst()) {
            do {
                int id = accesspointCursor.getInt(0);
                String bssid = accesspointCursor.getString(1);
                String ssid = accesspointCursor.getString(2);
                String capabilities = accesspointCursor.getString(3);
                int level = accesspointCursor.getInt(4);
                int frequency = accesspointCursor.getInt(5);
                AccessPoint accessPoint = new AccessPoint(id, bssid, ssid, capabilities, level, frequency);
                accessPoints.put(bssid, accessPoint);
            } while (accesspointCursor.moveToNext());
        }
        db.close();
        return accessPoints;
    }

    public ArrayList<Fingerprint> getFingerprints(Map<String, AccessPoint> accessPoints) {
        ArrayList<Fingerprint> fingerprints = new ArrayList<>();
        String FINGERPRINTS_SELECT = "SELECT * FROM " + Fingerprint.TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor fingerprintCursor = db.rawQuery(FINGERPRINTS_SELECT, null);
        if (fingerprintCursor.moveToFirst()) {
            do {
                int id = fingerprintCursor.getInt(0);
                Double x = fingerprintCursor.getDouble(1);
                Double y = fingerprintCursor.getDouble(2);
                HashMap<String, Measurement> measurements = new HashMap<>();
                // get measurements
                String MEASUREMENTS_SELECT = "SELECT * FROM " + Measurement.TABLE_NAME +
                        " WHERE " + Measurement.COLUMN_FINGERPRINT + " = " + id;
                Cursor measurementCursor = db.rawQuery(MEASUREMENTS_SELECT, null);
                if (measurementCursor.moveToFirst()) {
                    do {
                        AccessPoint ap = accessPoints.get(measurementCursor.getString(2));
                        Measurement measurement = new Measurement(
                                measurementCursor.getInt(0),
                                ap,
                                measurementCursor.getDouble(3),
                                measurementCursor.getInt(4)
                        );
                        measurements.put(ap.getBSSID(), measurement);
                    } while (measurementCursor.moveToNext());
                }
                fingerprints.add(new Fingerprint(id, x, y, measurements));
            } while (fingerprintCursor.moveToNext());
        }
        db.close();
        return fingerprints;
    }

    public void purge() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + AccessPoint.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Fingerprint.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + Measurement.TABLE_NAME);
        db.close();
    }
}

package com.example.olzhas.myapplication;

import java.util.Objects;

public class AccessPoint {
    public static String TABLE_NAME = "access_points";
    public static String COLUMN_ID = "id";
    public static String COLUMN_BSSID = "bssid";
    public static String COLUMN_SSID = "ssid";
    public static String COLUMN_CAPABILITIES = "capabilities";
    public static String COLUMN_LEVEL = "level";
    public static String COLUMN_FREQUENCY = "frequency";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ("
                    + COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + COLUMN_BSSID + " TEXT, "
                    + COLUMN_SSID + " TEXT, "
                    + COLUMN_CAPABILITIES + " TEXT, "
                    + COLUMN_LEVEL + " INT, "
                    + COLUMN_FREQUENCY + " INT"
                    + ")";

    private static int AUTOINCREMENT_ID = 0;

    private int id;
    private String BSSID;
    private String SSID;
    private String capabilities;
    private int level;
    private int frequency;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccessPoint that = (AccessPoint) o;
        return BSSID.equals(that.BSSID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(BSSID);
    }

    AccessPoint(int id, String BSSID, String SSID, String capabilities, int level, int frequency) {
        this.id = id;
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.capabilities = capabilities;
        this.level = level;
        this.frequency = frequency;
        if (AUTOINCREMENT_ID < id) AUTOINCREMENT_ID = id;
    }

    AccessPoint(String BSSID, String SSID, String capabilities, int level, int frequency) {
        AUTOINCREMENT_ID++;
        this.id = AUTOINCREMENT_ID;
        this.BSSID = BSSID;
        this.SSID = SSID;
        this.capabilities = capabilities;
        this.level = level;
        this.frequency = frequency;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setBSSID(String BSSID) {
        this.BSSID = BSSID;
    }

    public void setSSID(String SSID) {
        this.SSID = SSID;
    }

    public int getId() {
        return id;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public int getLevel() {
        return level;
    }

    public int getFrequency() {
        return frequency;
    }

    public String getBSSID() {
        return BSSID;
    }

    public String getSSID() {
        return SSID;
    }
}

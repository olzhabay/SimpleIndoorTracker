package com.example.olzhas.myapplication;

import android.util.Pair;

public class Position {
    private double x;
    private double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void move(Pair<Double, Double> pair) {
        this.x = pair.first;
        this.y = pair.second;
    }

    public void move(double x, double y) {
        this.x = x;
        this.y = y;
    }
}

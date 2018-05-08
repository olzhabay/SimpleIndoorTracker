package com.example.olzhas.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.qozix.tileview.TileView;

public class MapViewActivity extends TileViewActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TileView tileView = getTileView();
        tileView.setSize(5464, 2783);
        tileView.addDetailLevel(1.0f, "tiles/map-%d_%d.png");
        tileView.defineBounds(0.0, 2783.0, 5464.0, 0.0);
        tileView.setScale(0.5f);
        tileView.setViewportPadding(256);
        tileView.setShouldRenderWhilePanning(true);
    }
}

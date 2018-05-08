package com.example.olzhas.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.qozix.tileview.TileView;
import com.qozix.tileview.hotspots.HotSpot;
import com.qozix.tileview.hotspots.HotSpotManager;
import com.qozix.tileview.markers.MarkerLayout;

public class MapViewActivity extends TileViewActivity {
    private static int LEFT = 0;
    private static int TOP = 2783;
    private static int RIGHT = 5464;
    private static int BOTTOM = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final TileView tileView = getTileView();
        tileView.setSize(RIGHT, TOP);
        tileView.addDetailLevel(1.0f, "tiles/map-%d_%d.png");
        tileView.defineBounds(LEFT, TOP, RIGHT, BOTTOM);
        tileView.setScale(0.5f);
        tileView.setViewportPadding(256);
        tileView.setShouldRenderWhilePanning(true);
        HotSpot hotSpot = new HotSpot();
        hotSpot.setTag(this);
        hotSpot.set(new Rect(LEFT, BOTTOM, RIGHT, TOP));
        hotSpot.setHotSpotTapListener(new HotSpot.HotSpotTapListener() {
            @Override
            public void onHotSpotTap(HotSpot hotSpot, int x, int y) {
                Activity activity = (Activity) hotSpot.getTag();
                Log.d("hotspottagged", "coordinates " + x + " " + y);
            }
        });
        tileView.addHotSpot(hotSpot);

//        tileView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                MapViewActivity.super.onTouchEvent(event);
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//                    float x = event.getX();
//                    float y = event.getY();
//                    String msg = "coordinate : " + x + " / " + y;
//                    Toast.makeText(MapViewActivity.this, msg, Toast.LENGTH_SHORT).show();
//                    return true;
//                }
//                return false;
//            }
//        });
        registerForContextMenu(getTileView());
    }
}

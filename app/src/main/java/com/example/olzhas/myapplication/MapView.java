package com.example.olzhas.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class MapView extends ImageView {
    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    @Override
    protected void onDraw(Canvas canvas) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }


}

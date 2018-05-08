package com.example.olzhas.myapplication;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

// ref: https://gist.github.com/xalexchen/6737649

public class MapView extends ImageView {

    Bitmap bitmap;
    BitmapFactory.Options options;

    public MapView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    public void loadMap(Resources resources, int resId) {
        options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        bitmap = Bitmap.createBitmap(options.outWidth, options.outHeight, Bitmap.Config.ARGB_8888);
        options.inJustDecodeBounds = false;
        options.inBitmap = bitmap;
        BitmapFactory.decodeResource(resources, resId, options);
        setImageBitmap(bitmap);
    }


}

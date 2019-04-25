package com.example.micandmaster;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;


public class WaveFormView extends View {
    private Paint paint = new Paint();

    public WaveFormView(Context context) {
        super(context);
    }

    public WaveFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WaveFormView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        paint.setARGB(100, 100, 100, 100);
        canvas.drawLine(0, 0, 100, 100, paint);
    }
}

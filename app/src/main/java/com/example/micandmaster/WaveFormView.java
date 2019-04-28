package com.example.micandmaster;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.example.micandmaster.audio.WaveFormCalc;

import java.io.File;


public class WaveFormView extends View {
    private Paint paint;
    private WaveFormCalc waveFormCalc;

    public WaveFormView(Context context) {
        super(context);
        init();
    }

    public WaveFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WaveFormView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setColor(0xff101010);
    }

    public void drawWaveForm(File audioFile) {
        WaveFormCalc waveCalc = new WaveFormCalc(audioFile);
        for(int value : waveCalc.getValues()){
            System.out.println(value);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(200, 200, 300, 300, paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int mode = MeasureSpec.getMode(widthMeasureSpec);
//        int size = MeasureSpec.getSize(widthMeasureSpec);
//        // Try for a width based on our minimum
//        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
//        int w = resolveSizeAndState(minw, widthMeasureSpec, 1);
//
//        // Whatever the width ends up being, ask for a height that would let the pie
//        // get as big as it can
//        int minh = MeasureSpec.getSize(w) - (int)mTextWidth + getPaddingBottom() + getPaddingTop();
//        int h = resolveSizeAndState(MeasureSpec.getSize(w) - (int)mTextWidth, heightMeasureSpec, 0);

        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }
}

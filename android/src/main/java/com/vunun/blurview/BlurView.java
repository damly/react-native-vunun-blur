package com.vunun.blurview;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class BlurView extends ViewGroup {

    private BlurCalculate mBlurCalculate = null;

    public BlurView(Context context) {
        super(context);
        mBlurCalculate = new BlurCalculate(this);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mBlurCalculate.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mBlurCalculate.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        if (mBlurCalculate.isCanvasChanged(canvas))
            mBlurCalculate.BlurCanvas();
        else {
            mBlurCalculate.DrawCanvas(canvas);
            super.dispatchDraw(canvas);
        }
    }

    public void setBlurType(@Nullable String type) {
        mBlurCalculate.setBlurType(type);
    }
}

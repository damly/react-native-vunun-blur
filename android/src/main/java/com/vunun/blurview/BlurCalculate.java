package com.vunun.blurview;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;

public class BlurCalculate {

    private View mView;
    private Bitmap bitmap;
    private Canvas mCanvas;
    private Rect mRect;
    private Matrix mMatrix;
    private Matrix mDrawMatrix;
    private int realheight, realwidth;
    // rs
    private RenderScript rs;
    private Allocation input;
    private Allocation output;
    private ScriptIntrinsicBlur script;
    private float radius = 12.0f;
    int i = -1;
    private int action = 0;
    private static final float BITMAP_RATIO = 0.1f;
    private int overColor = 0x00FFFFFF;

    public BlurCalculate(View view) {
        this.mView = view;
        rs = RenderScript.create(view.getContext());
        mCanvas = new Canvas();
        mRect = new Rect();
        mMatrix = new Matrix();
        mDrawMatrix = new Matrix();
    }

    public boolean isCanvasChanged(Canvas canvas) {
        return canvas == mCanvas;
    }

    public void onAttachedToWindow() {
        mView.getViewTreeObserver().addOnPreDrawListener(onPreDrawListener);
    }

    public void onDetachedFromWindow() {
        mView.getViewTreeObserver().removeOnPreDrawListener(onPreDrawListener);
        if (bitmap != null)
            bitmap.recycle();
        bitmap = null;
    }

    public void DrawCanvas(Canvas canvas) {
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, mDrawMatrix, null);
            canvas.drawColor(overColor);
        }
    }

    public void BlurCanvas() {

        input = Allocation.createFromBitmap(rs, bitmap, Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
        output = Allocation.createTyped(rs, input.getType());
        script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmap);
    }

    public void setBlurType(@Nullable String type) {
        String blurType = type.toLowerCase();
        if(blurType.equals("light")) {
            overColor = 0x33FFFFFF;
        }
        else if(blurType.equals("xlight"))  {
            overColor = 0x99FFFFFF;
        }
        else if(blurType.equals("dark"))  {
            overColor = 0x11000000;
        }
    }

    private void getScreenBitmap() {

        mView.getGlobalVisibleRect(mRect);
        realheight = mView.getHeight();
        realwidth = mView.getWidth();
        int w = Math.round(realwidth * BITMAP_RATIO) + 4;
        int h = Math.round(realheight * BITMAP_RATIO) + 4;

        if (w <= 0 || h <= 0)
            return;
        if (bitmap == null || bitmap.getWidth() != w || bitmap.getHeight() != h) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mMatrix.setScale(BITMAP_RATIO, BITMAP_RATIO);
            //mMatrix.invert(mDrawMatrix);
            mDrawMatrix.setScale(11.5f, 11.5f);
        }

        float dx = -(Math.min(0, mView.getLeft()) + mRect.left);
        float dy = action == 0 ? (-(Math.min(0, mView.getTop()) + mRect.top)) : -(mRect.bottom - ((mView.getBottom() - mView.getTop())));
        mCanvas.restoreToCount(1);
        mCanvas.setBitmap(bitmap);
        mCanvas.setMatrix(mMatrix);
        mCanvas.translate(dx, dy);
        mCanvas.save();

        mView.getRootView().draw(mCanvas);
    }

    private final ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {

            if (mView.getVisibility() == View.VISIBLE) {
                getScreenBitmap();
            }
            return true;
        }
    };


    /**
     * 通过调用系统高斯模糊api的方法模糊
     *
     * @param bitmap    source bitmap
     * @param outBitmap out bitmap
     * @param radius    0 < radius <= 25
     * @param context   context
     * @return out bitmap
     */
    public static Bitmap blurBitmap(Bitmap bitmap, Bitmap outBitmap, float radius, Context context) {
        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
        Allocation allOut = Allocation.createFromBitmap(rs, outBitmap);
        blurScript.setRadius(radius);
        blurScript.setInput(allIn);
        blurScript.forEach(allOut);
        allOut.copyTo(outBitmap);
        rs.destroy();

        return outBitmap;
    }

    /**
     * 改变图片对比度,达到使图片明暗变化的效果
     *
     * @param srcBitmap source bitmap
     * @param contrast  图片亮度，0：全黑；小于1，比原图暗；1.0f原图；大于1比原图亮
     * @return bitmap
     */
    public static Bitmap darkBitmap(Bitmap srcBitmap, float contrast) {

        float offset = (float) 60.0; //picture RGB offset

        int imgHeight, imgWidth;
        imgHeight = srcBitmap.getHeight();
        imgWidth = srcBitmap.getWidth();

        Bitmap bmp = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{contrast, 0, 0, 0, offset,
                0, contrast, 0, 0, offset,
                0, 0, contrast, 0, offset,
                0, 0, 0, 1, 0});

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);

        return bmp;
    }

    /**
     * 比例压缩图片
     *
     * @param sourceBitmap 源bitmap
     * @param scaleFactor  小于1，将bitmap缩小
     * @return 缩小scaleFactor倍后的bitmap
     */
    public static Bitmap compressBitmap(Bitmap sourceBitmap, float scaleFactor) {
        Bitmap overlay = Bitmap.createBitmap((int) (sourceBitmap.getWidth() * scaleFactor),
                (int) (sourceBitmap.getHeight() * scaleFactor), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.translate(0, 0);
        canvas.scale(scaleFactor, scaleFactor);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(sourceBitmap, 0, 0, paint);
        return overlay;
    }
}
package com.vunun.blurview;

import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.support.annotation.Nullable;
import android.view.View;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

public class BlurViewManager extends ViewGroupManager<BlurView> {
    public static final String REACT_CLASS = "BlurView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public BlurView createViewInstance(ThemedReactContext context) {
        return new BlurView(context);
    }


    @ReactProp(name = "blurType")
    public void setBlurType(BlurView view, @Nullable String type) {
        view.setBlurType(type);
    }
}


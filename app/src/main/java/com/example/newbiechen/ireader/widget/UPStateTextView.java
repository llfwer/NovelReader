package com.example.newbiechen.ireader.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by tamrylei on 2018/1/17.
 */

public class UPStateTextView extends AppCompatTextView {

    public UPStateTextView(Context context) {
        super(context);
    }

    public UPStateTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public UPStateTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        boolean isPressed = isPressed();
        boolean isDisable = !isEnabled();
        setAlpha(isPressed || isDisable ? 0.5f : 1.0f);
    }
}
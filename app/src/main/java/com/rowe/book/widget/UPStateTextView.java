package com.rowe.book.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
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

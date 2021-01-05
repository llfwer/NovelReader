package com.rowe.book.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rowe.book.R;

public class UPEmptyView extends LinearLayout {

    public static final int TYPE_LOGIN = 1;
    public static final int TYPE_EMPTY = 2;
    public static final int TYPE_ERROR = 3;

    private ImageView mImage;
    private TextView mTitle;
    private TextView mButton;

    public UPEmptyView(Context context) {
        this(context, null);
    }

    public UPEmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UPEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);

        LayoutInflater.from(context).inflate(R.layout.up_empty_view, this);

        mImage = findViewById(R.id.up_empty_image_view);
        mTitle = findViewById(R.id.up_empty_title_view);
        mButton = findViewById(R.id.up_empty_button_view);
    }

    public void show(int type, String title) {
        show(type, 0, title, null, null);
    }

    public void show(int type, String title, String btnText, OnClickListener listener) {
        show(type, 0, title, btnText, listener);
    }

    public void show(int type, int imageRes, String title, String btnText, OnClickListener listener) {
        setVisibility(VISIBLE);

        int defaultImageRes = 0, defaultTitleRes = 0;

        switch (type) {
            case TYPE_LOGIN:

                defaultImageRes = R.drawable.up_common_icon_empty_no_login;
                defaultTitleRes = R.string.up_common_empty_login;

                break;
            case TYPE_EMPTY:

                defaultImageRes = R.drawable.up_common_icon_empty_no_data;
                defaultTitleRes = R.string.up_common_empty_data;

                break;
            case TYPE_ERROR:

                defaultImageRes = R.drawable.up_common_icon_empty_no_network;
                defaultTitleRes = R.string.up_common_empty_network;

                break;
        }

        if (imageRes != 0) {
            mImage.setImageResource(imageRes);
        } else if (defaultImageRes != 0) {
            mImage.setImageResource(defaultImageRes);
        }

        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        } else if (defaultTitleRes != 0) {
            mTitle.setText(defaultTitleRes);
        }

        if (TextUtils.isEmpty(btnText)) {
            mButton.setVisibility(GONE);
        } else {
            mButton.setText(btnText);
            mButton.setVisibility(VISIBLE);
        }

        mButton.setOnClickListener(listener);
    }
}
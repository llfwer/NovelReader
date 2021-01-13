package com.rowe.book.page;

import androidx.annotation.ColorRes;

import com.rowe.book.R;

/**
 * 作用：页面的展示风格。
 */
public enum UPPageStyle {
    BG_0(R.color.nb_read_font_1, R.color.nb_read_bg_1),
    BG_1(R.color.nb_read_font_2, R.color.nb_read_bg_2),
    BG_2(R.color.nb_read_font_3, R.color.nb_read_bg_3),
    BG_3(R.color.nb_read_font_4, R.color.nb_read_bg_4),
    BG_4(R.color.nb_read_font_5, R.color.nb_read_bg_5),
    NIGHT(R.color.up_read_bg_night_color, R.color.up_read_font_night_color);

    private int mFontColor;
    private int mBgColor;

    UPPageStyle(@ColorRes int fontColor, @ColorRes int bgColor) {
        mFontColor = fontColor;
        mBgColor = bgColor;
    }

    public int getFontColor() {
        return mFontColor;
    }

    public int getBgColor() {
        return mBgColor;
    }
}
package com.example.newbiechen.ireader.other;

import com.example.newbiechen.ireader.utils.ScreenUtils;
import com.example.newbiechen.ireader.utils.SharedPreUtils;
import com.example.newbiechen.ireader.widget.page.PageMode;
import com.example.newbiechen.ireader.widget.page.PageStyle;

/**
 * 阅读器的配置管理
 */
public class UPSettingManager {
    private static final String SHARED_READ_BG = "shared_read_bg";
    private static final String SHARED_READ_BRIGHTNESS = "shared_read_brightness";
    private static final String SHARED_READ_IS_BRIGHTNESS_AUTO = "shared_read_is_brightness_auto";
    private static final String SHARED_READ_TEXT_SIZE = "shared_read_text_size";
    private static final String SHARED_READ_IS_TEXT_DEFAULT = "shared_read_text_default";
    private static final String SHARED_READ_PAGE_MODE = "shared_read_mode";
    private static final String SHARED_READ_NIGHT_MODE = "shared_night_mode";
    private static final String SHARED_READ_VOLUME_TURN_PAGE = "shared_read_volume_turn_page";
    private static final String SHARED_READ_FULL_SCREEN = "shared_read_full_screen";
    private static final String SHARED_READ_CONVERT_TYPE = "shared_read_convert_type";

    private static volatile UPSettingManager sInstance;

    private SharedPreUtils mSp;

    public static UPSettingManager getInstance() {
        if (sInstance == null) {
            synchronized (UPSettingManager.class) {
                if (sInstance == null) {
                    sInstance = new UPSettingManager();
                }
            }
        }
        return sInstance;
    }

    private UPSettingManager() {
        mSp = SharedPreUtils.getInstance();
    }

    public void setPageStyle(PageStyle pageStyle) {
        mSp.putInt(SHARED_READ_BG, pageStyle.ordinal());
    }

    public void setBrightness(int progress) {
        mSp.putInt(SHARED_READ_BRIGHTNESS, progress);
    }

    public void setAutoBrightness(boolean isAuto) {
        mSp.putBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, isAuto);
    }

    public void setDefaultTextSize(boolean isDefault) {
        mSp.putBoolean(SHARED_READ_IS_TEXT_DEFAULT, isDefault);
    }

    public void setTextSize(int textSize) {
        mSp.putInt(SHARED_READ_TEXT_SIZE, textSize);
    }

    public void setPageMode(PageMode mode) {
        mSp.putInt(SHARED_READ_PAGE_MODE, mode.ordinal());
    }

    public void setNightMode(boolean isNight) {
        mSp.putBoolean(SHARED_READ_NIGHT_MODE, isNight);
    }

    public int getBrightness() {
        return mSp.getInt(SHARED_READ_BRIGHTNESS, 40);
    }

    public boolean isBrightnessAuto() {
        return mSp.getBoolean(SHARED_READ_IS_BRIGHTNESS_AUTO, false);
    }

    public int getTextSize() {
        return mSp.getInt(SHARED_READ_TEXT_SIZE, ScreenUtils.spToPx(28));
    }

    public boolean isDefaultTextSize() {
        return mSp.getBoolean(SHARED_READ_IS_TEXT_DEFAULT, false);
    }

    public PageMode getPageMode() {
        int mode = mSp.getInt(SHARED_READ_PAGE_MODE, PageMode.SIMULATION.ordinal());
        return PageMode.values()[mode];
    }

    public PageStyle getPageStyle() {
        int style = mSp.getInt(SHARED_READ_BG, PageStyle.BG_0.ordinal());
        return PageStyle.values()[style];
    }

    public boolean isNightMode() {
        return mSp.getBoolean(SHARED_READ_NIGHT_MODE, false);
    }

    public void setVolumeTurnPage(boolean isTurn) {
        mSp.putBoolean(SHARED_READ_VOLUME_TURN_PAGE, isTurn);
    }

    public boolean isVolumeTurnPage() {
        return mSp.getBoolean(SHARED_READ_VOLUME_TURN_PAGE, false);
    }

    public void setFullScreen(boolean isFullScreen) {
        mSp.putBoolean(SHARED_READ_FULL_SCREEN, isFullScreen);
    }

    public boolean isFullScreen() {
        return mSp.getBoolean(SHARED_READ_FULL_SCREEN, false);
    }

    public void setConvertType(int convertType) {
        mSp.putInt(SHARED_READ_CONVERT_TYPE, convertType);
    }

    public int getConvertType() {
        return mSp.getInt(SHARED_READ_CONVERT_TYPE, 0);
    }
}
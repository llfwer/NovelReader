package com.rowe.book.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.rowe.book.App;

public class UPSharedPreUtils {
    private static final String SHARED_NAME = "IReader_pref";

    private static volatile UPSharedPreUtils sInstance;

    public static UPSharedPreUtils getInstance() {
        if (sInstance == null) {
            synchronized (UPSharedPreUtils.class) {
                if (sInstance == null) {
                    sInstance = new UPSharedPreUtils();
                }
            }
        }
        return sInstance;
    }

    private SharedPreferences mSp;

    private UPSharedPreUtils() {
        mSp = App.getContext().getSharedPreferences(SHARED_NAME, Context.MODE_MULTI_PROCESS);
    }

    public String getString(String key) {
        return mSp.getString(key, "");
    }

    public void putString(String key, String value) {
        mSp.edit().putString(key, value).apply();
    }

    public void putInt(String key, int value) {
        mSp.edit().putInt(key, value).apply();
    }

    public void putBoolean(String key, boolean value) {
        mSp.edit().putBoolean(key, value).apply();
    }

    public int getInt(String key, int def) {
        return mSp.getInt(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return mSp.getBoolean(key, def);
    }
}
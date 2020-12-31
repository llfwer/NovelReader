package com.example.newbiechen.ireader.utils;

import android.content.Context;
import android.content.Intent;

import com.example.newbiechen.ireader.activity.UPReadActivity;
import com.example.newbiechen.ireader.model.bean.CollBookBean;

public class UPRouteUtil {
    public static void gotoReadActivity(Context context, CollBookBean collBook, boolean isCollected) {
        context.startActivity(new Intent(context, UPReadActivity.class)
                .putExtra(UPReadActivity.EXTRA_IS_COLLECTED, isCollected)
                .putExtra(UPReadActivity.EXTRA_COLL_BOOK, collBook));
    }
}
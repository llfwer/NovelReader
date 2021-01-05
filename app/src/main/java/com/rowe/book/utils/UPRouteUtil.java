package com.rowe.book.utils;

import android.content.Context;
import android.content.Intent;

import com.rowe.book.activity.UPReadActivity;
import com.rowe.book.model.bean.CollBookBean;

public class UPRouteUtil {
    public static void gotoReadActivity(Context context, CollBookBean collBook) {
        context.startActivity(new Intent(context, UPReadActivity.class)
                .putExtra(UPReadActivity.EXTRA_COLL_BOOK, collBook));
    }
}
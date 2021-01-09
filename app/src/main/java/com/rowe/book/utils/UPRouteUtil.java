package com.rowe.book.utils;

import android.content.Context;
import android.content.Intent;

import com.rowe.book.activity.UPReadActivity;
import com.rowe.book.book.UPBook;

public class UPRouteUtil {
    public static void gotoReadActivity(Context context, UPBook bookData) {
        context.startActivity(new Intent(context, UPReadActivity.class)
                .putExtra(UPReadActivity.EXTRA_BOOK_DATA, bookData));
    }
}
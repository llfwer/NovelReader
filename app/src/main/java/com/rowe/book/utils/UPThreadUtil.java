package com.rowe.book.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 公共线程池
 */
public class UPThreadUtil {

    private static final ExecutorService sExecutorService =
            Executors.newFixedThreadPool(2);

    private static final Handler sMainHandler = new Handler(Looper.getMainLooper());

    public static void execute(Runnable runnable) {
        sExecutorService.execute(runnable);
    }

    public static void executeOnMainThread(Runnable runnable) {
        if (Looper.getMainLooper().getThread() == Thread.currentThread()) {
            runnable.run();
        } else {
            sMainHandler.post(runnable);
        }
    }

    public static void callbackToMain(Runnable runnable) {
        sMainHandler.post(runnable);
    }
}

package com.rowe.book.other;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

public class UPTaskAgent {

    private HandlerThread mThread;
    private Handler mWorkHandler;
    private Handler mMainHandler;

    public UPTaskAgent() {
        mThread = new HandlerThread("handler_pages");
        mThread.start();
        mWorkHandler = new Handler(mThread.getLooper());
        mMainHandler = new Handler(Looper.getMainLooper());
    }

    public void quit() {
        mThread.quitSafely();
    }

    public void execute(Runnable task) {
        mWorkHandler.post(task);
    }

    public void cancel(Runnable task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (mWorkHandler.hasCallbacks(task)) {
                mWorkHandler.removeCallbacks(task);
            }
        } else {
            mWorkHandler.removeCallbacks(task);
        }
    }
}
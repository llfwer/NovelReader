package com.rowe.book.other;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class UPTaskAgent {

    private HandlerThread mThread;
    private Handler mWorkHandler;
    private Handler mMainHandler;

    public UPTaskAgent() {
        mThread = new HandlerThread("handler_pages");
        mThread.start();

        mWorkHandler = new Handler(mThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                UPTask task = (UPTask) msg.obj;
                if (task != null) {
                    task.onWork();
                    mMainHandler.obtainMessage(task.hashCode(), task).sendToTarget();
                }
                return true;
            }
        });
        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                UPTask task = (UPTask) msg.obj;
                if (task != null) {
                    task.onResult();
                }
                return true;
            }
        });
    }

    public void quit() {
        mThread.quitSafely();
    }

    public void execute(UPTask task) {
        mWorkHandler.obtainMessage(task.hashCode(), task).sendToTarget();
    }

    public void cancel(UPTask task) {
        if (task != null) {
            int what = task.hashCode();

            mWorkHandler.removeMessages(what);
            mMainHandler.removeMessages(what);
        }
    }
}
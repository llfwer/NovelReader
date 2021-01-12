package com.rowe.book.other;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

public class UPTaskAgent<T> {

    private HandlerThread mThread;
    private Handler mWorkHandler;
    private Handler mMainHandler;

    public UPTaskAgent() {
        mThread = new HandlerThread("handler_pages");
        mThread.start();

        mWorkHandler = new Handler(mThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                UPTask<T> task = (UPTask<T>) msg.obj;
                if (task != null && !task.isCanceled()) {
                    UPRunnable<T> runnable = task.getRunnable();
                    try {
                        task.setResult(runnable.onWork());
                    } catch (Exception e) {
                        task.setException(e);
                    }
                    mMainHandler.obtainMessage(task.hashCode(), task).sendToTarget();
                }
                return true;
            }
        });
        mMainHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                UPTask<T> task = (UPTask<T>) msg.obj;
                if (task != null && !task.isCanceled()) {
                    UPRunnable<T> runnable = task.getRunnable();
                    if (task.isSuccessful()) {
                        runnable.onResult(task.getResult());
                    } else {
                        runnable.onError(task.getException());
                    }
                }
                return true;
            }
        });
    }

    public void quit() {
        mThread.quitSafely();
    }

    public UPTask<T> enqueue(UPRunnable<T> runnable) {
        if (runnable != null) {
            UPTask<T> task = new UPTask<T>(runnable);
            mWorkHandler.obtainMessage(task.hashCode(), task).sendToTarget();
            return task;
        }
        return null;
    }

    public void cancel(UPTask<T> task) {
        if (task != null) {
            task.setCanceled(true);

            int what = task.hashCode();

            mWorkHandler.removeMessages(what);
            mMainHandler.removeMessages(what);
        }
    }
}
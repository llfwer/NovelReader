package com.rowe.book.other;

public class UPTask<T> {
    private UPRunnable<T> mRunnable;
    private T mResult;
    private Exception mException;
    private boolean mCanceled = false;

    public UPTask(UPRunnable<T> runnable) {
        mRunnable = runnable;
    }

    public boolean isSuccessful() {
        return mException == null;
    }

    public UPRunnable<T> getRunnable() {
        return mRunnable;
    }

    public T getResult() {
        return mResult;
    }

    public void setResult(T result) {
        mResult = result;
    }

    public Exception getException() {
        return mException;
    }

    public void setException(Exception exception) {
        mException = exception;
    }

    public boolean isCanceled() {
        return mCanceled;
    }

    public void setCanceled(boolean canceled) {
        mCanceled = canceled;
    }
}
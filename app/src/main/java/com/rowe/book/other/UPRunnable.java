package com.rowe.book.other;

public interface UPRunnable<T> {
    T onWork() throws Exception;

    void onResult(T data);

    void onError(Exception e);
}
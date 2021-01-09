package com.rowe.book.data;

import com.rowe.book.book.UPChapter;

import java.util.List;

public class UPBookResponse {
    public static final int ERR_CODE_SUCCESS = 0;
    public static final int ERR_CODE_ERROR = -90001;

    private int mRetCode;

    // 章节列表
    private List<UPChapter> chapterList;

    public UPBookResponse() {
        mRetCode = ERR_CODE_ERROR;
    }

    public UPBookResponse(int retCode) {
        mRetCode = retCode;
    }

    public void setRetCode(int retCode) {
        mRetCode = retCode;
    }

    public boolean isSuccessful() {
        return mRetCode == ERR_CODE_SUCCESS;
    }

    public List<UPChapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<UPChapter> chapterList) {
        this.chapterList = chapterList;
    }
}
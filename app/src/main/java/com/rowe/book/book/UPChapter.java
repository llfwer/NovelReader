package com.rowe.book.book;

/**
 * title : 第一章 他叫白小纯
 * link : http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2
 * unreadble : false
 */
public class UPChapter {
    //标题
    public String title;
    //所属的书籍
    public String bookId;
    //在书籍文件中的起始位置
    public long start;
    //在书籍文件中的终止位置
    public long end;

    public UPChapter() {

    }

    public UPChapter(String bookId) {
        this.bookId = bookId;
    }
}
package com.rowe.book.book;

/**
 * title : 序章
 * bookId : 3994b87b2f565b91
 * start : 0
 * end : 475
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
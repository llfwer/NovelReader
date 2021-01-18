package com.rowe.book.book;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * id : 3994b87b2f565b91
 * name : 《再活一万次》（校对版全本）作者：兰帝魅晨
 * path : /storage/emulated/0/EBook/《再活一万次》（校对版全本）作者：兰帝魅晨.txt
 * modifyTime : 1609832200000
 * readTime : 1610445585328
 * chaptersCount : --
 * chapterList : --
 * chapter : 22
 * chapterTitle : 第二十二章 非人
 * pageIndex : 0
 * hasRead : true
 */
public class UPBook implements Parcelable {
    public String id; //书籍id
    public String name;//书名
    public String path;//路径
    public long modifyTime = 0L;//文件修改时间
    public long readTime = 0L;//最新阅读日期
    public int chaptersCount;//总章节数
    public List<UPChapter> chapterList;///章节列表
    public int chapter = 0;//当前阅读章节
    public String chapterTitle;//当前阅读章节标题
    public int pageIndex = 0;//当前的页码
    public boolean hasRead = false;//是否未阅读

    public UPBook() {

    }

    protected UPBook(Parcel in) {
        id = in.readString();
        name = in.readString();
        path = in.readString();
        modifyTime = in.readLong();
        readTime = in.readLong();
        chapter = in.readInt();
        chapterTitle = in.readString();
        pageIndex = in.readInt();
        hasRead = in.readByte() != 0;
    }

    public static final Creator<UPBook> CREATOR = new Creator<UPBook>() {
        @Override
        public UPBook createFromParcel(Parcel in) {
            return new UPBook(in);
        }

        @Override
        public UPBook[] newArray(int size) {
            return new UPBook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(path);
        dest.writeLong(modifyTime);
        dest.writeLong(readTime);
        dest.writeInt(chapter);
        dest.writeString(chapterTitle);
        dest.writeInt(pageIndex);
        dest.writeByte((byte) (hasRead ? 1 : 0));
    }
}
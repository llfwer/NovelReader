package com.rowe.book.book;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * _id : 53663ae356bdc93e49004474
 * title : 逍遥派
 * author : 白马出淤泥
 * shortIntro : 金庸武侠中有不少的神秘高手，书中或提起名字，或不曾提起，总之他们要么留下了绝世秘笈，要么就名震武林。 独孤九剑的创始者，独孤求败，他真的只创出九剑吗？ 残本葵花...
 * cover : /cover/149273897447137
 * hasCp : true
 * latelyFollower : 60213
 * retentionRatio : 22.87
 * updated : 2017-05-07T18:24:34.720Z
 * <p>
 * chaptersCount : 1660
 * lastChapter : 第1659章 朱长老
 */
public class UPBookData implements Parcelable {
    public String id; //书籍id
    public String name;//书名
    public String path;//路径
    public long modifyTime = 0L;//文件修改时间
    public long readTime = 0L;//最新阅读日期
    public int chaptersCount;//总章节数
    public List<Chapter> chapterList;///章节列表
    public int chapter = 0;//当前阅读章节
    public String chapterTitle;//当前阅读章节标题
    public int pageIndex = 0;//当前的页码
    public boolean hasRead = false;//是否未阅读

    public UPBookData() {

    }

    protected UPBookData(Parcel in) {
        id = in.readString();
        name = in.readString();
        path = in.readString();
        modifyTime = in.readLong();
        readTime = in.readLong();
        chapter = in.readInt();
        chapterTitle = in.readString();
        hasRead = in.readByte() != 0;
    }

    public static final Creator<UPBookData> CREATOR = new Creator<UPBookData>() {
        @Override
        public UPBookData createFromParcel(Parcel in) {
            return new UPBookData(in);
        }

        @Override
        public UPBookData[] newArray(int size) {
            return new UPBookData[size];
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
        dest.writeByte((byte) (hasRead ? 1 : 0));
    }

    /**
     * title : 第一章 他叫白小纯
     * link : http://read.qidian.com/chapter/rJgN8tJ_cVdRGoWu-UQg7Q2/6jr-buLIUJSaGfXRMrUjdw2
     * unreadble : false
     */
    public static class Chapter {
        //标题
        public String title;
        //所属的书籍
        public String bookId;
        //在书籍文件中的起始位置
        public long start;
        //在书籍文件中的终止位置
        public long end;
    }
}
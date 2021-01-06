package com.rowe.book.book;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;


public class UPBookDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";

    /**
     * 数据库版本
     * 版本1: 初始版本号
     */
    private static final int DB_VERSION = 1;

    // 存储书籍
    public static final String TABLE_BOOK = "book";
    // 存储章节
    public static final String TABLE_CHAPTER = "chapter";

    UPBookDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BOOK + " (" +
                BookColumns.ID + " INTEGER," +
                BookColumns._ID + " TEXT," +
                BookColumns.NAME + " TEXT," +
                BookColumns.PATH + " TEXT," +
                BookColumns.MODIFY + " INTEGER," +
                BookColumns.READ + " INTEGER," +
                BookColumns.CHAPTER + " INTEGER," +
                BookColumns.CHAPTER_TITLE + " TEXT," +
                BookColumns.PAGE_INDEX + " INTEGER," +
                BookColumns.HAS_READ + " INTEGER" +
                ")");

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS book_id ON " +
                TABLE_BOOK + " (" +
                BookColumns._ID +
                ");");

        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHAPTER + " (" +
                ChapterColumns.ID + " INTEGER," +
                ChapterColumns.TITLE + " TEXT," +
                ChapterColumns.BOOK_ID + " TEXT," +
                ChapterColumns.START + " INTEGER," +
                ChapterColumns.END + " INTEGER" +
                ")");

        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS chapter_id ON " +
                TABLE_CHAPTER + " (" +
                ChapterColumns.ID +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    interface BookColumns {
        String ID = BaseColumns._ID;
        //书籍id
        String _ID = "__id";
        //书名
        String NAME = "name";
        //路径
        String PATH = "path";
        //文件修改时间
        String MODIFY = "modify";
        //最新阅读日期
        String READ = "read";
        //当前阅读章节
        String CHAPTER = "chapter";
        //当前阅读章节标题
        String CHAPTER_TITLE = "chapter_title";
        //当前的页码
        String PAGE_INDEX = "page_index";
        //是否未阅读
        String HAS_READ = "has_read";
    }

    interface ChapterColumns {
        String ID = BaseColumns._ID;
        //标题
        String TITLE = "title";
        //所属的书籍
        String BOOK_ID = "bookId";
        //在书籍文件中的起始位置
        String START = "start";
        //在书籍文件中的终止位置
        String END = "_end";
    }
}
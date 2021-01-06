package com.rowe.book.book;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class UPBookDBManager {

    private volatile static UPBookDBManager sInstance;

    public static UPBookDBManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (UPBookDBManager.class) {
                if (sInstance == null) {
                    sInstance = new UPBookDBManager(context);
                }
            }
        }
        return sInstance;
    }

    private UPBookDBHelper mDBHelper;

    private UPBookDBManager(Context context) {
        final Context appContext = context.getApplicationContext();
        if (appContext != null) {
            context = appContext;
        }
        mDBHelper = new UPBookDBHelper(context);
    }

    public UPBookData getBook(String bookId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = null;
        UPBookData book = null;
        try {
            String[] columns = {
                    UPBookDBHelper.BookColumns._ID,
                    UPBookDBHelper.BookColumns.NAME,
                    UPBookDBHelper.BookColumns.PATH,
                    UPBookDBHelper.BookColumns.MODIFY,
                    UPBookDBHelper.BookColumns.READ,
                    UPBookDBHelper.BookColumns.CHAPTER,
                    UPBookDBHelper.BookColumns.CHAPTER_TITLE,
                    UPBookDBHelper.BookColumns.HAS_READ
            };

            String where = UPBookDBHelper.BookColumns._ID + " = ?";

            String[] args = {
                    bookId
            };

            cursor = db.query(UPBookDBHelper.TABLE_BOOK, columns, where, args, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                book = new UPBookData();
                book.id = cursor.getString(0);
                book.name = cursor.getString(1);
                book.path = cursor.getString(2);
                book.modifyTime = cursor.getLong(3);
                book.readTime = cursor.getLong(4);
                book.chapter = cursor.getInt(5);
                book.chapterTitle = cursor.getString(6);
                book.hasRead = cursor.getInt(7) == 1;
            }
        } catch (Exception e) {
            //ignore
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return book;
    }

    public void saveBook(UPBookData data) {
        if (data == null) return;

        try {
            final SQLiteDatabase db = mDBHelper.getWritableDatabase();

            ContentValues values = new ContentValues();

            values.put(UPBookDBHelper.BookColumns._ID, data.id);
            values.put(UPBookDBHelper.BookColumns.NAME, data.name);
            values.put(UPBookDBHelper.BookColumns.PATH, data.path);
            values.put(UPBookDBHelper.BookColumns.MODIFY, data.modifyTime);
            values.put(UPBookDBHelper.BookColumns.READ, data.readTime);
            values.put(UPBookDBHelper.BookColumns.CHAPTER, data.chapter);
            values.put(UPBookDBHelper.BookColumns.CHAPTER_TITLE, data.chapterTitle);
            values.put(UPBookDBHelper.BookColumns.HAS_READ, data.hasRead ? 1 : 0);

            db.replace(UPBookDBHelper.TABLE_BOOK, null, values);
        } catch (Exception e) {
            //ignore
        }
    }

    public void saveBookList(List<UPBookData> bookList) {
        if (bookList == null || bookList.isEmpty()) return;

        try {
            final SQLiteDatabase db = mDBHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();

            for (UPBookData data : bookList) {
                if (data == null) continue;
                values.clear();

                values.put(UPBookDBHelper.BookColumns._ID, data.id);
                values.put(UPBookDBHelper.BookColumns.NAME, data.name);
                values.put(UPBookDBHelper.BookColumns.PATH, data.path);
                values.put(UPBookDBHelper.BookColumns.MODIFY, data.modifyTime);
                values.put(UPBookDBHelper.BookColumns.READ, data.readTime);
                values.put(UPBookDBHelper.BookColumns.CHAPTER, data.chapter);
                values.put(UPBookDBHelper.BookColumns.CHAPTER_TITLE, data.chapterTitle);
                values.put(UPBookDBHelper.BookColumns.HAS_READ, data.hasRead ? 1 : 0);

                db.replace(UPBookDBHelper.TABLE_BOOK, null, values);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            //ignore
        }
    }

    public void deleteBook(UPBookData data) {
        if (data == null) return;

        try {
            final SQLiteDatabase db = mDBHelper.getWritableDatabase();

            String where = UPBookDBHelper.BookColumns._ID + " = ?";
            String[] args = {
                    data.id
            };
            db.delete(UPBookDBHelper.TABLE_BOOK, where, args);

            where = UPBookDBHelper.ChapterColumns.BOOK_ID + " = ?";
            args = new String[]{
                    data.id
            };
            db.delete(UPBookDBHelper.TABLE_CHAPTER, where, args);
        } catch (Exception e) {
            //ignore
        }
    }

    public List<UPBookData> getBookList() {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = null;
        List<UPBookData> bookList = null;
        try {
            String[] columns = {
                    UPBookDBHelper.BookColumns._ID,
                    UPBookDBHelper.BookColumns.NAME,
                    UPBookDBHelper.BookColumns.PATH,
                    UPBookDBHelper.BookColumns.MODIFY,
                    UPBookDBHelper.BookColumns.READ,
                    UPBookDBHelper.BookColumns.CHAPTER,
                    UPBookDBHelper.BookColumns.CHAPTER_TITLE,
                    UPBookDBHelper.BookColumns.HAS_READ
            };

            String orderBy = UPBookDBHelper.BookColumns.READ + " DESC";

            cursor = db.query(UPBookDBHelper.TABLE_BOOK, columns, null, null, null, null, orderBy);
            if (cursor != null && cursor.moveToFirst()) {
                bookList = new ArrayList<>(cursor.getCount());
                do {
                    UPBookData data = new UPBookData();
                    data.id = cursor.getString(0);
                    data.name = cursor.getString(1);
                    data.path = cursor.getString(2);
                    data.modifyTime = cursor.getLong(3);
                    data.readTime = cursor.getLong(4);
                    data.chapter = cursor.getInt(5);
                    data.chapterTitle = cursor.getString(6);
                    data.hasRead = cursor.getInt(7) == 1;

                    bookList.add(data);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            //ignore
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return bookList;
    }

    public void saveChapterList(List<UPBookData.Chapter> chapterList) {
        if (chapterList == null || chapterList.isEmpty()) return;

        try {
            final SQLiteDatabase db = mDBHelper.getWritableDatabase();
            db.beginTransaction();

            ContentValues values = new ContentValues();

            for (UPBookData.Chapter data : chapterList) {
                if (data == null) continue;
                values.clear();

                values.put(UPBookDBHelper.ChapterColumns.TITLE, data.title);
                values.put(UPBookDBHelper.ChapterColumns.BOOK_ID, data.bookId);
                values.put(UPBookDBHelper.ChapterColumns.START, data.start);
                values.put(UPBookDBHelper.ChapterColumns.END, data.end);

                db.replace(UPBookDBHelper.TABLE_BOOK, null, values);
            }

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            //ignore
        }
    }

    public List<UPBookData.Chapter> getChapterList(String bookId) {
        SQLiteDatabase db = mDBHelper.getReadableDatabase();
        Cursor cursor = null;
        List<UPBookData.Chapter> chapterList = null;
        try {
            String[] columns = {
                    UPBookDBHelper.ChapterColumns.TITLE,
                    UPBookDBHelper.ChapterColumns.BOOK_ID,
                    UPBookDBHelper.ChapterColumns.START,
                    UPBookDBHelper.ChapterColumns.END
            };

            String where = UPBookDBHelper.ChapterColumns.BOOK_ID + " = ?";

            String[] args = {
                    bookId
            };

            cursor = db.query(UPBookDBHelper.TABLE_CHAPTER, columns, where, args, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                chapterList = new ArrayList<>(cursor.getCount());
                do {
                    UPBookData.Chapter data = new UPBookData.Chapter();
                    data.title = cursor.getString(0);
                    data.bookId = cursor.getString(1);
                    data.start = cursor.getLong(2);
                    data.end = cursor.getLong(3);

                    chapterList.add(data);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            //ignore
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return chapterList;
    }
}
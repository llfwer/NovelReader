package com.example.newbiechen.ireader.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取媒体库的数据。
 */
public class UPMediaManager {
    private static final int ALL_BOOK_FILE = 1;

    private static final Uri FILE_URI = Uri.parse("content://media/external/file");
    private static final String SELECTION = MediaStore.Files.FileColumns.DATA + " like ?";
    private static final String SEARCH_TYPE = "%.txt";
    private static final String SORT_ORDER = MediaStore.Files.FileColumns.DISPLAY_NAME + " DESC";
    private static final String[] FILE_PROJECTION = {
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME
    };

    public interface Callback {
        void onResponse(List<File> files);
    }

    /**
     * 获取媒体库中所有的书籍文件
     * <p>
     * 暂时只支持 TXT
     */
    public static void getAllBookFile(FragmentActivity activity, final Callback callback) {
        final WeakReference<Context> contextWref = new WeakReference<>(activity.getApplicationContext());
        activity.getSupportLoaderManager().initLoader(ALL_BOOK_FILE, null, new LoaderManager.LoaderCallbacks<Cursor>() {
            @NonNull
            @Override
            public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
                Context context = contextWref.get();
                return new InternalLoader(context);
            }

            @Override
            public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
                List<File> files = InternalLoader.parseData(data);
                if (callback != null) {
                    callback.onResponse(files);
                }
            }

            @Override
            public void onLoaderReset(@NonNull Loader<Cursor> loader) {

            }
        });
    }

    private static class InternalLoader extends CursorLoader {

        InternalLoader(Context context) {
            super(context);

            setUri(FILE_URI);
            setProjection(FILE_PROJECTION);
            setSelection(SELECTION);
            setSelectionArgs(new String[]{SEARCH_TYPE});
            setSortOrder(SORT_ORDER);
        }

        static List<File> parseData(Cursor cursor) {
            List<File> files = new ArrayList<>();
            // 判断是否存在数据
            if (cursor == null) {
                // TODO:当媒体库没有数据的时候，需要做相应的处理
                // 暂时直接返回空数据
                return null;
            }
            // 重复使用Loader时，需要重置cursor的position；
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    if (file.isDirectory() || !file.exists()) {
                        continue;
                    } else {
                        files.add(file);
                    }
                }
            }
            return files;
        }

        /**
         * 从Cursor中读取对应columnName的值
         *
         * @param cursor
         * @param columnName
         * @param defaultValue
         * @return 当columnName无效时返回默认值；
         */
        static Object getValueFromCursor(@NonNull Cursor cursor, String columnName, Object defaultValue) {
            try {
                int index = cursor.getColumnIndexOrThrow(columnName);
                int type = cursor.getType(index);
                if (type == Cursor.FIELD_TYPE_STRING) {
                    // TO SOLVE:某些手机的数据库将数值类型存为String类型
                    return cursor.getString(index);
                } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                    if (defaultValue instanceof Long) {
                        return cursor.getLong(index);
                    } else if (defaultValue instanceof Integer) {
                        return cursor.getInt(index);
                    }
                } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                    if (defaultValue instanceof Float) {
                        return cursor.getFloat(index);
                    } else if (defaultValue instanceof Double) {
                        return cursor.getDouble(index);
                    }
                } else if (type == Cursor.FIELD_TYPE_BLOB) {
                    if (defaultValue instanceof Blob) {
                        return cursor.getBlob(index);
                    }
                } else if (type == Cursor.FIELD_TYPE_NULL) {
                    return defaultValue;
                }
            } catch (Exception e) {
                // Nothing
            }
            return defaultValue;
        }
    }
}
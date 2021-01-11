package com.rowe.book.widget.page;

import com.rowe.book.App;
import com.rowe.book.book.UPBook;
import com.rowe.book.book.UPBookDBManager;
import com.rowe.book.book.UPChapter;
import com.rowe.book.data.UPBookCallback;
import com.rowe.book.data.UPBookResponse;
import com.rowe.book.utils.IOUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;

/**
 * Created by newbiechen on 17-7-1.
 * 问题:
 * 1. 异常处理没有做好
 */

public class LocalPageLoader extends PageLoader {
    private static final String TAG = "LocalPageLoader";

    public LocalPageLoader(PageView pageView, UPBook bookData) {
        super(pageView, bookData);
        mStatus = STATUS_PARING;
    }

    /**
     * 从文件中提取一章的内容
     *
     * @param chapter
     * @return
     */
    private byte[] getChapterContent(UPChapter chapter) {
        RandomAccessFile bookStream = null;
        try {
            bookStream = new RandomAccessFile(mAgent.getFile(), "r");
            bookStream.seek(chapter.start);
            int extent = (int) (chapter.end - chapter.start);
            byte[] content = new byte[extent];
            bookStream.read(content, 0, extent);
            return content;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtil.closeQuietly(bookStream);
        }

        return new byte[0];
    }

    @Override
    public void saveRecord() {
        super.saveRecord();
        //修改当前COllBook记录
        if (mBookData != null && isChapterListPrepare) {
            //表示当前CollBook已经阅读
            mBookData.hasRead = true;
            mBookData.chapterTitle = mChapterList.get(mCurChapterPos).title;
            mBookData.readTime = System.currentTimeMillis();
            //直接更新
            UPBookDBManager.getInstance(App.getContext()).saveBook(mBookData);
        }
    }

    @Override
    public void refreshChapterList() {
        // 对于文件是否存在，或者为空的判断，不作处理。 ==> 在文件打开前处理过了。
        File file = mAgent.getFile();

        long lastModified = file.lastModified();

        // 判断文件是否已经加载过，并具有缓存
        if (lastModified == mBookData.modifyTime && mBookData.chapterList != null) {

            mChapterList = mBookData.chapterList;
            isChapterListPrepare = true;

            //提示目录加载完成
            if (mPageChangeListener != null) {
                mPageChangeListener.onCategoryFinish(mChapterList);
            }

            // 加载并显示当前章节
            openChapter();

            return;
        }

        mAgent.getChapterList(new UPBookCallback() {
            @Override
            public void onResponse(UPBookResponse response) {
                if (response.isSuccessful()) {
                    mChapterList = response.getChapterList();
                    isChapterListPrepare = true;

                    // 提示目录加载完成
                    if (mPageChangeListener != null) {
                        mPageChangeListener.onCategoryFinish(mChapterList);
                    }

                    // 存储章节到数据库
                    mBookData.chapterList = mChapterList;
                    mBookData.modifyTime = lastModified;

                    // 加载并显示当前章节
                    openChapter();
                } else {
                    chapterError();
                    //Log.d(TAG, "file load error:" + e.toString());
                }
            }
        });
    }

    @Override
    protected BufferedReader getChapterReader(UPChapter chapter) throws Exception {
        //从文件中获取数据
        byte[] content = getChapterContent(chapter);
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        BufferedReader br = new BufferedReader(new InputStreamReader(bais, mAgent.getCharsetName()));
        return br;
    }

    @Override
    protected boolean hasChapterData(UPChapter chapter) {
        return true;
    }
}

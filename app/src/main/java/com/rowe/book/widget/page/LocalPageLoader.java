package com.rowe.book.widget.page;

import com.rowe.book.App;
import com.rowe.book.book.UPBook;
import com.rowe.book.book.UPBookDBManager;
import com.rowe.book.book.UPChapter;
import com.rowe.book.data.UPBookAgent;
import com.rowe.book.utils.IOUtil;
import com.rowe.book.utils.UPCharset;
import com.rowe.book.utils.UPFileUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * Created by newbiechen on 17-7-1.
 * 问题:
 * 1. 异常处理没有做好
 */

public class LocalPageLoader extends PageLoader {
    private static final String TAG = "LocalPageLoader";

    //获取书本的文件
    private File mBookFile;
    //编码类型
    private UPCharset mCharset;

    private UPBookAgent mAgent;

    public LocalPageLoader(PageView pageView, UPBook bookData) {
        super(pageView, bookData);
        mStatus = STATUS_PARING;
        mAgent = new UPBookAgent(mContext, bookData);
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
            bookStream = new RandomAccessFile(mBookFile, "r");
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
    public void closeBook() {
        super.closeBook();
    }

    @Override
    public void refreshChapterList() {
        // 对于文件是否存在，或者为空的判断，不作处理。 ==> 在文件打开前处理过了。
        mBookFile = new File(mBookData.path);
        //获取文件编码
        mCharset = UPFileUtils.getCharset(mBookFile.getAbsolutePath());

        long lastModified = mBookFile.lastModified();

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

        mAgent.getChapterList(new UPBookAgent.Callback() {
            @Override
            public void onResponse(List<UPChapter> chapterList) {
                if (chapterList == null || chapterList.isEmpty()) {
                    chapterError();
                    //Log.d(TAG, "file load error:" + e.toString());
                } else {
                    mChapterList = chapterList;
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
                }
            }
        });
    }

    @Override
    protected BufferedReader getChapterReader(UPChapter chapter) throws Exception {
        //从文件中获取数据
        byte[] content = getChapterContent(chapter);
        ByteArrayInputStream bais = new ByteArrayInputStream(content);
        BufferedReader br = new BufferedReader(new InputStreamReader(bais, mCharset.getName()));
        return br;
    }

    @Override
    protected boolean hasChapterData(UPChapter chapter) {
        return true;
    }
}

package com.rowe.book.data;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.rowe.book.book.UPBook;
import com.rowe.book.book.UPBookDBManager;
import com.rowe.book.book.UPChapter;
import com.rowe.book.utils.IOUtil;
import com.rowe.book.utils.UPCharset;
import com.rowe.book.utils.UPFileUtils;
import com.rowe.book.utils.UPThreadUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UPBookAgent {
    //默认从文件中获取数据的长度
    private final static int BUFFER_SIZE = 512 * 1024;
    //没有标题的时候，每个章节的最大长度
    private final static int MAX_LENGTH_WITH_NO_CHAPTER = 10 * 1024;
    // "序(章)|前言"
    private final static Pattern mPreChapterPattern = Pattern.compile("^(\\s{0,10})((\u5e8f[\u7ae0\u8a00]?)|(\u524d\u8a00)|(\u6954\u5b50))(\\s{0,10})$", Pattern.MULTILINE);

    //正则表达式章节匹配模式
    // "(第)([0-9零一二两三四五六七八九十百千万壹贰叁肆伍陆柒捌玖拾佰仟]{1,10})([章节回集卷])(.*)"
    private static final String[] CHAPTER_PATTERNS = new String[]{"^(.{0,8})(\u7b2c)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\u7ae0\u8282\u56de\u96c6\u5377])(.{0,30})$",
            "^(\\s{0,4})([\\(\u3010\u300a]?(\u5377)?)([0-9\u96f6\u4e00\u4e8c\u4e24\u4e09\u56db\u4e94\u516d\u4e03\u516b\u4e5d\u5341\u767e\u5343\u4e07\u58f9\u8d30\u53c1\u8086\u4f0d\u9646\u67d2\u634c\u7396\u62fe\u4f70\u4edf]{1,10})([\\.:\uff1a\u0020\f\t])(.{0,30})$",
            "^(\\s{0,4})([\\(\uff08\u3010\u300a])(.{0,30})([\\)\uff09\u3011\u300b])(\\s{0,2})$",
            "^(\\s{0,4})(\u6b63\u6587)(.{0,20})$",
            "^(.{0,4})(Chapter|chapter)(\\s{0,4})([0-9]{1,4})(.{0,30})$"};

    private UPBook mData;
    private File mFile;
    private UPCharset mCharset;//编码类型
    private UPBookDBManager mManager;

    //章节解析模式
    private Pattern mChapterPattern = null;

    private Handler mHandler;

    public UPBookAgent(Context context, UPBook data) {
        mData = data;
        if (data != null && !TextUtils.isEmpty(data.path)) {
            mFile = new File(data.path);
            //获取文件编码
            mCharset = UPFileUtils.getCharset(mFile.getAbsolutePath());
        }
        mManager = UPBookDBManager.getInstance(context);
        mHandler = new Handler(Looper.getMainLooper());
    }

    public File getFile() {
        return mFile;
    }

    public String getCharsetName() {
        return mCharset == null ? null : mCharset.getName();
    }

    /**
     * 未完成的部分:
     * 1. 序章的添加
     * 2. 章节存在的书本的虚拟分章效果
     */
    public void getChapterList(UPBookCallback callback) {
        if (mData == null) {
            callbackOnUIThread(callback, null);
            return;
        }
        // 数据库有则从数据库取
        List<UPChapter> chapterList = mManager.getChapterList(mData.id);
        if (chapterList != null && !chapterList.isEmpty()) {
            UPBookResponse response = new UPBookResponse(UPBookResponse.ERR_CODE_SUCCESS);
            response.setChapterList(chapterList);
            callbackOnUIThread(callback, response);
            return;
        }
        // 路径异常返回null
        if (mFile == null || !mFile.exists()) {
            callbackOnUIThread(callback, null);
            return;
        }
        // 现场解析章节数据
        UPThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<UPChapter> chapters = new ArrayList<>();
                    //获取文件流
                    RandomAccessFile bookStream = new RandomAccessFile(mFile, "r");
                    //寻找匹配文章标题的正则表达式，判断是否存在章节名
                    boolean hasChapter = checkChapterType(bookStream);
                    //加载章节
                    byte[] buffer = new byte[BUFFER_SIZE];
                    //获取到的块起始点，在文件中的位置
                    long curOffset = 0;
                    //block的个数
                    int blockPos = 0;
                    //读取的长度
                    int length;

                    //获取文件中的数据到buffer，直到没有数据为止
                    while ((length = bookStream.read(buffer, 0, buffer.length)) > 0) {
                        ++blockPos;
                        //如果存在Chapter
                        if (hasChapter) {
                            //将数据转换成String
                            String blockContent = new String(buffer, 0, length, mCharset.getName());
                            //当前Block下使过的String的指针
                            int seekPos = 0;
                            //进行正则匹配
                            Matcher matcher = mChapterPattern.matcher(blockContent);
                            //如果存在相应章节
                            while (matcher.find()) {
                                //获取匹配到的字符在字符串中的起始位置
                                int chapterStart = matcher.start();

                                //如果 seekPos == 0 && nextChapterPos != 0 表示当前block处前面有一段内容
                                //第一种情况一定是序章 第二种情况可能是上一个章节的内容
                                if (seekPos == 0 && chapterStart != 0) {
                                    //获取当前章节的内容
                                    String chapterContent = blockContent.substring(seekPos, chapterStart);
                                    //设置指针偏移
                                    seekPos += chapterContent.length();

                                    //如果当前对整个文件的偏移位置为0的话，那么就是序章
                                    if (curOffset == 0) {
                                        //创建序章
                                        UPChapter preChapter = new UPChapter(mData.id);
                                        preChapter.title = "序章";
                                        preChapter.start = 0;
                                        preChapter.end = chapterContent.getBytes(mCharset.getName()).length; //获取String的byte值,作为最终值

                                        //如果序章大小大于30才添加进去
                                        if (preChapter.end - preChapter.start > 30) {
                                            chapters.add(preChapter);
                                        }

                                        //创建当前章节
                                        UPChapter curChapter = new UPChapter(mData.id);
                                        curChapter.title = matcher.group();
                                        curChapter.start = preChapter.end;
                                        chapters.add(curChapter);
                                    }
                                    //否则就block分割之后，上一个章节的剩余内容
                                    else {
                                        //获取上一章节
                                        UPChapter lastChapter = chapters.get(chapters.size() - 1);
                                        //将当前段落添加上一章去
                                        lastChapter.end += chapterContent.getBytes(mCharset.getName()).length;

                                        //如果章节内容太小，则移除
                                        if (lastChapter.end - lastChapter.start < 30) {
                                            chapters.remove(lastChapter);
                                        }

                                        //创建当前章节
                                        UPChapter curChapter = new UPChapter(mData.id);
                                        curChapter.title = matcher.group();
                                        curChapter.start = lastChapter.end;
                                        chapters.add(curChapter);
                                    }
                                } else {
                                    //是否存在章节
                                    if (chapters.size() != 0) {
                                        //获取章节内容
                                        String chapterContent = blockContent.substring(seekPos, matcher.start());
                                        seekPos += chapterContent.length();

                                        //获取上一章节
                                        UPChapter lastChapter = chapters.get(chapters.size() - 1);
                                        lastChapter.end = lastChapter.start + chapterContent.getBytes(mCharset.getName()).length;

                                        //如果章节内容太小，则移除
                                        if (lastChapter.end - lastChapter.start < 30) {
                                            chapters.remove(lastChapter);
                                        }

                                        //创建当前章节
                                        UPChapter curChapter = new UPChapter(mData.id);
                                        curChapter.title = matcher.group();
                                        curChapter.start = lastChapter.end;
                                        chapters.add(curChapter);
                                    }
                                    //如果章节不存在则创建章节
                                    else {
                                        UPChapter curChapter = new UPChapter(mData.id);
                                        curChapter.title = matcher.group();
                                        curChapter.start = 0;
                                        chapters.add(curChapter);
                                    }
                                }
                            }
                        }
                        //进行本地虚拟分章
                        else {
                            //章节在buffer的偏移量
                            int chapterOffset = 0;
                            //当前剩余可分配的长度
                            int strLength = length;
                            //分章的位置
                            int chapterPos = 0;

                            while (strLength > 0) {
                                ++chapterPos;
                                //是否长度超过一章
                                if (strLength > MAX_LENGTH_WITH_NO_CHAPTER) {
                                    //在buffer中一章的终止点
                                    int end = length;
                                    //寻找换行符作为终止点
                                    for (int i = chapterOffset + MAX_LENGTH_WITH_NO_CHAPTER; i < length; ++i) {
                                        if (buffer[i] == UPCharset.BLANK) {
                                            end = i;
                                            break;
                                        }
                                    }
                                    UPChapter chapter = new UPChapter(mData.id);
                                    chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
                                    chapter.start = curOffset + chapterOffset + 1;
                                    chapter.end = curOffset + end;
                                    chapters.add(chapter);
                                    //减去已经被分配的长度
                                    strLength = strLength - (end - chapterOffset);
                                    //设置偏移的位置
                                    chapterOffset = end;
                                } else {
                                    UPChapter chapter = new UPChapter(mData.id);
                                    chapter.title = "第" + blockPos + "章" + "(" + chapterPos + ")";
                                    chapter.start = curOffset + chapterOffset + 1;
                                    chapter.end = curOffset + length;
                                    chapters.add(chapter);
                                    strLength = 0;
                                }
                            }
                        }

                        //block的偏移点
                        curOffset += length;

                        if (hasChapter) {
                            //设置上一章的结尾
                            UPChapter lastChapter = chapters.get(chapters.size() - 1);
                            lastChapter.end = curOffset;
                        }

                        //当添加的block太多的时候，执行GC
                        if (blockPos % 15 == 0) {
                            System.gc();
                            System.runFinalization();
                        }
                    }

                    UPBookResponse response = new UPBookResponse(UPBookResponse.ERR_CODE_SUCCESS);
                    response.setChapterList(chapters);
                    callbackOnUIThread(callback, response);

                    mManager.saveChapterList(chapters);

                    IOUtil.closeQuietly(bookStream);

                    System.gc();
                    System.runFinalization();
                } catch (Exception e) {
                    callbackOnUIThread(callback, null);
                }
            }
        });
    }

    private void callbackOnUIThread(UPBookCallback callback, UPBookResponse response) {
        if (callback != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onResponse(response == null ? new UPBookResponse() : response);
                }
            });
        }
    }

    /**
     * 1. 检查文件中是否存在章节名
     * 2. 判断文件中使用的章节名类型的正则表达式
     *
     * @return 是否存在章节名
     */
    private boolean checkChapterType(RandomAccessFile bookStream) throws IOException {
        //首先获取128k的数据
        byte[] buffer = new byte[BUFFER_SIZE / 4];
        int length = bookStream.read(buffer, 0, buffer.length);
        //进行章节匹配
        for (String str : CHAPTER_PATTERNS) {
            Pattern pattern = Pattern.compile(str, Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(new String(buffer, 0, length, mCharset.getName()));
            //如果匹配存在，那么就表示当前章节使用这种匹配方式
            if (matcher.find()) {
                mChapterPattern = pattern;
                //重置指针位置
                bookStream.seek(0);
                return true;
            }
        }

        //重置指针位置
        bookStream.seek(0);
        return false;
    }
}
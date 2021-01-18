package com.rowe.book.book;

import java.util.List;

/**
 * position : 0
 * title : 第二十二章 非人
 * titleLines : 1
 * lines : {"第二十二章 非人","　　第二十二章非人\n","　　手劈砖头的能人有，但半米直径的石头，",...}
 */
public class UPTxtPage {
    public int position;//页码
    public String title;//章节名
    public int titleLines; //当前 lines 中为 title 的行数。
    public List<String> lines;//文本数据
}
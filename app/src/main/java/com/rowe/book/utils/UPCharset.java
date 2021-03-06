package com.rowe.book.utils;

/**
 * 编码类型
 */
public enum UPCharset {
    UTF8("UTF-8"),
    UTF16LE("UTF-16LE"),
    UTF16BE("UTF-16BE"),
    GBK("GBK");

    private String mName;
    public static final byte BLANK = 0x0a;

    private UPCharset(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }
}
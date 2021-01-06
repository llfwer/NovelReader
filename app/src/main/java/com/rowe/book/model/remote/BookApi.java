package com.rowe.book.model.remote;

import com.rowe.book.model.bean.packages.ChapterInfoPackage;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface BookApi {

    /**
     * 章节的内容
     * 这里采用的是同步请求。
     *
     * @param url
     * @return
     */
    @GET("http://chapter2.zhuishushenqi.com/chapter/{url}")
    Single<ChapterInfoPackage> getChapterInfoPackage(@Path("url") String url);
}
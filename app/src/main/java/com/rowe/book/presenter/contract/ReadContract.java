package com.rowe.book.presenter.contract;

import com.rowe.book.ui.base.BaseContract;
import com.rowe.book.widget.page.TxtChapter;

import java.util.List;

/**
 * Created by newbiechen on 17-5-16.
 */

public interface ReadContract extends BaseContract {
    interface View extends BaseContract.BaseView {
        void finishChapter();

        void errorChapter();
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void loadChapter(String bookId, List<TxtChapter> bookChapterList);
    }
}

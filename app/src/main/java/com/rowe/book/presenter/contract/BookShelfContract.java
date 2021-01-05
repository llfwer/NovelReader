package com.rowe.book.presenter.contract;

import com.rowe.book.model.bean.CollBookBean;
import com.rowe.book.ui.base.BaseContract;

import java.util.List;

/**
 * Created by newbiechen on 17-5-8.
 */

public interface BookShelfContract {

    interface View extends BaseContract.BaseView {
        void finishRefresh(List<CollBookBean> collBookBeans);

        void finishUpdate();

        void showErrorTip(String error);
    }

    interface Presenter extends BaseContract.BasePresenter<View> {
        void refreshCollBooks();

        void updateCollBooks(List<CollBookBean> collBookBeans);
    }
}

package com.example.newbiechen.ireader.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.model.flag.BookListType;
import com.example.newbiechen.ireader.ui.base.BaseTabActivity;
import com.example.newbiechen.ireader.ui.fragment.BookListFragment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by newbiechen on 17-5-1.
 */

public class BookListActivity extends BaseTabActivity {
    /************Params*******************/
    private final CompositeDisposable mDisposable = new CompositeDisposable();

    @Override
    protected int getContentId() {
        return R.layout.activity_book_list;
    }

    @Override
    protected List<Fragment> createTabFragments() {
        List<Fragment> fragments = new ArrayList<>(BookListType.values().length);
        for (BookListType type : BookListType.values()) {
            fragments.add(BookListFragment.newInstance(type));
        }
        return fragments;
    }

    @Override
    protected List<String> createTabTitles() {
        List<String> titles = new ArrayList<>(BookListType.values().length);
        for (BookListType type : BookListType.values()) {
            titles.add(type.getTypeName());
        }
        return titles;
    }

    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        getSupportActionBar().setTitle("主题书单");
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void initClick() {
        super.initClick();
    }


    @Override
    protected void processLogic() {
        super.processLogic();
    }
}
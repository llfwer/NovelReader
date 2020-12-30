package com.example.newbiechen.ireader.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.fragment.UPAutomaticFragment;
import com.example.newbiechen.ireader.fragment.UPBaseFragment;
import com.example.newbiechen.ireader.fragment.UPManualFragment;

import java.util.ArrayList;
import java.util.List;

public class UPFileActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private CheckBox mCheckBox;
    private Button mDelete;
    private Button mAddBook;

    private UPBaseFragment[] mFragments;

    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mToolbar = findViewById(R.id.up_file_toolbar);
        initToolBar();

        mTabLayout = findViewById(R.id.up_file_tab);
        mViewPager = findViewById(R.id.up_file_view_pager);
        mDelete = findViewById(R.id.up_file_delete);
        mCheckBox = findViewById(R.id.up_file_selected_all);

        mFragments = new UPBaseFragment[]{
                new UPAutomaticFragment(),
                new UPManualFragment()
        };

        InternalAdapter adapter = new InternalAdapter(getSupportFragmentManager());
        for (UPBaseFragment fragment : mFragments) {
            adapter.addFragment(fragment.getFragmentTitle(this), fragment);
        }

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(mListener);
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);

        mToolbar.setTitle("本机导入");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
    }

    private ViewPager.OnPageChangeListener mListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            mIndex = position;

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private class InternalAdapter extends FragmentPagerAdapter {
        private List<String> mTitles = new ArrayList<>();
        private List<Fragment> mFragments = new ArrayList<>();

        public InternalAdapter(FragmentManager manager) {
            super(manager);
        }

        public void addFragment(String title, Fragment fragment) {
            mTitles.add(title);
            mFragments.add(fragment);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }
    }
}
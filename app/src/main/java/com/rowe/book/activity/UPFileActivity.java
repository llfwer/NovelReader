package com.rowe.book.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.rowe.book.R;
import com.rowe.book.adapter.UPFileAdapter;
import com.rowe.book.book.UPBookDBManager;
import com.rowe.book.book.UPBookData;
import com.rowe.book.fragment.UPAutomaticFragment;
import com.rowe.book.fragment.UPBaseFragment;
import com.rowe.book.fragment.UPFileBaseFragment;
import com.rowe.book.fragment.UPManualFragment;
import com.rowe.book.utils.UPMD5Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UPFileActivity extends AppCompatActivity implements View.OnClickListener, UPFileAdapter.Callback1, CompoundButton.OnCheckedChangeListener {
    private Toolbar mToolbar;

    private CheckBox mCheckBox;
    private Button mDelete;
    private Button mAddBook;

    private UPFileBaseFragment[] mFragments;

    private int mIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        mToolbar = findViewById(R.id.up_file_toolbar);
        initToolBar();

        TabLayout mTabLayout = findViewById(R.id.up_file_tab);
        ViewPager mViewPager = findViewById(R.id.up_file_view_pager);

        mFragments = new UPFileBaseFragment[]{
                new UPAutomaticFragment(),
                new UPManualFragment()
        };

        InternalAdapter adapter = new InternalAdapter(getSupportFragmentManager());
        for (UPBaseFragment fragment : mFragments) {
            adapter.addFragment(fragment.getFragmentTitle(this), fragment);
        }

        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(1);
        mTabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(mListener);

        mCheckBox = findViewById(R.id.up_file_selected_all);
        mDelete = findViewById(R.id.up_file_delete);
        mAddBook = findViewById(R.id.up_file_add_book);

        mCheckBox.setOnCheckedChangeListener(this);
        mDelete.setOnClickListener(this);
        mAddBook.setOnClickListener(this);
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
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == mCheckBox && buttonView.isPressed()) {
            mFragments[mIndex].setCheckedAll(isChecked);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mDelete) {
            //弹出，确定删除文件吗。
            new AlertDialog.Builder(this)
                    .setTitle("删除文件")
                    .setMessage("确定删除文件吗?")
                    .setPositiveButton(R.string.nb_common_sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除选中的文件
                            mFragments[mIndex].deleteCheckedFiles();
                            //提示删除文件成功
                            Toast.makeText(UPFileActivity.this, "删除文件成功", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton(R.string.nb_common_cancel, null)
                    .show();
        } else if (view == mAddBook) {
            addBook();
        } else {
            finish();
        }
    }

    private void addBook() {
        UPFileBaseFragment fragment = mFragments[mIndex];

        List<File> files = fragment.getCheckList();
        if (files == null || files.isEmpty()) {
            Toast.makeText(this, "没有书籍可以加入书架", Toast.LENGTH_SHORT).show();
            return;
        }

        List<UPBookData> bookList = new ArrayList<>();
        for (File file : files) {
            if (file == null || !file.exists()) continue;
            String path = file.getAbsolutePath();
            UPBookData data = new UPBookData();
            data.id = UPMD5Util.strToMd5By16(path);
            data.name = file.getName().replace(".txt", "");
            data.path = path;
            data.modifyTime = file.lastModified();
            data.readTime = System.currentTimeMillis();
            bookList.add(data);
        }
        UPBookDBManager.getInstance(this).saveBookList(bookList);

        fragment.requestData();

        Toast.makeText(this, "成功添加" + bookList.size() + "本书", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCheckChanged() {
        updateView();
    }

    @Override
    public void onCategoryChanged() {
        updateView();
    }

    private void updateView() {
        UPFileBaseFragment fragment = mFragments[mIndex];
        List<File> list = fragment.getCheckList();
        if (list.isEmpty()) {
            mAddBook.setText(R.string.nb_file_add_shelf);
            mDelete.setEnabled(false);
            mAddBook.setEnabled(false);
        } else {
            mAddBook.setText(getString(R.string.nb_file_add_shelves, list.size()));
            mDelete.setEnabled(true);
            mAddBook.setEnabled(true);
        }

        if (fragment.isCheckAll()) {
            mCheckBox.setChecked(true);
            mCheckBox.setText("取消");
        } else {
            mCheckBox.setChecked(false);
            mCheckBox.setText("全选");
        }
    }

    private ViewPager.OnPageChangeListener mListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            mIndex = position;
            updateView();
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    private static class InternalAdapter extends FragmentPagerAdapter {
        private List<String> mTitles = new ArrayList<>();
        private List<Fragment> mFragments = new ArrayList<>();

        public InternalAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
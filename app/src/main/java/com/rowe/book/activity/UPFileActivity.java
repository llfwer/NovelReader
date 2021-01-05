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

import com.rowe.book.R;
import com.rowe.book.adapter.UPFileAdapter;
import com.rowe.book.fragment.UPAutomaticFragment;
import com.rowe.book.fragment.UPBaseFragment;
import com.rowe.book.fragment.UPFileBaseFragment;
import com.rowe.book.fragment.UPManualFragment;
import com.rowe.book.model.bean.CollBookBean;
import com.rowe.book.model.local.BookRepository;
import com.rowe.book.utils.Constant;
import com.rowe.book.utils.MD5Utils;
import com.rowe.book.utils.StringUtils;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UPFileActivity extends AppCompatActivity implements View.OnClickListener, UPFileAdapter.Callback1, CompoundButton.OnCheckedChangeListener {
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

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

        mTabLayout = findViewById(R.id.up_file_tab);
        mViewPager = findViewById(R.id.up_file_view_pager);
        mCheckBox = findViewById(R.id.up_file_selected_all);
        mDelete = findViewById(R.id.up_file_delete);
        mAddBook = findViewById(R.id.up_file_add_book);

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
            //获取选中的文件
            List<File> files = mFragments[mIndex].getCheckList();
            //转换成CollBook,并存储
            List<CollBookBean> collBooks = convertCollBook(files);
            BookRepository.getInstance().saveCollBooks(collBooks);
            //设置HashMap为false
            mFragments[mIndex].requestData();
            //提示加入书架成功
            Toast.makeText(this, getString(R.string.nb_file_add_succeed, collBooks.size()), Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
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

    /**
     * 将文件转换成CollBook
     *
     * @param files:需要加载的文件列表
     * @return
     */
    private List<CollBookBean> convertCollBook(List<File> files) {
        List<CollBookBean> collBooks = new ArrayList<>(files.size());
        for (File file : files) {
            //判断文件是否存在
            if (!file.exists()) continue;

            CollBookBean collBook = new CollBookBean();
            collBook.set_id(MD5Utils.strToMd5By16(file.getAbsolutePath()));
            collBook.setTitle(file.getName().replace(".txt", ""));
            collBook.setAuthor("");
            collBook.setShortIntro("无");
            collBook.setCover(file.getAbsolutePath());
            collBook.setLocal(true);
            collBook.setLastChapter("开始阅读");
            collBook.setUpdated(StringUtils.dateConvert(file.lastModified(), Constant.FORMAT_BOOK_DATE));
            collBook.setLastRead(StringUtils.dateConvert(System.currentTimeMillis(), Constant.FORMAT_BOOK_DATE));
            collBooks.add(collBook);
        }
        return collBooks;
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
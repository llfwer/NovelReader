package com.rowe.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rowe.book.activity.UPFileActivity;
import com.rowe.book.adapter.UPBookAdapter;
import com.rowe.book.model.local.BookRepository;
import com.rowe.book.utils.UPPermissionUtil;
import com.rowe.book.widget.UPEmptyView;
import com.rowe.book.widget.UPDividerItemDecoration;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private RecyclerView mListView;
    private UPEmptyView mEmptyView;
    private View mLoadingView;

    private UPBookAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (UPPermissionUtil.checkPermissions(this)) {
            initView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == UPPermissionUtil.CODE) {
            if (UPPermissionUtil.checkResult(grantResults)) {
                initView();
            } else {
                Toast.makeText(this, "没有权限无法提供服务，拜拜了您", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);

        mToolbar.setLogo(R.mipmap.logo);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle("");
        }
    }

    private void initView() {
        mToolbar = findViewById(R.id.up_main_toolbar);
        initToolBar();

        mListView = findViewById(R.id.up_main_list_view);
        mEmptyView = findViewById(R.id.up_main_empty_view);
        mLoadingView = findViewById(R.id.up_main_loading_view);

        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.addItemDecoration(new UPDividerItemDecoration(this));
        mListView.setAdapter(mAdapter = new UPBookAdapter());

        requestData();
    }

    private void requestData() {
        mAdapter.setData(BookRepository.getInstance().getCollBooks());

        if (mAdapter.getItemCount() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, UPFileActivity.class);
        startActivityForResult(intent, 1001);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            requestData();
        }
    }

    private long mLastBackTime = 0;

    @Override
    public void onBackPressed() {
        long now = System.currentTimeMillis();
        if (now - mLastBackTime >= 3000) {
            Toast.makeText(this, "再按一次返回桌面", Toast.LENGTH_SHORT).show();
            mLastBackTime = now;
        } else {
            super.onBackPressed();
        }
    }

    private void showEmptyView() {
        mListView.setVisibility(View.GONE);
        mEmptyView.show(UPEmptyView.TYPE_EMPTY, null);
        mLoadingView.setVisibility(View.GONE);
    }

    private void showErrorView() {
        mListView.setVisibility(View.GONE);
        mEmptyView.show(UPEmptyView.TYPE_ERROR, null);
        mLoadingView.setVisibility(View.GONE);
    }

    private void showContentView() {
        mListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    private void showLoadingView() {
        mListView.setVisibility(View.GONE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.VISIBLE);
    }
}
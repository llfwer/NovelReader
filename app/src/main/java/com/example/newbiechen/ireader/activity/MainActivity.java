package com.example.newbiechen.ireader.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.adapter.UPBookAdapter;
import com.example.newbiechen.ireader.model.local.BookRepository;
import com.example.newbiechen.ireader.utils.UPPermissionUtil;
import com.example.newbiechen.ireader.widget.UPEmptyView;
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration;

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
        mToolbar.setTitle("");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initView() {
        mToolbar = findViewById(R.id.up_main_toolbar);
        initToolBar();

        mListView = findViewById(R.id.up_main_list_view);
        mEmptyView = findViewById(R.id.up_main_empty_view);
        mLoadingView = findViewById(R.id.up_main_loading_view);

        mListView.setLayoutManager(new LinearLayoutManager(this));
        mListView.addItemDecoration(new DividerItemDecoration(this));
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
        startActivity(intent);
        return super.onOptionsItemSelected(item);
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
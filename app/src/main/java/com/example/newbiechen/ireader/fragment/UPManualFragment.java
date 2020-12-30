package com.example.newbiechen.ireader.fragment;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.adapter.UPFileAdapter;
import com.example.newbiechen.ireader.utils.FileUtils;
import com.example.newbiechen.ireader.widget.UPEmptyView;
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UPManualFragment extends UPBaseFragment implements View.OnClickListener {
    private TextView mPathView;
    private RecyclerView mListView;
    private UPEmptyView mEmptyView;
    private View mLoadingView;

    private UPFileAdapter mAdapter;

    private UPFileAdapter.Callback1 mCallback;

    private File mPath;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof UPFileAdapter.Callback1) {
            mCallback = (UPFileAdapter.Callback1) context;
        }
    }

    @Override
    public void onDetach() {
        mCallback = null;
        super.onDetach();
    }

    @Override
    public int getFragmentLayoutId() {
        return R.layout.up_manual_fragment;
    }

    @Override
    public void initView(View view) {
        Context context = getContext();

        mPathView = view.findViewById(R.id.up_manual_path_view);
        mListView = view.findViewById(R.id.up_manual_list_view);
        mEmptyView = view.findViewById(R.id.up_manual_empty_view);
        mLoadingView = view.findViewById(R.id.up_manual_loading_view);

        view.findViewById(R.id.up_manual_back_view).setOnClickListener(this);

        mListView.setLayoutManager(new LinearLayoutManager(context));
        mListView.addItemDecoration(new DividerItemDecoration(context));
        mListView.setAdapter(mAdapter = new UPFileAdapter(mCallback, new UPFileAdapter.Callback2() {
            @Override
            public void onFolderClick(File file) {
                mPath = file;
                initData();
            }
        }));

        initData();
    }

    @Override
    public void onClick(View view) {
        if (mPath != null) {
            if (mPath != Environment.getExternalStorageDirectory()) {
                mPath = mPath.getParentFile();
                initData();
            }
        }
    }

    private void initData() {
        if (mPath == null) {
            mPath = Environment.getExternalStorageDirectory();
        }

        //获取数据
        File[] files = mPath.listFiles(new InternalFilter());
        if (files == null || files.length == 0) {
            mAdapter.setData(null);
        } else {
            //转换成List
            List<File> fileList = Arrays.asList(files);
            //排序
            Collections.sort(fileList, new InternalComparator());
            //加入
            mAdapter.setData(fileList);
        }

        if (mAdapter.getItemCount() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }

        if (mCallback != null) {
            mCallback.onCategoryChanged();
        }
    }

    @Override
    public void startRefreshData(int reason) {

    }

    @Override
    public void stopRefreshData() {

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

    public class InternalFilter implements FileFilter {
        @Override
        public boolean accept(File pathname) {
            if (pathname.getName().startsWith(".")) {
                return false;
            }
            //文件夹内部数量为0
            if (pathname.isDirectory() && pathname.list().length == 0) {
                return false;
            }

            /**
             * 现在只支持TXT文件的显示
             */
            //文件内容为空,或者不以txt为开头
            if (!pathname.isDirectory() &&
                    (pathname.length() == 0 || !pathname.getName().endsWith(FileUtils.SUFFIX_TXT))) {
                return false;
            }
            return true;
        }
    }

    public class InternalComparator implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            if (o1.isDirectory() && o2.isFile()) {
                return -1;
            }
            if (o2.isDirectory() && o1.isFile()) {
                return 1;
            }
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }
}
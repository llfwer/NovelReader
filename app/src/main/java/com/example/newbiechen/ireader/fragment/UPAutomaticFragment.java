package com.example.newbiechen.ireader.fragment;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.adapter.UPFileAdapter;
import com.example.newbiechen.ireader.data.UPMediaManager;
import com.example.newbiechen.ireader.widget.UPEmptyView;
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration;

import java.io.File;
import java.util.List;

public class UPAutomaticFragment extends UPFileBaseFragment {
    private RecyclerView mListView;
    private UPEmptyView mEmptyView;
    private View mLoadingView;

    private UPFileAdapter mAdapter;

    private UPFileAdapter.Callback1 mCallback;

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
    public String getFragmentTitle(Context context) {
        return "智能导入";
    }

    @Override
    public int getFragmentLayoutId() {
        return R.layout.up_automatic_fragment;
    }

    @Override
    public void initView(View view) {
        Context context = getContext();

        mListView = view.findViewById(R.id.up_manual_list_view);
        mEmptyView = view.findViewById(R.id.up_manual_empty_view);
        mLoadingView = view.findViewById(R.id.up_manual_loading_view);

        mListView.setLayoutManager(new LinearLayoutManager(context));
        mListView.addItemDecoration(new DividerItemDecoration(context));
        mListView.setAdapter(mAdapter = new UPFileAdapter(mCallback, null));

        requestData();
    }

    @Override
    public List<File> getCheckList() {
        return mAdapter.getCheckList();
    }

    @Override
    public boolean isCheckAll() {
        return mAdapter.isCheckAll();
    }

    @Override
    public void setCheckedAll(boolean checkAll) {
        mAdapter.checkAll(checkAll);
    }

    @Override
    public void deleteCheckedFiles() {
        mAdapter.deleteCheckedFiles();
    }

    @Override
    public void startRefreshData(int reason) {

    }

    @Override
    public void stopRefreshData() {

    }

    @Override
    public void requestData() {
        UPMediaManager.getAllBookFile(getActivity(), new UPMediaManager.Callback() {
            @Override
            public void onResponse(List<File> files) {
                mAdapter.setData(files);
                if (mAdapter.getItemCount() == 0) {
                    showEmptyView();
                } else {
                    showContentView();
                }

                if (mCallback != null) {
                    mCallback.onCategoryChanged();
                }
            }
        });
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
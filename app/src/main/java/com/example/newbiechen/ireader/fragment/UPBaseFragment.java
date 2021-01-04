package com.example.newbiechen.ireader.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class UPBaseFragment extends Fragment {

    public static final int REFRESH_REASON_ACTIVE_STATE = 1; // 活跃状态变化
    public static final int REFRESH_REASON_PULL_DOWN = 2; // 下拉刷新

    protected View mRootView;

    private boolean mIsActiveState = false; // 是否活跃状态

    public String getFragmentTitle(Context context) {
        return null;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getFragmentLayoutId(), container, false);

            initView(mRootView);
        } else {
            if (mRootView.getParent() != null) {
                ((ViewGroup) mRootView.getParent()).removeView(mRootView);
            }
        }

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        mIsActiveState = true;
        startRefreshData(REFRESH_REASON_ACTIVE_STATE);
    }

    @Override
    public void onPause() {
        mIsActiveState = false;
        stopRefreshData();

        super.onPause();
    }

    public boolean isVisibleToUser() {
        return mIsActiveState;
    }

    public abstract int getFragmentLayoutId();

    public abstract void initView(View view);

    public abstract void startRefreshData(int reason);

    public abstract void stopRefreshData();

    private BroadcastReceiver mLocalReceiver;

    public void registerLocalReceiver(String... actions) {
        if (actions == null || actions.length == 0) return;

        Context context = getContext();
        if (context == null) return;

        if (mLocalReceiver == null) {
            mLocalReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveLocalBroadcast(context, intent);
                }
            };

            final IntentFilter filter = new IntentFilter();
            for (String action : actions) {
                filter.addAction(action);
            }

            LocalBroadcastManager.getInstance(context).registerReceiver(mLocalReceiver, filter);
        }
    }

    public void unregisterLocalReceiver() {
        if (mLocalReceiver != null) {
            Context context = getContext();
            if (context != null) {
                LocalBroadcastManager.getInstance(context).unregisterReceiver(mLocalReceiver);
            }
            mLocalReceiver = null;
        }
    }

    public void onReceiveLocalBroadcast(Context context, Intent intent) {
    }

    private BroadcastReceiver mReceiver;

    public void registerReceiver(String... actions) {
        if (actions == null || actions.length == 0) return;

        Context context = getContext();
        if (context == null) return;

        if (mReceiver == null) {
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    onReceiveBroadcast(context, intent);
                }
            };

            final IntentFilter filter = new IntentFilter();
            for (String action : actions) {
                filter.addAction(action);
            }

            context.registerReceiver(mReceiver, filter);
        }
    }

    public void unregisterReceiver() {
        if (mReceiver != null) {
            Context context = getContext();
            if (context != null) {
                context.unregisterReceiver(mReceiver);
            }
            mReceiver = null;
        }
    }

    public void onReceiveBroadcast(Context context, Intent intent) {
    }

    public void onNetworkAvailable() {
    }
}
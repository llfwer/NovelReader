package com.example.newbiechen.ireader.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.other.UPSettingManager;

public class UPSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private SwitchCompat mSwitchVolume;
    private SwitchCompat mSwitchScreen;
    private Spinner mConvertTypeView;

    private UPSettingManager mSettingManager;
    private boolean mVolumeChecked;
    private boolean mFullScreenChecked;
    private int mConvertType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = findViewById(R.id.up_settings_toolbar);
        initToolBar();

        mSettingManager = UPSettingManager.getInstance();

        mVolumeChecked = mSettingManager.isVolumeTurnPage();
        mFullScreenChecked = mSettingManager.isFullScreen();
        mConvertType = mSettingManager.getConvertType();

        mSwitchVolume = findViewById(R.id.up_settings_switch_volume);
        mSwitchVolume.setChecked(mVolumeChecked);
        findViewById(R.id.up_settings_view_volume).setOnClickListener(this);

        mSwitchScreen = findViewById(R.id.up_settings_switch_full_screen);
        mSwitchScreen.setChecked(mFullScreenChecked);
        findViewById(R.id.up_settings_view_full_screen).setOnClickListener(this);

        mConvertTypeView = findViewById(R.id.up_settings_spinner_convert_type);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.conversion_type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mConvertTypeView.setAdapter(adapter);
        mConvertTypeView.setSelection(mConvertType);
        mConvertTypeView.setOnItemSelectedListener(mItemSelectedListener);
    }

    private void initToolBar() {
        setSupportActionBar(mToolbar);

        mToolbar.setTitle("阅读设置");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.up_settings_view_volume) {
            mVolumeChecked = !mVolumeChecked;
            mSwitchVolume.setChecked(mVolumeChecked);
            mSettingManager.setVolumeTurnPage(mVolumeChecked);
        } else if (id == R.id.up_settings_view_full_screen) {
            mFullScreenChecked = !mFullScreenChecked;
            mSwitchScreen.setChecked(mFullScreenChecked);
            mSettingManager.setFullScreen(mFullScreenChecked);
        } else {//导航栏返回按钮
            finish();
        }
    }

    private AdapterView.OnItemSelectedListener mItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mConvertType = position;
            mSettingManager.setConvertType(position);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
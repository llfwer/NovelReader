package com.rowe.book.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rowe.book.R;
import com.rowe.book.activity.UPReadActivity;
import com.rowe.book.activity.UPSettingsActivity;
import com.rowe.book.adapter.UPPageStyleAdapter;
import com.rowe.book.other.UPSettingManager;
import com.rowe.book.utils.BrightnessUtils;
import com.rowe.book.utils.ScreenUtils;
import com.rowe.book.widget.page.PageLoader;
import com.rowe.book.widget.page.PageMode;
import com.rowe.book.widget.page.PageStyle;

import java.util.Arrays;


public class UPReadSettingDialog extends Dialog {
    private static final String TAG = "ReadSettingDialog";
    private static final int DEFAULT_TEXT_SIZE = 16;

    private ImageView mIvBrightnessMinus;
    private SeekBar mSbBrightness;
    private ImageView mIvBrightnessPlus;
    private CheckBox mCbBrightnessAuto;
    private TextView mTvFontMinus;
    private TextView mTvFont;
    private TextView mTvFontPlus;
    private CheckBox mCbFontDefault;
    private RadioGroup mRgPageMode;

    private RadioButton mRbSimulation;
    private RadioButton mRbCover;
    private RadioButton mRbSlide;
    private RadioButton mRbScroll;
    private RadioButton mRbNone;
    private RecyclerView mBgListView;
    private TextView mTvMore;
    /************************************/
    private UPPageStyleAdapter mPageStyleAdapter;
    private UPSettingManager mSettingManager;
    private PageLoader mPageLoader;
    private Activity mActivity;

    private PageMode mPageMode;
    private PageStyle mPageStyle;

    private int mBrightness;
    private int mTextSize;

    private boolean isBrightnessAuto;
    private boolean isTextDefault;


    public UPReadSettingDialog(@NonNull Activity activity, PageLoader pageLoader) {
        super(activity, R.style.ReadSettingDialog);
        mActivity = activity;
        mPageLoader = pageLoader;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.up_read_setting_view);

        //设置Dialog显示的位置
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.BOTTOM;
            window.setAttributes(lp);
        }


        mSettingManager = UPSettingManager.getInstance();

        isBrightnessAuto = mSettingManager.isBrightnessAuto();
        mBrightness = mSettingManager.getBrightness();
        mTextSize = mSettingManager.getTextSize();
        isTextDefault = mSettingManager.isDefaultTextSize();
        mPageMode = mSettingManager.getPageMode();
        mPageStyle = mSettingManager.getPageStyle();


        mIvBrightnessMinus = findViewById(R.id.read_setting_iv_brightness_minus);
        mSbBrightness = findViewById(R.id.read_setting_sb_brightness);
        mIvBrightnessPlus = findViewById(R.id.read_setting_iv_brightness_plus);
        mCbBrightnessAuto = findViewById(R.id.read_setting_cb_brightness_auto);
        mTvFontMinus = findViewById(R.id.read_setting_tv_font_minus);
        mTvFont = findViewById(R.id.read_setting_tv_font);
        mTvFontPlus = findViewById(R.id.read_setting_tv_font_plus);
        mCbFontDefault = findViewById(R.id.read_setting_cb_font_default);
        mRgPageMode = findViewById(R.id.read_setting_rg_page_mode);
        mRbSimulation = findViewById(R.id.read_setting_rb_simulation);
        mRbCover = findViewById(R.id.read_setting_rb_cover);
        mRbSlide = findViewById(R.id.read_setting_rb_slide);
        mRbScroll = findViewById(R.id.read_setting_rb_scroll);
        mRbNone = findViewById(R.id.read_setting_rb_none);
        mBgListView = findViewById(R.id.read_setting_rv_bg);
        mTvMore = findViewById(R.id.read_setting_tv_more);

        mSbBrightness.setProgress(mBrightness);
        mTvFont.setText(mTextSize + "");
        mCbBrightnessAuto.setChecked(isBrightnessAuto);
        mCbFontDefault.setChecked(isTextDefault);
        initPageMode();
        //RecyclerView
        setUpAdapter();

        initClick();
    }

    private void setUpAdapter() {
        Drawable[] drawables = {
                getDrawable(R.color.nb_read_bg_1)
                , getDrawable(R.color.nb_read_bg_2)
                , getDrawable(R.color.nb_read_bg_3)
                , getDrawable(R.color.nb_read_bg_4)
                , getDrawable(R.color.nb_read_bg_5)};

        mPageStyleAdapter = new UPPageStyleAdapter(new UPPageStyleAdapter.Callback() {
            @Override
            public void onItemClick(View view, int position) {
                mPageStyleAdapter.setStyle(position);
                mPageLoader.setPageStyle(PageStyle.values()[position]);
            }
        });
        mBgListView.setLayoutManager(new GridLayoutManager(getContext(), 5));
        mBgListView.setAdapter(mPageStyleAdapter);
        mPageStyleAdapter.setData(Arrays.asList(drawables));

        mPageStyleAdapter.setStyle(mPageStyle.ordinal());
    }

    private void initPageMode() {
        switch (mPageMode) {
            case SIMULATION:
                mRbSimulation.setChecked(true);
                break;
            case COVER:
                mRbCover.setChecked(true);
                break;
            case SLIDE:
                mRbSlide.setChecked(true);
                break;
            case NONE:
                mRbNone.setChecked(true);
                break;
            case SCROLL:
                mRbScroll.setChecked(true);
                break;
        }
    }

    private Drawable getDrawable(int drawRes) {
        return ContextCompat.getDrawable(getContext(), drawRes);
    }

    private void initClick() {
        //亮度调节
        mIvBrightnessMinus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() - 1;
                    if (progress < 0) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                }
        );
        mIvBrightnessPlus.setOnClickListener(
                (v) -> {
                    if (mCbBrightnessAuto.isChecked()) {
                        mCbBrightnessAuto.setChecked(false);
                    }
                    int progress = mSbBrightness.getProgress() + 1;
                    if (progress > mSbBrightness.getMax()) return;
                    mSbBrightness.setProgress(progress);
                    BrightnessUtils.setBrightness(mActivity, progress);
                    //设置进度
                    UPSettingManager.getInstance().setBrightness(progress);
                }
        );

        mSbBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int progress = seekBar.getProgress();
                if (mCbBrightnessAuto.isChecked()) {
                    mCbBrightnessAuto.setChecked(false);
                }
                //设置当前 Activity 的亮度
                BrightnessUtils.setBrightness(mActivity, progress);
                //存储亮度的进度条
                UPSettingManager.getInstance().setBrightness(progress);
            }
        });

        mCbBrightnessAuto.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        //获取屏幕的亮度
                        BrightnessUtils.setBrightness(mActivity, BrightnessUtils.getScreenBrightness(mActivity));
                    } else {
                        //获取进度条的亮度
                        BrightnessUtils.setBrightness(mActivity, mSbBrightness.getProgress());
                    }
                    UPSettingManager.getInstance().setAutoBrightness(isChecked);
                }
        );

        //字体大小调节
        mTvFontMinus.setOnClickListener(
                (v) -> {
                    if (mCbFontDefault.isChecked()) {
                        mCbFontDefault.setChecked(false);
                    }
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) - 1;
                    if (fontSize < 0) return;
                    mTvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
        );

        mTvFontPlus.setOnClickListener(
                (v) -> {
                    if (mCbFontDefault.isChecked()) {
                        mCbFontDefault.setChecked(false);
                    }
                    int fontSize = Integer.valueOf(mTvFont.getText().toString()) + 1;
                    mTvFont.setText(fontSize + "");
                    mPageLoader.setTextSize(fontSize);
                }
        );

        mCbFontDefault.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isChecked) {
                        int fontSize = ScreenUtils.dpToPx(DEFAULT_TEXT_SIZE);
                        mTvFont.setText(fontSize + "");
                        mPageLoader.setTextSize(fontSize);
                    }
                }
        );

        //Page Mode 切换
        mRgPageMode.setOnCheckedChangeListener(
                (group, checkedId) -> {
                    PageMode pageMode;
                    switch (checkedId) {
                        case R.id.read_setting_rb_simulation:
                            pageMode = PageMode.SIMULATION;
                            break;
                        case R.id.read_setting_rb_cover:
                            pageMode = PageMode.COVER;
                            break;
                        case R.id.read_setting_rb_slide:
                            pageMode = PageMode.SLIDE;
                            break;
                        case R.id.read_setting_rb_scroll:
                            pageMode = PageMode.SCROLL;
                            break;
                        case R.id.read_setting_rb_none:
                            pageMode = PageMode.NONE;
                            break;
                        default:
                            pageMode = PageMode.SIMULATION;
                            break;
                    }
                    mPageLoader.setPageMode(pageMode);
                }
        );

        //更多设置
        mTvMore.setOnClickListener(
                (v) -> {
                    Intent intent = new Intent(getContext(), UPSettingsActivity.class);
                    mActivity.startActivityForResult(intent, UPReadActivity.REQUEST_MORE_SETTING);
                    //关闭当前设置
                    dismiss();
                }
        );
    }

    public boolean isBrightFollowSystem() {
        if (mCbBrightnessAuto == null) {
            return false;
        }
        return mCbBrightnessAuto.isChecked();
    }
}

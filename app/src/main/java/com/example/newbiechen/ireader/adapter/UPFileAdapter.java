package com.example.newbiechen.ireader.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.model.local.BookRepository;
import com.example.newbiechen.ireader.utils.Constant;
import com.example.newbiechen.ireader.utils.FileUtils;
import com.example.newbiechen.ireader.utils.MD5Utils;
import com.example.newbiechen.ireader.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UPFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<File> mDataList = new ArrayList<>();
    private List<File> mCheckList = new ArrayList<>();

    private int mCheckableCount;

    public interface Callback1 {
        void onCheckChanged();

        void onCategoryChanged();
    }

    private Callback1 mCallback1;

    public interface Callback2 {
        void onFolderClick(File file);
    }

    private Callback2 mCallback2;

    public UPFileAdapter(Callback1 callback1, Callback2 callback2) {
        mCallback1 = callback1;
        mCallback2 = callback2;
    }

    public void setData(List<File> list) {
        mDataList.clear();
        mCheckList.clear();
        mCheckableCount = 0;
        if (list != null) {
            for (File item : list) {
                if (item != null) {
                    if (!isFileLoaded(item)) {
                        mCheckableCount++;
                    }
                    mDataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean isFileLoaded(File file) {
        String id = MD5Utils.strToMd5By16(file.getAbsolutePath());
        return BookRepository.getInstance().getCollBook(id) != null;
    }

    public void checkAll(boolean check) {
        if (check) {
            for (File item : mDataList) {
                if (!mCheckList.contains(item)) {
                    mCheckList.add(item);
                }
            }
        } else {
            mCheckList.clear();
        }
        if (mCallback1 != null) {
            mCallback1.onCheckChanged();
        }
        notifyDataSetChanged();
    }

    public List<File> getCheckList() {
        return mCheckList;
    }

    public boolean isCheckAll() {
        return mCheckableCount == mCheckList.size();
    }

    public void deleteCheckedFiles() {
        Iterator<File> iterator = mCheckList.iterator();
        while (iterator.hasNext()) {
            File item = iterator.next();
            if (item.exists()) {
                if (item.delete()) {
                    iterator.remove();
                    mDataList.remove(item);
                }
            }
        }
        if (mCallback1 != null) {
            mCallback1.onCheckChanged();
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InternalHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.up_file_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((InternalHolder) holder).bindData(mDataList.get(position));
    }

    private class InternalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView mIvIcon;
        private CheckBox mCbSelect;
        private TextView mTvName;
        private LinearLayout mLlBrief;
        private TextView mTvTag;
        private TextView mTvSize;
        private TextView mTvDate;
        private TextView mTvSubCount;

        private File mData;

        InternalHolder(@NonNull View itemView) {
            super(itemView);

            mIvIcon = itemView.findViewById(R.id.file_iv_icon);
            mCbSelect = itemView.findViewById(R.id.file_cb_select);
            mTvName = itemView.findViewById(R.id.file_tv_name);
            mLlBrief = itemView.findViewById(R.id.file_ll_brief);
            mTvTag = itemView.findViewById(R.id.file_tv_tag);
            mTvSize = itemView.findViewById(R.id.file_tv_size);
            mTvDate = itemView.findViewById(R.id.file_tv_date);
            mTvSubCount = itemView.findViewById(R.id.file_tv_sub_count);

            itemView.setOnClickListener(this);
        }

        void bindData(File data) {
            mData = data;

            Context context = itemView.getContext();

            if (data.isDirectory()) {
                //图片
                mIvIcon.setVisibility(View.VISIBLE);
                mCbSelect.setVisibility(View.GONE);
                mIvIcon.setImageResource(R.drawable.ic_dir);
                //名字
                mTvName.setText(data.getName());
                //介绍
                mLlBrief.setVisibility(View.GONE);
                mTvSubCount.setVisibility(View.VISIBLE);

                mTvSubCount.setText(context.getString(R.string.nb_file_sub_count, data.list().length));
            } else {
                if (isFileLoaded(data)) {
                    mIvIcon.setImageResource(R.drawable.ic_file_loaded);
                    mIvIcon.setVisibility(View.VISIBLE);
                    mCbSelect.setVisibility(View.GONE);
                } else {
                    mCbSelect.setChecked(mCheckList.contains(data));
                    mIvIcon.setVisibility(View.GONE);
                    mCbSelect.setVisibility(View.VISIBLE);
                }

                mLlBrief.setVisibility(View.VISIBLE);
                mTvSubCount.setVisibility(View.GONE);

                mTvName.setText(data.getName());
                mTvSize.setText(FileUtils.getFileSize(data.length()));
                mTvDate.setText(StringUtils.dateConvert(data.lastModified(), Constant.FORMAT_FILE_DATE));
            }
        }

        @Override
        public void onClick(View view) {
            if (mData.isDirectory()) {
                if (mCallback2 != null) {
                    mCallback2.onFolderClick(mData);
                }
            } else {
                if (!isFileLoaded(mData)) {
                    if (mCheckList.contains(mData)) {
                        mCheckList.remove(mData);
                    } else {
                        mCheckList.add(mData);
                    }
                    notifyDataSetChanged();

                    if (mCallback1 != null) {
                        mCallback1.onCheckChanged();
                    }
                }
            }
        }
    }
}
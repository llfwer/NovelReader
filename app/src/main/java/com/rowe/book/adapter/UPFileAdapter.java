package com.rowe.book.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rowe.book.App;
import com.rowe.book.R;
import com.rowe.book.book.UPBookDBManager;
import com.rowe.book.utils.Constant;
import com.rowe.book.utils.FileUtils;
import com.rowe.book.utils.StringUtils;
import com.rowe.book.utils.UPMD5Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UPFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_FILE = 1;
    private static final int TYPE_FOLDER = 2;

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
                    if (isCheckable(item)) {
                        mCheckableCount++;
                    }
                    mDataList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    private boolean isFileLoaded(File file) {
        String id = UPMD5Util.strToMd5By16(file.getAbsolutePath());
        return UPBookDBManager.getInstance(App.getContext()).getBook(id) != null;
    }

    private boolean isCheckable(File item) {
        return item.isFile() && !isFileLoaded(item);
    }

    public void checkAll(boolean check) {
        if (check) {
            for (File item : mDataList) {
                if (isCheckable(item) && !mCheckList.contains(item)) {
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
        return mCheckableCount > 0 && mCheckableCount == mCheckList.size();
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

    @Override
    public int getItemViewType(int position) {
        return mDataList.get(position).isDirectory() ? TYPE_FOLDER : TYPE_FILE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_FOLDER) {
            return new InternalHolder1(inflater.inflate(R.layout.up_file_folder_item_view, parent, false));
        } else {
            return new InternalHolder2(inflater.inflate(R.layout.up_file_file_item_view, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InternalHolder1) {
            ((InternalHolder1) holder).bindData(mDataList.get(position));
        } else if (holder instanceof InternalHolder2) {
            ((InternalHolder2) holder).bindData(mDataList.get(position));
        }
    }

    // 文件夹
    private class InternalHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mName;
        private TextView mCount;

        private File mData;

        InternalHolder1(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.up_folder_name);
            mCount = itemView.findViewById(R.id.up_folder_count);

            itemView.setOnClickListener(this);
        }

        void bindData(File data) {
            mData = data;

            Context context = itemView.getContext();

            //名字
            String nameText = data.getName();
            mName.setText(TextUtils.isEmpty(nameText) ? "--" : nameText);

            //子文件数目
            String[] subFiles = data.list();
            mCount.setText(context.getString(R.string.up_folder_sub_count, subFiles == null ? 0 : subFiles.length));
        }

        @Override
        public void onClick(View view) {
            if (mData.isDirectory()) {
                if (mCallback2 != null) {
                    mCallback2.onFolderClick(mData);
                }
            }
        }
    }

    // 文件
    private class InternalHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CheckBox mCheckBox;
        private View mIcon;
        private TextView mName;
        private TextView mTag;
        private TextView mSize;
        private TextView mDate;

        private File mData;

        InternalHolder2(@NonNull View itemView) {
            super(itemView);

            mCheckBox = itemView.findViewById(R.id.up_file_check);
            mIcon = itemView.findViewById(R.id.up_file_icon);
            mName = itemView.findViewById(R.id.up_file_name);
            mTag = itemView.findViewById(R.id.up_file_tag);
            mSize = itemView.findViewById(R.id.up_file_size);
            mDate = itemView.findViewById(R.id.up_file_date);

            itemView.setOnClickListener(this);
        }

        void bindData(File data) {
            mData = data;

            Context context = itemView.getContext();

            // 状态
            if (isFileLoaded(data)) {
                mIcon.setVisibility(View.VISIBLE);
                mCheckBox.setVisibility(View.GONE);
            } else {
                mCheckBox.setChecked(mCheckList.contains(data));
                mIcon.setVisibility(View.GONE);
                mCheckBox.setVisibility(View.VISIBLE);
            }

            //名字
            String nameText = data.getName();
            mName.setText(TextUtils.isEmpty(nameText) ? "--" : nameText);

            //大小
            mSize.setText(FileUtils.getFileSize(data.length()));

            // 日期
            mDate.setText(StringUtils.dateConvert(data.lastModified(), Constant.FORMAT_FILE_DATE));
        }

        @Override
        public void onClick(View view) {
            if (isCheckable(mData)) {
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
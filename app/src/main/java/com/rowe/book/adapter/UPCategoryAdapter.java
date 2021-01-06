package com.rowe.book.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rowe.book.R;
import com.rowe.book.widget.page.UPTxtChapter;

import java.util.ArrayList;
import java.util.List;

public class UPCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UPTxtChapter> mDataList = new ArrayList<>();

    public interface Callback {
        void onItemClick(View view, UPTxtChapter data, int position);
    }

    private Callback mCallback;

    public UPCategoryAdapter(Callback callback) {
        mCallback = callback;
    }

    public void setData(List<UPTxtChapter> list) {
        mDataList.clear();
        if (list != null) {
            mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    private int mCurrentChapter = 0;

    public void setChapter(int position) {
        mCurrentChapter = position;
        notifyDataSetChanged();
    }

    private void removeItem(UPTxtChapter data) {
        mDataList.remove(data);
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
                .inflate(R.layout.up_category_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((InternalHolder) holder).bindData(mDataList.get(position), position);
    }

    private class InternalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitle;

        private UPTxtChapter mData;
        private int mPosition;

        InternalHolder(@NonNull View itemView) {
            super(itemView);

            mTitle = itemView.findViewById(R.id.up_category_title);

            itemView.setOnClickListener(this);
        }

        void bindData(UPTxtChapter data, int position) {
            mData = data;
            mPosition = position;

            //标题
            String titleText = data == null ? null : data.title;
            mTitle.setText(TextUtils.isEmpty(titleText) ? "--" : titleText);

            mTitle.setSelected(position == mCurrentChapter);
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                mCallback.onItemClick(view, mData, mPosition);
            }
        }
    }
}
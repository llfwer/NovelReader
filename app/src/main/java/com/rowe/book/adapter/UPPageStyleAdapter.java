package com.rowe.book.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rowe.book.R;

import java.util.ArrayList;
import java.util.List;

public class UPPageStyleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Drawable> mDataList = new ArrayList<>();

    public interface Callback {
        void onItemClick(View view, int position);
    }

    private Callback mCallback;

    public UPPageStyleAdapter(Callback callback) {
        mCallback = callback;
    }

    public void setData(List<Drawable> list) {
        mDataList.clear();
        if (list != null) {
            mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    private int mCurrentStyle = 0;

    public void setStyle(int position) {
        mCurrentStyle = position;
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
                .inflate(R.layout.up_page_style_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((InternalHolder) holder).bindData(mDataList.get(position), position);
    }

    private class InternalHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private View mBackground;
        private ImageView mImage;

        private int mPosition;

        InternalHolder(@NonNull View itemView) {
            super(itemView);

            mBackground = itemView.findViewById(R.id.read_bg_view);
            mImage = itemView.findViewById(R.id.read_bg_iv_checked);

            itemView.setOnClickListener(this);
        }

        void bindData(Drawable drawable, int position) {
            mPosition = position;

            mBackground.setBackground(drawable);

            mImage.setVisibility(position == mCurrentStyle ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            if (mCallback != null) {
                mCallback.onItemClick(view, mPosition);
            }
        }
    }
}
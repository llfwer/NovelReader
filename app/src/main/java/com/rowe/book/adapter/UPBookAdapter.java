package com.rowe.book.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.rowe.book.R;
import com.rowe.book.book.UPBookDBManager;
import com.rowe.book.book.UPBook;
import com.rowe.book.utils.UPRouteUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UPBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<UPBook> mDataList = new ArrayList<>();

    public void setData(List<UPBook> list) {
        mDataList.clear();
        if (list != null) {
            mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    private void removeItem(UPBook data) {
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
                .inflate(R.layout.up_book_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((InternalHolder) holder).bindData(mDataList.get(position));
    }

    private class InternalHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private ImageView mIvCover;
        private TextView mTvName;
        private TextView mTvChapter;
        private TextView mTvTime;
        private CheckBox mCbSelected;
        private ImageView mIvRedDot;
        private ImageView mIvTop;

        private UPBook mData;

        InternalHolder(@NonNull View itemView) {
            super(itemView);

            mIvCover = itemView.findViewById(R.id.coll_book_iv_cover);
            mTvName = itemView.findViewById(R.id.coll_book_tv_name);
            mTvChapter = itemView.findViewById(R.id.coll_book_tv_chapter);
            mTvTime = itemView.findViewById(R.id.coll_book_tv_lately_update);
            mCbSelected = itemView.findViewById(R.id.coll_book_cb_selected);
            mIvRedDot = itemView.findViewById(R.id.coll_book_iv_red_rot);
            mIvTop = itemView.findViewById(R.id.coll_book_iv_top);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        void bindData(UPBook data) {
            mData = data;

            Context context = itemView.getContext();

            //本地文件的图片
            Glide.with(context)
                    .load(R.drawable.ic_local_file)
                    .fitCenter()
                    .into(mIvCover);

            //书名
            mTvName.setText(data.name);

            mTvTime.setText("阅读进度:");
            //章节
            mTvChapter.setText(data.chapterTitle);
            //我的想法是，在Collection中加一个字段，当追更的时候设置为true。当点击的时候设置为false。
            //当更新的时候，最新数据跟旧数据进行比较，如果更新的话，设置为true。
            if (!data.hasRead) {
                mIvRedDot.setVisibility(View.VISIBLE);
            } else {
                mIvRedDot.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            Context context = itemView.getContext();
            if (context == null) return;

            //id表示本地文件的路径
            String path = mData.path;
            File file = new File(path);
            //判断这个本地文件是否存在
            if (file.exists() && file.length() != 0) {
                UPRouteUtil.gotoReadActivity(context, mData);
            } else {
                String tip = context.getString(R.string.nb_bookshelf_book_not_exist);
                //提示(从目录中移除这个文件)
                new AlertDialog.Builder(context)
                        .setTitle(R.string.nb_common_tip)
                        .setMessage(tip)
                        .setPositiveButton(R.string.nb_common_sure,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteBook(context, mData);
                                    }
                                })
                        .setNegativeButton(R.string.nb_common_cancel, null)
                        .show();
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Context context = itemView.getContext();
            if (context == null) return true;

            //开启Dialog,最方便的Dialog,就是AlterDialog
            String[] menus = new String[]{"删除"};
            AlertDialog collBookDialog = new AlertDialog.Builder(context)
                    .setTitle(mData.name)
                    .setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, menus),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteBook(context, mData);
                                }
                            })
                    .setNegativeButton(null, null)
                    .setPositiveButton(null, null)
                    .create();

            collBookDialog.show();
            return true;
        }
    }

    private void deleteBook(Context context, UPBook book) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete, null);
        CheckBox cb = view.findViewById(R.id.delete_cb_select);
        new AlertDialog.Builder(context)
                .setTitle("删除文件")
                .setView(view)
                .setPositiveButton(R.string.nb_common_sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean isSelected = cb.isSelected();
                        if (isSelected) {
                            ProgressDialog progressDialog = new ProgressDialog(context);
                            progressDialog.setMessage("正在删除中");
                            progressDialog.show();
                            //删除
                            File file = new File(book.path);
                            if (file.exists()) file.delete();

                            UPBookDBManager.getInstance(context).deleteBook(book);
                            //从Adapter中删除
                            removeItem(book);
                            progressDialog.dismiss();
                        } else {
                            UPBookDBManager.getInstance(context).deleteBook(book);
                            //从Adapter中删除
                            removeItem(book);
                        }
                    }
                })
                .setNegativeButton(R.string.nb_common_cancel, null)
                .show();
    }
}
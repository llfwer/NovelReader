package com.example.newbiechen.ireader.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.activity.MainActivity;
import com.example.newbiechen.ireader.activity.ReadActivity;
import com.example.newbiechen.ireader.model.bean.CollBookBean;
import com.example.newbiechen.ireader.model.local.BookRepository;
import com.example.newbiechen.ireader.utils.Constant;
import com.example.newbiechen.ireader.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UPBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CollBookBean> mDataList = new ArrayList<>();

    public void setData(List<CollBookBean> list) {
        mDataList.clear();
        if (list != null) {
            mDataList.addAll(list);
        }
        notifyDataSetChanged();
    }

    private void removeItem(CollBookBean data) {
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

        private CollBookBean mData;

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

        void bindData(CollBookBean data) {
            mData = data;

            Context context = itemView.getContext();

            if (data.isLocal()) {
                //本地文件的图片
                Glide.with(context)
                        .load(R.drawable.ic_local_file)
                        .fitCenter()
                        .into(mIvCover);
            } else {
                //书的图片
                Glide.with(context)
                        .load(Constant.IMG_BASE_URL + data.getCover())
                        .placeholder(R.drawable.ic_book_loading)
                        .error(R.drawable.ic_load_error)
                        .fitCenter()
                        .into(mIvCover);
            }
            //书名
            mTvName.setText(data.getTitle());
            if (!data.isLocal()) {
                //时间
                mTvTime.setText(StringUtils.
                        dateConvert(data.getUpdated(), Constant.FORMAT_BOOK_DATE) + ":");
                mTvTime.setVisibility(View.VISIBLE);
            } else {
                mTvTime.setText("阅读进度:");
            }
            //章节
            mTvChapter.setText(data.getLastChapter());
            //我的想法是，在Collection中加一个字段，当追更的时候设置为true。当点击的时候设置为false。
            //当更新的时候，最新数据跟旧数据进行比较，如果更新的话，设置为true。
            if (data.isUpdate()) {
                mIvRedDot.setVisibility(View.VISIBLE);
            } else {
                mIvRedDot.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View view) {
            Context context = itemView.getContext();
            if (context == null) return;

            if (mData.isLocal()) {
                //id表示本地文件的路径
                String path = mData.getCover();
                File file = new File(path);
                //判断这个本地文件是否存在
                if (file.exists() && file.length() != 0) {
                    ReadActivity.startActivity(context,
                            mData, true);
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
            } else {
                ReadActivity.startActivity(context, mData, true);
            }
        }

        @Override
        public boolean onLongClick(View view) {
            Context context = itemView.getContext();
            if (context == null) return true;

            //开启Dialog,最方便的Dialog,就是AlterDialog
            String[] menus = new String[]{"删除"};
            AlertDialog collBookDialog = new AlertDialog.Builder(context)
                    .setTitle(mData.getTitle())
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

    /**
     * 默认删除本地文件
     *
     * @param context
     * @param collBook
     */
    private void deleteBook(Context context, CollBookBean collBook) {
        if (collBook.isLocal()) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_delete, null);
            CheckBox cb = (CheckBox) view.findViewById(R.id.delete_cb_select);
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
                                File file = new File(collBook.getCover());
                                if (file.exists()) file.delete();
                                BookRepository.getInstance().deleteCollBook(collBook);
                                BookRepository.getInstance().deleteBookRecord(collBook.get_id());

                                //从Adapter中删除
                                removeItem(collBook);
                                progressDialog.dismiss();
                            } else {
                                BookRepository.getInstance().deleteCollBook(collBook);
                                BookRepository.getInstance().deleteBookRecord(collBook.get_id());
                                //从Adapter中删除
                                removeItem(collBook);
                            }
                        }
                    })
                    .setNegativeButton(R.string.nb_common_cancel, null)
                    .show();
        }
    }
}
package com.example.newbiechen.ireader.ui.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.newbiechen.ireader.R;
import com.example.newbiechen.ireader.RxBus;
import com.example.newbiechen.ireader.event.DeleteResponseEvent;
import com.example.newbiechen.ireader.event.DeleteTaskEvent;
import com.example.newbiechen.ireader.model.bean.CollBookBean;
import com.example.newbiechen.ireader.model.local.BookRepository;
import com.example.newbiechen.ireader.presenter.BookShelfPresenter;
import com.example.newbiechen.ireader.presenter.contract.BookShelfContract;
import com.example.newbiechen.ireader.ui.adapter.CollBookAdapter;
import com.example.newbiechen.ireader.ui.base.BaseMVPActivity;
import com.example.newbiechen.ireader.utils.PermissionsChecker;
import com.example.newbiechen.ireader.utils.RxUtils;
import com.example.newbiechen.ireader.utils.ToastUtils;
import com.example.newbiechen.ireader.widget.itemdecoration.DividerItemDecoration;
import com.example.newbiechen.ireader.widget.refresh.ScrollRefreshRecyclerView;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import butterknife.BindView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class MainActivity extends BaseMVPActivity<BookShelfContract.Presenter>
        implements BookShelfContract.View {
    /*************Constant**********/
    private static final int WAIT_INTERVAL = 2000;
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;

    static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /***************Object*********************/
    private PermissionsChecker mPermissionsChecker;
    /*****************Params*********************/
    private boolean isPrepareFinish = false;

    @BindView(R.id.book_shelf_rv_content)
    ScrollRefreshRecyclerView mRvContent;

    /************************************/
    private CollBookAdapter mCollBookAdapter;

    //是否是第一次进入
    private boolean isInit = true;

    @Override
    protected int getContentId() {
        return R.layout.activity_base_tab;
    }

    /**************init method***********************/
    @Override
    protected void setUpToolbar(Toolbar toolbar) {
        super.setUpToolbar(toolbar);
        toolbar.setLogo(R.mipmap.logo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("");
    }

    @Override
    protected void initWidget() {
        super.initWidget();

        setUpAdapter();
    }

    private void setUpAdapter() {
        //添加Footer
        mCollBookAdapter = new CollBookAdapter();
        mRvContent.setLayoutManager(new LinearLayoutManager(this));
        mRvContent.addItemDecoration(new DividerItemDecoration(this));
        mRvContent.setAdapter(mCollBookAdapter);
    }

    @Override
    protected void initClick() {
        super.initClick();

        //删除书籍 (写的丑了点)
        Disposable deleteDisp = RxBus.getInstance()
                .toObservable(DeleteResponseEvent.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        event -> {
                            if (event.isDelete) {
                                ProgressDialog progressDialog = new ProgressDialog(this);
                                progressDialog.setMessage("正在删除中");
                                progressDialog.show();
                                BookRepository.getInstance().deleteCollBookInRx(event.collBook)
                                        .compose(RxUtils::toSimpleSingle)
                                        .subscribe(
                                                (Void) -> {
                                                    mCollBookAdapter.removeItem(event.collBook);
                                                    progressDialog.dismiss();
                                                }
                                        );
                            } else {
                                //弹出一个Dialog
                                AlertDialog tipDialog = new AlertDialog.Builder(this)
                                        .setTitle("您的任务正在加载")
                                        .setMessage("先请暂停任务再进行删除")
                                        .setPositiveButton("确定", (dialog, which) -> {
                                            dialog.dismiss();
                                        }).create();
                                tipDialog.show();
                            }
                        }
                );
        addDisposable(deleteDisp);

        mRvContent.setOnRefreshListener(
                () -> mPresenter.updateCollBooks(mCollBookAdapter.getItems())
        );

        mCollBookAdapter.setOnItemClickListener(
                (view, pos) -> {
                    //如果是本地文件，首先判断这个文件是否存在
                    CollBookBean collBook = mCollBookAdapter.getItem(pos);
                    if (collBook.isLocal()) {
                        //id表示本地文件的路径
                        String path = collBook.getCover();
                        File file = new File(path);
                        //判断这个本地文件是否存在
                        if (file.exists() && file.length() != 0) {
                            ReadActivity.startActivity(this,
                                    mCollBookAdapter.getItem(pos), true);
                        } else {
                            String tip = getString(R.string.nb_bookshelf_book_not_exist);
                            //提示(从目录中移除这个文件)
                            new AlertDialog.Builder(this)
                                    .setTitle(getResources().getString(R.string.nb_common_tip))
                                    .setMessage(tip)
                                    .setPositiveButton(getResources().getString(R.string.nb_common_sure),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteBook(collBook);
                                                }
                                            })
                                    .setNegativeButton(getResources().getString(R.string.nb_common_cancel), null)
                                    .show();
                        }
                    } else {
                        ReadActivity.startActivity(this,
                                mCollBookAdapter.getItem(pos), true);
                    }
                }
        );

        mCollBookAdapter.setOnItemLongClickListener(
                (v, pos) -> {
                    //开启Dialog,最方便的Dialog,就是AlterDialog
                    openItemDialog(mCollBookAdapter.getItem(pos));
                    return true;
                }
        );
    }

    @Override
    protected void processLogic() {
        super.processLogic();
        mRvContent.startRefresh();
    }

    private void openItemDialog(CollBookBean collBook) {
        String[] menus;
        if (collBook.isLocal()) {
            menus = getResources().getStringArray(R.array.nb_menu_local_book);
        } else {
            menus = getResources().getStringArray(R.array.nb_menu_net_book);
        }
        AlertDialog collBookDialog = new AlertDialog.Builder(this)
                .setTitle(collBook.getTitle())
                .setAdapter(new ArrayAdapter<String>(this,
                                android.R.layout.simple_list_item_1, menus),
                        (dialog, which) -> onItemMenuClick(menus[which], collBook))
                .setNegativeButton(null, null)
                .setPositiveButton(null, null)
                .create();

        collBookDialog.show();
    }

    private void onItemMenuClick(String which, CollBookBean collBook) {
        switch (which) {
            //置顶
            case "置顶":
                break;
            //缓存
            case "缓存":
                //2. 进行判断，如果CollBean中状态为未更新。那么就创建Task，加入到Service中去。
                //3. 如果状态为finish，并且isUpdate为true，那么就根据chapter创建状态
                //4. 如果状态为finish，并且isUpdate为false。
                downloadBook(collBook);
                break;
            //删除
            case "删除":
                deleteBook(collBook);
                break;
            //批量管理
            case "批量管理":
                break;
            default:
                break;
        }
    }

    private void downloadBook(CollBookBean collBook) {
        //创建任务
        mPresenter.createDownloadTask(collBook);
    }

    /**
     * 默认删除本地文件
     *
     * @param collBook
     */
    private void deleteBook(CollBookBean collBook) {
        if (collBook.isLocal()) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.dialog_delete, null);
            CheckBox cb = (CheckBox) view.findViewById(R.id.delete_cb_select);
            new AlertDialog.Builder(this)
                    .setTitle("删除文件")
                    .setView(view)
                    .setPositiveButton(getResources().getString(R.string.nb_common_sure), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            boolean isSelected = cb.isSelected();
                            if (isSelected) {
                                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                                progressDialog.setMessage("正在删除中");
                                progressDialog.show();
                                //删除
                                File file = new File(collBook.getCover());
                                if (file.exists()) file.delete();
                                BookRepository.getInstance().deleteCollBook(collBook);
                                BookRepository.getInstance().deleteBookRecord(collBook.get_id());

                                //从Adapter中删除
                                mCollBookAdapter.removeItem(collBook);
                                progressDialog.dismiss();
                            } else {
                                BookRepository.getInstance().deleteCollBook(collBook);
                                BookRepository.getInstance().deleteBookRecord(collBook.get_id());
                                //从Adapter中删除
                                mCollBookAdapter.removeItem(collBook);
                            }
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.nb_common_cancel), null)
                    .show();
        } else {
            RxBus.getInstance().post(new DeleteTaskEvent(collBook));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Class<?> activityCls = null;
        switch (id) {
            case R.id.action_scan_local_book:

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {

                    if (mPermissionsChecker == null) {
                        mPermissionsChecker = new PermissionsChecker(this);
                    }

                    //获取读取和写入SD卡的权限
                    if (mPermissionsChecker.lacksPermissions(PERMISSIONS)) {
                        //请求权限
                        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSIONS_REQUEST_STORAGE);
                        return super.onOptionsItemSelected(item);
                    }
                }

                activityCls = FileSystemActivity.class;
                break;
            default:
                break;
        }
        if (activityCls != null) {
            Intent intent = new Intent(this, activityCls);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (menu instanceof MenuBuilder) {
            try {
                Method method = menu.getClass().
                        getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                method.setAccessible(true);
                method.invoke(menu, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onPreparePanel(featureId, view, menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_STORAGE: {
                // 如果取消权限，则返回的值为0
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //跳转到 FileSystemActivity
                    Intent intent = new Intent(this, FileSystemActivity.class);
                    startActivity(intent);

                } else {
                    ToastUtils.show("用户拒绝开启读写权限");
                }
                return;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isPrepareFinish) {
            mRvContent.postDelayed(
                    () -> isPrepareFinish = false, WAIT_INTERVAL
            );
            isPrepareFinish = true;
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finishRefresh(List<CollBookBean> collBookBeans) {
        mCollBookAdapter.refreshItems(collBookBeans);
        //如果是初次进入，则更新书籍信息
        if (isInit) {
            isInit = false;
            mRvContent.post(
                    () -> mPresenter.updateCollBooks(mCollBookAdapter.getItems())
            );
        }
    }

    @Override
    public void finishUpdate() {
        //重新从数据库中获取数据
        mCollBookAdapter.refreshItems(BookRepository
                .getInstance().getCollBooks());
    }

    @Override
    public void showErrorTip(String error) {
        mRvContent.setTip(error);
        mRvContent.showTip();
    }

    @Override
    public void showError() {

    }

    @Override
    public void complete() {
        if (mRvContent.isRefreshing()) {
            mRvContent.finishRefresh();
        }
    }

    @Override
    protected BookShelfContract.Presenter bindPresenter() {
        return new BookShelfPresenter();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.refreshCollBooks();
    }
}

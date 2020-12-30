package com.example.newbiechen.ireader.fragment;

import java.io.File;
import java.util.List;

public abstract class UPFileBaseFragment extends UPBaseFragment {
    public abstract List<File> getCheckList();

    public abstract boolean isCheckAll();

    public abstract void setCheckedAll(boolean checkAll);

    public abstract void deleteCheckedFiles();

    public abstract void requestData();
}
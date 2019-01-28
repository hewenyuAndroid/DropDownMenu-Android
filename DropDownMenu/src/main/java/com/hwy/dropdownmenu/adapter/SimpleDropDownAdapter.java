package com.hwy.dropdownmenu.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import java.util.List;

/**
 * 作者: hewenyu
 * 日期: 2018/11/28 12:20
 * 说明: 泛型封装
 */
public abstract class SimpleDropDownAdapter<T> extends BaseDropDownAdapter {

    protected Context mContext;

    protected List<T> mDatas;

    protected LayoutInflater mInflater;

    public SimpleDropDownAdapter(Context context, List<T> datas) {
        this.mContext = context;
        this.mDatas = datas;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mDatas == null ? 0 : mDatas.size();
    }

    @Override
    public T getItem(int position) {
        return mDatas.get(position);
    }
}

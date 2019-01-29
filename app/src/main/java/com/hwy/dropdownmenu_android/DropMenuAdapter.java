package com.hwy.dropdownmenu_android;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.hwy.adapter.list.SimpleListAdapter;
import com.hwy.adapter.list.ViewHolder;
import com.hwy.dropdownmenu.adapter.SimpleDropDownAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者: hewenyu
 * 日期: 2018/11/28 12:43
 * 说明: 设置适配器
 */
public class DropMenuAdapter extends SimpleDropDownAdapter<String> {

    private List<String> mCateList;

    public DropMenuAdapter(Context context, List datas) {
        super(context, datas);
    }

    @Override
    public View getMenuView(int position, ViewGroup parent) {
        TextView tvMenu = (TextView) mInflater.inflate(R.layout.layout_drop_menu_tab, parent, false);
        tvMenu.setText(mDatas.get(position));
        return tvMenu;
    }

    @Override
    public View getDetailView(int position, ViewGroup parent) {
        View detailView = null;
        switch (position) {
            case 0: // 全部美食
                detailView = loadCatePage(position, parent);
                break;
            case 1: // 附近
                detailView = loadDetailPage(position, parent);
                break;
            case 2: // 智能排序
                detailView = loadDetailPage(position, parent);
                break;
            case 3: // 筛选
                detailView = loadDetailPage(position, parent);
                break;
        }

        return detailView;
    }

    /**
     * 通用页面
     *
     * @param position
     * @param parent
     * @return
     */
    private View loadDetailPage(int position, ViewGroup parent) {
        TextView detailView = (TextView) mInflater.inflate(R.layout.layout_drop_menu_detail, parent, false);
        detailView.setText(mDatas.get(position));
        ViewGroup.LayoutParams params = detailView.getLayoutParams();
        params.height = params.height * (position + 1);
        detailView.setLayoutParams(params);
        return detailView;
    }

    // region ---------- 全部美食 ----------

    /**
     * 美食页面
     *
     * @param position
     * @param parent
     * @return
     */
    private View loadCatePage(int position, ViewGroup parent) {
        initCateList();
        ListView listView = (ListView) mInflater.inflate(R.layout.layout_drop_menu_list, parent, false);
        listView.setAdapter(new SimpleListAdapter<String>(mContext, mCateList, R.layout.adapter_cate) {
            @Override
            public void convert(ViewHolder holder, String data, int position) {
                holder.setText(R.id.tv_cate, data);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(mContext, mCateList.get(position), Toast.LENGTH_SHORT).show();
                closeDetail();
            }
        });

        return listView;
    }

    private void initCateList() {
        mCateList = new ArrayList<>();
        mCateList.add("全部美食");
        mCateList.add("福建菜");
        mCateList.add("饮品店");
        mCateList.add("面包甜点");
        mCateList.add("生日蛋糕");
        mCateList.add("火锅");
        mCateList.add("自助餐");
        mCateList.add("小吃快餐");
        mCateList.add("日韩料理");
        mCateList.add("西餐");
        mCateList.add("生鲜蔬果");
        mCateList.add("聚餐宴请");
        mCateList.add("大闸蟹");
        mCateList.add("烧烤烤肉");
        mCateList.add("川湘菜");
        mCateList.add("江浙菜");
        mCateList.add("小龙虾");
        mCateList.add("香锅烤鱼");
        mCateList.add("粤菜");
        mCateList.add("西北菜");
    }

    // endregion ------------------------------

    @Override
    public void onMenuOpen(View menuView) {
        TextView tvMenu = (TextView) menuView;
        tvMenu.setTextColor(mContext.getResources().getColor(R.color.select_color));
    }

    @Override
    public void onMenuClose(View menuView) {
        TextView tvMenu = (TextView) menuView;
        tvMenu.setTextColor(mContext.getResources().getColor(R.color.normal_color));
    }
}

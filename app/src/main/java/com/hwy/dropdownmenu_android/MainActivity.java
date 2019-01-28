package com.hwy.dropdownmenu_android;

import android.os.Bundle;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.hwy.dropdownmenu.DropDownMenu;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DropDownMenu mDropDownMenu;

    private List<String> mDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        mDropDownMenu = findViewById(R.id.drop_down_menu);

        mDropDownMenu.setOpenAndCloseInterpolator(new LinearOutSlowInInterpolator());
        mDropDownMenu.setUpdateInterpolator(new LinearOutSlowInInterpolator());

        mDropDownMenu.setAdapter(new DropMenuAdapter(this, mDatas));

    }

    private void init() {
        mDatas = new ArrayList<>();
        mDatas.add("全部美食");
        mDatas.add("附近");
        mDatas.add("智能排序");
        mDatas.add("筛选");
    }

    public void onBtnClick(View view) {
        Toast.makeText(this, "Activity页面的按钮", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDropDownMenu.isOpen()) {
                mDropDownMenu.closeDetail();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

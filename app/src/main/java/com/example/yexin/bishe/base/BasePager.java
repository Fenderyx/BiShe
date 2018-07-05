package com.example.yexin.bishe.base;

import android.content.Context;
import android.view.View;

/**
 * Created by yexin on 2018/4/30.
 */

public abstract class BasePager {

    public final Context context;
    public final View rootView;
    public boolean isInitData = false;

    public BasePager(Context context){
        this.context = context;
        rootView = initView();
    }

    public abstract View initView();

    //子页面初始化数据   联网请求或者绑定数据时使用
    public void initData(){

    }
}

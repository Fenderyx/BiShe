package com.example.yexin.bishe.utils;

import android.app.Application;

import org.xutils.x;

/**
 * Created by yexin on 2018/5/8.
 */

public class MyApplication extends Application  {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.setDebug(false);
        x.Ext.init(this);
    }
}

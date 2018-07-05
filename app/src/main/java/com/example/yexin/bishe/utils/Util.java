package com.example.yexin.bishe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.TrafficStats;

import com.example.yexin.bishe.service.MusicService;

/**
 * Created by yexin on 2018/5/5.
 */

public class Util {

    private long lastTotalRxBytes = 0;
    private long lastTimeStamp = 0;



    public boolean isNetUri(String uri){
        boolean result = false;
        if(uri!=null){
            if (uri.toLowerCase().startsWith("http")||uri.toLowerCase().startsWith("rtsp")||uri.toLowerCase().startsWith("mms"))
                result = true;
        }
        return result;
    }

    public String getNetSpeed(Context context){
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) == TrafficStats.UNSUPPORTED ? 0 :(TrafficStats.getTotalRxBytes()/1024);//转为KB
        long nowTimeStamp = System.currentTimeMillis();
        long speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (nowTimeStamp - lastTimeStamp));//毫秒转换
        long speed2 = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 % (nowTimeStamp - lastTimeStamp));//毫秒转换

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        return String.valueOf(speed)+" kb/s";
    }

    public static void putCache(Context context,String key,String value){
        SharedPreferences preferences = context.getSharedPreferences("yexin",Context.MODE_PRIVATE);
        preferences.edit().putString(key,value).commit();
    }

    public static String getCache(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences("yexin",Context.MODE_PRIVATE);
        return preferences.getString(key,"");
    }

    public static void putPlayMode(Context context,String key,int value){
        SharedPreferences preferences = context.getSharedPreferences("yexin",Context.MODE_PRIVATE);
        preferences.edit().putInt(key,value).commit();
    }

    public static int getPlayMode(Context context,String key){
        SharedPreferences preferences = context.getSharedPreferences("yexin",Context.MODE_PRIVATE);
        return preferences.getInt(key, MusicService.MODEL_NORMAL);
    }
}

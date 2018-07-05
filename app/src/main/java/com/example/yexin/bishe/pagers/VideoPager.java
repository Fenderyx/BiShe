package com.example.yexin.bishe.pagers;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.activity.VideoPlayer;
import com.example.yexin.bishe.adapter.MediaPagerAdapter;
import com.example.yexin.bishe.base.BasePager;
import com.example.yexin.bishe.bean.MediaItem;

import java.util.ArrayList;

/**
 * Created by yexin on 2018/4/30.
 */

public class VideoPager extends BasePager {

    private ListView lv_media;
    private TextView tv_have_no_media;
    private ProgressBar pb_loading;

    private ArrayList<MediaItem> mediaItems;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mediaItems != null && mediaItems.size() > 0) {// 有数据、设置适配器\、文本隐藏
                lv_media.setAdapter(new MediaPagerAdapter(context, mediaItems, false));
                tv_have_no_media.setVisibility(View.GONE);
            } else {//没数据、文本显示
                tv_have_no_media.setVisibility(View.VISIBLE);
            }
            pb_loading.setVisibility(View.GONE);
        }
    };


    public VideoPager(Activity context) {
        super(context);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.video_pager, null);
        lv_media = (ListView) view.findViewById(R.id.lv_media);
        tv_have_no_media = (TextView) view.findViewById(R.id.tv_have_no_media);
        pb_loading = (ProgressBar) view.findViewById(R.id.pb_loading);
        lv_media.setOnItemClickListener(new VideoPagerItemClickListener());
        return view;
    }

    @Override
    public void initData() {
        //加载本地视频数据
        super.initData();
        getDataFromLocal();
    }

    /**
     * 1.遍历sdcard，根据后缀名
     * 2.从 内容提供者 中获取
     * 3.6.0以上的系统，要动态获取读取sdcard的权限
     */
    private void getDataFromLocal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //  6.0以上的系统要 动态获取权限
                //isGrantExternalRW((Activity) context);

                mediaItems = new ArrayList<MediaItem>();
                //1.获取内容解析者
                ContentResolver contentResolver = context.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard中的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//艺术家(歌曲的演唱者)
                };
                Cursor cursor = contentResolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        MediaItem mediaItem = new MediaItem(name, duration, size, data, artist);
                        mediaItems.add(mediaItem);
                    }
                    cursor.close();
                }
                //handler发消息
                handler.sendEmptyMessage(10);
            }
        }.start();
    }


    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    private class VideoPagerItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, VideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList",mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            context.startActivity(intent);

        }
    }


}

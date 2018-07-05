package com.example.yexin.bishe.pagers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.activity.VideoPlayer;
import com.example.yexin.bishe.adapter.CollectionAdapter;
import com.example.yexin.bishe.adapter.NetVideoPagerAdapter;
import com.example.yexin.bishe.base.BasePager;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.bean.MediaItemDB;
import com.example.yexin.bishe.utils.DbUtil;
import com.example.yexin.bishe.utils.LogUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by yexin on 2018/4/30.
 */

public class NetMusicPager extends BasePager {


    private ListView lv_media_coll;
    private TextView tv_have_no_coll;
    private ProgressBar pb_loading_coll;

    private DbUtil dbUtil;

    private ArrayList<MediaItemDB> mediaItemDBs;
    private ArrayList<MediaItem> mediaItems;
    private CollectionAdapter adapter;


    public NetMusicPager(Activity context) {
        super(context);
    }

    @Override
    public View initView() {
        dbUtil = new DbUtil();
        mediaItemDBs = new ArrayList<MediaItemDB>();
        mediaItems = new ArrayList<MediaItem>();
        View view = View.inflate(context, R.layout.collection, null);
        lv_media_coll = (ListView) view.findViewById(R.id.lv_media_coll);
        lv_media_coll.setOnItemClickListener(new MyItemClick());
        lv_media_coll.setOnItemLongClickListener(new MyItemLongClick());
        tv_have_no_coll = (TextView) view.findViewById(R.id.tv_have_no_coll);
        pb_loading_coll = (ProgressBar) view.findViewById(R.id.pb_loading_coll);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        mediaItemDBs = dbUtil.find();
        if (mediaItemDBs != null) {
            if (mediaItemDBs.size() > 0){
                //收藏有东西  -  设置适配器 - 设置gone
                adapter = new CollectionAdapter(context,mediaItemDBs);
                lv_media_coll.setAdapter(adapter);
                tv_have_no_coll.setVisibility(GONE);
            }else{
                //没有收藏任何视频  -  设置visiable
                tv_have_no_coll.setVisibility(View.VISIBLE);
            }
            pb_loading_coll.setVisibility(GONE);
        }
    }

    private class MyItemLongClick implements AdapterView.OnItemLongClickListener{

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
            //返回值问题：false click仍会调用  true click不会调用
            new AlertDialog.Builder(context)
                    .setTitle("删除")
                    .setMessage("您是否要删除此条收藏")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dbUtil.delete(mediaItemDBs.get(position).getVideoId());
                            initData();
                        }
                    })
                    .setNegativeButton("取消",null)
                    .create().show();
            return true;
        }
    }

    private class MyItemClick implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            getMediaItems();
            Intent intent = new Intent(context, VideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList", mediaItems);
            intent.putExtras(bundle);
            intent.putExtra("position", position);
            context.startActivity(intent);
        }
    }

    private void getMediaItems() {
        for (int i = 0; i < mediaItemDBs.size(); i++) {
            String id = mediaItemDBs.get(i).getVideoId();
            String name = mediaItemDBs.get(i).getVideoName();
            String url = mediaItemDBs.get(i).getHightUrl();
            String title = mediaItemDBs.get(i).getVideoTitle();
            String imgUrl = mediaItemDBs.get(i).getCoverImg();
            mediaItems.add(new MediaItem(id,name,url,title,imgUrl));
        }
    }
}

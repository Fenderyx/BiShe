package com.example.yexin.bishe.pagers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.activity.VideoPlayer;
import com.example.yexin.bishe.adapter.NetVideoPagerAdapter;
import com.example.yexin.bishe.base.BasePager;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.utils.Constants;
import com.example.yexin.bishe.utils.LogUtil;
import com.example.yexin.bishe.utils.StringToTime;
import com.example.yexin.bishe.utils.Util;
import com.example.yexin.bishe.view.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by yexin on 2018/4/30.
 */

public class NetVideoPager extends BasePager {

    @ViewInject(R.id.lv_media_net)
    private XListView lv_media_net;
    @ViewInject(R.id.tv_have_no_net)
    private TextView tv_have_no_net;
    @ViewInject(R.id.pb_loading_net)
    private ProgressBar pb_loading_net;

    private StringToTime stringToTime;

    private ArrayList<MediaItem> mediaItems;
    private boolean isLoadMore = false;
    private NetVideoPagerAdapter netVideoPagerAdapter;

    public NetVideoPager(Activity context) {
        super(context);
    }

    @Override
    public View initView() {
        stringToTime = new StringToTime();
        View view = View.inflate(context, R.layout.net_video_pager, null);
        x.view().inject(NetVideoPager.this, view);
        lv_media_net.setOnItemClickListener(new MyItemClickListener());
        lv_media_net.setPullLoadEnable(true);
        lv_media_net.setXListViewListener(new MyXListViewListener());
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        String cacheJson = Util.getCache(context,Constants.NET_VIDEO_URI);
        if (!TextUtils.isEmpty(cacheJson)){
            //有缓存
            jsonAndAdapter(cacheJson);
        }
        //联网请求
        getDataFromNet();
    }

    private void getDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URI);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Util.putCache(context,Constants.NET_VIDEO_URI,result);
                jsonAndAdapter(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.i("onError:" + ex.getMessage());
                showData();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.i("onCancelled");
            }

            @Override
            public void onFinished() {
                LogUtil.i("onFinished");
            }
        });
    }

    private void jsonAndAdapter(String result) {
        if (!isLoadMore) {
            mediaItems = dealWithJSON(result);
            //如果 mediaItems 符合条件就设置适配器（listview）
            showData();
        } else {
            //loadmore
            isLoadMore = false;
            mediaItems.addAll(dealWithJSON(result));
            netVideoPagerAdapter.notifyDataSetChanged();
            onLoad();
        }
    }

    private void showData() {
        if (mediaItems != null && mediaItems.size() > 0) {// 有数据、设置适配器\、文本隐藏
            netVideoPagerAdapter = new NetVideoPagerAdapter(context, mediaItems);
            lv_media_net.setAdapter(netVideoPagerAdapter);
            tv_have_no_net.setVisibility(View.GONE);
            onLoad();
        } else {//没数据、文本显示
            tv_have_no_net.setVisibility(View.VISIBLE);
        }
        pb_loading_net.setVisibility(View.GONE);
    }

    private void onLoad() {
        lv_media_net.stopRefresh();
        lv_media_net.stopLoadMore();
        lv_media_net.setRefreshTime(stringToTime.toTimeByDate(new Date()));
    }

    private ArrayList<MediaItem> dealWithJSON(String json) {
        ArrayList<MediaItem> mediaItems = new ArrayList<MediaItem>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.optJSONArray("trailers");
            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonItem = (JSONObject) jsonArray.get(i);
                    if (jsonItem != null) {
                        String movieName = jsonItem.optString("movieName");
                        String hightUrl = jsonItem.optString("hightUrl");
                        String videoTitle = jsonItem.optString("videoTitle");
                        String imageUrl = jsonItem.optString("coverImg");
                        String id = jsonItem.optString("id");
                        mediaItems.add(new MediaItem(id,movieName, hightUrl, videoTitle, imageUrl));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mediaItems;
    }

    private class MyItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(context, VideoPlayer.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("videoList", mediaItems);
            intent.putExtras(bundle);
            //减一是因为listview中加了个头
            intent.putExtra("position", position - 1);
            context.startActivity(intent);
        }
    }

    private class MyXListViewListener implements XListView.IXListViewListener {
        @Override
        public void onRefresh() {
            getDataFromNet();
        }

        @Override
        public void onLoadMore() {
            getMoreDataFromNet();
        }
    }

    private void getMoreDataFromNet() {
        RequestParams params = new RequestParams(Constants.NET_VIDEO_URI);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                isLoadMore = true;
                jsonAndAdapter(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                isLoadMore = false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.i("onCancelled");
                isLoadMore = false;
            }

            @Override
            public void onFinished() {
                LogUtil.i("onFinished");
                isLoadMore = false;
            }
        });
    }
}

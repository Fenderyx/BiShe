package com.example.yexin.bishe.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.utils.StringToTime;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yexin on 2018/5/2.
 */

public class NetVideoPagerAdapter extends BaseAdapter {

    private ArrayList<MediaItem> mediaItems;
    private Context context;
    private StringToTime stringToTime = new StringToTime();

    public NetVideoPagerAdapter(Context context, ArrayList<MediaItem> mediaItems) {
        this.context = context;
        this.mediaItems = mediaItems;
    }

    @Override
    public int getCount() {
        return mediaItems.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.net_video_item, null);
            viewHolder = new ViewHolder();
            viewHolder.iv_net_video = (ImageView) convertView.findViewById(R.id.iv_net_video);
            viewHolder.net_video_name = (TextView) convertView.findViewById(R.id.net_video_name);
            viewHolder.net_video_title = (TextView) convertView.findViewById(R.id.net_video_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.net_video_name.setText( mediaItem.getName());
        viewHolder.net_video_title.setText(mediaItem.getVideoTitle());
        x.image().bind(viewHolder.iv_net_video,mediaItem.getImageUrl());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_net_video;
        TextView net_video_name;
        TextView net_video_title;

    }

}

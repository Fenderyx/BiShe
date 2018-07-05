package com.example.yexin.bishe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.bean.MediaItemDB;

import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yexin on 2018/5/24.
 */

public class CollectionAdapter extends BaseAdapter {

    private final ArrayList<MediaItemDB> mediaItemDBs;
    private final Context context;

    public CollectionAdapter(Context context, ArrayList<MediaItemDB> mediaItemDBs) {
        this.context = context;
        this.mediaItemDBs = mediaItemDBs;
    }

    @Override
    public int getCount() {
        return mediaItemDBs.size();
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
        NetVideoPagerAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.net_video_item, null);
            viewHolder = new NetVideoPagerAdapter.ViewHolder();
            viewHolder.iv_net_video = (ImageView) convertView.findViewById(R.id.iv_net_video);
            viewHolder.net_video_name = (TextView) convertView.findViewById(R.id.net_video_name);
            viewHolder.net_video_title = (TextView) convertView.findViewById(R.id.net_video_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (NetVideoPagerAdapter.ViewHolder) convertView.getTag();
        }
        MediaItemDB mediaItemDB = mediaItemDBs.get(position);
        viewHolder.net_video_name.setText( mediaItemDB.getVideoName());
        viewHolder.net_video_title.setText(mediaItemDB.getVideoTitle());
        x.image().bind(viewHolder.iv_net_video,mediaItemDB.getCoverImg());
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_net_video;
        TextView net_video_name;
        TextView net_video_title;

    }
}

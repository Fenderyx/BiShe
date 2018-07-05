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

import java.util.ArrayList;

/**
 * Created by yexin on 2018/5/2.
 */

public class MediaPagerAdapter extends BaseAdapter {

    private ArrayList<MediaItem> mediaItems;
    private Context context;
    private StringToTime stringToTime = new StringToTime();
    private boolean isMusic;

    public MediaPagerAdapter(Context context, ArrayList<MediaItem> mediaItems, boolean isMusic) {
        this.context = context;
        this.mediaItems = mediaItems;
        this.isMusic = isMusic;
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
            convertView = View.inflate(context, R.layout.video_item, null);
            viewHolder = new ViewHolder();
            viewHolder.video_name = (TextView) convertView.findViewById(R.id.video_name);
            viewHolder.video_time = (TextView) convertView.findViewById(R.id.video_time);
            viewHolder.video_size = (TextView) convertView.findViewById(R.id.video_size);
            viewHolder.iv_default_video = (ImageView) convertView.findViewById(R.id.iv_default_video);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MediaItem mediaItem = mediaItems.get(position);
        viewHolder.video_name.setText(mediaItem.getName());
        viewHolder.video_time.setText(stringToTime.toTimeByInt((int) mediaItem.getDuration()));
        viewHolder.video_size.setText(Formatter.formatFileSize(context, mediaItem.getSize()));
        if (isMusic) {
            //音乐
            viewHolder.iv_default_video.setImageResource(R.drawable.music_default_icon);
        } else {
            //视频
            viewHolder.iv_default_video.setImageResource(R.drawable.video_default_icon);
        }
        return convertView;
    }

    static class ViewHolder {
        ImageView iv_default_video;
        TextView video_name;
        TextView video_time;
        TextView video_size;
    }

}

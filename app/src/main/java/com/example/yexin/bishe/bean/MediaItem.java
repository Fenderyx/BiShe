package com.example.yexin.bishe.bean;

import java.io.Serializable;

/**
 * Created by yexin on 2018/5/2.
 */

public class MediaItem implements Serializable {
    private String id;
    private String name;
    private long duration;
    private long size;
    private String data;
    private String artist;
    private String videoTitle;
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MediaItem(String id, String movieName, String hightUrl, String videoTitle, String imageUrl) {
        this.id = id;
        this.name = movieName;
        this.data = hightUrl;
        this.videoTitle = videoTitle;
        this.imageUrl = imageUrl;
    }

    public MediaItem(String name, long duration, long size, String data, String artist) {
        this.name = name;
        this.duration = duration;
        this.size = size;
        this.data = data;
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "MeidaItem{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }
}

package com.example.yexin.bishe.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

import static android.R.attr.name;

/**
 * Created by yexin on 2018/5/2.
 */

@Table(name = "mediaitemdb")
public class MediaItemDB {
    @Column(name = "vid",isId = true)
    private String videoId;
    @Column(name = "vname")
    private String videoName;
    @Column(name = "vtitle")
    private String videoTitle;
    @Column(name = "vurl")
    private String hightUrl;
    @Column(name = "vimg")
    private String coverImg;

    public MediaItemDB(){

    }
    public MediaItemDB(String videoId, String videoName, String videoTitle, String hightUrl, String coverImg) {
        this.videoId = videoId;
        this.videoName = videoName;
        this.videoTitle = videoTitle;
        this.hightUrl = hightUrl;
        this.coverImg = coverImg;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getHightUrl() {
        return hightUrl;
    }

    public void setHightUrl(String hightUrl) {
        this.hightUrl = hightUrl;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    @Override
    public String toString() {
        return "MediaItemDB{" +
                "videoId='" + videoId + '\'' +
                ", videoName='" + videoName + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", hightUrl='" + hightUrl + '\'' +
                ", coverImg='" + coverImg + '\'' +
                '}';
    }
}

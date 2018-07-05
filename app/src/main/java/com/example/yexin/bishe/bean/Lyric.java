package com.example.yexin.bishe.bean;

/**
 * Created by yexin on 2018/5/10.
 */

public class Lyric {
    private String content;
    private long timePoint;
    private long lightTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getLightTime() {
        return lightTime;
    }

    public void setLightTime(long lightTime) {
        this.lightTime = lightTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", lightTime=" + lightTime +
                '}';
    }
}

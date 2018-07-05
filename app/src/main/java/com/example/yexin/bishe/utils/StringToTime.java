package com.example.yexin.bishe.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.TimeZone;

/**
 * Created by yexin on 2018/5/2.
 */

public class StringToTime {
    SimpleDateFormat simpleDateFormat;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;

    public StringToTime() {
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter();
    }

    public String toTime(int time) {
        int totalsecond = time / 1000;
        int second = totalsecond % 60;
        int minute = (totalsecond / 60) % 60;
        int hour = (totalsecond / 3600);
        mFormatBuilder.setLength(0);

        if (hour > 0) {
            return mFormatter.format("%02d:%02d:%02d", hour, minute, second).toString();
        } else {
            return mFormatter.format("%02d:%02d", minute, second).toString();
        }
    }

    public String toTimeByInt(int time) {
        int totalsecond = time / 1000;
        int hour = (totalsecond / 3600);
        if (hour > 0) {
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        } else {
            simpleDateFormat = new SimpleDateFormat("mm:ss");
        }
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return simpleDateFormat.format(new Date(time));
    }

    public String toTimeByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(date);
    }


    //02:02.02
    public long strTime2LongTime(String strTime) {
        long result = -1;
        try {
            String[] s1 = strTime.split(":");
            String[] s2 = s1[1].split("\\.");
            long m = Long.parseLong(s1[0]);
            long s = Long.parseLong(s2[0]);
            long ms = Long.parseLong(s2[1]);
            result = m * 60 * 1000 + s * 1000 + ms * 10;
        } catch (Exception e) {
            e.printStackTrace();
            result = -1;
        }
        return result;
    }
}

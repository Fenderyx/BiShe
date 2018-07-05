package com.example.yexin.bishe.utils;

import com.example.yexin.bishe.bean.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by yexin on 2018/5/11.
 */

public class LyricUtil {

    private ArrayList<Lyric> lyrics;
    private boolean isLyricExists = false;

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }

    public boolean isLyricExists() {
        return isLyricExists;
    }

    private StringToTime stringToTime;

    public void readLyricFile(File file) {
        if (file == null || !file.exists()) {
            //歌词文件不存在
            lyrics = null;
            stringToTime = null;
            isLyricExists = false;
        } else {
            lyrics = new ArrayList<>();
            stringToTime = new StringToTime();
            isLyricExists = true;
            //1、解析歌词---- 一行的读取，解析
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), getCharset(file)));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    line = analyzeLyric(line);
                }
                reader.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //2、排序
            Collections.sort(lyrics, new Comparator<Lyric>() {
                @Override
                public int compare(Lyric o1, Lyric o2) {
                    if (o1.getTimePoint() > o2.getTimePoint()) {
                        return 1;
                    } else if (o1.getTimePoint() < o2.getTimePoint()) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });
            //3、计算每句的高亮时间
            for (int i = 0; i < lyrics.size(); i++) {
                Lyric oneLyric = lyrics.get(i);
                if (i + 1 < lyrics.size()) {
                    Lyric twoLyric = lyrics.get(i + 1);
                    oneLyric.setLightTime(twoLyric.getTimePoint() - oneLyric.getTimePoint());
                }
            }
        }
    }

    public String getCharset(File file) {
        String charset = "GBK";
        byte[] first3Bytes = new byte[3];
        try {
            boolean checked = false;
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            bis.mark(0);
            int read = bis.read(first3Bytes, 0, 3);
            if (read == -1)
                return charset;
            if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
                charset = "UTF-16LE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xFE
                    && first3Bytes[1] == (byte) 0xFF) {
                charset = "UTF-16BE";
                checked = true;
            } else if (first3Bytes[0] == (byte) 0xEF
                    && first3Bytes[1] == (byte) 0xBB
                    && first3Bytes[2] == (byte) 0xBF) {
                charset = "UTF-8";
                checked = true;
            }
            bis.reset();
            if (!checked) {
                int loc = 0;
                while ((read = bis.read()) != -1) {
                    loc++;
                    if (read >= 0xF0)
                        break;
                    if (0x80 <= read && read <= 0xBF)
                        break;
                    if (0xC0 <= read && read <= 0xDF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF)
                            continue;
                        else
                            break;
                    } else if (0xE0 <= read && read <= 0xEF) {
                        read = bis.read();
                        if (0x80 <= read && read <= 0xBF) {
                            read = bis.read();
                            if (0x80 <= read && read <= 0xBF) {
                                charset = "UTF-8";
                                break;
                            } else
                                break;
                        } else
                            break;
                    }
                }
            }
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return charset;
    }

    //[02:20.22][][]一句歌词
    //解析一句歌词
    private String analyzeLyric(String line) {
        int pos1 = line.indexOf("["); //0 ，如果没有返回-1
        int pos2 = line.indexOf("]");
        if (pos1 == 0 && pos2 != -1) {//至少有一句歌词
            //封装时间
            long[] times = new long[getCountTag(line)];
            String strTime = line.substring(pos1 + 1, pos2);
            times[0] = stringToTime.strTime2LongTime(strTime);
            String content = line;
            int i = 1;
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2 + 1);
                pos1 = content.indexOf("[");
                pos2 = content.indexOf("]");
                if (pos2 != -1) {//还有时间戳
                    strTime = content.substring(pos1 + 1, pos2);
                    times[i] = stringToTime.strTime2LongTime(strTime);
                    if (times[i] == -1) {
                        return "";
                    }
                    i++;
                }
            }
            Lyric lyric = new Lyric();
            for (int j = 0; j < times.length; j++) {
                if (times[j] != 0) {//有时间戳
                    lyric.setContent(content);
                    lyric.setTimePoint(times[j]);
                    lyrics.add(lyric);
                    lyric = new Lyric();
                }
            }
            return content;
        }

        return "";
    }

    //判断有几句歌词
    private int getCountTag(String line) {
        int result = -1;
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");
        if (left.length == 0 && right.length == 0) {
            result = 1;
        } else if (left.length > right.length) {
            result = left.length;
        } else {
            result = right.length;
        }
        return result;
    }

}

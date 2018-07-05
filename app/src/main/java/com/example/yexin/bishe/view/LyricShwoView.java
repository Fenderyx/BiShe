package com.example.yexin.bishe.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.yexin.bishe.bean.Lyric;
import com.example.yexin.bishe.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by yexin on 2018/5/10.
 */

@SuppressLint("AppCompatCustomView")
public class LyricShwoView extends TextView {

    private ArrayList<Lyric> lyrics;
    private Paint paint;
    private Paint paintWhite;

    //歌词列表中的索引， 是第几句歌词
    private int index;
    //歌词行高
    private float textHeight;
    private float currentProgress;
    private float lightTime;
    private float timePoint;

    private int height;
    private int width;

    public LyricShwoView(Context context) {
        this(context, null);
    }

    public LyricShwoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LyricShwoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void setLyrics(ArrayList<Lyric> lyrics) {
        this.lyrics = lyrics;
    }

    private void initView(Context context) {
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(DensityUtil.dip2px(context, 15));
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);

        paintWhite = new Paint();
        paintWhite.setColor(Color.WHITE);
        paintWhite.setTextSize(DensityUtil.dip2px(context, 15));
        paintWhite.setAntiAlias(true);
        paintWhite.setTextAlign(Paint.Align.CENTER);

        textHeight = DensityUtil.dip2px(context, 20);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyrics != null && lyrics.size() > 0) {
            //歌词向上推移
            float plush = 0;
            if (lightTime == 0) {
                plush = 0;
            } else {
                //  本句时间 : 本句高亮时间 = 本句移动距离 : 行高
                float detla = ((currentProgress - timePoint) / lightTime) * textHeight;
                //屏幕上的坐标 = 行高 + 移动距离
                plush = textHeight + detla;
            }
            canvas.translate(0, -plush);

            //当前
            String currentLyric = lyrics.get(index).getContent();
            canvas.drawText(currentLyric, width / 2, height / 2, paint);
            //Y轴的中间坐标
            float tempY = height / 2;
            //前面
            for (int i = index - 1; i >= 0; i--) {
                String preLyric = lyrics.get(i).getContent();
                tempY = tempY - textHeight;
                if (tempY < 0) {
                    break;
                }
                canvas.drawText(preLyric, width / 2, tempY, paintWhite);
            }
            //后面
            tempY = height / 2;
            for (int i = index + 1; i < lyrics.size(); i++) {
                String nextLyric = lyrics.get(i).getContent();
                tempY = tempY + textHeight;
                if (tempY > getHeight()) {
                    break;
                }
                canvas.drawText(nextLyric, width / 2, tempY, paintWhite);
            }
        } else {
            canvas.drawText("没有发现歌词", getWidth() / 2, getHeight() / 2, paint);
        }
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
        if (lyrics == null || lyrics.size() == 0)
            return;
        for (int i = 1; i < lyrics.size(); i++) {
            if (currentProgress < lyrics.get(i).getTimePoint()) {
                int tempIndex = i - 1;
                if (currentProgress >= lyrics.get(tempIndex).getTimePoint()) {
                    //当前歌词
                    index = tempIndex;
                    lightTime = lyrics.get(index).getLightTime();
                    timePoint = lyrics.get(index).getTimePoint();
                }
            }
        }
        //重新绘制
        invalidate();//主线程中执行  子线程-postInvalidate();

    }
}

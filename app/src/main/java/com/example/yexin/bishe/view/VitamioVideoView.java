package com.example.yexin.bishe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

import io.vov.vitamio.widget.*;

/**
 * Created by yexin on 2018/5/4.
 */

public class VitamioVideoView extends io.vov.vitamio.widget.VideoView {

    public VitamioVideoView(Context context) {
        this(context,null);
    }

    public VitamioVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VitamioVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec,heightMeasureSpec);
    }

    public void setVideoSize(int width, int height){
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        setLayoutParams(layoutParams);
    }

}

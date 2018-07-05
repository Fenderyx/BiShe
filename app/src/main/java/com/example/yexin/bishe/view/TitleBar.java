package com.example.yexin.bishe.view;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.activity.Search;

/**
 * Created by yexin on 2018/5/2.
 */

public class TitleBar extends LinearLayout implements View.OnClickListener {

    private View tv_search;
    private View rl_game;
    private View iv_record;
    private Context context;

    //代码 实例类
    public TitleBar(Context context) {
        this(context, null);
    }

    //布局 实例类 反射的方式
    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //需要设置样式时
    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        tv_search = getChildAt(0);
        rl_game = getChildAt(1);
        iv_record = getChildAt(2);

        tv_search.setOnClickListener(this);
        rl_game.setOnClickListener(this);
        iv_record.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_search:
                Intent intent = new Intent(context, Search.class);
                context.startActivity(intent);
                break;
            case R.id.rl_game:
                Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_record:
                Toast.makeText(context, "3", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

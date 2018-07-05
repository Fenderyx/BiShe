package com.example.yexin.bishe.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.yexin.bishe.R;

public class Search extends AppCompatActivity implements View.OnClickListener {

    private EditText mEt_input;
    private ImageView mIv_voice;
    private TextView mTv_search;
    private ListView mLv_search;
    private ProgressBar mProgress;
    private TextView mTv_nodata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bindViews();
    }

    // Content View Elements



    // End Of Content View Elements

    private void bindViews() {

        mEt_input = (EditText) findViewById(R.id.et_input);
        mIv_voice = (ImageView) findViewById(R.id.iv_voice);
        mTv_search = (TextView) findViewById(R.id.tv_search);
        mLv_search = (ListView) findViewById(R.id.lv_search);
        mProgress = (ProgressBar) findViewById(R.id.progress);
        mTv_nodata = (TextView) findViewById(R.id.tv_nodata);

        mIv_voice.setOnClickListener(this);
        mTv_search.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == mIv_voice){
            //语音输入
        }else if (v == mTv_search){
            //搜索
        }
    }
}

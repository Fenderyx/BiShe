package com.example.yexin.bishe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yexin.bishe.R;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.utils.LogUtil;
import com.example.yexin.bishe.utils.StringToTime;
import com.example.yexin.bishe.utils.Util;
import com.example.yexin.bishe.view.VitamioVideoView;

import java.util.ArrayList;
import java.util.Date;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class VitamioVideoPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRASS = 1;
    private static final int HIDE_CONTROLLER = 2;
    private static final int SHOW_NETSPEED = 3;
    private VitamioVideoView videoView;
    private Uri uri;
    private StringToTime stringToTime;
    private BatteryReceiver batteryReceiver;
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private GestureDetector detector;
    private boolean isShowController = true;

    private Util util;

    private int preCurrentPosition;
    private boolean isUseSystemListener = true;

    private boolean isFull = false;
    private int screenWidth;
    private int screenHeight;
    private int mVideoWidth;
    private int mVideoHeight;

    private boolean isNetUri;

    private boolean isMute;
    private AudioManager audioManager;
    private int currentVoice;
    private int maxVoice;

    private float startY;
    private float endY;
    private int hdVoice;


    private RelativeLayout rl_controller;
    private LinearLayout mController_top;
    private TextView mTv_controller_video_name;
    private ImageView mIv_controller_battery;
    private TextView mTv_controller_time;
    private Button mBtn_controller_voice;
    private SeekBar mController_seekbar_video_vioce;
    private Button mBtn_collection;
    private LinearLayout mController_bottom;
    private TextView mTv_current_prograss;
    private SeekBar mController_seekbar_video_prograss;
    private TextView mTv_total_prograss;
    private Button mBtn_controller_exit;
    private Button mBtn_controller_video_pre;
    private Button mBtn_controller_video_start_pause;
    private Button mBtn_controller_video_next;
    private Button mBtn_controller_video_siwch_screen;
    private LinearLayout ll_buffer;
    private TextView tv_buffer_net_speed;
    private TextView tv_loading_net_speed;
    private LinearLayout ll_loading;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRASS:
                    handler_PROGRASS();
                    // 3. 每秒更新一次
                    handler.removeMessages(PROGRASS);
                    handler.sendEmptyMessageDelayed(PROGRASS, 1000);
                    break;
                case HIDE_CONTROLLER:
                    rl_controller.setVisibility(View.GONE);
                    isShowController = false;
                    break;
                case SHOW_NETSPEED:
                    handler_SHOW_NETSPEED();
                    handler.removeMessages(SHOW_NETSPEED);
                    handler.sendEmptyMessageDelayed(SHOW_NETSPEED, 2000);
                    break;
            }
        }
    };

    private void handler_PROGRASS() {
        // 1.得到当前的视频播放进程
        int currentPosition = (int) videoView.getCurrentPosition();
        // 2.seekbar等
        mController_seekbar_video_prograss.setProgress(currentPosition);
        mTv_current_prograss.setText(stringToTime.toTimeByInt(currentPosition));

        mTv_controller_time.setText(stringToTime.toTimeByDate(new Date()));

        //缓冲进度的更新
        if (isNetUri) {
            int bufferPercentage = videoView.getBufferPercentage();
            int i = bufferPercentage * mController_seekbar_video_prograss.getMax();
            int hcprograss = i / 100;
            mController_seekbar_video_prograss.setProgress(hcprograss);
        } else {
            mController_seekbar_video_prograss.setProgress(0);
        }

        //监听视频卡顿
        if (!isUseSystemListener) {
            if (videoView.isPlaying()) {
                if ((currentPosition - preCurrentPosition) < 500) {
                    ll_buffer.setVisibility(View.VISIBLE);
                } else {
                    ll_buffer.setVisibility(View.GONE);
                }
            } else
                ll_buffer.setVisibility(View.GONE);
        }

        preCurrentPosition = currentPosition;
    }

    private void handler_SHOW_NETSPEED() {
        String netSpeed = util.getNetSpeed(VitamioVideoPlayer.this);

        tv_loading_net_speed.setText("加载中..." + netSpeed);
        tv_buffer_net_speed.setText("您的网速过慢，请暂停缓冲一下..." + netSpeed);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.initialize(this);
        setContentView(R.layout.activity_vitamio_video_player);
        findViewById();
        init();
        setListener();
        registerBattryBroadcast();
        getAndSetVideoUri();
        //设置控制面板 系统的
//        videoView.setMediaController(new MediaController(this));

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getY();
                hdVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                handler.removeMessages(PROGRASS);
                break;
            case MotionEvent.ACTION_MOVE:
                endY = event.getY();
                float changeVoice = ((startY - endY) / screenHeight) * maxVoice;
                int finalVoice = (int) Math.min(Math.max((changeVoice + hdVoice), 0), maxVoice);
                if (changeVoice != 0) {
                    isMute = true;
                    setVoice(finalVoice, isMute);
                }
                break;
            case MotionEvent.ACTION_UP:
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            currentVoice++;
            setVoice(currentVoice, true);
            handler.removeMessages(PROGRASS);
            handler.sendEmptyMessageDelayed(PROGRASS, 5000);
            return true;//这里的true-不显示系统音量变化 false-显示
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVoice--;
            setVoice(currentVoice, true);
            handler.removeMessages(PROGRASS);
            handler.sendEmptyMessageDelayed(PROGRASS, 5000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        //释放资源时，先释放子类，在释放父类
        if (batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            batteryReceiver = null;
        }

        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_controller_voice:
                setVoice(currentVoice, isMute);
                isMute = !isMute;
                break;
            case R.id.btn_collection:
                break;
            case R.id.btn_controller_exit:
                finish();
                break;
            case R.id.btn_controller_video_pre:
                playPre();
                break;
            case R.id.btn_controller_video_start_pause:
                playAndPause();
                break;
            case R.id.btn_controller_video_next:
                playNext();
                break;
            case R.id.btn_controller_video_siwch_screen:
                setFullAndDefault(isFull);
                break;
        }
        handler.removeMessages(HIDE_CONTROLLER);
        handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
    }

    @SuppressWarnings("unchecked")
    private void getAndSetVideoUri() {
        //get
        uri = getIntent().getData();

        mediaItems = (ArrayList<MediaItem>) getIntent().getSerializableExtra("videoList");
        position = getIntent().getIntExtra("position", 0);
        //set
        if (mediaItems != null && mediaItems.size() > 0) {

            MediaItem mediaItem = mediaItems.get(position);
            LogUtil.i(mediaItem.getData()+"media");
            isNetUri = util.isNetUri(mediaItem.getData());
            mTv_controller_video_name.setText(mediaItem.getName());
            videoView.setVideoPath(mediaItem.getData());
        } else if (uri != null) {
            LogUtil.i(uri.toString()+"uri");
            isNetUri = util.isNetUri(uri.toString());
            mTv_controller_video_name.setText(uri.toString());
            videoView.setVideoURI(uri);
        } else {
            //没有数据
            Toast.makeText(this, "无数据", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerBattryBroadcast() {
        batteryReceiver = new BatteryReceiver();
        IntentFilter intentFilter = new IntentFilter();
        //当电量变化
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReceiver, intentFilter);
    }

    private void playPre() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position--;
            if (position >= 0) {
                MediaItem mediaItem = mediaItems.get(position);
                mTv_controller_video_name.setText(mediaItem.getName());
                isNetUri = util.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());
            } else {
                //第一个
                position++; //position回到原来的“位置”
                Toast.makeText(this, "已经是第一个了", Toast.LENGTH_SHORT).show();
            }
        } else if (uri != null) {
            //上一个和下一个按钮设置灰色并且不可以点击
            setButton();
        }
    }

    private void playNext() {
        if (mediaItems != null && mediaItems.size() > 0) {
            position++;
            if (position < mediaItems.size()) {
                MediaItem mediaItem = mediaItems.get(position);
                mTv_controller_video_name.setText(mediaItem.getName());
                isNetUri = util.isNetUri(mediaItem.getData());
                videoView.setVideoPath(mediaItem.getData());
                Log.i("info", "position 1:" + position + "");
            } else {
                //最后一个
                position--; //position回到原来的“位置”
                Toast.makeText(this, "已经是最后一个了", Toast.LENGTH_SHORT).show();
                Log.i("info", "position 2:" + position + "");
            }
        } else if (uri != null) {
            //上一个和下一个按钮设置灰色并且不可以点击
            setButton();
        }
    }

    private void setButton() {
        if (mediaItems != null && mediaItems.size() > 0) {
            if (mediaItems.size() == 1) {
                mBtn_controller_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                mBtn_controller_video_pre.setEnabled(false);
                mBtn_controller_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                mBtn_controller_video_next.setEnabled(false);
            } else {
                if (position == 0) {
                    mBtn_controller_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
                    mBtn_controller_video_pre.setEnabled(false);
                } else if (position == mediaItems.size() - 1) {
                    mBtn_controller_video_next.setBackgroundResource(R.drawable.btn_next_gray);
                    mBtn_controller_video_next.setEnabled(false);
                } else {
                    mBtn_controller_video_pre.setBackgroundResource(R.drawable.btn_video_pre_selector);
                    mBtn_controller_video_pre.setEnabled(true);

                    mBtn_controller_video_next.setBackgroundResource(R.drawable.btn_video_next_selector);
                    mBtn_controller_video_next.setEnabled(true);
                }
            }
        } else if (uri != null) {
            mBtn_controller_video_pre.setBackgroundResource(R.drawable.btn_pre_gray);
            mBtn_controller_video_pre.setEnabled(false);
            mBtn_controller_video_next.setBackgroundResource(R.drawable.btn_next_gray);
            mBtn_controller_video_next.setEnabled(false);
        }
    }

    private void setListener() {
        //准备好监听
        videoView.setOnPreparedListener(new MyPreparedListener());
        //播放出错监听
        videoView.setOnErrorListener(new MyErrorListener());
        //播放完成监听
        videoView.setOnCompletionListener(new MyCompletionListener());

        mController_seekbar_video_prograss.setOnSeekBarChangeListener(new MyVideoSeekBarChangeListener());

        mController_seekbar_video_vioce.setOnSeekBarChangeListener(new MyVioceSeekBarChangeListener());

        if (isUseSystemListener) {
            //监听视频播放卡
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                videoView.setOnInfoListener(new MyOnInfoListener());
            }
        }

    }

    private void playAndPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
            mBtn_controller_video_start_pause.setBackgroundResource(R.drawable.btn_video_start_selector);

        } else {
            videoView.start();
            mBtn_controller_video_start_pause.setBackgroundResource(R.drawable.btn_video_pause_selector);
        }
    }

    private void setFullAndDefault(boolean isFull) {
        if (isFull) {
            //设置非全屏
            int width = screenWidth;
            int height = screenHeight;
            if (mVideoWidth * height < width * mVideoHeight) {
                width = height * mVideoWidth / mVideoHeight;
            } else if (mVideoWidth * height > width * mVideoHeight) {
                height = width * mVideoHeight / mVideoWidth;
            }
            videoView.setVideoSize(width, height);
            mBtn_controller_video_siwch_screen.setBackgroundResource(R.drawable.btn_video_siwch_screen_full_selector);
            this.isFull = false;
        } else {
            //设置全屏
            videoView.setVideoSize(screenWidth, screenWidth);
            mBtn_controller_video_siwch_screen.setBackgroundResource(R.drawable.btn_video_siwch_screen_default_selector);
            this.isFull = true;
        }
    }

    private void init() {
        util = new Util();
        stringToTime = new StringToTime();
        detector = new GestureDetector(this, new MyGestureDetectorListener());

        handler.sendEmptyMessage(SHOW_NETSPEED);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVoice = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVoice = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mController_seekbar_video_vioce.setMax(maxVoice);
        mController_seekbar_video_vioce.setProgress(currentVoice);
    }

    private void findViewById() {
        rl_controller = (RelativeLayout) findViewById(R.id.rl_controller);
        videoView = (VitamioVideoView) findViewById(R.id.videoview);
        mController_top = (LinearLayout) findViewById(R.id.controller_top);
        mTv_controller_video_name = (TextView) findViewById(R.id.tv_controller_video_name);
        mIv_controller_battery = (ImageView) findViewById(R.id.iv_controller_battery);
        mTv_controller_time = (TextView) findViewById(R.id.tv_controller_time);
        mBtn_controller_voice = (Button) findViewById(R.id.btn_controller_voice);
        mController_seekbar_video_vioce = (SeekBar) findViewById(R.id.controller_seekbar_video_vioce);
        mBtn_collection = (Button) findViewById(R.id.btn_collection);
        mController_bottom = (LinearLayout) findViewById(R.id.controller_bottom);
        mTv_current_prograss = (TextView) findViewById(R.id.tv_current_prograss);
        mController_seekbar_video_prograss = (SeekBar) findViewById(R.id.controller_seekbar_video_prograss);
        mTv_total_prograss = (TextView) findViewById(R.id.tv_total_prograss);
        mBtn_controller_exit = (Button) findViewById(R.id.btn_controller_exit);
        mBtn_controller_video_pre = (Button) findViewById(R.id.btn_controller_video_pre);
        mBtn_controller_video_start_pause = (Button) findViewById(R.id.btn_controller_video_start_pause);
        mBtn_controller_video_next = (Button) findViewById(R.id.btn_controller_video_next);
        mBtn_controller_video_siwch_screen = (Button) findViewById(R.id.btn_controller_video_siwch_screen);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_buffer_net_speed = (TextView) findViewById(R.id.tv_buffer_net_speed);
        tv_loading_net_speed = (TextView) findViewById(R.id.tv_loading_net_speed);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);

        mBtn_controller_voice.setOnClickListener(this);
        mBtn_collection.setOnClickListener(this);
        mBtn_controller_exit.setOnClickListener(this);
        mBtn_controller_video_pre.setOnClickListener(this);
        mBtn_controller_video_start_pause.setOnClickListener(this);
        mBtn_controller_video_next.setOnClickListener(this);
        mBtn_controller_video_siwch_screen.setOnClickListener(this);

    }

    private class MyPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            videoView.start();

            int duration = (int) videoView.getDuration();
            mController_seekbar_video_prograss.setMax(duration);
            mTv_total_prograss.setText(stringToTime.toTimeByInt(duration));

            handler.sendEmptyMessage(PROGRASS);

            mVideoWidth = mp.getVideoWidth();
            mVideoHeight = mp.getVideoHeight();

            ll_loading.setVisibility(View.GONE);

            setFullAndDefault(false);
        }

    }

    private class MyErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //万能播放器也拨不了
            AlertDialog.Builder builder = new AlertDialog.Builder(VitamioVideoPlayer.this);
            builder.setTitle("提示");
            builder.setMessage("无法播放此视频");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return true;
        }

    }

    private class MyCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            playNext();
        }

    }

    private class MyVideoSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 手指滑动时，引起seekbar进度的变化
         *
         * @param seekBar
         * @param progress
         * @param fromUser
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                videoView.seekTo(progress);
            }
        }

        /**
         * 手指触碰时
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLLER);
        }

        /**
         * 手指离开时
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
        }
    }

    private class MyVioceSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                if (progress > 0) {
                    isMute = true;
                } else {
                    isMute = false;
                }
                setVoice(progress, isMute);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            handler.removeMessages(HIDE_CONTROLLER);
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
        }
    }

    private void setVoice(int progress, boolean isMute) {
        if (isMute) {
            //是静音，设置非静音 或者 改变音量
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            mController_seekbar_video_vioce.setProgress(progress);
            currentVoice = progress;
        } else {
            //不是静音，设置静音
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            mController_seekbar_video_vioce.setProgress(0);
        }
    }

    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra("level", 0);

            if (level <= 0) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_0);
            } else if (level <= 10) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_10);
            } else if (level <= 20) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_20);
            } else if (level <= 40) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_40);
            } else if (level <= 60) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_60);
            } else if (level <= 80) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_80);
            } else if (level <= 100) {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_100);
            } else {
                mIv_controller_battery.setImageResource(R.drawable.ic_battery_100);
            }
        }
    }

    private class MyGestureDetectorListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            playAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            setFullAndDefault(isFull);
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isShowController) {
                //设置隐藏
                rl_controller.setVisibility(View.GONE);
                isShowController = false;
                handler.removeMessages(HIDE_CONTROLLER);
            } else {
                //设置显示
                rl_controller.setVisibility(View.VISIBLE);
                isShowController = true;
                handler.sendEmptyMessageDelayed(HIDE_CONTROLLER, 5000);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private class MyOnInfoListener implements MediaPlayer.OnInfoListener {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    ll_buffer.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    ll_buffer.setVisibility(View.GONE);
                    break;
            }
            return true;
        }
    }

}

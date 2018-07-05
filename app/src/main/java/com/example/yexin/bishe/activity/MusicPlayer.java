package com.example.yexin.bishe.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yexin.bishe.IMusicService;
import com.example.yexin.bishe.R;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.service.MusicService;
import com.example.yexin.bishe.utils.LyricUtil;
import com.example.yexin.bishe.utils.StringToTime;
import com.example.yexin.bishe.utils.Util;
import com.example.yexin.bishe.view.LyricShwoView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

public class MusicPlayer extends Activity implements View.OnClickListener {

    private static final int PROGRASS = 1;
    private static final int SHOW_LYRIC = 2;
    private ImageView mIv_music_anim;
    private TextView mTv_music_name;
    private TextView mTv_music_artist;
    private TextView mTv_music_time;
    private SeekBar mSeekbar_music;
    private Button mBtn_music_mode;
    private Button mBtn_music_pre;
    public Button mBtn_music_start_pause;
    private Button mBtn_music_next;
    private Button mBtn_music_list;

    private int position;
    private IMusicService iMusicService;
    private AnimationDrawable animationDrawable;
    private MyReceiver myReceiver;
    private StringToTime stringToTime;
    private boolean fromNotifi;
    private LyricShwoView music_lyric;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            iMusicService = IMusicService.Stub.asInterface(iBinder);
            if (iMusicService != null) {
                try {
                    if (!fromNotifi) {
                        iMusicService.startMusicByPosition(position);
                    } else {
                        showViewData();
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if (iMusicService != null) {
                try {
                    iMusicService.stop();
                    iMusicService = null;
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRASS:
                    handlPrograss();
                    break;
                case SHOW_LYRIC:
                    handlShowLyric();
                    break;
            }
        }
    };

    private void handlShowLyric() {
        try {
            //1、得到当前的进度
            int currentProgress = iMusicService.getCurrentProgress();
            //2、把进度传入lyric控件中 且 计算高亮
            music_lyric.setCurrentProgress(currentProgress);
            //3、实时发消息
            handler.removeMessages(SHOW_LYRIC);
            handler.sendEmptyMessage(SHOW_LYRIC);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    private void handlPrograss() {
        try {
            int currentProgress = iMusicService.getCurrentProgress();
            mSeekbar_music.setProgress(currentProgress);
            mTv_music_time.setText(stringToTime.toTimeByInt(currentProgress) + "/" + stringToTime.toTimeByInt(iMusicService.getCurrentDuration()));
            handler.removeMessages(PROGRASS);
            handler.sendEmptyMessageDelayed(PROGRASS, 1000);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        broadcast();
//        eventbus();
        bindViews();
        getintent();
        bindAndStartService();
    }

    private void eventbus() {
        EventBus.getDefault().register(this);
    }

    private void broadcast() {
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicService.MUSIC_PREPARED);
        registerReceiver(myReceiver, intentFilter);
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showMusicInfo();
        }
    }

//    @Subscribe(threadMode = ThreadMode.MAIN,sticky = false,priority = 0)
    public void showMusicInfo() {

        showLyric();
        showViewData();
        showPlayMode(false);
        checkStartPause();
    }

    private void checkStartPause() {
        try {
            if (iMusicService.isPlaying()){
                mBtn_music_start_pause.setBackgroundResource(R.drawable.btn_music_start_pause_selector);
            }else{
                mBtn_music_start_pause.setBackgroundResource(R.drawable.btn_music_pause_start_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showLyric() {
        LyricUtil lyricUtil = new LyricUtil();
        try {
            String path = iMusicService.getCurrentPath();
            path = path.substring(0,path.lastIndexOf("."));
            File file = new File(path+".lrc");
            if (!file.exists()){
                file = new File(path+".txt");
            }
            lyricUtil.readLyricFile(file);
            music_lyric.setLyrics(lyricUtil.getLyrics());
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        if (lyricUtil.isLyricExists()){
            //发消息 歌词同步
            handler.sendEmptyMessage(SHOW_LYRIC);
        }

    }

    private void showViewData() {
        try {
            mTv_music_name.setText(iMusicService.getCurrentName());
            mTv_music_artist.setText(iMusicService.getCurrentArtist());
            mSeekbar_music.setMax(iMusicService.getCurrentDuration());
            handler.sendEmptyMessage(PROGRASS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void bindAndStartService() {
        Intent intent = new Intent(this, MusicService.class);
        intent.setAction("com.example.yexin.bishe_MUSIC");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        //不至于实例化多个服务
        startService(intent);
    }

    private void getintent() {
        fromNotifi = getIntent().getBooleanExtra("Notification", false);
        if (!fromNotifi) {
            position = getIntent().getIntExtra("position", 0);
        }
    }

    private void bindViews() {
        mIv_music_anim = (ImageView) findViewById(R.id.iv_music_anim);
        mTv_music_name = (TextView) findViewById(R.id.tv_music_name);
        mTv_music_artist = (TextView) findViewById(R.id.tv_music_artist);
        mTv_music_time = (TextView) findViewById(R.id.tv_music_time);
        mSeekbar_music = (SeekBar) findViewById(R.id.seekbar_music);
        mBtn_music_mode = (Button) findViewById(R.id.btn_music_mode);
        mBtn_music_pre = (Button) findViewById(R.id.btn_music_pre);
        mBtn_music_start_pause = (Button) findViewById(R.id.btn_music_start_pause);
        mBtn_music_next = (Button) findViewById(R.id.btn_music_next);
        mBtn_music_list = (Button) findViewById(R.id.btn_music_list);
        music_lyric= (LyricShwoView) findViewById(R.id.music_lyric);

        mBtn_music_list.setOnClickListener(this);
        mBtn_music_mode.setOnClickListener(this);
        mBtn_music_next.setOnClickListener(this);
        mBtn_music_pre.setOnClickListener(this);
        mBtn_music_start_pause.setOnClickListener(this);

        mSeekbar_music.setOnSeekBarChangeListener(new MusicSeekBarListener());

        mIv_music_anim.setBackgroundResource(R.drawable.music_animation_list);
        animationDrawable = (AnimationDrawable) mIv_music_anim.getBackground();
        animationDrawable.start();

        stringToTime = new StringToTime();
    }


    @Override
    public void onClick(View v) {
        if (v == mBtn_music_mode) {
            //切换播放模式
            setPlayModel();
        } else if (v == mBtn_music_pre) {
            //上一首
            if (iMusicService != null) {
                try {
                    iMusicService.musicPre();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == mBtn_music_start_pause) {
            //暂停，继续
            toggle();
        } else if (v == mBtn_music_next) {
            //下一首
            if (iMusicService != null) {
                try {
                    iMusicService.musicNext();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        } else if (v == mBtn_music_list) {
            //显示列表
        }
    }

    private void setPlayModel() {
        try {
            int playModel = iMusicService.getPlayMode();
            if (playModel == MusicService.MODEL_NORMAL) {
                playModel = MusicService.MODEL_ALL;
            } else if (playModel == MusicService.MODEL_ALL) {
                playModel = MusicService.MODEL_SINGLE;
            } else if (playModel == MusicService.MODEL_SINGLE) {
                playModel = MusicService.MODEL_NORMAL;
            } else {
                playModel = MusicService.MODEL_NORMAL;
            }

            iMusicService.setPlayMode(playModel);
            showPlayMode(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void showPlayMode(boolean b) {
        try {
            int playModel = iMusicService.getPlayMode();
            if (playModel == MusicService.MODEL_NORMAL) {
                mBtn_music_mode.setBackgroundResource(R.drawable.btn_music_play_normal_selector);
                if (b)
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
            } else if (playModel == MusicService.MODEL_ALL) {
                mBtn_music_mode.setBackgroundResource(R.drawable.btn_music_play_all_selector);
                if (b)
                    Toast.makeText(this, "列表循环", Toast.LENGTH_SHORT).show();
            } else if (playModel == MusicService.MODEL_SINGLE) {
                mBtn_music_mode.setBackgroundResource(R.drawable.btn_music_play_single_selector);
                if (b)
                    Toast.makeText(this, "单曲循环", Toast.LENGTH_SHORT).show();
            } else {
                mBtn_music_mode.setBackgroundResource(R.drawable.btn_music_play_normal_selector);
                if (b)
                    Toast.makeText(this, "顺序播放", Toast.LENGTH_SHORT).show();
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 音乐暂停继续切换
     */
    private void toggle() {
        if (iMusicService != null) {
            try {
                if (iMusicService.isPlaying()) {
                    iMusicService.pause();
                    mBtn_music_start_pause.setBackgroundResource(R.drawable.btn_music_pause_start_selector);
                    animationDrawable.stop();
                } else {
                    iMusicService.start();
                    mBtn_music_start_pause.setBackgroundResource(R.drawable.btn_music_start_pause_selector);
                    animationDrawable.start();
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
//        EventBus.getDefault().unregister(this);
        handler.removeCallbacksAndMessages(null);
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        super.onDestroy();
    }

    private class MusicSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    iMusicService.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }

}

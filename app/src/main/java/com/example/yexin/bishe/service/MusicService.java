package com.example.yexin.bishe.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;

import com.example.yexin.bishe.IMusicService;
import com.example.yexin.bishe.R;
import com.example.yexin.bishe.activity.MusicPlayer;
import com.example.yexin.bishe.activity.VideoPlayer;
import com.example.yexin.bishe.bean.MediaItem;
import com.example.yexin.bishe.utils.LogUtil;
import com.example.yexin.bishe.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {
    public static final String MUSIC_PREPARED = "com.example.yexin.bishe.musicPrepared";
    private static final String VIDEOSTART = "com.example.yexin.bishe.videostart";
    private static final String VIDEOSTOP = "com.example.yexin.bishe.videostop";
    private static final String APPQ = "com.example.yexin.bishe.appquit";
    private ArrayList<MediaItem> mediaItems;
    private int position;
    private MediaItem mediaItem;
    private MediaPlayer mediaPlayer;
    private NotificationManager manager;

    public static final int MODEL_NORMAL = 1;
    public static final int MODEL_SINGLE = 2;
    public static final int MODEL_ALL = 3;
    private int playMode = MODEL_NORMAL;

    private MusicPause musicPause;
    private MusicStart musicStart;
    private AppQuit appQuit;


    public MusicService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        videoSmusicP();
        videoPmusicS();
        appquit();
        playMode = Util.getPlayMode(this, "playMode");
        //加载音乐列表
        getBDYY();
    }

    private void appquit() {
        appQuit = new AppQuit();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(APPQ);
        registerReceiver(appQuit, intentFilter);
    }

    class AppQuit extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                manager.cancel(1);
            }
        }
    }

    private void videoPmusicS() {
        musicStart = new MusicStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VIDEOSTOP);
        registerReceiver(musicStart, intentFilter);
    }

    class MusicStart extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }
        }
    }

    private void videoSmusicP() {
        musicPause = new MusicPause();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(VIDEOSTART);
        registerReceiver(musicPause, intentFilter);
    }

    class MusicPause extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }
        }
    }

    private void getBDYY() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                mediaItems = new ArrayList<MediaItem>();
                //1.获取内容解析者
                ContentResolver contentResolver = getContentResolver();
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME,//视频文件在sdcard中的名称
                        MediaStore.Audio.Media.DURATION,//视频总时长
                        MediaStore.Audio.Media.SIZE,//大小
                        MediaStore.Audio.Media.DATA,//视频的绝对地址
                        MediaStore.Audio.Media.ARTIST,//艺术家(歌曲的演唱者)
                };
                Cursor cursor = contentResolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(0);
                        long duration = cursor.getLong(1);
                        long size = cursor.getLong(2);
                        String data = cursor.getString(3);
                        String artist = cursor.getString(4);
                        MediaItem mediaItem = new MediaItem(name, duration, size, data, artist);
                        mediaItems.add(mediaItem);
                    }
                    cursor.close();
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        mediaPlayer.pause();
        manager.cancel(1);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return stub;
    }

    private IMusicService.Stub stub = new IMusicService.Stub() {
        MusicService musicService = MusicService.this;

        @Override
        public void startMusicByPosition(int position) throws RemoteException {
            musicService.startMusicByPosition(position);
        }

        @Override
        public void start() throws RemoteException {
            musicService.start();
        }

        @Override
        public void pause() throws RemoteException {
            musicService.pause();
        }

        @Override
        public void stop() throws RemoteException {
            musicService.stop();
        }

        @Override
        public int getCurrentProgress() throws RemoteException {
            return musicService.getCurrentProgress();
        }

        @Override
        public int getCurrentDuration() throws RemoteException {
            return musicService.getCurrentDuration();
        }

        @Override
        public String getCurrentName() throws RemoteException {
            return musicService.getCurrentName();
        }

        @Override
        public String getCurrentArtist() throws RemoteException {
            return musicService.getCurrentArtist();
        }

        @Override
        public String getCurrentPath() throws RemoteException {
            return musicService.getCurrentPath();
        }

        @Override
        public void musicNext() throws RemoteException {
            musicService.musicNext();
        }

        @Override
        public void musicPre() throws RemoteException {
            musicService.musicPre();
        }

        @Override
        public void setPlayMode(int playMode) throws RemoteException {
            musicService.setPlayMode(playMode);
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return musicService.getPlayMode();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return musicService.isPlaying();
        }

        @Override
        public void seekTo(int position) throws RemoteException {
            musicService.seekTo(position);
        }

    };

    private void startMusicByPosition(int position) {
        this.position = position;
        if (mediaItems != null && mediaItems.size() > 0) {
            mediaItem = mediaItems.get(position);

            if (mediaPlayer != null) {
                mediaPlayer.reset();
            }
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(mediaItem.getData());
                //设置监听
                mediaPlayer.setOnPreparedListener(new MusicPreparedListener());
                mediaPlayer.setOnCompletionListener(new MusicCompletionListener());
                mediaPlayer.setOnErrorListener(new MusicErrorListener());
                mediaPlayer.prepareAsync();

                if (playMode == MODEL_SINGLE) {
                    //单曲循环-不会触发播放完成的回调
                    mediaPlayer.setLooping(true);
                } else {
                    mediaPlayer.setLooping(false);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void start() {
        mediaPlayer.start();

        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MusicPlayer.class);
        intent.putExtra("Notification", true);
        LogUtil.i("notifi");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_music_playing)
                .setContentTitle("手机影音")
                .setContentText("正在播放：" + getCurrentName())
                .setContentIntent(pendingIntent)
                .build();
        manager.notify(1, notification);
    }

    private void pause() {
        mediaPlayer.pause();

        manager.cancel(1);
    }

    private void stop() {
        mediaPlayer.stop();
        manager.cancel(1);
    }

    private int getCurrentProgress() {
        return mediaPlayer.getCurrentPosition();
    }

    private int getCurrentDuration() {
        return mediaPlayer.getDuration();
    }

    private String getCurrentName() {
        return mediaItem.getName();
    }

    private String getCurrentArtist() {
        return mediaItem.getArtist();
    }

    private String getCurrentPath() {
        return mediaItem.getData();
    }

    private void musicNext() {
        //1、根据当前的播放模式，设置下一个音乐的位置
        setNextPosition();
        //2、根据当前的播放模式和下标位置去播放音乐
        startNextMusic();
    }

    private void startNextMusic() {
        int playModel = getPlayMode();
        if (playModel == MusicService.MODEL_NORMAL) {
            if (position < mediaItems.size()) {
                //正常范围
                startMusicByPosition(position);
            } else {
                position = mediaItems.size() - 1;
            }
        } else if (playModel == MusicService.MODEL_ALL) {
            startMusicByPosition(position);
        } else if (playModel == MusicService.MODEL_SINGLE) {
            startMusicByPosition(position);
        } else {
            if (position < mediaItems.size()) {
                //正常范围
                startMusicByPosition(position);
            } else {
                position = mediaItems.size() - 1;
            }
        }
    }

    private void setNextPosition() {
        int playModel = getPlayMode();
        if (playModel == MusicService.MODEL_NORMAL) {
            position++;
        } else if (playModel == MusicService.MODEL_ALL) {
            position++;
            position = position > mediaItems.size() - 1 ? 0 : position;
        } else if (playModel == MusicService.MODEL_SINGLE) {
            position++;
            position = position > mediaItems.size() - 1 ? 0 : position;
        } else {
            position++;
        }
    }

    private void musicPre() {
        //1、根据当前的播放模式，设置上一个音乐的位置
        setPrePosition();
        //2、根据当前的播放模式和下标位置去播放音乐
        startPreMusic();
    }

    private void startPreMusic() {
        int playModel = getPlayMode();
        if (playModel == MusicService.MODEL_NORMAL) {
            if (position >= 0) {
                startMusicByPosition(position);
            } else {
                position = 0;
            }
        } else if (playModel == MusicService.MODEL_ALL) {
            startMusicByPosition(position);
        } else if (playModel == MusicService.MODEL_SINGLE) {
            startMusicByPosition(position);
        } else {
            if (position >= 0) {
                startMusicByPosition(position);
            } else {
                position = 0;
            }
        }
    }

    private void setPrePosition() {
        int playModel = getPlayMode();
        if (playModel == MusicService.MODEL_NORMAL) {
            position--;
        } else if (playModel == MusicService.MODEL_ALL) {
            position--;
            position = position < 0 ? mediaItems.size() - 1 : position;
        } else if (playModel == MusicService.MODEL_SINGLE) {
            position--;
            position = position < 0 ? mediaItems.size() - 1 : position;
        } else {
            position--;
        }
    }

    private void setPlayMode(int playMode) {
        this.playMode = playMode;
        Util.putPlayMode(this, "playMode", playMode);

        if (playMode == MODEL_SINGLE) {
            //单曲循环-不会触发播放完成的回调
            mediaPlayer.setLooping(true);
        } else {
            mediaPlayer.setLooping(false);
        }
    }

    private int getPlayMode() {
        return playMode;
    }

    private boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    private void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    private class MusicPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mp) {
            Intent intent = new Intent(MUSIC_PREPARED);
            sendBroadcast(intent);
//            EventBus.getDefault().post(mediaItem);
            start();
        }
    }

    private class MusicCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mp) {
            musicNext();
        }
    }

    private class MusicErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            musicNext();
            return true;
        }
    }

}

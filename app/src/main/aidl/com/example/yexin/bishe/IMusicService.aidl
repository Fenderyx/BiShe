// IMusicService.aidl
package com.example.yexin.bishe;

// Declare any non-default types here with import statements

interface IMusicService {

    /**
    * 根据位置打开对应的音乐
    * @param position
    */
    void startMusicByPosition(int position);
    /**
    * 播放音乐
    */
    void start();

    /**
    * 暂停音乐
    */
    void pause();

    /**
    * 停止
    */
    void stop();

    /**
    * 得到当前的播放进度
    * @return
    */
    int getCurrentProgress();

    /**
    * 得到当前的音乐的总时长
    * @return
    */
    int getCurrentDuration();

    /**
    * 得到当前音乐的歌曲名
    * @return
    */
    String getCurrentName();

    /**
    * 得到当前音乐的演唱者
    * @return
    */
    String getCurrentArtist();

    /**
    * 得到当前音乐的路径
    * @return
    */
    String getCurrentPath();

    void musicNext();

    void musicPre();

    /**
    * 播放模式
    * @param playMode
    */
    void setPlayMode(int playMode);

    int getPlayMode();

    boolean isPlaying();

    void seekTo(int position);

}

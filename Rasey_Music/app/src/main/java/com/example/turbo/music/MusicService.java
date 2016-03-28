package com.example.turbo.music;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service {

    static MediaPlayer mediaPlayer = new MediaPlayer();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if(PlayActivity.if_JustStart) {
            songPlay();
            PlayActivity.if_JustStart = false;
            PlayActivity.mAdapter.notifyDataSetChanged();
            putInit();
        }
        else{
            nextSong();
            PlayActivity.mAdapter.notifyDataSetChanged();
            putInit();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        putInit();
        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    public static void songPlay() {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(PlayActivity.mp3Infos.get(PlayActivity.musicNumber).getUrl());
            mediaPlayer.prepare();
            mediaPlayer.start();
            PlayActivity.pauseAble = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void nextSong() {
        PlayActivity.getMusicNumber();
        songPlay();
    }

    public void putInit(){
        SharedPreferences.Editor init = getSharedPreferences("data", MODE_PRIVATE).edit();
        if(PlayActivity.index == 3){
            init.putInt("index", 0);
        }
        else if(PlayActivity.index == 4){
            init.putInt("index", 1);
        }
        else if(PlayActivity.index == 5){
            init.putInt("index", 2);
        }
        else{
            init.putInt("index", PlayActivity.index);
        }
        init.putInt("title_shake", PlayActivity.title_shake);
        init.putInt("title_handle", PlayActivity.title_handle);
        init.putInt("musicNumber", PlayActivity.musicNumber);
        init.putBoolean("should_play", PlayActivity.should_play);
        //init.putBoolean("if_JustStart", if_JustStart);
        init.commit();
    }

}

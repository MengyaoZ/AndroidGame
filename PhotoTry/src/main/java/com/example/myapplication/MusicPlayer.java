package com.example.myapplication;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by user on 2015/11/20.
 */
public class MusicPlayer {
    private static MediaPlayer player;

    //public static MusicPlayer get() {
    //    return new MusicPlayer();
    //}

    public static void playMusic() {
        // 开始播放音乐
        player.start();
        // 音乐播放完毕的事件处理
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                // 循环播放
                try {
                    player.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
        // 播放音乐时发生错误的事件处理
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub
                // 释放资源
                try {
                    player.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });
    }

    public static void init(Context context, int musicId) {
        try {
            // 创建MediaPlayer对象
            player = new MediaPlayer();
            switch (musicId) {
                case 0:
                // 将音乐保存在res/raw/xingshu.mp3,R.java中自动生成{public static final int xingshu=0x7f040000;}
                    player = MediaPlayer.create(context, R.raw.dream_catcher);
                    break;
                case 1:
                    player = MediaPlayer.create(context, R.raw.speak_softly_love);
                    break;
                case 2:
                    player = MediaPlayer.create(context, R.raw.explosive);
                    break;
                case 3:
                    player = MediaPlayer.create(context, R.raw.time_for_three);
                    break;
            }

            // 在MediaPlayer取得播放资源与stop()之后要准备PlayBack的状态前一定要使用MediaPlayer.prepeare()
            player.prepare();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        player.stop();
    }
    public static void pauseMusic() {
        player.pause();
    }
}

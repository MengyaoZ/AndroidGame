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
        player.start();

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                // ѭ������
                try {
                    player.start();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            public boolean onError(MediaPlayer mp, int what, int extra) {
                // TODO Auto-generated method stub

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
            // ����MediaPlayer����
            player = new MediaPlayer();
            switch (musicId) {
                case 0:
                // �����ֱ�����res/raw/xingshu.mp3,R.java���Զ�����{public static final int xingshu=0x7f040000;}
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

            // ��MediaPlayerȡ�ò�����Դ��stop()֮��Ҫ׼��PlayBack��״̬ǰһ��Ҫʹ��MediaPlayer.prepeare()
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

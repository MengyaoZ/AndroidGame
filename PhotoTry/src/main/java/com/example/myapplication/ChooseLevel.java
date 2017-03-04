package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by user on 2015/11/16.
 */
public class ChooseLevel extends Activity {
    private Button beginner;
    private Button middle;
    private Button hard;
    private Button expert;
    private Button crazy;
    private ImageView mute;
    private SharedPreferences config;
    private int themeNum;

    private boolean isPlaying;

    public void onCreate(Bundle savedInstanceState){
        getIntent().setAction("Already created");
        config = getSharedPreferences("configuration", MODE_PRIVATE);
        themeNum = config.getInt("theme", 0);

        switch (themeNum) {
            case 0:
                setTheme(R.style.AppTheme);
                break;
            case 1:
                setTheme(R.style.AppTheme1);
                break;
            case 2:
                setTheme(R.style.AppTheme2);
                break;
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_level);

        beginner = (Button) this.findViewById(R.id.beginner);
        middle = (Button) this.findViewById(R.id.middle);
        hard = (Button) this.findViewById(R.id.hard);
        expert = (Button) this.findViewById(R.id.expert);
        crazy = (Button) this.findViewById(R.id.crazy);
        mute = (ImageView) this.findViewById(R.id.mute_level);


        if(Variables.isFirst()) {
            MusicPlayer.init(getApplicationContext(), 0);
            Variables.setFirst(false);
        }
        if(Variables.isPlaying()) {
            MusicPlayer.playMusic();
            mute.setImageResource(R.drawable.ic_volume_up_black_48dp);
        } else{
            mute.setImageResource(R.drawable.ic_volume_off_black_48dp);
        }

        LevelSelector levelSelector = new LevelSelector();
        beginner.setOnClickListener(levelSelector);
        middle.setOnClickListener(levelSelector);
        hard.setOnClickListener(levelSelector);
        expert.setOnClickListener(levelSelector);
        crazy.setOnClickListener(levelSelector);

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Variables.isPlaying()) {
                    MusicPlayer.pauseMusic();
                    Variables.setPlaying(false);
                    //MusicPlayer.get().setPlaying(false);
                    isPlaying = false;
                    mute.setImageResource(R.drawable.ic_volume_off_black_48dp);
                } else {
                    MusicPlayer.playMusic();
                    Variables.setPlaying(true);
                    //MusicPlayer.get().setPlaying(true);
                    isPlaying = true;
                    mute.setImageResource(R.drawable.ic_volume_up_black_48dp);
                }
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("isPlaying", isPlaying);
                editor.apply();
            }
        });

    }
    class LevelSelector implements View.OnClickListener {
        Intent data = new Intent(ChooseLevel.this, Game.class);
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.beginner:
                    data.putExtra("level", "3");
                    break;
                case R.id.middle:
                    data.putExtra("level", "4");
                    break;
                case R.id.hard:
                    data.putExtra("level", "5");
                    break;
                case R.id.expert:
                    data.putExtra("level", "6");
                    break;
                case R.id.crazy:
                    data.putExtra("level", "7");
                    break;
                default:
                    data.putExtra("level", "-1");
                    break;
            }
            data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            data.putExtra("resume", false);
            startActivity(data);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isPlaying = config.getBoolean("isPlaying", true);
        Variables.setPlaying(isPlaying);
        if(Variables.isPlaying()) {
            mute.setImageResource(R.drawable.ic_volume_up_black_48dp);
        } else{
            mute.setImageResource(R.drawable.ic_volume_off_black_48dp);
        }
    }

    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        handler.cancel();
    }
}

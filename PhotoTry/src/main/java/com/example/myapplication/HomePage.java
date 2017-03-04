package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by user on 2015/11/25.
 */
public class HomePage extends Activity {
    private Button start;
    private Button resume;
    private Button configuration;
    private ImageView mute;
    private SharedPreferences sharedPreferences;
    private SharedPreferences config;
    private int themeNum;
    private int musicNum;
    private boolean isPlaying;

    public void onCreate(Bundle savedInstanceState){
        getIntent().setAction("Already created");
        config = getSharedPreferences("configuration", MODE_PRIVATE);
        themeNum = config.getInt("theme", 0);
        musicNum = config.getInt("music", 0);
        isPlaying = config.getBoolean("isPlaying", true);
        Log.i("theme",String.valueOf(themeNum));
        Log.i("is play at creating", String.valueOf(isPlaying));
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
        setContentView(R.layout.home_page);

        Variables.setPlaying(isPlaying);
        start = (Button)this.findViewById(R.id.start);
        resume = (Button)this.findViewById(R.id.resume_main);
        configuration = (Button)this.findViewById(R.id.configuration_main);
        mute = (ImageView)this.findViewById(R.id.mute_home);
        sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);

        if(Variables.isFirst()) {
            MusicPlayer.init(getApplicationContext(), musicNum);
            Variables.setFirst(false);
        }
        if(Variables.isPlaying()) {
            MusicPlayer.playMusic();
            mute.setImageResource(R.drawable.ic_volume_up_black_48dp);
        } else{
            mute.setImageResource(R.drawable.ic_volume_off_black_48dp);
        }

        mute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Variables.isPlaying()) {
                    MusicPlayer.pauseMusic();
                    Variables.setPlaying(false);
                    isPlaying = false;

                    //MusicPlayer.get().setPlaying(false);
                    mute.setImageResource(R.drawable.ic_volume_off_black_48dp);
                } else {
                    MusicPlayer.playMusic();
                    Variables.setPlaying(true);
                    isPlaying = true;
                    //MusicPlayer.get().setPlaying(true);
                    mute.setImageResource(R.drawable.ic_volume_up_black_48dp);
                }
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("isPlaying", isPlaying);
                editor.apply();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, MainActivity.class);
                startActivity(intent);
            }
        });
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasSave = sharedPreferences.getBoolean("saved",false);
                if(hasSave) {
                    Intent intent = new Intent(HomePage.this, Game.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("resume", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No saved game available!", Toast.LENGTH_LONG).show();
                }
            }
        });
        configuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Configuration.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume() {

        /*themeNum = config.getInt("theme", 0);
        isPlaying = config.getBoolean("isPlaying", true);
        Log.i("new theme", String.valueOf(themeNum));
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
        }*/
        if(getIntent().getAction() == null || !getIntent().getAction().equals("Already created")) {
            Intent in = new Intent(this, HomePage.class);
            in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            finish();
            startActivity(in);
            Log.i("resuming", "ohno");
        } else {
            getIntent().setAction(null);
        }
        super.onResume();

    }
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        handler.cancel();
    }

}

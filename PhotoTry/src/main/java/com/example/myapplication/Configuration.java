package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

/**
 * Created by user on 2015/11/27.
 */
public class Configuration extends Activity {
    private Spinner themeChooser;
    private Spinner musicChooser;
    private SeekBar soundAdjust;
    private ImageView preview;
    private ImageView sound;
    private CheckBox check;
    private ImageView cancel;
    private ImageView apply;
    private ImageView save;
    private boolean soundOut;
    private int volume;
    private int themeNum;
    private int musicNum;
    private boolean show;

    private AudioManager audioManager;
    private int maxVolume;
    private int currentVolume;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean fromGame;
    //String[] themeList;
    //String[] musicList;

    public void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("configuration", MODE_PRIVATE);
        soundOut = sharedPreferences.getBoolean("isPlaying", true);
        themeNum = sharedPreferences.getInt("theme", 0);
        musicNum = sharedPreferences.getInt("music", 0);
        show = sharedPreferences.getBoolean("show", false);
        getIntent().setAction("Already created");
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
        setContentView(R.layout.configuration);

        themeChooser = (Spinner)this.findViewById(R.id.theme_chooser);
        musicChooser = (Spinner)this.findViewById(R.id.music_chooser);
        soundAdjust = (SeekBar)this.findViewById(R.id.sound_bar);
        sound = (ImageView)this.findViewById(R.id.sound_icon);
        check = (CheckBox)this.findViewById(R.id.check_show);
        cancel = (ImageView)this.findViewById(R.id.cancel_configuration);
        apply = (ImageView)this.findViewById(R.id.apply_configuration);
        save = (ImageView)this.findViewById(R.id.save_configuration);
        preview = (ImageView)this.findViewById(R.id.preview);

        Intent data = getIntent();
        fromGame = data.getBooleanExtra("game", false);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volume = currentVolume * 100 / maxVolume;
        //themeList = getResources().getStringArray(R.array.theme_arrays);
        Log.i("themeNum get", String.valueOf(themeNum));
        themeChooser.setSelection(themeNum);
        //musicList = getResources().getStringArray(R.array.music_arrays);
        musicChooser.setSelection(musicNum);
        check.setChecked(show);
        if(!soundOut) {
            sound.setImageResource(R.drawable.ic_volume_off_black_48dp);
            soundAdjust.setProgress(0);
        } else {
            sound.setImageResource(R.drawable.ic_volume_up_black_48dp);
            soundAdjust.setProgress(volume);
        }

        themeChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (fromGame && position != themeNum) {
                    final Dialog dialog = new Dialog(Configuration.this, R.style.DialogTheme);
                    dialog.setContentView(R.layout.theme_dialog);
                    ImageView ok = (ImageView) dialog.findViewById(R.id.ok_theme);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            themeChooser.setSelection(themeNum);
                            dialog.dismiss();
                        }
                    });
                    dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            themeChooser.setSelection(themeNum);
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    themeNum = position;
                    switch (themeNum) {
                        case 0: preview.setImageResource(R.drawable.theme0);
                            break;
                        case 1: preview.setImageResource(R.drawable.theme1);
                            break;
                        case 2: preview.setImageResource(R.drawable.theme2);
                    }
                }
                Log.i("themeNum", String.valueOf(themeNum));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        musicChooser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (musicNum != position) {
                    MusicPlayer.stopMusic();
                    MusicPlayer.init(getApplicationContext(), position);
                    if (soundOut) {
                        MusicPlayer.playMusic();
                    }
                    musicNum = position;
                    Log.i("musicNum", String.valueOf(musicNum));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                show = isChecked;
            }
        });
        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!soundOut) {
                    sound.setImageResource(R.drawable.ic_volume_up_black_48dp);
                    MusicPlayer.playMusic();
                    currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    volume = currentVolume * 100 / maxVolume;
                    soundAdjust.setProgress(volume);
                    soundOut = true;
                } else {
                    sound.setImageResource(R.drawable.ic_volume_off_black_48dp);
                    soundAdjust.setProgress(0);
                    MusicPlayer.pauseMusic();
                    soundOut = false;
                }
            }
        });
        soundAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (!soundOut) {
                        sound.setImageResource(R.drawable.ic_volume_up_black_48dp);
                        MusicPlayer.playMusic();
                        soundOut = true;
                    }
                    currentVolume = progress * maxVolume / 100;
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int original = sharedPreferences.getInt("music",0);
                if(original != musicNum) {
                    MusicPlayer.stopMusic();
                    MusicPlayer.init(getApplicationContext(), original);
                }
                if (Variables.isPlaying()) {
                    MusicPlayer.playMusic();
                } else {
                    MusicPlayer.pauseMusic();
                }
                finish();
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                editor.putBoolean("isPlaying", soundOut);
                editor.apply();
                editor.putInt("theme", themeNum);
                editor.apply();
                editor.putInt("music", musicNum);
                editor.apply();
                editor.putBoolean("show", show);
                editor.apply();
                recreate();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor = sharedPreferences.edit();
                editor.putBoolean("isPlaying", soundOut);
                Log.i("sound out", String.valueOf(soundOut));
                editor.apply();
                editor.putInt("theme", themeNum);
                editor.apply();
                editor.putInt("music", musicNum);
                editor.apply();
                editor.putBoolean("show", show);
                editor.apply();
                finish();
            }
        });
        editor = sharedPreferences.edit();
    }

    @Override
    protected void onResume() {

        super.onResume();

    }
}

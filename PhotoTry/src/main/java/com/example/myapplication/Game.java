package com.example.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * Created by user on 2015/11/17.
 */
public class Game extends Activity {
    private static final int NO_SHOW = 0;
    private static final int SHOW = 1;
    private static final int MOVE_UP = 0;
    private static final int MOVE_DOWN = 1;
    private static final int MOVE_LEFT = 2;
    private static final int MOVE_RIGHT = 3;

    private ImageView exit;
    private ImageView pause;
    private ImageView restart;
    private ImageView save;
    private TextView timer;
    private ImageView sound;
    private ImageView configuration;
    private ImageView hint;
    private ImageView share;
    private GridView gridView;
    private int level;
    private Board board;
    private ArrayList<Tile> tiles;
    private int width;
    private MyAdapter m;


    private Handler handler;
    private Runnable runnable;
    private int secondTime;
    private int minute;
    private int second;
    private int hour;
    private boolean hasSaved = false;
    private boolean isEnd = false;
    private boolean show = false;
    private SharedPreferences sharedPreferences;
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
        setContentView(R.layout.game);

        init();

        Log.i("Here on no!!!!!!!!", String.valueOf(level));
        if(Variables.isPlaying()) {
            sound.setImageResource(R.drawable.ic_volume_up_black_48dp);
        } else{
            sound.setImageResource(R.drawable.ic_volume_off_black_48dp);
        }

        if(Variables.isGaming()) {
            pause.setImageResource(R.drawable.ic_pause_black_48dp);
        } else{
            pause.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        }
        //Toast.makeText(getApplicationContext(),String.valueOf(level),Toast.LENGTH_SHORT).show();



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if((Variables.isGaming() || !Variables.isGameStarted()) && !isEnd) {
                    int blankIndex = board.getBlankIndex();
                    if (blankIndex - i == level) {
                        if(!Variables.isGameStarted())
                            startGame();
                        board.move(MOVE_UP);
                        m.notifyDataSetChanged();
                        hasSaved = false;
                        check();
                    }
                    if (i - blankIndex == level) {
                        if(!Variables.isGameStarted())
                            startGame();
                        board.move(MOVE_DOWN);
                        m.notifyDataSetChanged();
                        hasSaved = false;
                        check();
                    }
                    if (blankIndex - i == 1) {
                        if(!Variables.isGameStarted())
                            startGame();
                        board.move(MOVE_LEFT);
                        m.notifyDataSetChanged();
                        hasSaved = false;
                        check();
                    }
                    if (i - blankIndex == 1) {
                        if(!Variables.isGameStarted())
                            startGame();
                        board.move(MOVE_RIGHT);
                        m.notifyDataSetChanged();
                        hasSaved = false;
                        check();
                    }
                } else if(!isEnd) {
                        resumeDialog();
                }
            }
        });
        //gridItemList.get(10).put("ItemImage", R.drawable.camera);
        //saImageItems.notifyDataSetChanged();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*String time = timer.getText().toString();
                int score = Integer.valueOf(time.replaceAll(":",""));
                showDialog(time, score);*/
                if(!hasSaved && !isEnd) {
                    exitDialog();
                } else {
                    finish();
                }
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Variables.isGaming()) {
                    pause.setImageResource(R.drawable.ic_play_arrow_black_48dp);
                    Variables.setIsGaming(false);
                    handler.removeCallbacks(runnable);
                } else{
                    pause.setImageResource(R.drawable.ic_pause_black_48dp);
                    Variables.setIsGaming(true);
                    handler.postDelayed(runnable, 1000);
                }
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redraw();
            }
        });
        //final AudioManager manager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        sound.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //manager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                if(Variables.isPlaying()) {
                    MusicPlayer.pauseMusic();
                    Variables.setPlaying(false);
                    isPlaying = false;
                    sound.setImageResource(R.drawable.ic_volume_off_black_48dp);
                } else {
                    MusicPlayer.playMusic();
                    Variables.setPlaying(true);
                    isPlaying = true;
                    sound.setImageResource(R.drawable.ic_volume_up_black_48dp);
                }
                SharedPreferences.Editor editor = config.edit();
                editor.putBoolean("isPlaying", isPlaying);
                editor.apply();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
                hasSaved = true;
            }
        });
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hintDialog();
            }
        });

        configuration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Game.this, Configuration.class);
                intent.putExtra("game", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                ShareSDK.initSDK(getApplicationContext());
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle("PhotoPuzzle");  //���30���ַ�
                // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
                oks.setText("I stuck in this Photo Puzzle! Somebody help!");
                oks.setImagePath(Environment.getExternalStorageDirectory().getPath()+ "/pp/temp2.png");
                oks.show(getApplicationContext());
            }
        });

        runnable = new Runnable() {

            @Override
            public void run() {
                setTime(secondTime);
                secondTime++;
                handler.postDelayed(this,1000);
            }
        };
        Log.i("Can't be here", String.valueOf(minute));
    }
    public void onReceivedSslError(WebView view, SslErrorHandler handler,
                                   SslError error) {
        handler.cancel();
    }
    private void init() {
        exit = (ImageView)this.findViewById(R.id.exit);
        pause = (ImageView)this.findViewById(R.id.pause);
        restart = (ImageView)this.findViewById(R.id.restart);
        save = (ImageView)this.findViewById(R.id.save);
        timer = (TextView)this.findViewById(R.id.timer);
        sound = (ImageView)this.findViewById(R.id.sound);
        configuration = (ImageView)this.findViewById(R.id.configuration);
        hint = (ImageView)this.findViewById(R.id.hints);
        share = (ImageView)this.findViewById(R.id.share);
        gridView = (GridView)this.findViewById(R.id.grid);
        isEnd = false;

        show = config.getBoolean("show", false);
        secondTime = 0;
        handler = new Handler();
        getLevel();

    }

    private void getLevel() {


        Intent data = getIntent();
        Boolean resumed = data.getBooleanExtra("resume", false);
        if(resumed) {
            SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
            //ArrayList<Tile> oldTiles = new ArrayList<Tile>();
            level = sharedPreferences.getInt("level", 0);

            //draw();
            retrieve();
        } else {
            String levelStr = data.getStringExtra("level");
            level = Integer.parseInt(levelStr);
            draw();
        }
    }

    private void draw() {
        gridView.setNumColumns(level);
        File f = new File(Environment.getExternalStorageDirectory().getPath()+"/pp/temp.png");
        Uri saveUri = Uri.fromFile(f);
        try {
            Bitmap source = BitmapFactory.decodeStream(getContentResolver().openInputStream(saveUri));
            Bitmap blank = BitmapFactory.decodeResource(getResources(), R.drawable.camera);
            board = new Board(level, source, blank);
        } catch(Exception e) {
            e.printStackTrace();
        }
        //ArrayList<HashMap<String, Object>> gridItemList = new ArrayList<HashMap<String, Object>>();
        Variables.setGameStarted(false);
        Variables.setIsGaming(false);
        tiles = board.init();
        m = new MyAdapter(tiles,show);
        //��Ӳ�����ʾ
        gridView.setAdapter(m);
    }

    private void redraw() {
        handler.removeCallbacks(runnable);
        secondTime = 0;
        hour = 0;
        minute = 0;
        second = 0;
        isEnd = false;
        timer.setText("00:00");
        Variables.setGameStarted(false);
        Variables.setIsGaming(false);
        tiles = board.init();
        m.notifyDataSetChanged();
    }
    private void showDialog(String time, int score) {
        final Dialog dialog = new Dialog(Game.this,R.style.DialogTheme);
        dialog.setContentView(R.layout.dialog);
        ImageView share = (ImageView)dialog.findViewById(R.id.share);
        ImageView newGame = (ImageView)dialog.findViewById(R.id.newGame);
        ImageView cancelShare = (ImageView)dialog.findViewById(R.id.cancel_share);
        TextView timeUsed = (TextView)dialog.findViewById(R.id.time);
        TextView scoreGet = (TextView)dialog.findViewById(R.id.score);
        final int finalScore = score;
        timeUsed.setText(time);
        scoreGet.setText(String.valueOf(score));
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShareSDK.initSDK(getApplicationContext());
                OnekeyShare oks = new OnekeyShare();
                oks.disableSSOWhenAuthorize();
                oks.setTitle("PhotoPuzzle");  //���30���ַ�
                // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
                oks.setText("I just got " + finalScore + " in this Photo Puzzle! Can you beat me?");
                oks.setImagePath("/sdcard/pp/temp.png");
                oks.show(getApplicationContext());
            }
        });
        newGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        cancelShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void resumeDialog() {
        final Dialog dialog = new Dialog(Game.this,R.style.DialogTheme);

        dialog.setContentView(R.layout.resume_dialog);
        ImageView yes = (ImageView)dialog.findViewById(R.id.yes_resume);
        ImageView no = (ImageView) dialog.findViewById(R.id.no_resume);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pause.setImageResource(R.drawable.ic_pause_black_48dp);
                Variables.setIsGaming(true);
                handler.postDelayed(runnable, 1000);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void exitDialog() {
        final Dialog dialog = new Dialog(Game.this,R.style.DialogTheme);

        dialog.setContentView(R.layout.exit_dialog);
        ImageView yes = (ImageView)dialog.findViewById(R.id.yes_exit);
        ImageView no = (ImageView) dialog.findViewById(R.id.no_exit);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                save();
                finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        if(Variables.isGaming()) {
            pause.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            //Variables.setIsGaming(false);
            handler.removeCallbacks(runnable);
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if(Variables.isGaming() && !isEnd) {
                    pause.setImageResource(R.drawable.ic_pause_black_48dp);
                    //Variables.setIsGaming(true);
                    handler.postDelayed(runnable, 1000);
                }
            }
        });
        dialog.show();
    }

    private void hintDialog() {
        final Dialog dialog = new Dialog(Game.this,R.style.DialogTheme);

        dialog.setContentView(R.layout.hint_dialog);
        ImageView hintImage = (ImageView)dialog.findViewById(R.id.hint_image);
        Bitmap hintBitmap = BitmapFactory.decodeFile("/sdcard/pp/temp.png");
        hintImage.setImageBitmap(hintBitmap);
        if(Variables.isGaming()) {
            pause.setImageResource(R.drawable.ic_play_arrow_black_48dp);
            //Variables.setIsGaming(false);
            handler.removeCallbacks(runnable);
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (Variables.isGaming() && !isEnd) {
                    pause.setImageResource(R.drawable.ic_pause_black_48dp);
                    //Variables.setIsGaming(true);
                    handler.postDelayed(runnable, 1000);
                }
            }
        });
        dialog.show();
    }
    private void startGame() {
        Variables.setGameStarted(true);
        Variables.setIsGaming(true);
        pause.setImageResource(R.drawable.ic_pause_black_48dp);
        Toast.makeText(getApplicationContext(),"game started", Toast.LENGTH_SHORT).show();
        handler.postDelayed(runnable, 1000);
    }
    private void check() {
        if(board.isGoal()){
            handler.removeCallbacks(runnable);
            String time = timer.getText().toString();
            int score = scoreCal(time);
            showDialog(time, score);
            isEnd = true;
        }
    }
    private void save() {
        SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        File saveFile = new File("/sdcard/pp/save.txt");
        if(saveFile.exists()) {
            saveFile.delete();
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(saveFile));
            FileOutputStream bitmapOut = null;
            int i = 0;
            for(Tile tile : tiles) {
                out.write(String.valueOf(tile.getIndex()));
                out.newLine();
                bitmapOut = new FileOutputStream("/sdcard/pp/" + i + ".png");
                Bitmap temp = tile.getImage();
                temp.compress(Bitmap.CompressFormat.PNG, 100, bitmapOut);
                i++;
            }
            bitmapOut.close();
            out.close();
            Bitmap save = BitmapFactory.decodeFile("/sdcard/pp/temp.png");
            FileOutputStream saveOut = new FileOutputStream("/sdcard/pp/save.png");
            save.compress(Bitmap.CompressFormat.PNG, 100, saveOut);
            // BufferedWriter out = new BufferedWriter(new FileWriter(saveFile));

               /* Gson gson = new Gson();
                String json = gson.toJson(board);*/
                // editor.putString("tile" + i, json);
                // editor.apply();
                /*Log.i("save", json);
                out.append(json);
            out.append("\n");
            out.append(String.valueOf(secondTime));
            out.flush();
            out.close();*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        int currentTime = secondTime;
        editor.putInt("time", currentTime);
        editor.apply();
        editor.putBoolean("saved", true);
        editor.apply();
        editor.putInt("level", level);
        editor.apply();
        Toast.makeText(getApplicationContext(), "game saved",Toast.LENGTH_SHORT).show();
    }
    private void retrieve() {
        // SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
        //ArrayList<Tile> oldTiles = new ArrayList<Tile>();

        try {
            Bitmap save = BitmapFactory.decodeFile("/sdcard/pp/save.png");
            FileOutputStream saveOut = new FileOutputStream("/sdcard/pp/temp.png");
            save.compress(Bitmap.CompressFormat.PNG, 100, saveOut);
        }catch(Exception e) {

        }
        Log.i("Here!!!!!!!!", String.valueOf(level));
        gridView.setNumColumns(level);

        //tiles.clear();

        File saveFile = new File("/sdcard/pp/save.txt");
        try{
            tiles = new ArrayList<Tile>();
            /*BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(saveFile)));
                Gson gson = new Gson();
                String json = in.readLine();
                board = gson.fromJson(json, Board.class);
                Log.i("no",json);
                tiles = board.getTiles();

            secondTime = Integer.parseInt(in.readLine());
            in.close();*/
            BufferedReader in = new BufferedReader(new FileReader(saveFile));

            for (int i = 0; i < level * level; i++) {
                int index = Integer.valueOf(in.readLine());
                Bitmap temp = BitmapFactory.decodeFile("/sdcard/pp/" + i + ".png");
                tiles.add(new Tile(index, temp));
            }
            board = new Board(level, tiles);
            SharedPreferences sharedPreferences = getSharedPreferences("test", MODE_PRIVATE);
            secondTime = sharedPreferences.getInt("time", 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setTime(secondTime);
        m = new MyAdapter(tiles,show);
        gridView.setAdapter(m);

        Variables.setGameStarted(false);
        Variables.setIsGaming(false);
        Log.i("Here Here Here!!!!!!!!", board.toString());
    }
    private void setTime(int secondTime) {
        if (secondTime > 3600) {
            hour = secondTime/3600;
            minute = (secondTime % 3600) / 60;
            second = secondTime % 60;
        }
        else if (secondTime > 59) {
            minute = secondTime / 60;
            second = secondTime % 60;
        } else {
            second = secondTime;
        }
        if (secondTime > 3600)
            timer.setText("" + hour + ":" + String.format("%02d", minute) + ":" + String.format("%02d", second));
        else
            timer.setText("" + String.format("%02d", minute) + ":" + String.format("%02d", second));
    }

    private void takeScreenshot() {
        View rootView = findViewById(android.R.id.content).getRootView();
        rootView.setDrawingCacheEnabled(true);
        Bitmap screeShot = rootView.getDrawingCache();
        try {
            FileOutputStream bitmapOut = new FileOutputStream("/sdcard/pp/temp2.png");
            screeShot.compress(Bitmap.CompressFormat.PNG, 100, bitmapOut);
            bitmapOut.flush();
            bitmapOut.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private int scoreCal(String time) {
        int parse = Integer.valueOf(time.replaceAll(":",""));
        double score = (level - 2) * 1.0 / parse * 1000 * level * level;
        return (int) score;
    }
    public class MyAdapter extends BaseAdapter {
        ArrayList<Tile> tiles;
        boolean show;

        public MyAdapter(ArrayList<Tile> tiles, boolean show){
            this.tiles = tiles;
            this.show = show;
        }
        @Override
        public int getCount(){
            return tiles.size();
        }
        @Override
        public Object getItem(int position){
            return tiles.get(position);
        }
        @Override
        public long getItemId(int position){
            return position;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(show) {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) Game.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View itemView = inflater.inflate(R.layout.tile_item, null);
                    ImageView image = (ImageView) itemView.findViewById(R.id.tileImage);
                    TextView text = (TextView) itemView.findViewById(R.id.tileNumber);
                    text.setText(String.valueOf(tiles.get(position).getIndex()+1));
                    image.setImageBitmap(tiles.get(position).getImage());

                    return itemView;
                } else {
                    ImageView image = (ImageView) convertView.findViewById(R.id.tileImage);
                    TextView text = (TextView) convertView.findViewById(R.id.tileNumber);
                    text.setText(String.valueOf(tiles.get(position).getIndex()+1));
                    image.setImageBitmap(tiles.get(position).getImage());
                    return convertView;
                }
            }else {
                if (convertView == null) {
                    LayoutInflater inflater = (LayoutInflater) Game.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View itemView = inflater.inflate(R.layout.tile_item, null);
                    ImageView image = (ImageView) itemView.findViewById(R.id.tileImage);
                    image.setImageBitmap(tiles.get(position).getImage());

                    return itemView;
                } else {
                    ImageView image = (ImageView) convertView.findViewById(R.id.tileImage);
                    image.setImageBitmap(tiles.get(position).getImage());
                    return convertView;
                }
            }
        }
    }
    @Override
    protected void onDestroy () {
        super.onDestroy();
        ShareSDK.stopSDK(getApplicationContext());
    }
    @Override
    protected void onPause() {
        super.onPause();
        pause.setImageResource(R.drawable.ic_play_arrow_black_48dp);
        Variables.setIsGaming(false);
        handler.removeCallbacks(runnable);
    }
    @Override
    protected void onResume(){
        super.onResume();
        isPlaying = config.getBoolean("isPlaying", true);
        show = config.getBoolean("show", false);
        Variables.setPlaying(isPlaying);
        if(Variables.isPlaying()) {
            sound.setImageResource(R.drawable.ic_volume_up_black_48dp);
        } else{
            sound.setImageResource(R.drawable.ic_volume_off_black_48dp);
        }
        m = new MyAdapter(tiles, show);
        gridView.setAdapter(m);
    }
}

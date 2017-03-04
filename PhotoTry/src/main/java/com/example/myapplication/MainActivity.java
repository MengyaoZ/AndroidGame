package com.example.myapplication;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {
    private static final String TEMP_IMAGE_STORE = "temp.png";
    private static final int FROM_CAMERA = 0;
    private static final int FROM_ALBUM = 1;
    private static final int CROP_IMAGE = 2;
    private static Uri saveUri;
    private static File f;

    private ImageView selectPhotoCamera;
    private ImageView selectPhotoAlbum;
    private ImageView photoView;
    private ImageView confirm;
    private ImageView cancel;
    private ImageView mute;

    private SharedPreferences config;
    private int themeNum;
    private boolean isPlaying;
    private boolean isChosen = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        setContentView(R.layout.activity_main);

        selectPhotoCamera = (ImageView)this.findViewById(R.id.from_camera);
        selectPhotoAlbum = (ImageView)this.findViewById(R.id.from_album);
        photoView = (ImageView)this.findViewById(R.id.PhotoView);
        confirm = (ImageView)this.findViewById(R.id.Confirm);

        mute = (ImageView)this.findViewById(R.id.mute_main);
        /*LayoutParams params = photoView.getLayoutParams();
        params.height = photoView.getWidth();
        photoView.setLayoutParams(params);*/
        //photoView.setBackgroundColor(Color.GRAY);

        isChosen = false;
        /*if(Variables.isFirst()) {
            MusicPlayer.init(getApplicationContext(), musicNum);
            Variables.setFirst(false);
        }*/
        if(Variables.isPlaying()) {
            mute.setImageResource(R.drawable.ic_volume_up_black_48dp);
        } else{
            mute.setImageResource(R.drawable.ic_volume_off_black_48dp);
        }
        //Toast.makeText(getApplicationContext(),level,Toast.LENGTH_SHORT).show();
        //File f = new File(Environment.getExternalStorageDirectory()+File.separator+TEMP_IMAGE_STORE);
        File path = new File(Environment.getExternalStorageDirectory().getPath()+"/pp");
        if(!path.exists())
            path.mkdirs();
        f = new File(Environment.getExternalStorageDirectory().getPath()+"/pp/temp.png");
        //try {f.createNewFile();}catch (Exception e){Log.i("Here", "Still wrong");e.printStackTrace();};
        saveUri = Uri.fromFile(f);
        if(!f.exists()) {Log.i("Here", "not created");}

        selectPhotoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //camera.putExtra("crop", "true");
                //camera.putExtra("aspectX", 1);
                //camera.putExtra("aspectY", 1);
                //camera.putExtra("outputX", 200);
                //camera.putExtra("outputY", 200);
                //camera.putExtra("scale", true);
                camera.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, saveUri);
                camera.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
                //camera.putExtra("return-data", true);
                startActivityForResult(camera, FROM_CAMERA);
            }
        });
        selectPhotoAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent album = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //album.setType("image/*");
                //album.putExtra("crop", "true");
                //album.putExtra("aspectX", 1);
                //album.putExtra("aspectY", 1);
                //album.putExtra("outputX", 200);
                //album.putExtra("outputY", 200);
                //album.putExtra("scale", true);
                //album.putExtra("output", uri);
                //album.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                //album.putExtra("return-data", false);
                if (album.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(album, FROM_ALBUM);
                    //Toast.makeText(MainActivity.this, "yes", Toast.LENGTH_SHORT).show();
                }
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isChosen) {
                    Intent data = new Intent(MainActivity.this, ChooseLevel.class);
                    data.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(data);
                } else {
                    final Dialog dialog = new Dialog(MainActivity.this,R.style.DialogTheme);
                    dialog.setContentView(R.layout.error_dialog);
                    ImageView ok = (ImageView)dialog.findViewById(R.id.ok);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK) {
            if (requestCode == FROM_CAMERA) {
                //Bitmap cameraPhoto = data.getParcelableExtra("data");
                try {
                Bitmap cameraPhoto = BitmapFactory.decodeStream(getContentResolver().openInputStream(saveUri));
                //photoView.setImageBitmap(cameraPhoto);
                cropPicture(saveUri);
                } catch (Exception e) {
                    Log.i("Here", "this");
                    e.printStackTrace();
                }
            } else if (requestCode == FROM_ALBUM) {
                //Bitmap AlbumPhoto = data.getParcelableExtra("data");
                //Bundle extras = data.getExtras();
                //if (extras != null) {
                    //Bitmap albumPhoto = extras.getParcelable("data");
                    Uri uri = data.getData();
                    try {
                        Bitmap albumPhoto = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                        //photoView.setImageBitmap(albumPhoto);
                        cropPicture(uri);
                    } catch (Exception e) {
                        Log.i("Here", "this");
                        e.printStackTrace();
                    }
                    //photoView.setImageBitmap(albumPhoto);
                    Toast.makeText(MainActivity.this, uri.toString(), Toast.LENGTH_SHORT).show();
                //}
            }
            else if(requestCode == CROP_IMAGE) {
                Bundle extras = data.getExtras();
                Log.i("cropped", String.valueOf(f));
                //Bitmap photo = extras.getParcelable("data");
                //photoView.setImageBitmap(photo);
                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    photoView.setImageBitmap(photo);
                    try {
                    // Bitmap photo = BitmapFactory.decodeStream(getContentResolver().openInputStream(saveUri));
                        FileOutputStream out = new FileOutputStream(f);
                        photo.compress(Bitmap.CompressFormat.PNG, 100, out);
                        //out.flush();
                        //out.close();
                        //Bitmap photo2 = BitmapFactory.decodeStream(getContentResolver().openInputStream(saveUri));
                        //photoView.setImageBitmap(photo2);
                        isChosen = true;
                    } catch (Exception e) {
                        Log.i("Here", "this");
                        e.printStackTrace();
                    }
                }
            }
        } //else
            //Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        //isChosen = false;
    }
    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    private void cropPicture(Uri uri) {

        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri

            cropIntent.setDataAndType(uri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            cropIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, saveUri);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, CROP_IMAGE);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
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



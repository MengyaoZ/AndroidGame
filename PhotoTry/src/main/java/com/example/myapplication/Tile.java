package com.example.myapplication;

import android.graphics.Bitmap;

/**
 * Created by user on 2015/11/17.
 */
public class Tile {
    private int index;
    private Bitmap image;

    Tile(int index, Bitmap image) {
        this.index = index;
        this.image = image;
    }

    public void setIndex(int i) {
        this.index = i;
    }

    public int getIndex() {
        return index;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
    public Bitmap getImage() {
        return image;
    }
}

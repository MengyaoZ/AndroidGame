package com.example.myapplication;

/**
 * Created by user on 2015/11/17.
 */

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class Board implements Comparable<Board> {

    private static final int MOVE_UP = 0;
    private static final int MOVE_DOWN = 1;
    private static final int MOVE_LEFT = 2;
    private static final int MOVE_RIGHT = 3;

    private ArrayList<Tile> tiles;
    private int N;
    private int total;
    private int blankIndex;

    public Board(int dimension, Bitmap image, Bitmap blankImage) {
        this.N = dimension;
        this.total = N * N;
        this.tiles = splitImage(N, image, blankImage);
    }
    public Board(int dimension, ArrayList<Tile> tiles) {
        this.N = dimension;
        this.total = N * N;
        this.tiles = tiles;
        for (int i = 0; i < total; i++) {
            if (tiles.get(i).getIndex() == total - 1) {
                blankIndex = i;
                break;
            }
        }
    }

    public void reconstruct(int dimension, ArrayList<Tile> tiles) {
        this.N = dimension;
        this.total = N * N;
        this.tiles = tiles;
        for (int i = 0; i < total; i++) {
            if (tiles.get(i).getIndex() == total - 1) {
                blankIndex = i;
                break;
            }
        }
    }
    public int getBlankIndex() {
        return blankIndex;
    }
    public ArrayList<Tile> getTiles() {
        return tiles;
    }
    /*public void setBlankIndex(ArrayList<Tile> list) {
        for (int i = 0; i < total; i++) {
            if (list.get(i).getIndex() == total - 1) {
                blankIndex = i;
                break;
            }
        } }*/

    public void move(int command) {
        switch (command) {
            case MOVE_UP:
                if (blankIndex > N - 1) {
                    Collections.swap(tiles, blankIndex, blankIndex - N);
                    blankIndex = blankIndex - N;
                }
                break;
            case MOVE_DOWN:
                if(blankIndex < total - N) {
                    Collections.swap(tiles, blankIndex, blankIndex + N);
                    blankIndex = blankIndex + N;
                }
                break;
            case MOVE_LEFT:
                if (blankIndex % N != 0) {
                    Collections.swap(tiles, blankIndex, blankIndex - 1);
                    blankIndex = blankIndex -1;
                }
                break;
            case MOVE_RIGHT:
                if (blankIndex % N != N - 1) {
                    Collections.swap(tiles, blankIndex, blankIndex + 1);
                    blankIndex = blankIndex + 1;
                }
                break;
        }
    }

    public ArrayList<Tile> init() {
        Collections.shuffle(tiles);
        for (int i = 0; i < total; i++) {
            if (tiles.get(i).getIndex() == total - 1) {
                blankIndex = i;
                break;
            }
        }
        return tiles;
    }

    public int hamming() {
        int hamming = 0;
        for (int i = 0; i < total; i++) {
            if (tiles.get(i).getIndex() != i)
                hamming++;
        }
        return hamming;
    }

    public int manhattan() {
        return 0;
    }

    public boolean isGoal() {
        for (int i = 0; i < total; i++) {
            if (tiles.get(i).getIndex() != i) {
                return false;
            }
        }
        return true;
    }

    public Board twin() {
        ArrayList<Tile> twin = new ArrayList<Tile>();
        for (Tile t : tiles) {
            twin.add(t);
        }
        if (twin.get(0).getIndex() != total - 1 && twin.get(1).getIndex() != total - 1) {
            Collections.swap(twin, 0, 1);
        } else {
            Collections.swap(twin, 2, 3);
        }
        return new Board(N, twin);
    }

    public boolean equals(Board another) {
        if (another == this)
            return true;
        if (another == null)
            return false;
        if (another.N != this.N)
            return false;
        for (int i = 0; i < total; i++) {
            if (another.tiles.get(i).getIndex() != this.tiles.get(i).getIndex())
                return false;
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        PriorityQueue<Board> p = new PriorityQueue<Board>();
        ArrayList<Tile> neighbor = new ArrayList<Tile>();
        for (Tile t : tiles) {
            neighbor.add(t);
        }
        if (blankIndex > N - 1) {
            Collections.swap(neighbor, blankIndex, blankIndex - N);
            p.add(new Board(N, neighbor));
            Collections.swap(neighbor, blankIndex, blankIndex - N);
        }
        if (blankIndex < total - N) {
            Collections.swap(neighbor, blankIndex, blankIndex + N);
            p.add(new Board(N, neighbor));
            Collections.swap(neighbor, blankIndex, blankIndex + N);
        }
        if (blankIndex % N != 0) {
            Collections.swap(neighbor, blankIndex, blankIndex - 1);
            p.add(new Board(N, neighbor));
            Collections.swap(neighbor, blankIndex, blankIndex - 1);
        }
        if (blankIndex % N != N - 1) {
            Collections.swap(neighbor, blankIndex, blankIndex + 1);
            p.add(new Board(N, neighbor));
            Collections.swap(neighbor, blankIndex, blankIndex + 1);
        }
        return p;
    }

    /*
     * public String toString() {
     *
     * }
     */
    public int compareTo(Board another) {
        return this.hamming() - another.hamming();
    }

    public ArrayList<Tile> splitImage(int n, Bitmap image, Bitmap blankImage) {
        ArrayList<Tile> splitTiles = new ArrayList<Tile>();
        int width = image.getWidth();
        int height = image.getHeight();
        int pieceWidth = width / n;
        int pieceHeight = height / n;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int xValue = j * pieceWidth;
                int yValue = i * pieceHeight;
                Bitmap tileImage;
                if(i == n - 1 && j == n - 1){
                    tileImage = Bitmap.createBitmap(blankImage,0,0,pieceWidth,pieceHeight);
                }
                else {
                    tileImage = Bitmap.createBitmap(image, xValue, yValue,
                            pieceWidth, pieceHeight);
                }

                Tile tile = new Tile(j + i * n, tileImage);

                splitTiles.add(tile);
            }
        }
        return splitTiles;
    }

    public String toString() {
        return "level=" + String.valueOf(N) + ", i am a board";
    }
}


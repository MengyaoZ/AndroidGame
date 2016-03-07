package com.example.myapplication;

/**
 * Created by user on 2015/11/20.
 */
public class Variables {
    private static boolean isFirst = true;
    private static boolean isPlaying = true;
    private static boolean isGaming = false;
    private static boolean gameStarted = false;

    public static boolean isFirst() { return isFirst; }
    public static void setFirst(boolean isFirst) {
        Variables.isFirst = isFirst;
    }

    public static boolean isPlaying() { return isPlaying; }
    public static void setPlaying(boolean isPlaying) {
        Variables.isPlaying = isPlaying;
    }

    public static boolean isGaming() { return isGaming; }
    public static void setIsGaming(boolean isGaming) {
        Variables.isGaming = isGaming;
    }

    public static boolean isGameStarted() { return gameStarted; }
    public static void setGameStarted(boolean gameStarted) { Variables.gameStarted = gameStarted; }
}

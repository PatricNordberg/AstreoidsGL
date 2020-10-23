package com.hfad.astreoidsgl;

import android.graphics.Color;

public class GameConfig {

    //Jukebox
    public static int HURT = 0;
    public static int START_GAME = 0;
    public static int PICKUP_COIN = 0;
    public static int LASER = 0;
    public static int EXPLOSION = 0;
    public static int GAME_OVER = 0;
    public static final int MAX_STREAMS = 3;
    public static final int BG_VOLUME_LEFT = 100;
    public static final int BG_VOLUME_RIGHT = 100;


    //Game
    public static final double NANOS_TO_SECONDS = 1.0 / 1000000000;
    public static final int BG_COLOR = Color.rgb(135, 206, 235);
    public static int STAGE_WIDTH = 1280;
    public static int STAGE_HEIGHT = 720;
    public static final float METERS_TO_SHOW_X = 16f;
    public static final float METERS_TO_SHOW_Y = 0f;//0 for calculate this for us

}

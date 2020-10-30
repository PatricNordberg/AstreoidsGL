package com.hfad.astreoidsgl;

public class GameConfig {

    public static final float WORLD_WIDTH = 250;
    public static final float WORLD_HEIGHT = 140;
    //Asteroid
    public static final int ASTEROID_COUNT = 10;
    public static final int PARTICLE_COUNT = 20;
    //Bullet
    public static final float SPEED = 120f;
    public static final float TIME_TO_LIVE = 1.5f; //seconds
    public static final int MAX_STREAMS = 3;
    public static final int BG_VOLUME_LEFT = 100;
    public static final int BG_VOLUME_RIGHT = 100;
    public static final float ROTATION_VELOCITY = 360f;
    public static final float THRUST = 40f;
    public static final float DRAG = 0.99f;
    public static final float TIME_BETWEEN_SHOTS = 0.25f; //seconds.
    public static final float TIME_BETWEEN_HYPERSPACE = 0.75f; //seconds.
    public static final float TIME_BETWEEN_BOOST = 0.75f; //seconds.
    //General
    static final float[] BG_COLOR = {0 / 255f, 0 / 255f, 0 / 255f, 1f}; //RGBA
    static final float METERS_TO_SHOW_X = WORLD_WIDTH; //160m x 90m, the entire game world in view
    static final float METERS_TO_SHOW_Y = WORLD_HEIGHT;
    //Star
    static final int STAR_COUNT = 100;
    //Jukebox
    public static int HURT = 0;
    public static int START_GAME = 0;
    public static int PICKUP_COIN = 0;
    public static int LASER = 0;
    public static int EXPLOSION = 0;
    public static int GAME_OVER = 0;
    public static int BOOST = 0;
    public static int HYPERSPACE = 0;
    //Player
    public static int _health = 3;
    public static int _score = 0;

}
